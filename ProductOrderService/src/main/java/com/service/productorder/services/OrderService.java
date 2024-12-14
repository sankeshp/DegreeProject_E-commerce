package com.service.productorder.services;

import com.service.productorder.dtos.OrderDTO;
import com.service.productorder.dtos.OrderResponseDTO;
import com.service.productorder.dtos.UserDTO;

import java.util.List;

public interface OrderService {

	OrderDTO placeOrder(Long userId, Long cartId, String paymentMethod);
	
	OrderDTO getOrder(Long userId, Long orderId);

	OrderDTO getOrder(Long orderId);

	List<OrderDTO> getOrdersByUser(UserDTO userDTO);
	
	OrderResponseDTO getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(Long userId, Long orderId, String orderStatus);
}
