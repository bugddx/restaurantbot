package com.restaurantbot.orderitem.mapper;

import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    OrderItem toEntity(CreateOrderItemRequest request);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "menuItemName", source = "menuItem.name")
    OrderItemResponse toResponse(OrderItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    void update(UpdateOrderItemRequest request,
                @MappingTarget OrderItem entity);
}
