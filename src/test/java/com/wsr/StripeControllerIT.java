package com.wsr;

import com.stripe.model.Product;
import com.wsr.model.dto.RequestDTO;
import com.wsr.service.StripeService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class StripeControllerIT extends StripeControllerTest {
    @InjectMock
    StripeService stripeService;
    
    @Test
    void testHostedCheckout() throws Exception {
        Mockito.when(stripeService.getHostedCheckout(any(RequestDTO.class))).thenReturn("hosted-checkout-url");
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/checkout/hosted")
                .then()
                .statusCode(200)
                .body(equalTo("hosted-checkout-url"));
    }
    
    @Test
    void testIntegratedCheckout() throws Exception {
        Mockito.when(stripeService.getIntegratedCheckout(any(RequestDTO.class))).thenReturn("integrated-checkout-url");
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/checkout/integrated")
                .then()
                .statusCode(200)
                .body(equalTo("integrated-checkout-url"));
    }
    
    @Test
    void testNewSubscription() throws Exception {
        Mockito.when(stripeService.newSubscription(any(RequestDTO.class))).thenReturn("new-subscription-id");
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/subscriptions/new")
                .then()
                .statusCode(200)
                .body(equalTo("new-subscription-id"));
    }
    
    @Test
    void testCancelSubscription() throws Exception {
        Mockito.when(stripeService.cancelSubscription(any(RequestDTO.class))).thenReturn("subscription-cancelled");
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/subscriptions/cancel")
                .then()
                .statusCode(200)
                .body(equalTo("subscription-cancelled"));
    }
    
    @Test
    void testViewSubscriptions() throws Exception {
        List<Map<String, String>> subscriptions = List.of(Map.of("id", "sub_123"));
        Mockito.when(stripeService.viewSubscriptions(any(RequestDTO.class))).thenReturn(subscriptions);
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/subscriptions/list")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo("sub_123"));
    }
    
    @Test
    void testNewSubscriptionWithTrial() throws Exception {
        Mockito.when(stripeService.newSubscriptionWithTrial(any(RequestDTO.class))).thenReturn("trial-subscription-id");
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/subscriptions/trial")
                .then()
                .statusCode(200)
                .body(equalTo("trial-subscription-id"));
    }
    
    @Test
    void testListInvoices() throws Exception {
        List<Map<String, String>> invoices = List.of(Map.of("id", "inv_123"));
        Mockito.when(stripeService.listInvoices(any(RequestDTO.class))).thenReturn(invoices);
        
        given()
                .contentType(ContentType.JSON)
                .body(new RequestDTO())
                .when()
                .post("/stripe/invoices/list")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo("inv_123"));
    }
    
    @Test
    void testListAllProducts() throws Exception {
        List<Product> products = List.of(new Product());
        Mockito.when(stripeService.listAllProducts()).thenReturn(products);
        
        given()
                .when()
                .get("/stripe/products/list")
                .then()
                .statusCode(200);
    }
}
