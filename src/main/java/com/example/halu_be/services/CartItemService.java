package com.example.halu_be.services;

import com.example.halu_be.models.Cart;
import com.example.halu_be.models.CartItem;
import com.example.halu_be.models.Product;
import com.example.halu_be.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ Add logging support
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public List<CartItem> getItemsByCart(Cart cart) {
        try {
            return cartItemRepository.findByCart(cart);
        } catch (Exception e) {
            log.error("Failed to get items for cart ID {}: {}", cart.getId(), e.getMessage());
            throw new RuntimeException("Could not retrieve cart items.", e);
        }
    }

    public Optional<CartItem> getItemByCartAndProduct(Cart cart, Product product) {
        try {
            return cartItemRepository.findByCartAndProduct(cart, product);
        } catch (Exception e) {
            log.error("Failed to get cart item for cart ID {} and product ID {}: {}", cart.getId(), product.getId(), e.getMessage());
            throw new RuntimeException("Could not get cart item.", e);
        }
    }

    public CartItem saveCartItem(CartItem cartItem) {
        try {
            return cartItemRepository.save(cartItem);
        } catch (Exception e) {
            log.error("Failed to save cart item for product ID {}: {}", cartItem.getProduct().getId(), e.getMessage());
            throw new RuntimeException("Could not save cart item.", e);
        }
    }

    public void deleteCartItem(Long id) {
        try {
            cartItemRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete cart item with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Could not delete cart item.", e);
        }
    }
}
