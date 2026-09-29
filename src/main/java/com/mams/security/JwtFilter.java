package com.mams.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserDetailsServiceImpl uds;

    public JwtFilter(JwtService j, UserDetailsServiceImpl u) {
        jwt = j;
        uds = u;
    }

    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws java.io.IOException, ServletException {

        String h = req.getHeader("Authorization");

        if (h != null && h.startsWith("Bearer ")) {
            try {
                String user = jwt.username(h.substring(7));
                UserDetails d = uds.loadUserByUsername(user);

                if (SecurityContextHolder.getContext().getAuthentication() == null)
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    d,
                                    null,
                                    d.getAuthorities()
                            )
                    );

            } catch (Exception ignored) {
            }
        }

        chain.doFilter(req, res);
    }
}