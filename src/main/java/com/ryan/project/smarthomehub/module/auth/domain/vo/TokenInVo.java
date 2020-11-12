package com.ryan.project.smarthomehub.module.auth.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */

@Data
public class TokenInVo {

    @NotBlank
    String client_id;

    @NotBlank
    String client_secret;

    @NotBlank
    String grant_type;

    String code;

    String redirect_uri;

    String refresh_token;
}
