package com.ryan.project.smarthomehub.exception;

import lombok.NoArgsConstructor;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
@NoArgsConstructor
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
