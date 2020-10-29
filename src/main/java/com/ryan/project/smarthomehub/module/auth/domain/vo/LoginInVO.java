package com.ryan.project.smarthomehub.module.auth.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Descritption
 * @Date 2020/10/16
 * @Author tangqianli
 */
@Data
public class LoginInVO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String clientId;
}
