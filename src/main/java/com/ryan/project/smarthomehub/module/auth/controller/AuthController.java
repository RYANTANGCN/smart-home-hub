package com.ryan.project.smarthomehub.module.auth.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.ryan.project.smarthomehub.module.auth.dao.UserDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class AuthController {

    private String projectId;

    private String SSO_URL = "https://sso.ryantang.org/login";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    IClientStoreService clientStoreService;

    //TODO bad smell!
    @GetMapping("auth")
    public String auth(HttpServletRequest request, HttpServletResponse response) {

        log.debug("request url:{}", request.getRequestURI());

        String clientId = request.getParameter("client_id");
        String redirectUri = request.getParameter("redirect_uri");
        String state = request.getParameter("state");
        String scope = request.getParameter("scope");
        String responseType = request.getParameter("response_type");
        String userLocale = request.getParameter("user_locale");

        //valid client id
        if ((clientId == null || clientId.equals("")) || !clientStoreService.validateClientId(clientId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "UNAUTHORIZED";
        }

        //valid redirect url
        String redirectUrlPrefix1 = String.format("https://oauth-redirect.googleusercontent.com/r/%s", projectId);
        String redirectUrlPrefix2 = String.format("https://oauth-redirect-sandbox.googleusercontent.com/r/", projectId);
        if ((!redirectUri.startsWith(redirectUrlPrefix1)) || (!redirectUri.startsWith(redirectUrlPrefix2))) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return "PRECONDITION_FAILED";
        }

        String loginPageUrl = String.format(SSO_URL + "?client_id=%s&redirect_uri=%s&state=%s&scope=%s&response_type=code&user_locale=%s", clientId, redirectUri, state, scope, userLocale);
        log.debug("redirect to login page:{}", loginPageUrl);
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", loginPageUrl);

        return "";
    }

    //TODO bad smell!
    @PostMapping("login")
    public String login(String username, String password, String clientId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.debug("login,body={}", request.getReader().lines().collect(Collectors.joining()));

        //TODO
        if (StrUtil.isAllBlank(username, password)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "";
        }

        User user = userDao.findByUsername(username);

        if (user == null || !(new Digester(DigestAlgorithm.SHA256).digestHex(password).equals(user.getPassword()))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "";
        }

        //Generate Auth Code
        String authCode = IdUtil.objectId();
        //cache in redis
        redisTemplate.opsForValue().set(authCode, clientId, 10L, TimeUnit.MINUTES);

        return authCode;
    }

    //TODO bad smell
    @PostMapping("token")
    public String token(String client_id, String client_secret, String grant_type, String code, String redirect_uri, String refresh_token, HttpServletResponse response) {

        log.debug("token,body={}");

        if (StrUtil.hasBlank(client_id, client_secret, grant_type, code, redirect_uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"error\": \"invalid_grant\"}";
        }

        //TODO
        if ("authorization_code".equals(grant_type)) {
            String clientId = redisTemplate.opsForValue().get(code);
            if (client_id.equals(client_id)) {
                //generate refresh token
            }
        }

        //TODO
        if ("refresh_token".equals(grant_type)) {

        }

        return "";
    }
}
