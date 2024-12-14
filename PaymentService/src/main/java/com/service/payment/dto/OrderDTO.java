package com.service.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	
	private Long orderId;
	private String email;
	private LocalDate orderDate;
	private Double totalAmount;
	private String orderStatus;

}
