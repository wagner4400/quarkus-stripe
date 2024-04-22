package com.wsr.util;

//https://docs.stripe.com/testing?testing-method=card-numbers#international-cards
public enum TestCards {
    VISA_BR("pm_card_br", "4000000760000002", "tok_br");
    
    String paymentMethod;
    String cardNumber;
    
    String token;
    TestCards(String paymentMethod, String cardNumber, String token) {
        this.cardNumber = cardNumber;
        this.paymentMethod = paymentMethod;
        this.token = token;
    }
}
