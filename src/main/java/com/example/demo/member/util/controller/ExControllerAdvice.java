<<<<<<< HEAD
package com.example.demo.member.util.controller;

import com.example.demo.member.util.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResult> testing(NullPointerException e) {
        ErrorResult errorResult = new ErrorResult("EMAIL", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
=======
//package com.example.demo.member.util.controller;
//
//import com.example.demo.member.util.ErrorResult;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class ExControllerAdvice {
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<ErrorResult> testing(NullPointerException e) {
//        ErrorResult errorResult = new ErrorResult("EMAIL", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//    }
//}
>>>>>>> 6cb878cdb58f44497aaa7be24b93759e3b081559