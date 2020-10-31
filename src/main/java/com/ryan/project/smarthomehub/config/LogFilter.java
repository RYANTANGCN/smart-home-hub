package com.ryan.project.smarthomehub.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class LogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            log.debug(request.getServerName());
            log.debug(((RequestFacade)request).getServletPath());
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
