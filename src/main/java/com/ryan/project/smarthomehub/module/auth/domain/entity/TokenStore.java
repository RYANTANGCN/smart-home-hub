package com.ryan.project.smarthomehub.module.auth.domain.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @Descritption
 * @Date 2020/10/15
 * @Author ryan
 */
@Data
@Entity
public class TokenStore {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer clientId;

    private String refreshToken;

    private LocalDateTime expiredTime;

    private Boolean isRevoke;

    private String name;

    private String remark;

}
