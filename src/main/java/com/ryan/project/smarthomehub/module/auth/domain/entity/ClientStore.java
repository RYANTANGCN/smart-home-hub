package com.ryan.project.smarthomehub.module.auth.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ClientStore {

    @Id
    private Integer id;

    private String clientId;

    private String clientSecret;

    private boolean isDelete;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
 }
