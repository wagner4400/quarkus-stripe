package com.wsr.config;

import com.stripe.net.RequestOptions;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.UUID;

@Provider
public class IdempotencyFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String idempotencyKey = UUID.randomUUID().toString();
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        
        // Store options in request properties for later use
        requestContext.setProperty("requestOptions", options);
    }
}