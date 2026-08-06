package com.restaurantbot.order.mapper;

import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "table", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    Order toEntity(CreateOrderRequest request);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "table.id", target = "tableId")
    OrderResponse toResponse(Order entity);

    void updateStatus(
            UpdateOrderStatusRequest request,
            @MappingTarget Order entity
    );

}
