package com.service.productorder.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartItemId;
	
	@ManyToOne
	@JoinColumn(name = "cart_id")
	private Cart cart;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	private Integer quantity;
	private double discount;
	private double productPrice;

	public CartItem(long id, Product product, int quantity, double discount, double productPrice) {
		this.cartItemId = id;
		this.product = product;
		this.quantity = quantity;
		this.discount = discount;
		this.productPrice = productPrice;
	}
}
