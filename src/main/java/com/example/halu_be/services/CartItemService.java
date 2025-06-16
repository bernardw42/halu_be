package com.example.halu_be.services;

import com.example.halu_be.models.Cart;
import com.example.halu_be.models.CartItem;
import com.example.halu_be.models.Product;
import com.example.halu_be.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public List<CartItem> getItemsByCart(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    public Optional<CartItem> getItemByCartAndProduct(Cart cart, Product product) { // ✅ FIXED
        return cartItemRepository.findByCartAndProduct(cart, product);
    }


    public CartItem saveCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}
