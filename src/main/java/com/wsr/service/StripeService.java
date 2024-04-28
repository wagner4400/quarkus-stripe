package com.wsr.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.InvoiceItem;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Product;
import com.stripe.model.ProductSearchResult;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductSearchParams;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.wsr.model.dto.RequestDTO;
import com.wsr.repository.ProductDAO;
import com.wsr.util.StripeUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class StripeService {
    
    Logger LOG = Logger.getLogger(StripeService.class);
    
    @ConfigProperty(name="stripe.api.key")
    String STRIPE_API_KEY;
    
    @ConfigProperty(name="client.base.url")
    String CLIENT_BASE_URL;
    
    public String getHostedCheckout(RequestDTO requestDTO) throws StripeException {
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = StripeUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        
        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(CLIENT_BASE_URL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(CLIENT_BASE_URL + "/failure");
        
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
    
    public String getIntegratedCheckout(@RequestBody RequestDTO requestDTO) throws StripeException {
        
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer or creating a new one if needed
        Customer customer = StripeUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        
        PaymentIntent paymentIntent;
        
        if (!requestDTO.isInvoiceNeeded()) {
            // If invoice is not needed, create a PaymentIntent directly and send it to the client
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(Long.parseLong(StripeUtil.calculateOrderAmount(requestDTO.getItems())))
                            .setCurrency("brl")
                            .setCustomer(customer.getId())
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods
                                            .builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();
            
            paymentIntent = PaymentIntent.create(params);
        } else {
            // If invoice is needed, create the invoice object, add line items to it, and finalize it to create the PaymentIntent automatically
            InvoiceCreateParams invoiceCreateParams = new InvoiceCreateParams.Builder()
                    .setCustomer(customer.getId())
                    .build();
            
            Invoice invoice = Invoice.create(invoiceCreateParams);
            
            // Add each item to the invoice one by one
            for (Product product : requestDTO.getItems()) {
                
                // Look for existing Product in Stripe before creating a new one
                Product stripeProduct;
                
                ProductSearchResult results = Product.search(ProductSearchParams.builder()
                        .setQuery("metadata['app_id']:'" + product.getId() + "'")
                        .build());
                
                if (results.getData().size() != 0)
                    stripeProduct = results.getData().get(0);
                else {
                    
                    // If a product is not found in Stripe database, create it
                    ProductCreateParams productCreateParams = new ProductCreateParams.Builder()
                            .setName(product.getName())
                            .putMetadata("app_id", product.getId())
                            .build();
                    
                    stripeProduct = Product.create(productCreateParams);
                }
                
                // Create an invoice line item using the product object for the line item
                InvoiceItemCreateParams invoiceItemCreateParams = new InvoiceItemCreateParams.Builder()
                        .setInvoice(invoice.getId())
                        .setQuantity(1L)
                        .setCustomer(customer.getId())
                        .setPriceData(
                                InvoiceItemCreateParams.PriceData.builder()
                                        .setProduct(stripeProduct.getId())
                                        .setCurrency(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getCurrency())
                                        .setUnitAmountDecimal(ProductDAO.getProduct(product.getId()).getDefaultPriceObject().getUnitAmountDecimal())
                                        .build())
                        .build();
                
                InvoiceItem.create(invoiceItemCreateParams);
            }
            
            // Mark the invoice as final so that a PaymentIntent is created for it
            invoice = invoice.finalizeInvoice();
            
            // Retrieve the payment intent object from the invoice
            paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
        }
        
        // Send the client secret from the payment intent to the client
        return paymentIntent.getClientSecret();
    }
    
    public String newSubscription(@RequestBody RequestDTO requestDTO) throws StripeException {
        
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = StripeUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        
        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        // For subscriptions, you need to set the mode as subscription
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(CLIENT_BASE_URL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(CLIENT_BASE_URL + "/failure");
        
        
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
                                            // For subscriptions, you need to provide the details on how often they would recur
                                            .setRecurring(SessionCreateParams.LineItem.PriceData.Recurring.builder().setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH).build())
                                            .build())
                            .build());
        }
        
        Session session = Session.create(paramsBuilder.build());
        
        return session.getUrl();
    }
    
    public String cancelSubscription(RequestDTO requestDTO) throws StripeException {
        Stripe.apiKey = STRIPE_API_KEY;
        
        Subscription subscription =
                Subscription.retrieve(
                        requestDTO.getSubscriptionId()
                );
        
        Subscription deletedSubscription =
                subscription.cancel();
        
        return deletedSubscription.getStatus();
    }
    
    public List<Map<String, String>> viewSubscriptions(RequestDTO requestDTO) throws StripeException {
        
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer record from Stripe
        Customer customer = StripeUtil.findCustomerByEmail(requestDTO.getCustomerEmail());
        
        // If no customer record was found, no subscriptions exist either, so return an empty list
        if (customer == null) {
            return new ArrayList<>();
        }
        
        // Search for subscriptions for the current customer
        SubscriptionCollection subscriptions = Subscription.list(
                SubscriptionListParams.builder()
                        .setCustomer(customer.getId())
                        .build());
        
        List<Map<String, String>> response = new ArrayList<>();
        
        // For each subscription record, query its item records and collect in a list of objects to send to the client
        for (Subscription subscription : subscriptions.getData()) {
            SubscriptionItemCollection currSubscriptionItems =
                    SubscriptionItem.list(SubscriptionItemListParams.builder()
                            .setSubscription(subscription.getId())
                            .addExpand("data.price.product")
                            .build());
            
            for (SubscriptionItem item : currSubscriptionItems.getData()) {
                HashMap<String, String> subscriptionData = new HashMap<>();
                subscriptionData.put("appProductId", item.getPrice().getProductObject().getMetadata().get("app_id"));
                subscriptionData.put("subscriptionId", subscription.getId());
                subscriptionData.put("subscribedOn", new SimpleDateFormat("dd/MM/yyyy").format(new Date(subscription.getStartDate() * 1000)));
                subscriptionData.put("nextPaymentDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date(subscription.getCurrentPeriodEnd() * 1000)));
                subscriptionData.put("price", item.getPrice().getUnitAmountDecimal().toString());
                
                if (subscription.getTrialEnd() != null && new Date(subscription.getTrialEnd() * 1000).after(new Date()))
                    subscriptionData.put("trialEndsOn", new SimpleDateFormat("dd/MM/yyyy").format(new Date(subscription.getTrialEnd() * 1000)));
                response.add(subscriptionData);
            }
            
        }
        
        return response;
        
    }
    
    public String newSubscriptionWithTrial(RequestDTO requestDTO) throws StripeException {
        
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = StripeUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        
        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(CLIENT_BASE_URL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(CLIENT_BASE_URL + "/failure")
                        // For trials, you need to set the trial period in the session creation request
                        .setSubscriptionData(SessionCreateParams.SubscriptionData.builder().setTrialPeriodDays(30L).build());
        
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
                                            .setRecurring(SessionCreateParams.LineItem.PriceData.Recurring.builder().setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH).build())
                                            .build())
                            .build());
        }
        
        Session session = Session.create(paramsBuilder.build());
        
        return session.getUrl();
    }
    
    public List<Map<String, String>> listInvoices(RequestDTO requestDTO) throws StripeException {
        
        Stripe.apiKey = STRIPE_API_KEY;
        
        // Start by finding existing customer record from Stripe
        Customer customer = StripeUtil.findCustomerByEmail(requestDTO.getCustomerEmail());
        
        // If no customer record was found, no subscriptions exist either, so return an empty list
        if (customer == null) {
            return new ArrayList<>();
        }
        
        // Search for invoices for the current customer
        Map<String, Object> invoiceSearchParams = new HashMap<>();
        invoiceSearchParams.put("customer", customer.getId());
        InvoiceCollection invoices =
                Invoice.list(invoiceSearchParams);
        
        List<Map<String, String>> response = new ArrayList<>();
        
        // For each invoice, extract its number, amount, and PDF URL to send to the client
        for (Invoice invoice : invoices.getData()) {
            HashMap<String, String> map = new HashMap<>();
            
            map.put("number", invoice.getNumber());
            map.put("amount", String.valueOf((invoice.getTotal() / 100f)));
            map.put("url", invoice.getInvoicePdf());
            
            response.add(map);
        }
        
        return response;
    }
}
