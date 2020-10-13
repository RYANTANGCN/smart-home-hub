package com.ryan.project.smarthomehub.module.auth.controller;

import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    private String projectId;

    @Autowired
    IClientStoreService clientStoreService;

    @GetMapping("auth")
    public String auth(HttpServletRequest request, HttpServletResponse response) {

        String clientId = request.getParameter("client_id");
        String redirectUrl = request.getParameter("redirect_url");
        //valid client id
        if ((clientId==null||clientId.equals(""))||!clientStoreService.validateClientId(clientId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "UNAUTHORIZED";
        }

        //valid redirect url
        String redirectUrlPrefix1 = String.format("https://oauth-redirect.googleusercontent.com/r/%s", projectId);
        String redirectUrlPrefix2 = String.format("https://oauth-redirect-sandbox.googleusercontent.com/r/", projectId);
        if ((!redirectUrl.startsWith(redirectUrlPrefix1)) || (!redirectUrl.startsWith(redirectUrlPrefix2))) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return "PRECONDITION_FAILED";
        }

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location","https://sso.ryantang.org/login");
        return "";
    }
}
