package com.ryan.project.smarthomehub.module.auth.service;

import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenOutVo;

/**
 * @Descritption
 * @Date 2020/10/19
 * @Author tangqianli
 */
public interface ITokenService {

    /**
     * generate access_token and refresh_token
     * @param tokenInVo
     * @return
     */
    TokenOutVo processAuthorizationCode(TokenInVo tokenInVo);

    /**
     * generate new access_token
     * @param tokenInVo
     * @return
     */
    TokenOutVo processRefreshToken(TokenInVo tokenInVo);

    /**
     * get userId from access token
     * @param accessToken
     * @return
     */
    String getUserOpenId(String accessToken);

    /**
     * revoke refresh token
     * @param accessToken
     */
    void revokeRefreshToken(String accessToken);

}
