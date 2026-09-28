package com.restaurantbot.whatsapp.service;

import com.restaurantbot.whatsapp.dto.WhatsAppWebhookPayload;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppWebhookService {

    public void processWebhook(WhatsAppWebhookPayload payload) {
        if (payload == null) {
            return;
        }

        if (payload.entry() == null || payload.entry().isEmpty()) {
            return;
        }

        // Phase 1:
        // Receive and validate the webhook envelope.
        //
        // Message parsing, deduplication, restaurant resolution,
        // conversation processing, and outbound replies will be
        // implemented in later WhatsApp milestones.
    }
}
