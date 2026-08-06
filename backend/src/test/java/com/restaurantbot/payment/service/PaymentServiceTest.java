package com.restaurantbot.payment.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;
import com.restaurantbot.payment.entity.Payment;
import com.restaurantbot.payment.entity.PaymentMethod;
import com.restaurantbot.payment.entity.PaymentStatus;
import com.restaurantbot.payment.mapper.PaymentMapper;
import com.restaurantbot.payment.repository.PaymentRepository;
import com.restaurantbot.payment.service.PaymentServiceImpl;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private PaymentServiceImpl service;

    private Restaurant restaurant;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
            .id(1L)
            .name("Pizza Palace")
            .build();

        order = Order.builder()
            .id(10L)
            .restaurant(restaurant)
            .totalAmount(new BigDecimal("500"))
            .build();

        payment = Payment.builder()
            .id(100L)
            .order(order)
            .paymentMethod(PaymentMethod.CASH)
            .status(PaymentStatus.COMPLETED)
            .amount(new BigDecimal("500"))
            .transactionReference("TXN001")
            .paidAt(OffsetDateTime.now())
            .build();
    }

@Test
void create_shouldCreatePayment() {

    CreatePaymentRequest request =
        new CreatePaymentRequest(
                PaymentMethod.CASH,
                "TXN001"
                );

    Payment mapped = new Payment();

    PaymentResponse response =
        new PaymentResponse(
                100L,
                10L,
                PaymentMethod.CASH,
                PaymentStatus.COMPLETED,
                new BigDecimal("500"),
                "TXN001",
                payment.getPaidAt()
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.existsByOrderId(10L))
        .thenReturn(false);

    when(mapper.toEntity(request))
        .thenReturn(mapped);

    when(paymentRepository.save(mapped))
        .thenReturn(payment);

    when(mapper.toResponse(payment))
        .thenReturn(response);

    PaymentResponse result =
        service.create(
                1L,
                10L,
                request
                );

    assertThat(result).isEqualTo(response);

    verify(paymentRepository).save(mapped);
}

@Test
void create_shouldThrow_whenOrderNotFound() {

    CreatePaymentRequest request =
        new CreatePaymentRequest(
                PaymentMethod.CASH,
                "TXN001"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.create(
                1L,
                10L,
                request
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order not found");

    verify(paymentRepository, never()).save(any());
}

@Test
void create_shouldThrow_whenPaymentAlreadyExists() {

    CreatePaymentRequest request =
        new CreatePaymentRequest(
                PaymentMethod.CASH,
                "TXN001"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.existsByOrderId(10L))
        .thenReturn(true);

    assertThatThrownBy(() ->
            service.create(
                1L,
                10L,
                request
                ))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessage("Payment already exists for this order");

    verify(paymentRepository, never()).save(any());
}

@Test
void getById_shouldReturnPayment() {

    PaymentResponse response =
        new PaymentResponse(
                100L,
                10L,
                PaymentMethod.CASH,
                PaymentStatus.COMPLETED,
                new BigDecimal("500"),
                "TXN001",
                payment.getPaidAt()
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.findByIdAndOrderId(
                100L,
                10L))
        .thenReturn(Optional.of(payment));

    when(mapper.toResponse(payment))
        .thenReturn(response);

    PaymentResponse result =
        service.getById(
                1L,
                10L,
                100L
                );

    assertThat(result).isEqualTo(response);
}

@Test
void getById_shouldThrow_whenPaymentNotFound() {

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.findByIdAndOrderId(
                100L,
                10L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getById(
                1L,
                10L,
                100L
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Payment not found");
}

@Test
void getAll_shouldReturnPayments() {

    PaymentResponse response =
        new PaymentResponse(
                100L,
                10L,
                PaymentMethod.CASH,
                PaymentStatus.COMPLETED,
                new BigDecimal("500"),
                "TXN001",
                payment.getPaidAt()
                );

    when(paymentRepository.findAll())
        .thenReturn(List.of(payment));

    when(mapper.toResponse(payment))
        .thenReturn(response);

    List<PaymentResponse> result =
        service.getAll(1L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(response);
}

@Test
void update_shouldUpdatePayment() {

    UpdatePaymentRequest request =
        new UpdatePaymentRequest(
                PaymentStatus.REFUNDED
                );

    PaymentResponse response =
        new PaymentResponse(
                100L,
                10L,
                PaymentMethod.CASH,
                PaymentStatus.REFUNDED,
                new BigDecimal("500"),
                "TXN001",
                payment.getPaidAt()
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.findByIdAndOrderId(
                100L,
                10L))
        .thenReturn(Optional.of(payment));

    doAnswer(invocation -> {
        Payment entity = invocation.getArgument(1);
        entity.setStatus(PaymentStatus.REFUNDED);
        return null;
    }).when(mapper).update(any(), any());

    when(paymentRepository.save(payment))
        .thenReturn(payment);

    when(mapper.toResponse(payment))
        .thenReturn(response);

    PaymentResponse result =
        service.update(
                1L,
                10L,
                100L,
                request
                );

    assertThat(result).isEqualTo(response);
}

@Test
void delete_shouldDeletePayment() {

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.findByIdAndOrderId(
                100L,
                10L))
        .thenReturn(Optional.of(payment));

    service.delete(
            1L,
            10L,
            100L
            );

    verify(paymentRepository).delete(payment);
}

@Test
void delete_shouldThrow_whenPaymentNotFound() {

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(paymentRepository.findByIdAndOrderId(
                100L,
                10L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.delete(
                1L,
                10L,
                100L
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Payment not found");
}

}
