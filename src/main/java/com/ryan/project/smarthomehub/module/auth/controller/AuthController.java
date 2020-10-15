package com.ryan.project.smarthomehub.module.auth.controller;

import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class AuthController {

    private String projectId;

    private String SSO_URL = "https://sso.ryantang.org/login";

    @Autowired
    IClientStoreService clientStoreService;

    @GetMapping("auth")
    public String auth(HttpServletRequest request, HttpServletResponse response) {

        log.debug("request url:{}",request.getRequestURI());

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

    @PostMapping("login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) {

        //TODO

        return "";
    }
}
