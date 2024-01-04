package com.example.demo.member.user.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.master.repository.MasterRepository;
import com.example.demo.member.user.dto.request.NaverUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.dto.request.KakaoUserDTO;
import com.example.demo.member.user.dto.request.LoginRequestDTO;
import com.example.demo.member.user.dto.request.UserJoinRequestDTO;
import com.example.demo.member.user.dto.response.LoginResponseDTO;
import com.example.demo.member.user.dto.response.UserJoinResponseDTO;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import com.example.demo.token.dto.TokenDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static javax.crypto.Cipher.SECRET_KEY;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MasterRepository masterRepository;

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.redirect_url}")
    private String KAKAO_REDIRECT_URI;
    @Value("${kakao.client_secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${naver.client_id}")
    private String NAVER_CLIENT_ID;

    @Value("${naver.client_secret}")
    private String NAVER_CLIENT_SECRET;

    public boolean isDuplicateId(String id) {

        return userRepository.existsById(id) || lawyerRepository.existsById(id) || id.equals("#master");
    }

    public boolean isDuplicateEmail(String email) {

        return userRepository.existsByEmail(email) || lawyerRepository.existsByEmail(email);
    }

    public boolean isDuplicateNickname(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    // 회원 가입 처리(사용자)
    public UserJoinResponseDTO createUser(final UserJoinRequestDTO dto) {

        String encoded = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        User saved = userRepository.save(dto.toEntity());
        log.info("사용자 회원 가입 정상 수행됨! - saved user - {}", saved);

        UserJoinResponseDTO responseDTO = new UserJoinResponseDTO(saved);
        Member member = responseDTO.insertMember(saved);
        memberRepository.save(member);

        return responseDTO;
    }

    // 로그인
    public LoginResponseDTO authenticate(
            HttpServletResponse response,
            final LoginRequestDTO dto
    ) {
        // 아이디를 통해 회원 정보 조회
        Member member = memberRepository.findById(dto.getId()).orElseThrow(
                        () -> new RuntimeException("no-account")
        );

        log.info("\n\n\n로그인 요청 한 회원: {}\n\n\n", member.getId());

        // 비밀번호 얻기
        String password = null;
        if(member.getAuthority().equals("user")) {
            password =  member.getUser().getPassword();
        } else if(member.getAuthority().equals("lawyer")) {
            password =  member.getLawyer().getLawyerPw();
        }
        log.info("\n\n\n비밀번호: {}\n\n\n", password);

        // 비밀번호 검증
        String rawPassword = dto.getPassword(); // 입력한 비번
        String encodedPassword = password; // DB에 저장된 암호화된 비번

        if(!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("wrong-password");
        }

        // 아이디와 비밀번호가 일치할 경우 로그인 성공
        log.info("{}님 로그인 성공!", member.getId());

        // 토큰 dto 생성(accessToken과 refreshToken도 생성됨)
        TokenDTO tokenDTO = tokenProvider.createToken(member);
        log.info("\n\n\n토큰 DTO가 생성됨 - {}\n\n\n", tokenDTO);

        if(member.getAuthority().equals("user")) {
            member.getUser().setRefreshToken(tokenDTO.getRefreshToken());
        } else if(member.getAuthority().equals("lawyer")) {
            member.getLawyer().setRefreshToken(tokenDTO.getRefreshToken());
        }

        if(!authenticateLawyer(member))
            return LoginResponseDTO.builder()
                    .id(member.getId())
                    .name(member.getLawyer().getName())
                    .accessToken(tokenDTO.getAccessToken())
                    .refreshToken(tokenDTO.getRefreshToken())
                    .authority("notApproval").build();

        memberRepository.save(member);

        // Response 헤더에 필요한 토큰 정보 추가하여 반환
        response.addHeader("Authorization", "Bearer" + tokenDTO.getAccessToken());
        response.addHeader("Refresh-Token", tokenDTO.getRefreshToken());
        log.info("\n\n\nResponse에 헤더 추가됨 - {}\n\n\n", response.getHeader("Authorization"));

        return new LoginResponseDTO(member, tokenDTO);
    }

    private boolean authenticateLawyer(Member member) {

        Optional<Lawyer> byId = lawyerRepository.findById(member.getId());

        if(byId.isPresent()) {
                return byId.orElseThrow().isApproval();
        }
        return true;
    }

    // 네이버 로그인
    public LoginResponseDTO naverService(String code, String state) {

        Map<String, Object> responseData
                = getNaverAccessToken(code, state);

        String accessToken = (String) responseData.get("access_token");
        String refreshToken = (String) responseData.get("refresh_token");

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // 토큰을 통해 사용자 정보 가져오기
        NaverUserDTO dto = getNaverUserInfo(accessToken);

        if(!isDuplicateEmail(dto.getEmail())) {
            userRepository.save(dto.toEntity(accessToken));
        }
        User foundUser = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        Member member = Member.builder()
                .id(foundUser.getId())
                .authority(foundUser.getAuthority())
                .user(foundUser)
                .build();

        memberRepository.save(member);

        TokenDTO tokenDTO = tokenProvider.createToken(member);// 토큰 발급

        foundUser.setAccessToken(accessToken);
        foundUser.setRefreshToken(refreshToken);
        userRepository.save(foundUser);

        return new LoginResponseDTO(member, tokenDTO);

    }

    private Map<String, Object> getNaverAccessToken(String code, String state) {

        // 요청 uri
        String requestUri = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        log.info("getNaverAccessToken   code: {}, state: {}", code, state);
        // 요청 바디(파라미터) 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", state);

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        log.info("params: {}", params);
        // 네이버 서버로 POST 통신
        RestTemplate template = new RestTemplate();

        try{
        // 통신을 보내면서 응답데이터를 리턴
//        ResponseEntity<Map> responseEntity
//                = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

            ResponseEntity<Map> responseEntity = template.postForEntity(requestUri, requestEntity, Map.class);
            log.info("url: {}", responseEntity);
        // 응답 데이터에서 필요한 정보를 가져오기
        Map<String, Object> responseData = (Map<String, Object>)responseEntity.getBody();
        log.info("토큰 요청 응답 데이터: {}", responseData);

        return responseData;
        } catch (Exception e) {
            log.error("액세스 토큰 요청 중 에러 발생", e);
            return null;
        }
    }

    private NaverUserDTO getNaverUserInfo(String accessToken) {
        // 요청 uri
        String requestUri = "https://openapi.naver.com/v1/nid/me";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        log.info("userInfo: {}", headers);
        // 요청 보내기
        RestTemplate template = new RestTemplate();
        ResponseEntity<Map> responseEntity
                = template.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> responseMap = (Map<String, Object>)responseEntity.getBody().get("response");
        // 응답 바디 읽기
        NaverUserDTO naverUserDTO = new NaverUserDTO();
        naverUserDTO.setId((String)responseMap.get("id"));
        naverUserDTO.setEmail((String)responseMap.get("email"));
        naverUserDTO.setName((String)responseMap.get("name"));
        log.info("user profile: {}", naverUserDTO);

        return naverUserDTO;

    }

    // 카카오 로그인
    public LoginResponseDTO kakaoService(String code) {

        // 인가코드를 통해 토큰 발급받기
        Map<String, Object> responseData
                = getKakaoAccessToken(code);

        String accessToken = (String) responseData.get("access_token");
        String refreshToken = (String) responseData.get("refresh_token");

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // 토큰을 통해 사용자 정보 가져오기
        KakaoUserDTO dto = getKakaoUserInfo(accessToken);

        if(!isDuplicateEmail(dto.getKakaoAccount().getEmail())) {
            userRepository.save(dto.toEntity(accessToken));
        }
        User foundUser = userRepository.findByEmail(dto.getKakaoAccount().getEmail()).orElseThrow();

        Member member = Member.builder()
                .id(foundUser.getId())
                .authority(foundUser.getAuthority())
                .user(foundUser)
                .build();

        memberRepository.save(member);

        TokenDTO tokenDTO = tokenProvider.createToken(member);// 토큰 발급

        foundUser.setAccessToken(accessToken);
        foundUser.setRefreshToken(refreshToken);
        userRepository.save(foundUser);

        return new LoginResponseDTO(member, tokenDTO);
    }


    private KakaoUserDTO getKakaoUserInfo(String accessToken) {

        // 요청 uri
        String requestUri = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 보내기
        RestTemplate template = new RestTemplate();
        ResponseEntity<KakaoUserDTO> responseEntity
                = template.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), KakaoUserDTO.class);

        // 응답 바디 읽기
        KakaoUserDTO responseData = responseEntity.getBody();
        log.info("user profile: {}", responseData);

        return responseData;
    }

    private Map<String, Object> getKakaoAccessToken(String code) {

        // 요청 uri
        String requestUri = "https://kauth.kakao.com/oauth/token";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 바디(파라미터) 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // 카카오 공식 문서 기준 값으로 세팅
        params.add("client_id", KAKAO_CLIENT_ID); // 카카오 디벨로퍼 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URI); // 카카오 디벨로퍼 등록된 redirect uri
        params.add("code", code); // 프론트에서 인가 코드 요청시 전달받은 코드값
        params.add("client_secret", KAKAO_CLIENT_SECRET); // 카카오 디벨로퍼 client secret(활성화 시 추가해 줘야 함)

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        // 카카오 서버로 POST 통신
        RestTemplate template = new RestTemplate();

        // 통신을 보내면서 응답데이터를 리턴
        ResponseEntity<Map> responseEntity
                = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

        // 응답 데이터에서 필요한 정보를 가져오기
        Map<String, Object> responseData = (Map<String, Object>)responseEntity.getBody();
        log.info("토큰 요청 응답 데이터: {}", responseData);

        return responseData;
    }

    // 액세스 토큰이 만료되었다면 이 메서드 호출하기
    public Map<String, Object> updateKakaoToken(String code) {

        Map<String, Object> responseData = getKakaoAccessToken(code);
        String refreshToken = (String) responseData.get("refresh_token");
        String accessToken = (String) responseData.get("access_token");

        String id = String.valueOf(getKakaoUserInfo(accessToken).getId());
        User foundUser = userRepository.findById(id).orElseThrow();

        // 요청 uri
        String requestUri = "https://kauth.kakao.com/oauth/token";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 바디(파라미터) 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("refresh_token", refreshToken);
        params.add("client_secret", KAKAO_CLIENT_SECRET); // 카카오 디벨로퍼 client secret(활성화 시 추가해 줘야 함)

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        // 카카오 서버로 POST 통신
        RestTemplate template = new RestTemplate();

        // 통신을 보내면서 응답데이터를 리턴
        ResponseEntity<Map> responseEntity
                = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

        // 응답 데이터에서 필요한 정보를 가져오기
        Map<String, Object> response = (Map<String, Object>)responseEntity.getBody();
        log.info("토큰 갱신 요청 응답 데이터: {}", response);

        // 액세스 토큰 갱신 및 저장
        String newAccessToken = (String) response.get("access_token");
        foundUser.setAccessToken(newAccessToken);
        Member member = Member.builder()
                .id(id)
                .authority(foundUser.getAuthority())
                .user(foundUser)
                .build();

        memberRepository.save(member);

        return response;
    }

    public String logout(HttpServletRequest request, final TokenMemberInfo memberInfo) {

        String accessToken = request.getHeader("Authorization");

        log.info("\n\n\naccessToken - {}", accessToken);

        Member member = memberRepository.findById((memberInfo.getId())).orElseThrow();

        // 카카오 로그아웃
        if(member.getUser().getAuthority().equals("user") && member.getUser().getJoinMethod().equals("kakao")) {

            String reqUri = "https://kapi.kakao.com/v1/user/logout";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            RestTemplate template = new RestTemplate();

            ResponseEntity<String> responseData
                    = template.exchange(reqUri, HttpMethod.POST, new HttpEntity<>(headers), String.class);

            log.info("logout: {}", responseData);

            return responseData.getBody();
        } else if(member.getUser().getAuthority().equals("user") && member.getUser().getJoinMethod().equals("naver")) {
            String reqUri = "https://nid.naver.com/oauth2.0/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "delete");
            params.add("client_id", NAVER_CLIENT_ID);
            params.add("client_secret", NAVER_CLIENT_SECRET);
            params.add("access_token", accessToken);
            params.add("service_provider", "NAVER");

            // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
            HttpEntity<Object> requestEntity = new HttpEntity<>(params);

            log.info("logout: {}", requestEntity);

            RestTemplate template = new RestTemplate();

            ResponseEntity<String> responseData = template.postForEntity(reqUri, requestEntity, String.class);
            return responseData.getBody();
        }

        // 카카오 로그인을 한 사람이 아닐 경우
        return null;
    }

    // 법봉 충전
    public void getHammerCharge(int hammer, TokenMemberInfo userInfo) {

        User user = userRepository.findById(userInfo.getId()).orElseThrow();
        user.setHammer(user.getHammer() + hammer);

        userRepository.save(user);
    }


    public int hammerCnt(TokenMemberInfo userInfo) {

        if(userRepository.findById(userInfo.getId()).isPresent()) {
            return userRepository.findById(userInfo.getId()).orElseThrow().getHammer();
        } else {
            return lawyerRepository.findById(userInfo.getId()).orElseThrow().getHammer();
        }
    }


    public Map<String, Object> updateNaverToken(String code, String state) {

        Map<String, Object> responseData = getNaverAccessToken(code, state);
        String refreshToken = (String) responseData.get("refresh_token");
        String accessToken = (String) responseData.get("access_token");

        String id = String.valueOf(getNaverUserInfo(accessToken).getId());
        User foundUser = userRepository.findById(id).orElseThrow();

        // 요청 uri
        String requestUri = "https://nid.naver.com/oauth2.0/token";

        // 요청 바디(파라미터) 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("refresh_token", refreshToken);
        params.add("client_secret", NAVER_CLIENT_SECRET); // 카카오 디벨로퍼 client secret(활성화 시 추가해 줘야 함)

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params);

        // 카카오 서버로 POST 통신
        RestTemplate template = new RestTemplate();

        // 통신을 보내면서 응답데이터를 리턴
        ResponseEntity<Map> responseEntity
                = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

        // 응답 데이터에서 필요한 정보를 가져오기
        Map<String, Object> response = (Map<String, Object>)responseEntity.getBody();
        log.info("토큰 갱신 요청 응답 데이터: {}", response);

        // 액세스 토큰 갱신 및 저장
        String newAccessToken = (String) response.get("access_token");
        foundUser.setAccessToken(newAccessToken);
        Member member = Member.builder()
                .id(id)
                .authority(foundUser.getAuthority())
                .user(foundUser)
                .build();

        memberRepository.save(member);

        return response;
    }


    public boolean validateReqLogin(LoginRequestDTO dto) {
        return memberRepository.findById(dto.getId()).isPresent()
                && memberRepository.findById(dto.getId()).orElseThrow().getAuthority().equals(dto.getReqAuthority());
    }
}
