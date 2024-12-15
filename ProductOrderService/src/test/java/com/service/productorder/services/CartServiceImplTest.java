package com.service.productorder.services;

import com.service.productorder.dtos.CartDTO;
import com.service.productorder.entites.Cart;
import com.service.productorder.entites.CartItem;
import com.service.productorder.entites.Product;
import com.service.productorder.exceptions.APIException;
import com.service.productorder.exceptions.ResourceNotFoundException;
import com.service.productorder.repositories.CartItemRepo;
import com.service.productorder.repositories.CartRepo;
import com.service.productorder.repositories.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private ModelMapper modelMapper;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(0.0);

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Sample Product");
        product.setQuantity(10);
        product.setSpecialPrice(100.0);
        product.setDiscount(10.0);
    }

    @Test
    void addProductToCart_ShouldAddProductSuccessfully_WhenValidInputs() {
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(1L, 1L)).thenReturn(null);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setProductPrice(product.getSpecialPrice());

        CartDTO cartDTO = new CartDTO();
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.addProductToCart(1L, 1L, 2);

        assertNotNull(result);
        verify(cartItemRepo, times(1)).save(any(CartItem.class));
        verify(cartRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenCartNotFound() {
        when(cartRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(1L, 1L, 2));

        verify(cartRepo, times(1)).findById(1L);
        verifyNoInteractions(productRepo, cartItemRepo, modelMapper);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductNotFound() {
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(1L, 1L, 2));

        verify(cartRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).findById(1L);
        verifyNoInteractions(cartItemRepo, modelMapper);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductAlreadyInCart() {
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(1L, 1L)).thenReturn(new CartItem());

        assertThrows(APIException.class, () -> cartService.addProductToCart(1L, 1L, 2));

        verify(cartRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).findById(1L);
        verify(cartItemRepo, times(1)).findCartItemByProductIdAndCartId(1L, 1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductQuantityInsufficient() {
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(1L, 1L)).thenReturn(null);

        assertThrows(APIException.class, () -> cartService.addProductToCart(1L, 1L, 20));

        verify(cartRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).findById(1L);
        verify(cartItemRepo, times(1)).findCartItemByProductIdAndCartId(1L, 1L);
        verifyNoInteractions(modelMapper);
    }
}
