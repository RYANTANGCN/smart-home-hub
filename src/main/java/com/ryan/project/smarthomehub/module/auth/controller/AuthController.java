package com.ryan.project.smarthomehub.module.auth.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.ryan.project.smarthomehub.config.properties.HubProperties;
import com.ryan.project.smarthomehub.module.auth.dao.UserDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import com.ryan.project.smarthomehub.module.auth.domain.vo.LoginInVO;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    HubProperties hubProperties;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    IClientStoreService clientStoreService;

    //TODO bad smell!
    @GetMapping("auth")
    public ResponseEntity auth(HttpServletRequest request) {

        log.debug("request url:{}", request.getRequestURI());

        String clientId = request.getParameter("client_id");
        String redirectUri = request.getParameter("redirect_uri");
        String state = request.getParameter("state");
        String scope = request.getParameter("scope");
        String responseType = request.getParameter("response_type");
        String userLocale = request.getParameter("user_locale");

        //valid client id
        if ((clientId == null || clientId.equals("")) || !clientStoreService.validateClientId(clientId)) {
            return ResponseEntity.badRequest().build();
        }

        //valid redirect url
        String redirectUrlPrefix1 = String.format("https://oauth-redirect.googleusercontent.com/r/%s", hubProperties.getProjectId());
        String redirectUrlPrefix2 = String.format("https://oauth-redirect-sandbox.googleusercontent.com/r/%s", hubProperties.getProjectId());
        if ((!redirectUri.startsWith(redirectUrlPrefix1)) || (!redirectUri.startsWith(redirectUrlPrefix2))) {
            return ResponseEntity.badRequest().build();
        }

        //generate login page redirect url
        String loginPageUrl = String.format(hubProperties.getSsoUrl() + "?client_id=%s&redirect_uri=%s&state=%s&scope=%s&response_type=code&user_locale=%s", clientId, redirectUri, state, scope, userLocale);
        log.debug("redirect to login page:{}", loginPageUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", loginPageUrl).build();
    }

    //TODO bad smell!
    @CrossOrigin
    @PostMapping("login")
    public ResponseEntity<String> login(LoginInVO loginInVO) throws IOException {

        log.debug("login,body={}", loginInVO);

        //TODO
        if (StrUtil.isAllBlank(loginInVO.getUsername(), loginInVO.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        User user = userDao.findByUsername(loginInVO.getUsername());

        if (user == null || !(new Digester(DigestAlgorithm.SHA256).digestHex(loginInVO.getPassword()).equals(user.getPassword()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        //Generate Auth Code
        String authCode = IdUtil.objectId();
        //cache in redis
        redisTemplate.opsForValue().set(authCode, loginInVO.getClientId(), 10L, TimeUnit.MINUTES);

        return ResponseEntity.ok(authCode);
    }

    //TODO bad smell
    @PostMapping("token")
    public ResponseEntity token(String client_id, String client_secret, String grant_type, String code, String redirect_uri, String refresh_token, HttpServletResponse response) {

        log.debug("token,body={}");

        if (StrUtil.hasBlank(client_id, client_secret, grant_type, code, redirect_uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseEntity.badRequest().body("{\"error\": \"invalid_grant\"}") ;
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

        return ResponseEntity.ok().build();
    }
}
