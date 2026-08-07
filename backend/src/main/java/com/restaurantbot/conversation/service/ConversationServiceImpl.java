package com.restaurantbot.conversation.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.conversation.dto.ConversationResponse;
import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.mapper.ConversationMapper;
import com.restaurantbot.conversation.repository.ConversationRepository;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.conversation.handler.ConversationHandlerRegistry;
import com.restaurantbot.conversation.handler.ConversationHandler;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final RestaurantRepository restaurantRepository;
    private final ConversationMapper conversationMapper;
    private final ConversationHandlerRegistry handlerRegistry;

    @Override
    public Conversation createIfAbsent(
            String phoneNumber,
            Long restaurantId) {

        return conversationRepository
            .findByPhoneNumber(phoneNumber)
            .orElseGet(() -> {

                Restaurant restaurant =
                    restaurantRepository.findById(restaurantId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "Restaurant not found"
                                ));

                Conversation conversation =
                    Conversation.builder()
                    .phoneNumber(phoneNumber)
                    .restaurant(restaurant)
                    .state(ConversationState.START)
                    .lastInteractionAt(
                            OffsetDateTime.now()
                            )
                    .build();

                return conversationRepository.save(conversation);
            });
            }

    @Override
    @Transactional(readOnly = true)
    public Conversation findByPhoneNumber(
            String phoneNumber) {

        return conversationRepository
            .findByPhoneNumber(phoneNumber)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Conversation not found"
                        ));
            }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversation(
            String phoneNumber) {

        Conversation conversation =
            findByPhoneNumber(phoneNumber);

        return conversationMapper.toResponse(conversation);
            }

    @Override
    public void reset(
            String phoneNumber) {

        Conversation conversation =
            findByPhoneNumber(phoneNumber);

        conversation.setState(ConversationState.START);
        conversation.setCurrentOrder(null);
        conversation.setSelectedCategory(null);
        conversation.setSelectedItem(null);
        conversation.setSelectedOrderItem(null);
        conversation.setReservationGuestCount(null);
        conversation.setReservationDate(null);
        conversation.setReservationTime(null);
        conversation.setLastInteractionAt(
                OffsetDateTime.now()
                );

        conversationRepository.save(conversation);
            }

    @Override
    public void updateState(
            String phoneNumber,
            ConversationState state) {

        Conversation conversation =
            findByPhoneNumber(phoneNumber);

        conversation.setState(state);
        conversation.setLastInteractionAt(
                OffsetDateTime.now()
                );

        conversationRepository.save(conversation);
            }

    @Override
    public BotResponse processMessage(
            String phoneNumber,
            String message,
            Long restaurantId) {

        Conversation conversation =
            createIfAbsent(
                    phoneNumber,
                    restaurantId
                    );

        ConversationHandler handler =
            handlerRegistry.getHandler(
                    conversation.getState()
                    );

        BotResponse response =
            handler.handle(
                    conversation,
                    message
                    );

        conversation.setState(
                response.nextState()
                );

        conversation.setLastInteractionAt(
                OffsetDateTime.now()
                );

        conversationRepository.save(conversation);

        return response;
            }
}
