package com.service.productorder.repositories;

import com.service.productorder.entites.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
	
	@Query("SELECT c FROM Cart c WHERE c.userId = ?1 AND c.cartId = ?2")
	Cart findCartByEmailAndCartId(Long userId, Long cartId);

	@Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.productId = ?1")
	List<Cart> findCartsByProductId(Long productId);
}
