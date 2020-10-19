package com.ryan.project.smarthomehub.module.auth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenOutVo {

    private String token_type;

    private String access_token;

    private String refresh_token;

    private Integer expires_in;
}
