package com.ryan.project.smarthomehub.module.auth.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ryan.project.smarthomehub.module.auth.dao.TokenStoreDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.TokenStore;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenOutVo;
import com.ryan.project.smarthomehub.module.auth.service.IAuthService;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
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
                .refreshToken(refreshToken)
                .expiredTime(LocalDateTime.MAX)
                .isRevoke(false)
                .build());

        //generate access token
        String accessToken = IdUtil.simpleUUID();
        String key = String.format("ACCESS_TOKEN:%s", accessToken);
        stringRedisTemplate.opsForHash().put(key, "client_id", tokenInVo.getClient_id());
        stringRedisTemplate.opsForHash().put(key, "user_id", userId);
        stringRedisTemplate.expire(key, Duration.ofMinutes(65L));

        return TokenOutVo.builder()
                .token_type("Bearer")
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(3600)
                .build();
    }

    @Override
    public TokenOutVo grantAccessToken(TokenInVo tokenInVo) {
        return null;
    }
}
