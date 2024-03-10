package com.ryan.project.smarthomehub.module.auth.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * @Descritption
 * @Date 2020/10/15
 * @Author ryan
 */
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String openId;

    private String username;

    private String password;

    private boolean isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
