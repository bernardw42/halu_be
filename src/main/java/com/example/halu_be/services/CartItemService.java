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
        try {
            return cartItemRepository.findByCart(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving cart items for cart ID " + cart.getId(), e);
        }
    }

    public Optional<CartItem> getItemByCartAndProduct(Cart cart, Product product) {
        try {
            return cartItemRepository.findByCartAndProduct(cart, product);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching item for cart ID " + cart.getId() + " and product ID " + product.getId(), e);
        }
    }

    public CartItem saveCartItem(CartItem cartItem) {
        try {
            return cartItemRepository.save(cartItem);
        } catch (Exception e) {
            throw new RuntimeException("Error saving cart item (product ID " + cartItem.getProduct().getId() + ")", e);
        }
    }

    public void deleteCartItem(Long id) {
        try {
            cartItemRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cart item with ID " + id, e);
        }
    }
}
