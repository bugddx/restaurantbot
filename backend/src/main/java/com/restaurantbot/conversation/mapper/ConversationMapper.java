package com.restaurantbot.conversation.mapper;

import com.restaurantbot.conversation.dto.ConversationResponse;
import com.restaurantbot.conversation.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "currentOrderId", source = "currentOrder.id")
    @Mapping(target = "selectedCategoryId", source = "selectedCategory.id")
    @Mapping(target = "selectedItemId", source = "selectedItem.id")
    ConversationResponse toResponse(Conversation conversation);
}
