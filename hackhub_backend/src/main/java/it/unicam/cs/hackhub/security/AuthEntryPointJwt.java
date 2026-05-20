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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Entry point for handling unauthorized access attempts in JWT-based authentication.
 *
 * <p>
 * This component is invoked by Spring Security when an unauthenticated client attempts
 * to access a protected resource. It sends an HTTP 401 Unauthorized response to the client.
 * </p>
 *
 * <p>
 * It is typically used in stateless authentication systems based on JWT tokens to notify
 * clients that authentication is required or that the provided token is invalid or missing.
 * </p>
 */
@Component
public class AuthEntryPointJwt  implements AuthenticationEntryPoint {

    /**
     * Handles unauthorized access attempts by sending an HTTP 401 Unauthorized response.
     *
     * <p>
     * This method is automatically invoked by Spring Security when authentication fails
     * or when a protected resource is accessed without valid authentication credentials.
     * </p>
     *
     * @param request the HTTP request that resulted in an authentication exception
     * @param response the HTTP response used to send the error to the client
     * @param authException the exception that caused the authentication failure
     * @throws IOException if an input or output exception occurs while sending the response
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}