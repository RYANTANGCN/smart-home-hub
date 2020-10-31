package com.ryan.project.smarthomehub.module.fullfillment.controller;

import com.ryan.project.smarthomehub.module.fullfillment.service.ActionApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Descritption
 * @Date 2020/10/28
 * @Author tangqianli
 */
@Slf4j
@RestController
public class SmartHomeController {

    @Autowired
    ActionApp actionApp;

    @PostMapping("smarthome")
    public Object smartHome(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {

        String requestStr = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        log.debug(requestStr);

        Map<String, String> headersMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        Iterator<String> iterator = headerNames.asIterator();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            headersMap.put(headerName, request.getHeader(headerName));
        }
        String response = actionApp.handleRequest(requestStr, headersMap).get();
        log.debug(response);

        return response;
    }
}
