package com.ryan.project.smarthomehub.module.auth.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.ryan.project.smarthomehub.exception.GrantException;
import com.ryan.project.smarthomehub.module.auth.dao.UserDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;
import com.ryan.project.smarthomehub.module.auth.service.IAuthService;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    IClientStoreService clientStoreService;

    @Override
    public User login(String username, String password) {

        User user = userDao.findByUsername(username);
        if (user != null && (new Digester(DigestAlgorithm.SHA256).digestHex(password).equals(user.getPassword()))) {
            return user;
        }
        return null;
    }

    @Override
    public String generateAuthCode(String clientId,User user) {
        //Generate Auth Code
        String authCode = IdUtil.objectId();
        //cache in redis
        String key = String.format("AUTH_CODE:%s", authCode);
        log.debug("generate auth code redis key:{}", key);

        stringRedisTemplate.opsForHash().put(key, "client_id", clientId);
        stringRedisTemplate.opsForHash().put(key, "user_id", user.getOpenId());
        stringRedisTemplate.expire(key, Duration.ofMinutes(10L));
        return authCode;
    }

    /**
     * valid auth code
     * @param tokenInVo
     * @return userId
     */
    @Override
    public String validAuthCode(TokenInVo tokenInVo) {

        //valid auth code
        String authCodeKey = String.format("AUTH_CODE:%s", tokenInVo.getCode());
        if (!stringRedisTemplate.hasKey(authCodeKey)) {
            //auth code expired
            throw new GrantException(String.format("auth code doest not exist or expired,authCodeKey:%s", authCodeKey));
        }
        String userId = (String) stringRedisTemplate.opsForHash().get(authCodeKey, "user_id");
        String clientId = (String) stringRedisTemplate.opsForHash().get(authCodeKey, "client_id");

        //validate client_id is match
        if (StrUtil.hasBlank(userId, clientId) || (!clientId.equals(tokenInVo.getClient_id()))) {
            //auth code expired or not match
            throw new GrantException("valid client_id doesn't match");
        }

        //validate client id and client secret
        if (clientStoreService.getClientStoreByClientIdAndClientSecret(tokenInVo.getClient_id(), tokenInVo.getClient_secret())==null) {
            throw new GrantException("client_id or client_secret not validate");
        }

        return userId;
    }
}
