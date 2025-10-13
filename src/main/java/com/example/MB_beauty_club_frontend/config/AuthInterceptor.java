package com.example.MB_beauty_club_frontend.config;

import com.example.MB_beauty_club_frontend.dtos.auth.PendingRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = (String) request.getSession().getAttribute("sessionToken");

        // If the token is null, the user is not authenticated.
        // We no longer need to check the URI here, because WebMvcConfig already filtered out all public paths.
        if (token == null) {
            // Store the original request details for POST requests
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                request.getSession().setAttribute("pendingRequest", new PendingRequest(
                        request.getRequestURI(),
                        request.getMethod(),
                        request.getParameterMap()
                ));
            } else { // For GET requests, store the full URL
                String originalUrl = request.getRequestURI();
                String queryString = request.getQueryString();
                if (queryString != null && !queryString.isEmpty()) {
                    originalUrl += "?" + queryString;
                }
                request.getSession().setAttribute("redirectAfterLogin", originalUrl);
            }

            // Redirect to the login page
            response.sendRedirect("/auth/login");
            return false; // Stop the request from proceeding
        }

        // If the token exists, the user is authenticated. Allow the request to proceed.
        return true;
    }
}