package com.ryan.project.smarthomehub.module.hub.controller;

import com.ryan.project.smarthomehub.module.hub.service.ActionApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Descritption
 * @Date 2020/10/28
 * @Author tangqianli
 */
@RestController
public class SmartHomeController {

    @Autowired
    ActionApp actionApp;

    @PostMapping("smarthome")
    public Object smartHome(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String requestStr = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String response  = actionApp.handleRequest(requestStr, request.getParameterMap()).get();

        return response;
    }
}
