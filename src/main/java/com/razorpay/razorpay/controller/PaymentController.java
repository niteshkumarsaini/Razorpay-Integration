package com.razorpay.razorpay.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.razorpay.model.PaymentOrder;
import com.razorpay.razorpay.repository.PaymentOrderRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	   @Autowired
	    private PaymentOrderRepository paymentOrderRepository;

	private RazorpayClient razorpayClient;

	
	public PaymentController() throws Exception {
		this.razorpayClient = new RazorpayClient("", "");
	}

	@PostMapping("/createOrder")
	public String createOrder(@RequestBody Map<String,Object> body) {
		try {
                    int amount = Integer.parseInt((String) body.get("amount"));
                     amount = amount*100;
                    System.out.println("Amount: " +amount);
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt#1");

			JSONObject notes = new JSONObject();
			notes.put("notes_key_1", "Tea, Earl Grey, Hot");
			orderRequest.put("notes", notes);

			Order order = razorpayClient.Orders.create(orderRequest);
			    Date createdAtDate = (Date) order.get("created_at");
	            LocalDateTime createdAt = LocalDateTime.ofInstant(createdAtDate.toInstant(), ZoneId.systemDefault());
			
			PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setRazorpayOrderId(order.get("id"));
            paymentOrder.setReceipt(order.get("receipt"));
            paymentOrder.setAmount(order.get("amount"));
            paymentOrder.setCurrency(order.get("currency"));
            paymentOrder.setNotes(order.get("notes").toString());
            paymentOrder.setAmountPaid(order.get("amount_paid"));
            paymentOrder.setStatus(order.get("status"));
            paymentOrder.setCreatedAt(createdAt);

            paymentOrderRepository.save(paymentOrder);
			return order.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Order creation failed: " + e.getMessage();
		}
	}
}
