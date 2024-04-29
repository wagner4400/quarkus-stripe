package com.wsr.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.wsr.model.dto.RequestDTO;
import com.wsr.service.StripeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.List;
import java.util.Map;

@Path("/stripe")
public class StripeController {
    
    @Inject
    StripeService stripeService;
    
    @POST
    @Path(("/checkout/hosted"))
    public String hostedCheckout(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.getHostedCheckout(requestDTO);
    }
    @POST
    @Path(("/checkout/integrated"))
    public String integratedCheckout(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.getIntegratedCheckout(requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/new"))
    public String newSubscription(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.newSubscription(requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/cancel"))
    public String cancelSubscription(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.cancelSubscription(requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/list"))
    public List<Map<String, String>> viewSubscriptions(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.viewSubscriptions(requestDTO);
    }
    
    @POST
    @Path(("/subscriptions/trial"))
    public String newSubscriptionWithTrial(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.newSubscriptionWithTrial(requestDTO);
    }
    
    @POST
    @Path(("/invoices/list"))
    public List<Map<String, String>> listInvoices(@RequestBody RequestDTO requestDTO) throws StripeException {
        return stripeService.listInvoices(requestDTO);
    }
    
    @GET
    @Path(("/products/list"))
    public List<Product> listAllProducts() throws StripeException {
        return stripeService.listAllProducts();
    }
    
}