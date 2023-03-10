package com.ryan.project.smarthomehub.module.auth.controller;

import com.ryan.project.smarthomehub.exception.GrantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
public class AuthAdviceController {

    @ExceptionHandler(GrantException.class)
    public ResponseEntity authFail(GrantException grantException){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(grantException.getMessage());
    }
}
