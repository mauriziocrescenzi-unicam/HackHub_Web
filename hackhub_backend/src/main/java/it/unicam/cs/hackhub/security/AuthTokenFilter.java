/*
 * Copyright © 2025 Victoria Coacci, Daniel Duda, Fattori Filippo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package it.unicam.cs.hackhub.security;

import it.unicam.cs.hackhub.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * JWT authentication filter responsible for validating incoming requests and setting the authentication context.
 *
 * <p>
 * This filter intercepts each HTTP request and checks for the presence of a JWT token
 * in the Authorization header using the Bearer scheme.
 * </p>
 *
 * <p>
 * If a valid token is found, the filter extracts the user identity and role information,
 * loads the corresponding {@link org.springframework.security.core.userdetails.UserDetails},
 * and sets the authentication object in the {@link SecurityContextHolder}.
 * </p>
 *
 * <p>
 * This enables Spring Security to recognize the user as authenticated and authorize
 * access to protected resources.
 * </p>
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomUserDetailsService service;

    /**
     * Performs JWT token validation and sets the authentication in the security context if valid.
     *
     * <p>
     * This method extracts the JWT token from the Authorization header, validates it using
     * {@link JwtUtil}, retrieves user information, and creates an authenticated
     * {@link UsernamePasswordAuthenticationToken}.
     * </p>
     *
     * <p>
     * If the token is valid, the authentication is stored in the {@link SecurityContextHolder},
     * allowing the request to proceed as an authenticated user.
     * </p>
     *
     * @param req the HTTP request
     * @param res the HTTP response
     * @param chain the filter chain used to pass the request to the next filter
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input/output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {
            String header = req.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {

                String token = header.substring(7);
                Claims claims = jwtUtil.getClaims(token);

                String email = claims.getSubject();
               
                UserDetails userDetails = service.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Authentication Exception: " + e.getMessage());
        }

        chain.doFilter(req,res);
    }
}

