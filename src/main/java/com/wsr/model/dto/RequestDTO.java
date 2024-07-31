package com.wsr.model.dto;

import com.stripe.model.Product;

public class RequestDTO {
    Product[] items;
    String customerName;
    String customerEmail;
    String subscriptionId;
    boolean invoiceNeeded;
    
    public RequestDTO() {
    }
    
    public RequestDTO(Product[] items, String customerName, String customerEmail, String subscriptionId, boolean invoiceNeeded) {
        this.items = items;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.subscriptionId = subscriptionId;
        this.invoiceNeeded = invoiceNeeded;
    }
    
    public Product[] getItems() {
        return items;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public String getSubscriptionId() {
        return subscriptionId;
    }
    
    public boolean isInvoiceNeeded() {
        return invoiceNeeded;
    }
    
}
