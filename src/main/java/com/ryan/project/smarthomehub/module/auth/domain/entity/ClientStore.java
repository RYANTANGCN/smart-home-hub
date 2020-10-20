package com.ryan.project.smarthomehub.module.auth.domain.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class ClientStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String clientId;

    private String clientSecret;

    private boolean isDelete;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
 }
