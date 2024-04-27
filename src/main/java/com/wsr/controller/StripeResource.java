package com.wsr.controller;

import com.stripe.exception.StripeException;
import com.stripe.net.RequestOptions;
import com.wsr.model.dto.RequestDTO;
import com.wsr.service.StripeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.List;
import java.util.Map;

@Path("/stripe")
public class StripeResource {
    
    @Inject
    StripeService stripeService;
    
    @POST
    @Path(("/checkout/hosted"))
    public String hostedCheckout(@Context ContainerRequestContext requestContext,
                                 @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.getHostedCheckout(getRequestOptions(requestContext), requestDTO);
    }
    @POST
    @Path(("/checkout/integrated"))
    public String integratedCheckout(@Context ContainerRequestContext requestContext,
                                     @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.getIntegratedCheckout(getRequestOptions(requestContext), requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/new"))
    public String newSubscription(@Context ContainerRequestContext requestContext,
                                  @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.newSubscription(getRequestOptions(requestContext), requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/cancel"))
    public String cancelSubscription(@Context ContainerRequestContext requestContext,
                                     @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.cancelSubscription(getRequestOptions(requestContext), requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/list"))
    public List<Map<String, String>> viewSubscriptions(@Context ContainerRequestContext requestContext,
                                                       @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.viewSubscriptions(getRequestOptions(requestContext), requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/trial"))
    public String newSubscriptionWithTrial(@Context ContainerRequestContext requestContext,
                                           @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.newSubscriptionWithTrial(getRequestOptions(requestContext), requestDTO);
    }
    
    @POST
    @Path(("/invoices/list"))
    public List<Map<String, String>> listInvoices(@Context ContainerRequestContext requestContext,
                                           @RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.listInvoices(getRequestOptions(requestContext), requestDTO);
    }
    
    private RequestOptions getRequestOptions(ContainerRequestContext requestContext){
        return (RequestOptions) requestContext.getProperty("requestOptions");
    }
    
}