package com.service.productorder.repositories;

import com.service.productorder.entites.Cart;
import com.service.productorder.entites.CartItem;
import com.service.productorder.entites.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartRepoTest {

    @Mock
    private CartRepo cartRepo;

    private Cart cart;

    @BeforeEach
    public void setUp() {
        // Create Product
        Product product = new Product();
        product.setProductId(1001L);
        product.setProductName("Test Product");
        product.setPrice(100.00);
        product.setQuantity(10);

        // Create CartItem
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setDiscount(10.00);
        cartItem.setProductPrice(90.00);

        // Create Cart
        cart = new Cart();
        cart.setCartId(101L);
        cart.setUserId(1L);
        cart.setTotalPrice(200.00);
        cart.setCartItems(Collections.singletonList(cartItem));
    }

    @Test
    public void testFindCartByEmailAndCartId() {
        Long userId = 1L;
        Long cartId = 101L;

        when(cartRepo.findCartByEmailAndCartId(1L,101L)).thenReturn(cart);

        Cart cart = cartRepo.findCartByEmailAndCartId(userId, cartId);

        assertNotNull(cart, "Cart should not be null");
        assertEquals(userId, cart.getUserId(), "User ID should match the expected value");
        assertEquals(cartId, cart.getCartId(), "Cart ID should match the expected value");
    }

    @Test
    public void testFindCartsByProductId() {
        Long productId = 1001L;
        List<Cart> carts1 = List.of(cart);
        when(cartRepo.findCartsByProductId(1001L)).thenReturn(carts1);
        List<Cart> carts = cartRepo.findCartsByProductId(productId);

        // Ensure that the carts list is not empty
        assertNotNull(carts, "Carts list should not be null");
        assertFalse(carts.isEmpty(), "Carts list should not be empty");

        // Ensure that the first cart has cart items
        assertNotNull(carts.get(0).getCartItems(), "Cart items should not be null");
        assertFalse(carts.get(0).getCartItems().isEmpty(), "Cart items list should not be empty");

        // Ensure that the product ID is contained in the extracted product IDs
        List<Long> productIds = carts.get(0).getCartItems().stream()
                .map(CartItem::getProduct)
                .map(Product::getProductId)
                .toList();
        assertTrue(productIds.contains(productId), "Product ID should be present in the list");

    }
}
