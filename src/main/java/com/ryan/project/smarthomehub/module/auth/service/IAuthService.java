package com.ryan.project.smarthomehub.module.auth.service;

import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
public interface IAuthService {

    /**
     * user login
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * generate auth code
     * @param clientId
     * @param user
     * @return
     */
    String generateAuthCode(String clientId, User user);

    /**
     * valid auth code
     * @param tokenInVo
     * @return
     */
    String validAuthCode(TokenInVo tokenInVo);

}
