package com.asif.token.JwtUtil;

import com.asif.token.Entity.Users;
import com.asif.token.Repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtil jwtUtil;

    @Autowired
    private TokenRepository repo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String mobile = null;
        String jwt = null;

        // ✅ Token header से निकाले
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                mobile = jwtUtil.extractMobile(jwt);
            } catch (Exception e) {
                System.out.println("JWT parse error: " + e.getMessage());
            }
        }

        // ✅ अगर mobile मिला और context खाली है
        if (mobile != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Users user = repo.findByMobile(mobile).orElse(null);

            if (user != null && jwtUtil.validateToken(jwt)) {
                // ✅ Authentication object बनाए जिसमें getName() = mobile होगा
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                mobile, // principal (authentication.getName() यही return करेगा)
                                null,
                                List.of(()->"ROLE_USER")// authorities खाली रखो (तुम roles manually check कर रहे हो)
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
