package com.wsr.config;

import com.stripe.net.RequestOptions;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.UUID;

@Provider
public class StripeIdempotencyFilter implements ContainerRequestFilter {
    Logger LOG = Logger.getLogger(StripeIdempotencyFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.debug("Generating idempotency key for request");
        String idempotencyKey = UUID.randomUUID().toString();
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        
        // Store options in request properties for later use
        requestContext.setProperty("requestOptions", options);
        LOG.debug("Idempotency key generated: " + idempotencyKey);
    }
}