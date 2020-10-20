package com.ryan.project.smarthomehub.module.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @Descritption
 * @Date 2020/10/15
 * @Author ryan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TokenStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String clientId;

    private String refreshToken;

    private String userOpenId;

    private LocalDateTime expiredTime;

    private Boolean isRevoke;

    private String name;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
