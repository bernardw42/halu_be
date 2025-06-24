package com.example.halu_be.services.transactional;

import com.example.halu_be.dtos.CheckoutResponseDTO;
import com.example.halu_be.models.*;
import com.example.halu_be.models.transactional.Order;
import com.example.halu_be.models.transactional.OrderItem;
import com.example.halu_be.repositories.*;
import com.example.halu_be.repositories.transactional.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // ✅ POST: Checkout from cart
    @Transactional
    public CheckoutResponseDTO checkout(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus("PENDING");
        order.setCreatedAt(Timestamp.from(Instant.now()));
        order.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(86400))); // 24h to pay

        List<OrderItem> orderItems = new ArrayList<>();
        List<CheckoutResponseDTO.ItemSummary> itemSummaries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for: " + product.getTitle());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            itemSummaries.add(new CheckoutResponseDTO.ItemSummary(
                    product.getTitle(),
                    cartItem.getQuantity(),
                    product.getPrice()
            ));
        }

        order.setTotalPrice(total);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        long secondsRemaining = Duration.between(Instant.now(), savedOrder.getExpiresAt().toInstant()).getSeconds();

        return new CheckoutResponseDTO(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalPrice(),
                itemSummaries,
                savedOrder.getExpiresAt(),
                Math.max(0, secondsRemaining)
        );
    }

    // ✅ GET: Retrieve existing order by ID for display in payment screen
    public CheckoutResponseDTO getCheckoutDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<CheckoutResponseDTO.ItemSummary> itemSummaries = order.getItems().stream()
                .map(item -> new CheckoutResponseDTO.ItemSummary(
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .toList();

        long secondsRemaining = Duration.between(Instant.now(), order.getExpiresAt().toInstant()).getSeconds();

        return new CheckoutResponseDTO(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                itemSummaries,
                order.getExpiresAt(),
                Math.max(0, secondsRemaining)
        );
    }
}
