package com.service.productorder.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	private Long userId;

	@OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<OrderItem> orderItems = new ArrayList<>();

	private LocalDate orderDate;
	
	@OneToOne
	@JoinColumn(name = "payment_id")
	private Payment payment;
	
	private Double totalAmount;

	private OrderStatus orderStatus;
}