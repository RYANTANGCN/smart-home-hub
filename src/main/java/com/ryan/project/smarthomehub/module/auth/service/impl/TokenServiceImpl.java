package com.ryan.project.smarthomehub.module.auth.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ryan.project.smarthomehub.exception.GrantException;
import com.ryan.project.smarthomehub.module.auth.dao.TokenStoreDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;
import com.ryan.project.smarthomehub.module.auth.domain.entity.TokenStore;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenOutVo;
import com.ryan.project.smarthomehub.module.auth.service.IAuthService;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
@Slf4j
@Service
public class TokenServiceImpl implements ITokenService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    TokenStoreDao tokenStoreDao;

    @Autowired
    IClientStoreService clientStoreService;

    @Autowired
    IAuthService authService;

    @Override
    public TokenOutVo grantRefreshToken(TokenInVo tokenInVo) {

        //valid auth code
        String userId = authService.validAuthCode(tokenInVo);

        //generate refresh token
        String refreshToken = DigestUtil.sha256Hex(IdUtil.simpleUUID());
        tokenStoreDao.save(TokenStore.builder()
                .clientId(tokenInVo.getClient_id())
                .userOpenId(userId)
                .refreshToken(refreshToken)
                .expiredTime(LocalDateTime.MAX)
                .isRevoke(false)
                .build());

        //generate access token
        String accessToken = generateRefreshToken(tokenInVo.getClient_id(), userId);

        return TokenOutVo.builder()
                .token_type("Bearer")
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(3600)
                .build();
    }

    @Override
    public TokenOutVo grantAccessToken(TokenInVo tokenInVo) {

        //valid client_id and client_secret
        ClientStore clientStore = clientStoreService.getClientStoreByClientIdAndClientSecret(tokenInVo.getClient_id(), tokenInVo.getClient_secret());
        if (clientStore == null) {
            throw new GrantException();
        }

        //valid refresh token
        TokenStore tokenStore = tokenStoreDao.getTokenStoreByClientIdAndRefreshToken(clientStore.getClientId(), tokenInVo.getRefresh_token());
        if (tokenStore == null || tokenStore.getIsRevoke()) {
            throw new GrantException();
        }

        //generate access token
        String accessToken = generateRefreshToken(tokenInVo.getClient_id(), tokenStore.getUserOpenId());

        return TokenOutVo.builder()
                .token_type("Bearer")
                .access_token(accessToken)
                .refresh_token("")
                .expires_in(3600)
                .build();
    }

    @Override
    public String getUserOpenId(String accessToken) {
        if (StrUtil.isBlank(accessToken)) {
            throw new GrantException();
        }
        if (accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        }
        String key = String.format("ACCESS_TOKEN:%s", accessToken);
        String clientId = (String)stringRedisTemplate.opsForHash().get(key, "client_id");
        return clientId;
    }

    private String generateRefreshToken(String clientId, String userId) {
        String accessToken = IdUtil.simpleUUID();
        log.debug("generate access token:{}",accessToken);
        String key = String.format("ACCESS_TOKEN:%s", accessToken);
        log.debug("redis access_token key:{}",key);
        stringRedisTemplate.opsForHash().put(key, "client_id", clientId);
        stringRedisTemplate.opsForHash().put(key, "user_id", userId);
        stringRedisTemplate.expire(key, Duration.ofMinutes(65L));

        return accessToken;
    }
}
