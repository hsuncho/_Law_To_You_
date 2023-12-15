package com.example.demo.member.user.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.redirect_url}")
    private String KAKAO_REDIRECT_URI;
    @Value("${kakao.client_secret}")
    private String KAKAO_CLIENT_SECRET;


    public boolean isDuplicateId(String id) {

        return userRepository.existsById(id) || lawyerRepository.existsById(id);
    }

    public boolean isDuplicateEmail(String email) {

        return userRepository.existsByEmail(email) || lawyerRepository.existsByEmail(email);
    }

    public boolean isDuplicateNickname(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    // 회원 가입 처리(사용자)
    public UserJoinResponseDTO createUser(final UserJoinRequestDTO dto) {

        String id = dto.getId();
        String email = dto.getEmail();
        String nickname = dto.getNickname();

        if(isDuplicateId(id)) {
            log.warn("아이디가 중복되었습니다. - {}", id);
        }

        if(isDuplicateEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
        }

        if(isDuplicateNickname(nickname)) {
            log.warn("닉네임이 중복되었습니다. - {}", nickname);
        }

        String encoded = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        User saved = userRepository.save(dto.toEntity());
        log.info("사용자 회원 가입 정상 수행됨! - saved user - {}", saved);

        UserJoinResponseDTO responseDTO = new UserJoinResponseDTO(saved);
        Member member = responseDTO.insertMember(saved);
        memberRepository.save(member);

        return responseDTO;
    }


    public LoginResponseDTO authenticate(final LoginRequestDTO dto) {
        
        // 아이디를 통해 회원 정보 조회
        Member member = memberRepository.findById(dto.getId()).orElseThrow(
                () -> new RuntimeException("\n\n\n가입된 회원이 아닙니다.\n\n\n")
        );

        log.info("\n\n\n로그인 요청 한 회원: {}\n\n\n", member.getId());
        
        // authority로 member에서 user인지 lawyer인지 확인 후 비밀번호 얻기
        String password = null;
        if(member.getAuthority().equals("user")) {
            password = member.getUser().getPassword();

        } else if(member.getAuthority().equals("lawyer")) {
            password = member.getLawyer().getLawyerPw();
        }
        log.info("\n\n\n비밀번호: {}\n\n\n", password);

        // 비밀번호 검증
        String rawPassword = dto.getPassword(); // 입력한 비번
        String encodedPassword = password; // DB에 저장된 암호화된 비번

        if(!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
        
        // 아이디와 비밀번호가 일치할 경우 로그인 성공
        log.info("{}님 로그인 성공!", member.getId());

        // accessToken과 refreshToken 생성
        TokenDTO tokenDTO = tokenProvider.createToken(member);
    
        // DB에 refreshToken 저장
        member.getUser().setRefreshToken(tokenDTO.getRefreshToken());
        memberRepository.save(member);

        return new LoginResponseDTO(member, tokenDTO);

    }

    // 카카오 로그인
    public LoginResponseDTO kakaoService(String code) {

        // 인가코드를 통해 토큰 발급받기
        Map<String, Object> responseData
                = getKakaoAccessToken(code);
        log.info("token: {}", responseData.get("access_token"));

        // 토큰을 통해 사용자 정보 가져오기
        KakaoUserDTO dto = getKakaoUserInfo((String) responseData.get("access_token"));

        if(!isDuplicateEmail(dto.getKakaoAccount().getEmail())) {
            User saved
                    = userRepository.save(dto.toEntity((String) responseData.get("access_token")));
        }
        User foundUser = userRepository.findByEmail(dto.getKakaoAccount().getEmail()).orElseThrow();

        Member member = Member.builder()
                .id(foundUser.getId())
                .authority(foundUser.getAuthority())
                .user(foundUser)
                .build();

        memberRepository.save(member);

        TokenDTO tokenDTO = tokenProvider.createToken(member);// 토큰 발급
        
        foundUser.setAccessToken((String) responseData.get("access_token"));
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

    public String logout(final TokenMemberInfo memberInfo) {
        Member member = memberRepository.findById((memberInfo.getId())).orElseThrow();

        String accessToken = null;

        if(member.getAuthority().equals("user")) {
            accessToken = member.getUser().getAccessToken();
        } else if(member.getAuthority().equals("lawyer")) {
            accessToken = member.getLawyer().getAccessToken();
        }

        if(member.getUser().getJoinMethod().equals("kakao")) {
            String reqUri = "https://kapi.kakao.com/v1/user/logout";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            RestTemplate template = new RestTemplate();
            ResponseEntity<String> responseData
                    = template.exchange(reqUri, HttpMethod.POST, new HttpEntity<>(headers), String.class);

            return responseData.getBody();
        }

        // 카카오 로그인을 한 사람이 아닐 경우
        return null;

    }




























}
