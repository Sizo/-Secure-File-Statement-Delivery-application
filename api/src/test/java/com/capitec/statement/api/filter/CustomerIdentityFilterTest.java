package com.capitec.statement.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerIdentityFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CustomerIdentityFilter filter;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        filter = new CustomerIdentityFilter(jwtUtil, objectMapper);
        stringWriter = new StringWriter();
    }

    @Test
    void doFilter_WithValidJwtToken_Proceeds() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/statements");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtUtil.validateAndExtractCustomerId("valid.token")).thenReturn("cust-123");

        filter.doFilter(request, response, filterChain);

        verify(request).setAttribute("customerId", "cust-123");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithXCustomerIdHeader_Proceeds() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/statements");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-Customer-ID")).thenReturn("cust-456");

        filter.doFilter(request, response, filterChain);

        verify(request).setAttribute("customerId", "cust-456");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_MissingIdentity_Returns401() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/statements");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-Customer-ID")).thenReturn(null);
        
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_ActuatorPath_SkipsValidation() throws Exception {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        filter.doFilter(request, response, filterChain);

        verify(request, never()).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
    }
}
