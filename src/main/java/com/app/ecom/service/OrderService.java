package com.app.ecom.service;

import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.exception.EmptyCartException;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Order;
import com.app.ecom.model.OrderItem;
import com.app.ecom.model.OrderStatus;
import com.app.ecom.model.User;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(String userId) {
        User user = findUser(userId);
        List<CartItem> cartItems = cartService.getCart(user);
        validateCartNotEmpty(cartItems);

        Order savedOrder = orderRepository.save(buildOrder(user, cartItems));
        cartService.clearCart(userId);

        return mapToOrderResponse(savedOrder);
    }

    private User findUser(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private void validateCartNotEmpty(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order: cart is empty");
        }
    }

    private Order buildOrder(User user, List<CartItem> cartItems) {
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(calculateTotal(cartItems))
                .build();
        order.setItems(toOrderItems(cartItems, order));
        return order;
    }

    private BigDecimal calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> toOrderItems(List<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(item -> OrderItem.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .order(order)
                        .build())
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(this::toOrderItemDTO)
                        .toList(),
                order.getCreatedAt()
        );
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice()
        );
    }
}
