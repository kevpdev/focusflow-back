package fr.focusflow.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class CsrfDebugFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CsrfDebugFilter.class);

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        logger.trace("[CSRF Debug] Méthode : {}", httpRequest.getMethod());
        logger.trace("[CSRF Debug] URL : {}", httpRequest.getRequestURL());

        // Log de tous les headers
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.trace("[CSRF Debug] Header : {} = {}", headerName, httpRequest.getHeader(headerName));
        }

        chain.doFilter(request, response);
    }
}
