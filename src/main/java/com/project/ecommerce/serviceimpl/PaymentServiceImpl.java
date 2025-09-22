package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.PaymentRequest;
import com.project.ecommerce.dto.PaymentResponse;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderStatus;
import com.project.ecommerce.entity.Payment;
import com.project.ecommerce.entity.PaymentStatus;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.exception.UnauthorizedActionException;
import com.project.ecommerce.mapper.PaymentMapper;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.PaymentRepository;
import com.project.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final WebClient paymentWebClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Cacheable(value = "paymentCache", key = "#orderId")
    public PaymentResponse processPayment(UUID orderId, String username) {
        log.info("Initiating payment for order ID: {} by user: {}", orderId, username);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });

        if (!order.getUser().getUsername().equals(username)) {
            log.warn("Unauthorized payment attempt by {} for order {}", username, orderId);
            throw new UnauthorizedActionException("You are not authorized to pay for this order");
        }

        PaymentRequest request = PaymentRequest.builder()
                .amount(order.getTotalAmount())
                .orderId(order.getOrderId())
                .build();

        PaymentResponse gatewayResponse;
        try {
            gatewayResponse = paymentWebClient.post()
                    .uri("/charge")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Payment gateway error for order {}: {}", orderId, e.getMessage(), e);
            return PaymentResponse.builder()
                    .status(PaymentStatus.FAILED)
                    .message("Gateway error: " + e.getMessage())
                    .build();
        }

        if (gatewayResponse == null || gatewayResponse.getStatus() != PaymentStatus.SUCCESS) {
            String errorMsg = gatewayResponse != null ? gatewayResponse.getMessage() : "No response from gateway";
            log.warn("Payment failed for order {}: {}", orderId, errorMsg);
            return PaymentResponse.builder()
                    .status(PaymentStatus.FAILED)
                    .message(errorMsg)
                    .build();
        }

        LocalDateTime paidAt = LocalDateTime.now();
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .transactionId(gatewayResponse.getTransactionId())
                .paidAt(paidAt)
                .status(PaymentStatus.SUCCESS)
                .build();
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Payment successful for order {}: Transaction ID {}", orderId, gatewayResponse.getTransactionId());


        return paymentMapper.toPaymentResponse(payment, order);
    }
}
