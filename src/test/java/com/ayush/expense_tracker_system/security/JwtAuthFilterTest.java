package com.ayush.expense_tracker_system.security;

import com.ayush.expense_tracker_system.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JwtAuthFilterTest {
    private final JwtService mockJwtService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final FilterChain filterChain;
    private final JwtAuthFilter jwtAuthFilter;
    private final Claims claims;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilterTest() {
        this.mockJwtService = mock(JwtService.class);
        this.httpServletRequest = mock(HttpServletRequest.class);
        this.httpServletResponse = mock(HttpServletResponse.class);
        this.userDetailsService = mock(UserDetailsService.class);
        claims = mock(Claims.class);
        this.filterChain = mock(FilterChain.class);
        this.jwtAuthFilter = new JwtAuthFilter(mockJwtService, userDetailsService);
    }

    @Test
    void shouldAuthenticateWhenValidJwt() throws ServletException, IOException {
        String jwtToken = "valid.jwt.token";
        when(httpServletRequest.getHeader("Authorization"))
                .thenReturn("Bearer "+jwtToken);
        when(mockJwtService.extractClaims(jwtToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("test@mail.com");
        when(claims.get("role", String.class)).thenReturn("user");

        jwtAuthFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(mockJwtService).extractClaims(jwtToken);
        verify(claims).getSubject();
        verify(claims).get("role", String.class);
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

}
