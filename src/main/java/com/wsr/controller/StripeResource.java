package com.wsr.controller;

import com.stripe.exception.StripeException;
import com.stripe.net.RequestOptions;
import com.wsr.model.dto.RequestDTO;
import com.wsr.service.StripeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/stripe")
public class StripeResource {
    
    @Inject
    StripeService stripeService;
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path(("/checkout/integrated"))
    public String hostedCheckout(@Context ContainerRequestContext requestContext,
                                 @RequestBody RequestDTO requestDTO) throws StripeException {
        
        return stripeService.getHostedCheckout(getRequestOptions(requestContext), requestDTO);
    }
    
    private RequestOptions getRequestOptions(ContainerRequestContext requestContext){
        return (RequestOptions) requestContext.getProperty("requestOptions");
    }
    
}