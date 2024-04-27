package com.wsr.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.wsr.repository.ProductDAO;
import com.wsr.model.dto.RequestDTO;
import com.wsr.util.StripeCustomerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.resource.spi.ConfigProperty;

@ApplicationScoped
public class StripeService {
    
    @ConfigProperty
    String STRIPE_API_KEY;
    
    @ConfigProperty
    String CLIENT_BASE_URL;
    
    public String getHostedCheckout(RequestOptions requestOptions,
                                    RequestDTO requestDTO) throws StripeException {
        Stripe.apiKey = STRIPE_API_KEY;
        
        String clientBaseURL = CLIENT_BASE_URL;
        
        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = StripeCustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        
        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(clientBaseURL + "/failure");
        
        for (Product product : requestDTO.getItems()) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .putMetadata("app_id", product.getId())
                                                            .setName(product.getName())
                                                            .build()
                                            )
                                            .setCurrency(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getCurrency())
                                            .setUnitAmountDecimal(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getUnitAmountDecimal())
                                            .build())
                            .build());
        }
        
        // If invoice is needed, set the options appropriately
        if (requestDTO.isInvoiceNeeded()) {
            paramsBuilder.setInvoiceCreation(SessionCreateParams.InvoiceCreation.builder().setEnabled(true).build());
        }
        
        Session session = Session.create(paramsBuilder.build());
        
        return session.getUrl();
    }
    
}
