package com.example.halu_be.services;

import com.example.halu_be.dtos.CartItemDTO;
import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.*;
import com.example.halu_be.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ For logging
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public Optional<Cart> getCartByBuyer(User buyer) {
        try {
            return cartRepository.findByBuyer(buyer);
        } catch (Exception e) {
            log.error("Failed to find cart for buyer ID {}: {}", buyer.getId(), e.getMessage());
            throw new RuntimeException("Could not fetch cart.", e);
        }
    }

    public Cart saveCart(Cart cart) {
        try {
            return cartRepository.save(cart);
        } catch (Exception e) {
            log.error("Failed to save cart for buyer ID {}: {}", cart.getBuyer().getId(), e.getMessage());
            throw new RuntimeException("Could not save cart.", e);
        }
    }

    public List<CartItemDTO> getCartItemDTOsByBuyer(User buyer) {
        Cart cart = getCartByBuyer(buyer).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setBuyer(buyer);
            return saveCart(newCart);
        });

        List<CartItem> items;
        try {
            items = cartItemService.getItemsByCart(cart);
        } catch (Exception e) {
            log.error("Failed to get cart items for cart ID {}: {}", cart.getId(), e.getMessage());
            throw new RuntimeException("Could not retrieve cart items.", e);
        }

        List<CartItemDTO> dtos = new ArrayList<>();
        for (CartItem item : items) {
            Product p = item.getProduct();
            ProductDTO pDto = new ProductDTO(
                    p.getId(), p.getTitle(), p.getCategory(),
                    p.getPrice(), p.getDescription(), p.getImageUrl(), p.getQuantity()
            );
            dtos.add(new CartItemDTO(item.getId(), pDto, item.getQuantity()));
        }
        return dtos;
    }

    public CartItemDTO addProductToCart(User buyer, Product product) {
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new IllegalStateException("This product is out of stock.");
        }

        Cart cart = getCartByBuyer(buyer).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setBuyer(buyer);
            return saveCart(newCart);
        });

        try {
            CartItem item = cartItemService.getItemByCartAndProduct(cart, product)
                    .map(existing -> {
                        existing.setQuantity(existing.getQuantity() + 1);
                        return cartItemService.saveCartItem(existing);
                    })
                    .orElseGet(() -> {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(product);
                        newItem.setQuantity(1);
                        return cartItemService.saveCartItem(newItem);
                    });

            ProductDTO pDto = new ProductDTO(
                    product.getId(), product.getTitle(), product.getCategory(),
                    product.getPrice(), product.getDescription(), product.getImageUrl(), product.getQuantity()
            );
            return new CartItemDTO(item.getId(), pDto, item.getQuantity());
        } catch (Exception e) {
            log.error("Failed to add product ID {} to cart for buyer ID {}: {}", product.getId(), buyer.getId(), e.getMessage());
            throw new RuntimeException("Could not add product to cart.", e);
        }
    }

    public void removeProductFromCart(User buyer, Product product) {
        try {
            getCartByBuyer(buyer).ifPresent(cart -> {
                cartItemService.getItemByCartAndProduct(cart, product).ifPresent(item -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        cartItemService.saveCartItem(item);
                    } else {
                        cartItemService.deleteCartItem(item.getId());
                    }
                });
            });
        } catch (Exception e) {
            log.error("Failed to remove product ID {} from cart for buyer ID {}: {}", product.getId(), buyer.getId(), e.getMessage());
            throw new RuntimeException("Could not remove product from cart.", e);
        }
    }

    public void clearCart(User buyer) {
        try {
            getCartByBuyer(buyer).ifPresent(cart -> {
                List<CartItem> items = cartItemService.getItemsByCart(cart);
                for (CartItem item : items) {
                    cartItemService.deleteCartItem(item.getId());
                }
            });
        } catch (Exception e) {
            log.error("Failed to clear cart for buyer ID {}: {}", buyer.getId(), e.getMessage());
            throw new RuntimeException("Could not clear cart.", e);
        }
    }
}
