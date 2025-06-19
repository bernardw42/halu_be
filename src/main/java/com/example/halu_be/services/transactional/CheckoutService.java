package com.example.halu_be.services.transactional;

import com.example.halu_be.models.transactional.Order;
import com.example.halu_be.models.transactional.OrderItem;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.models.Cart;
import com.example.halu_be.models.CartItem;
import com.example.halu_be.repositories.*;
import com.example.halu_be.repositories.transactional.OrderRepository;
// import com.example.halu_be.repositories.transactional.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @Transactional
    public Order checkout(Long buyerId) {
        // Step 1: Get buyer user object
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        // Step 2: Get cart for buyer
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        // Step 3: Get items in the cart
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Step 4: Create Order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus("PENDING");
        order.setCreatedAt(Timestamp.from(Instant.now()));
        order.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(86400)));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct(); // already joined
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for: " + product.getTitle());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalPrice(total);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems); // optional

        return savedOrder;
    }
}
