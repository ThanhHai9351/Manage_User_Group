package com.example.springboot_demo.helpers;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.example.springboot_demo.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/v1/auth/login") || request.getServletPath().equals("/v1/auth/refresh");
    }

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userId;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("JWT Token is missing or invalid");
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "JWT Token is missing or invalid");
                // filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);

            // 1. Kiểm tra định dạng token
            if (!jwtService.isTokenFormatValid(jwt)) {
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "Invalid token format");
                return;
            }

            // 2. Kiểm tra hết hạn token
            if (jwtService.isTokenExpired(jwt)) {
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized",
                        "Token expired");
                return;
            }

            // 3. Kiểm tra chữ ký token
            if (!jwtService.isSignatureValid(jwt)) {
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized",
                        "Invalid token signature");
                return;
            }

            // 4. Kiểm tra issuer token
            if (!jwtService.isIssuerToken(jwt)) {
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized",
                        "Invalid token issuer");
                return;
            }

            // 5. Kiểm tra token đã bị blacklist chưa
            if (jwtService.isBlacklistedToken(jwt)) {
                sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized",
                        "Token is blacklisted");
                return;
            }

            userId = jwtService.getUserIdFromToken(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                logger.info("User authenticated: {}", userDetails.getUsername());

                // 6. Kiểm tra email token
                if (!jwtService.getEmailFromToken(jwt).equals(userDetails.getUsername())) {
                    sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Unauthorized",
                            "Invalid token email");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated: ", userDetails.getUsername());
            }

            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            logger.error("Failed to authenticate user: {}", e.getMessage());
            sendErrorResponse(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error",
                    e.getMessage());
            return;
        }
    }

    private void sendErrorResponse(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            int statusCode, String error, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
