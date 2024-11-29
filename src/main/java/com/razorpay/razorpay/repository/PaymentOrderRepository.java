package com.razorpay.razorpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.razorpay.razorpay.model.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long>{

	PaymentOrder findByRazorpayOrderId(String paymentId);

}
