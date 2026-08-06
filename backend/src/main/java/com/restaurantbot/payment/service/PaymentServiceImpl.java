package com.restaurantbot.payment.service;

import com.restaurantbot.common.exception.*;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;
import com.restaurantbot.payment.entity.Payment;
import com.restaurantbot.payment.entity.PaymentStatus;
import com.restaurantbot.payment.mapper.PaymentMapper;
import com.restaurantbot.payment.repository.PaymentRepository;
import com.restaurantbot.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper mapper;

    @Override
public PaymentResponse create(
        Long restaurantId,
        Long orderId,
        CreatePaymentRequest request) {

    Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Order not found"));

    if (paymentRepository.existsByOrderId(orderId)) {
        throw new ResourceAlreadyExistsException(
                "Payment already exists for this order");
    }

    Payment payment =
            mapper.toEntity(request);

    payment.setOrder(order);
    payment.setAmount(order.getTotalAmount());
    payment.setStatus(PaymentStatus.COMPLETED);
    payment.setPaidAt(OffsetDateTime.now());

    Payment saved =
            paymentRepository.save(payment);

    return mapper.toResponse(saved);
}

@Override
@Transactional(readOnly = true)
public PaymentResponse getById(
        Long restaurantId,
        Long orderId,
        Long paymentId) {

    Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Order not found"));

    Payment payment =
            paymentRepository.findByIdAndOrderId(
                    paymentId,
                    order.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Payment not found"));

    return mapper.toResponse(payment);
}

@Override
@Transactional(readOnly = true)
public List<PaymentResponse> getAll(
        Long restaurantId) {

    return paymentRepository.findAll()
            .stream()
            .filter(payment ->
                    payment.getOrder()
                            .getRestaurant()
                            .getId()
                            .equals(restaurantId))
            .map(mapper::toResponse)
            .toList();
}

@Override
public PaymentResponse update(
        Long restaurantId,
        Long orderId,
        Long paymentId,
        UpdatePaymentRequest request) {

    Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Order not found"));

    Payment payment =
            paymentRepository.findByIdAndOrderId(
                    paymentId,
                    order.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Payment not found"));

    mapper.update(request, payment);

    Payment saved =
            paymentRepository.save(payment);

    return mapper.toResponse(saved);
}

@Override
public void delete(
        Long restaurantId,
        Long orderId,
        Long paymentId) {

    Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Order not found"));

    Payment payment =
            paymentRepository.findByIdAndOrderId(
                    paymentId,
                    order.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Payment not found"));

    paymentRepository.delete(payment);
}

}
