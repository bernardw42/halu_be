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

        try {
            List<CartItem> items = cartItemService.getItemsByCart(cart);
            List<CartItemDTO> dtoList = new ArrayList<>();

            for (CartItem item : items) {
                Product p = item.getProduct();
                ProductDTO dto = new ProductDTO(
                        p.getId(), p.getTitle(), p.getCategory(),
                        p.getPrice(), p.getDescription(), p.getImageUrl(), p.getQuantity()
                );
                dtoList.add(new CartItemDTO(item.getId(), dto, item.getQuantity()));
            }

            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get cart items for buyer ID " + buyer.getId(), e);
        }
    }

    public CartItemDTO addProductToCart(User buyer, Product product) {
        if (product.getQuantity() == null || product.getQuantity() == 0) {
            throw new IllegalStateException("This product is out of stock and cannot be added to the cart.");
        }

        try {
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

            ProductDTO productDTO = new ProductDTO(
                    product.getId(), product.getTitle(), product.getCategory(),
                    product.getPrice(), product.getDescription(), product.getImageUrl(), product.getQuantity()
            );

            return new CartItemDTO(item.getId(), productDTO, item.getQuantity());
        } catch (Exception e) {
            throw new RuntimeException("Error adding product ID " + product.getId() + " to cart for buyer ID " + buyer.getId(), e);
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
            throw new RuntimeException("Error removing product ID " + product.getId() + " from cart of buyer ID " + buyer.getId(), e);
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
            throw new RuntimeException("Error clearing cart for buyer ID " + buyer.getId(), e);
        }
    }
    
}
