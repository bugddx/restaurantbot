package com.restaurantbot.payment.mapper;

import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;
import com.restaurantbot.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    Payment toEntity(CreatePaymentRequest request);

    @Mapping(target = "orderId", source = "order.id")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    void update(
            UpdatePaymentRequest request,
            @MappingTarget Payment payment
    );
}
