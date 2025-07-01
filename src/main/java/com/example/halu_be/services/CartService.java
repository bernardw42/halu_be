package com.example.halu_be.services;

import com.example.halu_be.dtos.CartItemDTO;
import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.*;
import com.example.halu_be.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public Optional<Cart> getCartByBuyer(User buyer) {
        return cartRepository.findByBuyer(buyer);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<CartItemDTO> getCartItemDTOsByBuyer(User buyer) {
        Cart cart = getCartByBuyer(buyer).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setBuyer(buyer);
            return saveCart(newCart);
        });

        List<CartItem> items = cartItemService.getItemsByCart(cart);
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
    }

    public void removeProductFromCart(User buyer, Product product) {
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
    }

    public void clearCart(User buyer) {
        getCartByBuyer(buyer).ifPresent(cart -> {
            List<CartItem> items = cartItemService.getItemsByCart(cart);
            for (CartItem item : items) {
                cartItemService.deleteCartItem(item.getId());
            }
        });
    }
}
