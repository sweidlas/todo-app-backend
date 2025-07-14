package com.example.graphqlserver.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.graphqlserver.exceptions.CustomException;
import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.repository.UserPrincipalRepository;
import com.example.graphqlserver.service.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserPrincipalRepository userRepository;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    private static final List<String> PUBLIC_PATHS = Arrays.asList("/auth");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow public paths
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}"); // TODO: throw custom exception error and catch
                                                                        // it in globalExceptionHandler
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userId = jwtService.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userId != null && authentication == null) {
                UserPrincipal user = this.userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
                if (jwtService.validateToken(jwt, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new CustomException(null);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
