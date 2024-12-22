package com.service.productorder.controllers;

import com.service.productorder.config.AppConstants;
import com.service.productorder.config.KafkaNotificationProducerClient;
import com.service.productorder.dtos.*;
import com.service.productorder.services.OrderService;
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
public class OrderController {
	
	@Autowired
	public OrderService orderService;

	@Autowired
	public UserService userService;

	@Autowired
	public KafkaNotificationProducerClient kafkaNotificationProducerClient;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final Logger logger = LogManager.getLogger(OrderController.class);

	@GetMapping("/admin/orders")
	public ResponseEntity<OrderResponseDTO> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		logger.info("getAllOrders");
		OrderResponseDTO orderResponseDTO = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
		logger.info(orderResponseDTO);
		return new ResponseEntity<OrderResponseDTO>(orderResponseDTO, HttpStatus.FOUND);
	}

	@GetMapping("public/users/{emailId}/orders")
	public ResponseEntity<List<OrderDTO>> getAllOrdersForUser(@PathVariable String emailId) {
		logger.info("getAllOrdersForUser");
		Object value = redisTemplate.opsForValue().get("getOrdersByUser_"+emailId);
		if(value != null)
			return new ResponseEntity<List<OrderDTO>>((List<OrderDTO>) value, HttpStatus.FOUND);

		UserDTO userDTO = userService.getUserByEmail(emailId);
		List<OrderDTO> orders = orderService.getOrdersByUser(userDTO);

		redisTemplate.opsForValue().set("getOrdersByUser_"+emailId, orders);
		logger.info(orders);
		return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
	}

	@GetMapping("public/users/{emailId}/orders/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByUserAndOrderId(@PathVariable String emailId, @PathVariable Long orderId) {
		logger.info("getOrderByUserAndOrderId");
		Object value = redisTemplate.opsForValue().get("getOrderByUser_"+emailId);
		if(value != null)
			return new ResponseEntity<OrderDTO>((OrderDTO) value, HttpStatus.FOUND);

		UserDTO userDTO = userService.getUserByEmail(emailId);
		OrderDTO order = orderService.getOrder(userDTO.getUserId(), orderId);
		redisTemplate.opsForValue().set("getOrdersByUser_"+emailId, order);
		logger.info(order);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
	}


	@GetMapping("public/orders/{orderId}")
	public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
		logger.info("getOrderById");
		Object value = redisTemplate.opsForValue().get("getOrderById_"+orderId);
		if(value != null)
			return new ResponseEntity<OrderDTO>((OrderDTO) value, HttpStatus.FOUND);

		OrderDTO order = orderService.getOrder(orderId);
		redisTemplate.opsForValue().set("getOrderById_"+orderId, order);
		logger.info(order);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
	}

	@PostMapping("/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}/order")
	public ResponseEntity<OrderDTO> orderProducts(@PathVariable String emailId, @PathVariable Long cartId, @PathVariable String paymentMethod) {
		logger.info("orderProducts");
		UserDTO userDTO = userService.getUserByEmail(emailId);
		OrderDTO order = orderService.placeOrder(userDTO.getUserId(), cartId, paymentMethod);

		SendNotificationMessageDTO sendNotificationMessageDTO = new SendNotificationMessageDTO();
		sendNotificationMessageDTO.setTo(userDTO.getEmail());
		sendNotificationMessageDTO.setSubject("Order placed");
		sendNotificationMessageDTO.setBody("Order has been placed for"+order.getOrderItems().toString());
		kafkaNotificationProducerClient.publishNotificationEvent(sendNotificationMessageDTO);

		logger.info(order);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
	}

	@PutMapping("admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String emailId, @PathVariable Long orderId, @PathVariable String orderStatus) {
		logger.info("updateOrderByUser");
		UserDTO userDTO = userService.getUserByEmail(emailId);
		OrderDTO order = orderService.updateOrder(userDTO.getUserId(), orderId, orderStatus);
		logger.info(order);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
	}

}
