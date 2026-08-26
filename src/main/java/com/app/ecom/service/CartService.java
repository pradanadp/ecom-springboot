package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.exception.InsufficientStockException;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addToCart(String userId, CartItemRequest request) {
        User user = findUser(userId);
        Product product = findProduct(request.getProductId());
        validateStock(product, request.getQuantity());

        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElseGet(() -> createCartItem(user, product));
        item.setQuantity(increaseQuantity(item, request.getQuantity()));
        item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        cartItemRepository.save(item);
    }

    private User findUser(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
                            + ". Available: " + product.getStockQuantity());
        }
    }

    private CartItem createCartItem(User user, Product product) {
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        return item;
    }

    private int increaseQuantity(CartItem existing, int requestedQuantity) {
        return (existing.getQuantity() == null ? 0 : existing.getQuantity()) + requestedQuantity;
    }

    @Transactional
    public void removeFromCart(String userId, Long productId) {
        User user = findUser(userId);
        Product product = findProduct(productId);

        long deletedCount = cartItemRepository.deleteByUserAndProduct(user, product);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException(
                    "Cart item not found for user: " + userId + " and product: " + productId);
        }
    }
}
