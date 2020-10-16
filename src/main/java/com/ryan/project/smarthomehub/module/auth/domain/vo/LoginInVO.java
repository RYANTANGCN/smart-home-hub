package com.ryan.project.smarthomehub.module.auth.domain.vo;

import lombok.Data;

/**
 * @Descritption
 * @Date 2020/10/16
 * @Author tangqianli
 */
@Data
public class LoginInVO {
    private String username;
    private String password;
    private String clientId;
}
