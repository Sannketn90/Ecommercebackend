package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.PaymentResponse;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "payment.transactionId", target = "transactionId")
    @Mapping(source = "payment.status", target = "status")
    @Mapping(source = "payment.paidAt", target = "paidAt")
    @Mapping(source = "payment.amount", target = "amount")
    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "order.user.username", target = "username")
    @Mapping(target = "message", constant = "Payment completed successfully")
    PaymentResponse toPaymentResponse(Payment payment, Order order);
}
