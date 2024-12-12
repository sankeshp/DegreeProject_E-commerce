package com.service.productorder.controllers;

import com.service.productorder.payloads.CartDTO;
import com.service.productorder.payloads.User;
import com.service.productorder.services.CartService;
import com.service.productorder.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {
	
	@Autowired
	private CartService cartService;

	@Autowired
	private UserService userService;

	@PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
		CartDTO cartDTO = cartService.addProductToCart(cartId, productId, quantity);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
	}

	@PostMapping("/public/users/{emailId}/carts")
	public ResponseEntity<CartDTO> addCart(@PathVariable String emailId) {
		User user = userService.getUserByEmail(emailId);
		CartDTO cartDTO = cartService.createCart(user.getUserId());
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
	}
	
	@GetMapping("/admin/carts")
	public ResponseEntity<List<CartDTO>> getCarts() {
		List<CartDTO> cartDTOs = cartService.getAllCarts();
		return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.FOUND);
	}
	
	@GetMapping("/public/users/{emailId}/carts/{cartId}")
	public ResponseEntity<CartDTO> getCartById(@PathVariable String emailId, @PathVariable Long cartId) {
		User user = userService.getUserByEmail(emailId);
		CartDTO cartDTO = cartService.getCart(user.getUserId(), cartId);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.FOUND);
	}
	
	@PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
		CartDTO cartDTO = cartService.updateProductQuantityInCart(cartId, productId, quantity);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
	}
	
	@DeleteMapping("/public/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
		String status = cartService.deleteProductFromCart(cartId, productId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
