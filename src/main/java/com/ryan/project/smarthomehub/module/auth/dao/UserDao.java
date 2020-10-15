package com.ryan.project.smarthomehub.module.auth.dao;

import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Descritption
 * @Date 2020/10/15
 * @Author ryan
 */
public interface UserDao extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
