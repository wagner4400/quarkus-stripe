package com.wsr.controller;

import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.net.RequestOptions;
import com.stripe.param.ProductListParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
    
//    @POST
//    @Path("/hosted-checkout")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response hostedCheckout(@Context ContainerRequestContext requestContext, SubscriptionRequestDTO requestDTO) throws Exception {
//
//        // Start by finding an existing customer record from Stripe or creating a new one if needed
//        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
//
//        // Next, create a checkout session by adding the details of the checkout
//        SessionCreateParams.Builder paramsBuilder =
//                SessionCreateParams.builder()
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setCustomer(customer.getId())
//                        .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
//                        .setCancelUrl(clientBaseURL + "/failure");
//
//        for (Product product : requestDTO.getItems()) {
//            paramsBuilder.addLineItem(
//                    SessionCreateParams.LineItem.builder()
//                            .setQuantity(1L)
//                            .setPriceData(
//                                    PriceData.builder()
//                                            .setProductData(
//                                                    PriceData.ProductData.builder()
//                                                            .putMetadata("app_id", product.getId())
//                                                            .setName(product.getName())
//                                                            .build()
//                                            )
//                                            .setCurrency(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getCurrency())
//                                            .setUnitAmountDecimal(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getUnitAmountDecimal())
//                                            .build())
//                            .build());
//        }
//        Session session = Session.create(paramsBuilder.build());
//        return session.getUrl();
//    }
    
}