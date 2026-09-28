package com.restaurantbot.whatsapp.controller;

import com.restaurantbot.whatsapp.dto.WhatsAppWebhookPayload;
import com.restaurantbot.whatsapp.service.WhatsAppWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WhatsAppWebhookController.class)
class WhatsAppWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WhatsAppWebhookService webhookService;

    @Test
    void verifyWebhook_shouldReturnChallenge() throws Exception {
        mockMvc.perform(
                        get("/api/v1/whatsapp/webhook")
                                .param("hub.mode", "subscribe")
                                .param("hub.verify_token", "test-token")
                                .param("hub.challenge", "123456")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("123456"));
    }

    @Test
    void verifyWebhook_shouldReturnEmptyBodyWhenChallengeMissing()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/whatsapp/webhook")
                                .param("hub.mode", "subscribe")
                                .param("hub.verify_token", "test-token")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void receiveWebhook_shouldAcceptPayload() throws Exception {
        String payload = """
                {
                    "object": "whatsapp_business_account",
                    "entry": [
                        {
                            "id": "123456789",
                            "changes": [
                                {
                                    "field": "messages",
                                    "value": {
                                        "messaging_product": "whatsapp"
                                    }
                                }
                            ]
                        }
                    ]
                }
                """;

        mockMvc.perform(
                        post("/api/v1/whatsapp/webhook")
                                .contentType("application/json")
                                .content(payload)
                )
                .andExpect(status().isOk());

        verify(webhookService)
                .processWebhook(any(WhatsAppWebhookPayload.class));
    }

    @Test
    void receiveWebhook_shouldAcceptEmptyEntry() throws Exception {
        String payload = """
                {
                    "object": "whatsapp_business_account",
                    "entry": []
                }
                """;

        mockMvc.perform(
                        post("/api/v1/whatsapp/webhook")
                                .contentType("application/json")
                                .content(payload)
                )
                .andExpect(status().isOk());

        verify(webhookService)
                .processWebhook(any(WhatsAppWebhookPayload.class));
    }
}
