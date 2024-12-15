package com.service.productorder.controllers;

import com.service.productorder.dtos.CartDTO;
import com.service.productorder.dtos.UserDTO;
import com.service.productorder.services.CartService;
import com.service.productorder.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final Logger logger = LogManager.getLogger(CartController.class);

	@GetMapping("/admin/carts")
	public ResponseEntity<List<CartDTO>> getAllCarts() {
		logger.info("Get all carts");
		List<CartDTO> cartDTOs = cartService.getAllCarts();
		logger.info(cartDTOs.toString());
		return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.FOUND);
	}

	@GetMapping("/public/users/{emailId}/carts/{cartId}")
	public ResponseEntity<CartDTO> getCartByEmailAndCartId(@PathVariable String emailId, @PathVariable Long cartId) {
		logger.info("Get cart by email id and cartId {}", cartId);
		Object value = redisTemplate.opsForValue().get("getCartById_"+emailId+cartId);
		if(value != null)
			return new ResponseEntity<CartDTO>((CartDTO) value, HttpStatus.FOUND);

		UserDTO userDTO = userService.getUserByEmail(emailId);
		CartDTO cartDTO = cartService.getCart(userDTO.getUserId(), cartId);

		redisTemplate.opsForValue().set("getCartById_"+emailId+cartId, cartDTO);
		logger.info(cartDTO.toString());
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.FOUND);
	}

	@PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
		logger.info("Add product to cart");
		CartDTO cartDTO = cartService.addProductToCart(cartId, productId, quantity);
		logger.info(cartDTO.toString());
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
	}

	@PostMapping("/public/users/{emailId}/carts")
	public ResponseEntity<CartDTO> addCartForUser(@PathVariable String emailId) {
		logger.info("Add cart to user");
		UserDTO userDTO = userService.getUserByEmail(emailId);
		CartDTO cartDTO = cartService.createCart(userDTO.getUserId());
		logger.info(cartDTO.toString());
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
	}

	@PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
		logger.info("Update cart product");
		CartDTO cartDTO = cartService.updateProductQuantityInCart(cartId, productId, quantity);
		logger.info(cartDTO.toString());
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
	}
	
	@DeleteMapping("/public/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
		logger.info("Delete cart product");
		String status = cartService.deleteProductFromCart(cartId, productId);
		logger.info(status);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
