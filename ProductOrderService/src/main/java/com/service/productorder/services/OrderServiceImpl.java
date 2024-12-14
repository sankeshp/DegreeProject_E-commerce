package com.service.productorder.services;

import com.service.productorder.entites.*;
import com.service.productorder.exceptions.APIException;
import com.service.productorder.exceptions.ResourceNotFoundException;
import com.service.productorder.dtos.OrderDTO;
import com.service.productorder.dtos.OrderItemDTO;
import com.service.productorder.dtos.OrderResponseDTO;
import com.service.productorder.dtos.UserDTO;
import com.service.productorder.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	public CartRepo cartRepo;

	@Autowired
	public OrderRepo orderRepo;

	@Autowired
	public OrderItemRepo orderItemRepo;

	@Autowired
	public CartService cartService;

	@Autowired
	public ModelMapper modelMapper;

	@Autowired
	public PaymentService paymentService;

	@Override
	public OrderDTO placeOrder(Long userId, Long cartId, String paymentMethod) {

		Cart cart = cartRepo.findCartByEmailAndCartId(userId, cartId);

		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}

		Order order = new Order();

		order.setUserId(userId);
		order.setOrderDate(LocalDate.now());

		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted !");

		Order savedOrder = orderRepo.save(order);

		List<CartItem> cartItems = cart.getCartItems();

		if (cartItems.isEmpty()) {
			throw new APIException("Cart is empty");
		}

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();

			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);

			orderItems.add(orderItem);
		}

		orderItems = orderItemRepo.saveAll(orderItems);

		cart.getCartItems().forEach(item -> {
			int quantity = item.getQuantity();

			Product product = item.getProduct();

			cartService.deleteProductFromCart(cartId, item.getProduct().getProductId());

			product.setQuantity(product.getQuantity() - quantity);
		});

		Payment payment = paymentService.makePayment(order,paymentMethod);

		order.setPayment(payment);

		OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

		orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

		return orderDTO;
	}

	@Override
	public List<OrderDTO> getOrdersByUser(UserDTO userDTO) {
		List<Order> orders = orderRepo.findAllByUserId(userDTO.getUserId());

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());

		if (orderDTOs.isEmpty()) {
			throw new APIException("No orders placed yet by the user with email: " + userDTO.getEmail());
		}

		return orderDTOs;
	}

	@Override
	public OrderDTO getOrder(Long userId, Long orderId) {

		Order order = orderRepo.findOrderByEmailAndOrderId(userId, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public OrderDTO getOrder(Long orderId) {
		Order order = orderRepo.findOrderByOrderId(orderId);
		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}
		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public OrderResponseDTO getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Order> pageOrders = orderRepo.findAll(pageDetails);

		List<Order> orders = pageOrders.getContent();

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());
		
		if ( orderDTOs.isEmpty()) {
			throw new APIException("No orders placed yet by the users");
		}

		OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
		
		orderResponseDTO.setContent(orderDTOs);
		orderResponseDTO.setPageNumber(pageOrders.getNumber());
		orderResponseDTO.setPageSize(pageOrders.getSize());
		orderResponseDTO.setTotalElements(pageOrders.getTotalElements());
		orderResponseDTO.setTotalPages(pageOrders.getTotalPages());
		orderResponseDTO.setLastPage(pageOrders.isLast());
		
		return orderResponseDTO;
	}

	@Override
	public OrderDTO updateOrder(Long userId, Long orderId, String orderStatus) {

		Order order = orderRepo.findOrderByEmailAndOrderId(userId, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		order.setOrderStatus(orderStatus);

		return modelMapper.map(order, OrderDTO.class);
	}

}
