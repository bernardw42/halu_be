package com.example.halu_be.controllers;

import com.example.halu_be.dtos.CartItemDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.services.CartService;
import com.example.halu_be.services.ProductService;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/{buyerId}")
    public List<CartItemDTO> getCartItems(@PathVariable Long buyerId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        if (buyer.isEmpty()) return Collections.emptyList();
        return cartService.getCartItemDTOsByBuyer(buyer.get());
    }

    @PostMapping("/{buyerId}/add/{productId}")
    public CartItemDTO addToCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getProductById(productId); // FIXED

        if (buyer.isEmpty() || product.isEmpty()) return null;

        return cartService.addProductToCart(buyer.get(), product.get());
    }


    @PostMapping("/{buyerId}/remove/{productId}")
    public void removeFromCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getProductById(productId); // FIXED

        if (buyer.isEmpty() || product.isEmpty()) return;

        cartService.removeProductFromCart(buyer.get(), product.get());
    }

}
