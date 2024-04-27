package com.wsr.util;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.Product;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.wsr.repository.ProductDAO;

public class StripeUtil {
    
    public static Customer findCustomerByEmail(String email) throws StripeException {
        CustomerSearchParams params =
                CustomerSearchParams
                        .builder()
                        .setQuery("email:'" + email + "'")
                        .build();
        
        CustomerSearchResult result = Customer.search(params);
        
        return result.getData().size() > 0 ? result.getData().get(0) : null;
    }
    
    public static Customer findOrCreateCustomer(String email, String name) throws StripeException {
        CustomerSearchParams params =
                CustomerSearchParams
                        .builder()
                        .setQuery("email:'" + email + "'")
                        .build();
        
        CustomerSearchResult result = Customer.search(params);
        
        Customer customer;
        
        if (result.getData().size() == 0) {
            
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(email)
                    .build();
            
            customer = Customer.create(customerCreateParams);
        } else {
            customer = result.getData().get(0);
        }
        
        return customer;
    }
    
    public static String calculateOrderAmount(Product[] items) {
        long total = 0L;
        
        for (Product item : items) {
            // Look up the application database to find the prices for the products in the given list
            total += ProductDAO.getProduct(item.getId()).getDefaultPriceObject().getUnitAmountDecimal().floatValue();
        }
        return String.valueOf(total);
    }
}
