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
    TokenOutVo grantRefreshToken(TokenInVo tokenInVo);

    /**
     * generate access_token
     * @param tokenInVo
     * @return
     */
    TokenOutVo grantAccessToken(TokenInVo tokenInVo);

    /**
     * get userId from access token
     * @param accessToken
     * @return
     */
    String getUserOpenId(String accessToken);

}
