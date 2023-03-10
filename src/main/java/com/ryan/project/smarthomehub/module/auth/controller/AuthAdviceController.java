package com.ryan.project.smarthomehub.module.auth.controller;

import com.ryan.project.smarthomehub.exception.GrantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuthAdviceController {

    @ExceptionHandler(GrantException.class)
    public ResponseEntity<Map<String,String>> authFail(GrantException grantException){
        Map<String, String> map = new HashMap<>();
        map.put("error", "invalid_grant");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
}
