package com.service.productorder.services;

import com.service.productorder.payloads.OrderDTO;
import com.service.productorder.payloads.OrderResponse;
import com.service.productorder.payloads.User;

import java.util.List;

public interface OrderService {
	
	OrderDTO placeOrder(Long userId, Long cartId, String paymentMethod);
	
	OrderDTO getOrder(Long userId, Long orderId);
	
	List<OrderDTO> getOrdersByUser(User user);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(Long userId, Long orderId, String orderStatus);
}
