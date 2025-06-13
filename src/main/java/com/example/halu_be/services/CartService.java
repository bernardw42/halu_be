package com.example.halu_be.services;

import com.example.halu_be.models.Cart;
import com.example.halu_be.models.User;
import com.example.halu_be.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Optional<Cart> getCartByBuyer(User buyer) {
        return cartRepository.findByBuyer(buyer);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
}
