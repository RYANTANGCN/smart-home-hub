package com.ryan.project.smarthomehub.config;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class LogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            log.debug("ServerName:{}", request.getServerName());
            log.debug("ServletPath:{}", ((RequestFacade) request).getServletPath());
            Iterator<String> iterator = ((RequestFacade) request).getHeaderNames().asIterator();
            while (iterator.hasNext()) {
                String head = iterator.next();
                log.debug("{}:{}", head, ((RequestFacade) request).getHeader(head));
            }
        } finally {
            chain.doFilter(request, response);
        }
    }
}
