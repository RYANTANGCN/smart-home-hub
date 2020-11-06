package com.ryan.project.smarthomehub.module.auth.dao;

import com.ryan.project.smarthomehub.module.auth.domain.entity.TokenStore;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Descritption
 * @Date 2020/10/15
 * @Author ryan
 */
public interface TokenStoreDao extends JpaRepository<TokenStore, Integer> {

    TokenStore getTokenStoreByClientIdAndRefreshToken(String clientId, String refreshToken);

    TokenStore getTokenStoreByClientIdAndRefreshTokenAndUserOpenId(String clientId, String refreshToken, String userOpenId);

}
