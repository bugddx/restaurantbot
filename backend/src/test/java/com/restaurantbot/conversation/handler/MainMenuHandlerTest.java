import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.handler.MainMenuHandler;
import com.restaurantbot.conversation.state.ConversationState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class MainMenuHandlerTest {

    private MainMenuHandler handler;

    private Conversation conversation;

    @BeforeEach
    void setUp() {
        handler = new MainMenuHandler();

        conversation = Conversation.builder()
                .id(100L)
                .phoneNumber("919876543210")
                .state(ConversationState.MAIN_MENU)
                .build();
    }

    @Test
    void handle_shouldOpenMenu_whenOptionOneSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("Choose a category");

        assertThat(response.endConversation())
                .isFalse();
    }

    @Test
    void handle_shouldOpenOrdering_whenOptionTwoSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);
    }

    @Test
    void handle_shouldOpenReservation_whenOptionThreeSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);
    }

    @Test
    void handle_shouldAskForOrderNumber_whenOptionFourSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "4"
                );

        assertThat(response.message())
                .contains("order number");
    }

    @Test
    void handle_shouldStayOnMainMenu_whenInvalidOption() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "99"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.MAIN_MENU);

        assertThat(response.message())
                .contains("didn't understand");
    }
}
