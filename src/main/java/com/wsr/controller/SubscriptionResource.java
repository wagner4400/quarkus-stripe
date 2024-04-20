package com.wsr.controller;

import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.net.RequestOptions;
import com.stripe.param.ProductListParams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/subscriptions")
public class SubscriptionResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(@Context ContainerRequestContext requestContext) throws Exception {
        RequestOptions options = (RequestOptions) requestContext.getProperty("requestOptions");
        
        ProductListParams params = ProductListParams.builder().setLimit(3L).build();
        ProductCollection products = Product.list(params);
        
        return Response.ok("Hello RESTEasy").build();
    }
}
