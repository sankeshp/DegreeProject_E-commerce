package com.service.productorder.repositories;

import com.service.productorder.entites.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
	
	@Query("SELECT o FROM Order o WHERE o.userId = ?1 AND o.id = ?2")
	Order findOrderByEmailAndOrderId(Long userId, Long cartId);

	List<Order> findAllByUserId(Long userId);

    Order findOrderByOrderId(Long orderId);
}
