package com.razorpay.razorpay.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.razorpay.model.PaymentOrder;
import com.razorpay.razorpay.repository.PaymentOrderRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WebhookController {
	
	 private final PaymentOrderRepository paymentOrderRepository;

	    public WebhookController(PaymentOrderRepository paymentOrderRepository) {
	        this.paymentOrderRepository = paymentOrderRepository;
	    }

	    @PostMapping("/api/webhook")
	    public String handleWebhook(HttpServletRequest request) {
	        	      
	        StringBuilder payload = new StringBuilder();
	        try (BufferedReader reader = request.getReader()) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                payload.append(line);
	            }
	           
	        } catch (IOException e) {
	            return "Failed to read payload";
	        }

	        // Process the payload
	        Map<String, Object> eventData = parseEventData(payload.toString());
	        Map<String, Object> paymentEntity = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) eventData.get("payload")).get("payment")).get("entity");
                          
	        updateDatabase(paymentEntity);

	        return "Webhook processed";
	    }

	   

	    private Map<String, Object> parseEventData(String payload) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        try {
	           
	            return objectMapper.readValue(payload, Map.class);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new HashMap<>();
	        }
	    }

	    private void updateDatabase(Map<String, Object> eventData) {
	              System.out.println("event Data: "  +eventData);
	        String paymentId = (String) eventData.get("order_id");
	        String status = (String) eventData.get("status");
	        String razorpaypaymentid= (String)eventData.get("id");
	        System.out.println("paymentId " +paymentId+" status "+status);
	        int amount = Integer.parseInt( eventData.get("amount").toString());
	     //   int amount =  (int) eventData.get("amount");

	        
	        PaymentOrder order = paymentOrderRepository.findByRazorpayOrderId(paymentId);
	        if (order != null) {
	            order.setAmountPaid(amount);
	            order.setStatus(status);
	            order.setRazorpayPaymentId(razorpaypaymentid);
	            paymentOrderRepository.save(order);
	        }
	        System.out.println("Database updated successfully");
	    }
}
