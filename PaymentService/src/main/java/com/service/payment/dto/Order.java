package com.service.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
	private Long orderId;
	private String email;
	//private List<OrderItemDTO> orderItems = new ArrayList<>();
	private LocalDate orderDate;
	//private PaymentDTO payment;
	private Double totalAmount;
	private String orderStatus;

}
