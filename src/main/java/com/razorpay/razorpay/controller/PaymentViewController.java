package com.razorpay.razorpay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

	   @GetMapping("/payment")
	    public String showPaymentForm() {
	        return "payment";
	    }
}
