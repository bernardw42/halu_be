package com.example.halu_be.services.transactional;

import com.example.halu_be.dtos.OrderSummaryDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.models.transactional.Order;
import com.example.halu_be.models.transactional.OrderItem;
import com.example.halu_be.repositories.ProductRepository;
import com.example.halu_be.repositories.transactional.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional("transactionManager")
    public String confirmPayment(Long orderId, User buyer) {
        try {
            Order order = getOrderById(orderId);
            if (!order.getBuyer().getId().equals(buyer.getId())) {
                throw new IllegalStateException("This order does not belong to you.");
            }
            if (!"PENDING".equals(order.getStatus())) {
                throw new IllegalStateException("Only PENDING orders can be paid.");
            }
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for: " + product.getTitle());
                }
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }
            order.setStatus("PAID");
            order.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(2 * 86400))); // 2 days to ship
            orderRepository.save(order);
            return "Order marked as PAID and stock reduced.";
        } catch (Exception e) {
            log.error("Payment confirmation failed for order ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Could not confirm payment.", e);
        }
    }

    @Transactional("transactionManager")
    public String confirmShipment(Long orderId, User seller) {
        try {
            Order order = getOrderById(orderId);
            boolean isSeller = order.getItems().stream()
                    .anyMatch(item -> item.getProduct().getOwner().getId().equals(seller.getId()));
            if (!isSeller) {
                throw new IllegalStateException("This order does not contain your products.");
            }
            if (!"PAID".equals(order.getStatus())) {
                throw new IllegalStateException("Cannot ship order that is not paid.");
            }
            order.setStatus("SHIPPED");
            orderRepository.save(order);
            return "Order marked as SHIPPED.";
        } catch (Exception e) {
            log.error("Shipment confirmation failed for order ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Could not confirm shipment.", e);
        }
    }

    @Transactional("transactionManager")
    public String cancelOrder(Long orderId, User user) {
        try {
            Order order = getOrderById(orderId);
            boolean isBuyer = order.getBuyer().getId().equals(user.getId());
            boolean isSeller = order.getItems().stream()
                    .anyMatch(item -> item.getProduct().getOwner().getId().equals(user.getId()));
            if (!isBuyer && !isSeller) {
                throw new IllegalStateException("You cannot cancel this order.");
            }
            if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("PAID")) {
                throw new IllegalStateException("Only pending or paid orders can be cancelled.");
            }
            if ("PAID".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            return "Order cancelled and stock restored if needed.";
        } catch (Exception e) {
            log.error("Order cancellation failed for order ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Could not cancel order.", e);
        }
    }

    @Transactional("transactionManager")
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional("transactionManager")
    public List<OrderSummaryDTO> getOrdersByBuyer(User buyer) {
        try {
            List<Order> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);
            return orders.stream().map(this::toDTO).toList();
        } catch (Exception e) {
            log.error("Failed to fetch orders for buyer ID {}: {}", buyer.getId(), e.getMessage(), e);
            throw new RuntimeException("Could not fetch buyer orders.", e);
        }
    }

    @Transactional("transactionManager")
    public List<OrderSummaryDTO> getOrdersBySeller(User seller) {
        try {
            return orderRepository.findAll().stream()
                    .filter(order -> order.getItems().stream()
                            .anyMatch(item -> item.getProduct().getOwner().getId().equals(seller.getId())))
                    .map(this::toDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch orders for seller ID {}: {}", seller.getId(), e.getMessage(), e);
            throw new RuntimeException("Could not fetch seller orders.", e);
        }
    }

    private OrderSummaryDTO toDTO(Order order) {
        List<OrderSummaryDTO.ItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderSummaryDTO.ItemDTO(
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderSummaryDTO(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getExpiresAt(),
                order.getTotalPrice(),
                itemDTOs
        );
    }

    @Scheduled(fixedRate = 60000)
    @Transactional("transactionManager")
    public void cancelUnpaidOrders() {
        Instant cutoff = Instant.now().minusSeconds(600);
        List<Order> pendingOrders = orderRepository.findAll().stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .filter(o -> o.getCreatedAt().toInstant().isBefore(cutoff))
                .toList();

        for (Order order : pendingOrders) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            log.warn("❌ Auto-cancelled unpaid order ID: {}", order.getId());
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional("transactionManager")
    public void cancelUnshippedOrders() {
        Instant now = Instant.now();
        List<Order> overduePaidOrders = orderRepository.findAll().stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .filter(o -> o.getExpiresAt().toInstant().isBefore(now))
                .toList();

        for (Order order : overduePaidOrders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }

            order.setStatus("CANCELLED");
            orderRepository.save(order);
            log.warn("❌ Auto-cancelled unshipped order ID: {}", order.getId());
        }
    }
}
