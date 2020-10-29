package com.ryan.project.smarthomehub.exception;

import lombok.NoArgsConstructor;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
@NoArgsConstructor
public class GrantException extends RuntimeException {

    public GrantException(String message) {
        super(message);
    }
}
