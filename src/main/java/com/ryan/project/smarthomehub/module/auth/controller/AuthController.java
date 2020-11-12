package com.ryan.project.smarthomehub.module.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.ryan.project.smarthomehub.config.properties.HubProperties;
import com.ryan.project.smarthomehub.exception.GrantException;
import com.ryan.project.smarthomehub.module.auth.dao.UserDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.User;
import com.ryan.project.smarthomehub.module.auth.domain.vo.LoginInVO;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenInVo;
import com.ryan.project.smarthomehub.module.auth.domain.vo.TokenOutVo;
import com.ryan.project.smarthomehub.module.auth.service.IAuthService;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @Autowired
    IAuthService authService;

    @Autowired
    ITokenService tokenService;

    //TODO bad smell!
    @GetMapping("auth")
    public ResponseEntity auth(HttpServletRequest request) {

        log.debug("request uri:{}", request.getRequestURI());
        log.debug("request query string:{}", request.getQueryString());

        String clientId = request.getParameter("client_id");
        String redirectUri = request.getParameter("redirect_uri");
        String state = request.getParameter("state");
        String scope = request.getParameter("scope");
        String responseType = request.getParameter("response_type");
        String userLocale = request.getParameter("user_locale");

        //valid response_type
        if (!"code".equals(responseType)) {
            return ResponseEntity.badRequest().build();
        }

        //valid client_id
        if (StrUtil.isBlank(clientId) || !clientStoreService.validateClientId(clientId)) {
            return ResponseEntity.badRequest().build();
        }

        //valid redirect_url
        String redirectUrlPrefix1 = String.format("https://oauth-redirect.googleusercontent.com/r/%s", hubProperties.getProjectId());
        String redirectUrlPrefix2 = String.format("https://oauth-redirect-sandbox.googleusercontent.com/r/%s", hubProperties.getProjectId());
        if ((!redirectUri.startsWith(redirectUrlPrefix1)) && (!redirectUri.startsWith(redirectUrlPrefix2))) {
            return ResponseEntity.badRequest().build();
        }

        //generate login page redirect-url
        String loginPageUrl = String.format(hubProperties.getSsoUrl() + "?client_id=%s&redirect_uri=%s&state=%s&scope=%s&response_type=code&user_locale=%s", clientId, redirectUri, state, scope, userLocale);
        log.debug("redirect to login page:{}", loginPageUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", loginPageUrl).build();
    }

    //TODO bad smell!
    @CrossOrigin
    @PostMapping("login")
    public ResponseEntity<String> login(@Validated LoginInVO loginInVO) throws IOException {

        log.debug("login,body={}", loginInVO);

        //TODO use @Validate
        if (StrUtil.hasBlank(loginInVO.getUsername(), loginInVO.getPassword(), loginInVO.getClientId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        //valid username password
        User user = authService.login(loginInVO.getUsername(), loginInVO.getPassword());
        if (user==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        //Generate Auth Code
        String authCode = authService.generateAuthCode(loginInVO.getClientId(),user);

        return ResponseEntity.ok(authCode);
    }

    //TODO bad smell
    @PostMapping("token")
    public ResponseEntity token(TokenInVo tokenInVo) {

        log.debug("token,body={}",tokenInVo);

        //TODO use @Validate
        if (StrUtil.hasBlank(tokenInVo.getClient_id(), tokenInVo.getClient_secret(), tokenInVo.getGrant_type(), tokenInVo.getCode(), tokenInVo.getRedirect_uri())) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"error\": \"invalid_grant\"}");
        }

        TokenOutVo tokenOutVo;

        switch (tokenInVo.getGrant_type()) {
            case "authorization_code":
                tokenOutVo = tokenService.processAuthorizationCode(tokenInVo);
                break;
            case "refresh_token":
                tokenOutVo = tokenService.processRefreshToken(tokenInVo);
            default:
                //TODO
                throw new GrantException("unexpect code");
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(tokenOutVo);
    }
}
