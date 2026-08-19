package com.restaurantbot.conversation.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.conversation.dto.ConversationResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.mapper.ConversationMapper;
import com.restaurantbot.conversation.repository.ConversationRepository;
import com.restaurantbot.conversation.service.ConversationServiceImpl;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private Restaurant restaurant;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        conversation = Conversation.builder()
                .id(100L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .state(ConversationState.START)
                .lastInteractionAt(OffsetDateTime.now())
                .build();
    }

    @Test
void createIfAbsent_shouldCreateNewConversation() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.empty());

    when(restaurantRepository.findById(1L))
            .thenReturn(Optional.of(restaurant));

    when(conversationRepository.save(any(Conversation.class)))
            .thenReturn(conversation);

    Conversation result =
            conversationService.createIfAbsent(
                    "919876543210",
                    1L
            );

    assertThat(result).isEqualTo(conversation);

    verify(conversationRepository).save(
            argThat(saved ->
                    saved.getPhoneNumber()
                            .equals("919876543210")
                    &&
                    saved.getRestaurant()
                            .equals(restaurant)
                    &&
                    saved.getState()
                            == ConversationState.START
            )
    );
}

@Test
void createIfAbsent_shouldReturnExistingConversation() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.of(conversation));

    Conversation result =
            conversationService.createIfAbsent(
                    "919876543210",
                    1L
            );

    assertThat(result).isEqualTo(conversation);

    verify(restaurantRepository, never())
            .findById(anyLong());

    verify(conversationRepository, never())
            .save(any());
}

@Test
void createIfAbsent_shouldThrow_whenRestaurantNotFound() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.empty());

    when(restaurantRepository.findById(1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            conversationService.createIfAbsent(
                    "919876543210",
                    1L
            ))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Restaurant not found");

    verify(conversationRepository, never())
            .save(any());
}

@Test
void findByPhoneNumber_shouldReturnConversation() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.of(conversation));

    Conversation result =
            conversationService.findByPhoneNumber(
                    "919876543210"
            );

    assertThat(result).isEqualTo(conversation);
}

@Test
void findByPhoneNumber_shouldThrow_whenNotFound() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            conversationService.findByPhoneNumber(
                    "919876543210"
            ))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Conversation not found");
}

@Test
void getConversation_shouldReturnResponse() {

    ConversationResponse response =
            new ConversationResponse(
                    100L,
                    "919876543210",
                    ConversationState.START,
                    null,
                    null,
                    null,
                    conversation.getLastInteractionAt()
            );

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.of(conversation));

    when(conversationMapper.toResponse(conversation))
            .thenReturn(response);

    ConversationResponse result =
            conversationService.getConversation(
                    "919876543210"
            );

    assertThat(result).isEqualTo(response);

    verify(conversationMapper)
            .toResponse(conversation);
}

@Test
void reset_shouldResetConversation() {

    conversation.setState(ConversationState.VIEW_CART);

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.of(conversation));

    conversationService.reset("919876543210");

    assertThat(conversation.getState())
            .isEqualTo(ConversationState.START);

    assertThat(conversation.getCurrentOrder())
            .isNull();

    assertThat(conversation.getSelectedCategory())
            .isNull();

    assertThat(conversation.getSelectedItem())
            .isNull();

    verify(conversationRepository)
            .save(conversation);
}

@Test
void updateState_shouldUpdateConversationState() {

    when(conversationRepository.findByPhoneNumber(
            "919876543210"))
            .thenReturn(Optional.of(conversation));

    conversationService.updateState(
            "919876543210",
            ConversationState.MAIN_MENU
    );

    assertThat(conversation.getState())
            .isEqualTo(ConversationState.MAIN_MENU);

    assertThat(conversation.getLastInteractionAt())
            .isNotNull();

    verify(conversationRepository)
            .save(conversation);
}

}
