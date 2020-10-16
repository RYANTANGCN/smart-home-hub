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
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    private String openId;

    private String username;

    private String password;

    private boolean isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
