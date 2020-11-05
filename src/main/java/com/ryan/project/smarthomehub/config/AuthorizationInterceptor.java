package com.ryan.project.smarthomehub.config;

import cn.hutool.core.util.StrUtil;
import com.ryan.project.smarthomehub.config.properties.HubProperties;
import com.ryan.project.smarthomehub.exception.GrantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * @Descritption
 * @Date 2020/11/2
 * @Author tangqianli
 */
@Slf4j
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    HubProperties hubProperties;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //filter un-need access_token servlet path
        log.info("request uri:{}",request.getServletPath());
        if (hubProperties.getExcludeUri().contains(request.getServletPath())) {
            return true;
        }

        String accessToken = request.getHeader("authorization");
        if (StrUtil.isBlank(accessToken)) {
            log.error("request uri:{},method:{},access token is empty", request.getServletPath(), request.getMethod());
            log.debug("request body:{}", request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
            throw new GrantException(String.format("servletPath:%s,method:%s,access token is empty", request.getServletPath(), request.getMethod()));
        }
        if (accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        }
        log.debug("access token:{}", accessToken);
        String key = String.format("ACCESS_TOKEN:%s", accessToken);
        if (stringRedisTemplate.hasKey(key)) {
            return true;
        }else {
            throw new GrantException(String.format("access token:%s not exist or expired", accessToken));
        }
    }
}
