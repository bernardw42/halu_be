package com.example.halu_be.controllers;

import com.example.halu_be.models.*;
import com.example.halu_be.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final ProductService productService;

    // Get all items in buyer's cart
    @GetMapping("/{buyerId}")
    public List<CartItem> getCartItems(@PathVariable Long buyerId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        if (buyer.isEmpty()) return Collections.emptyList();

        Cart cart = cartService.getCartByBuyer(buyer.get()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setBuyer(buyer.get());
            return cartService.saveCart(newCart);
        });

        return cartItemService.getItemsByCart(cart);
    }

    // Add product to cart (or increment quantity)
    @PostMapping("/{buyerId}/add/{productId}")
    public CartItem addToCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getAllProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

        if (buyer.isEmpty() || product.isEmpty()) return null;

        Cart cart = cartService.getCartByBuyer(buyer.get()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setBuyer(buyer.get());
            return cartService.saveCart(newCart);
        });

        Optional<CartItem> existing = cartItemService.getItemByCartAndProduct(cart, product.get());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            return cartItemService.saveCartItem(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product.get());
            newItem.setQuantity(1);
            return cartItemService.saveCartItem(newItem);
        }
    }

    // Remove product from cart (or decrement quantity)
    @PostMapping("/{buyerId}/remove/{productId}")
    public void removeFromCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getAllProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

        if (buyer.isEmpty() || product.isEmpty()) return;

        cartService.getCartByBuyer(buyer.get()).ifPresent(cart -> {
            cartItemService.getItemByCartAndProduct(cart, product.get()).ifPresent(item -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartItemService.saveCartItem(item);
                } else {
                    cartItemService.deleteCartItem(item.getId());
                }
            });
        });
    }
}
