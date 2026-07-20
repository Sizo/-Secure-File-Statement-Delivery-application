package com.capitec.statement.api.filter;

import com.capitec.statement.api.exception.CustomerIdentityException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Order(1)
public class CustomerIdentityFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public CustomerIdentityFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String customerId = extractCustomerId(httpRequest);
            if (customerId == null || customerId.isBlank()) {
                throw new CustomerIdentityException("Missing or invalid customer identity");
            }
            httpRequest.setAttribute("customerId", customerId);
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleErrorResponse(httpResponse, e.getMessage());
        }
    }

    private String extractCustomerId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtUtil.validateAndExtractCustomerId(token);
            } catch (Exception e) {
                throw new CustomerIdentityException("Invalid JWT token: " + e.getMessage());
            }
        }

        return request.getHeader("X-Customer-ID");
    }

    private void handleErrorResponse(HttpServletResponse response, String detail) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, detail);
        problemDetail.setTitle("Unauthorized");
        
        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
