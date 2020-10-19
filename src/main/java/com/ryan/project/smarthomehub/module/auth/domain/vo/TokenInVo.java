package com.ryan.project.smarthomehub.module.auth.domain.vo;

import lombok.Data;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */

@Data
public class TokenInVo {
    String client_id;
    String client_secret;
    String grant_type;
    String code;
    String redirect_uri;
    String refresh_token;
}
