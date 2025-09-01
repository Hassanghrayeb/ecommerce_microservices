package com.hamster.ecommerce.config;

import com.hamster.ecommerce.util.AuthorizationUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class AuthorizationFilter extends GenericFilterBean
{
    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);
    private final AuthorizationUtil authorizationUtil;

    public AuthorizationFilter(AuthorizationUtil authorizationUtil)
    {
        this.authorizationUtil = authorizationUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.debug("request URL: {}", request.getRequestURL());

        String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null)
        {
            //Remove 'Bearer' and a possible space delimiting the rest of the token (Case Insensitive)
            requestTokenHeader = requestTokenHeader.replaceFirst("^(?i)bearer ?", "");

            AuthorizationResults authorizationResults = authorizationUtil.processAccessToken(requestTokenHeader);
            authorizationUtil.setUserSecurityContext(authorizationResults);
        }

        filterChain.doFilter(request, response);
    }
}
