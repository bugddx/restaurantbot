package com.restaurantbot.whatsapp.dto;

import java.util.List;
import java.util.Map;

public record WhatsAppWebhookPayload(
        String object,
        List<Entry> entry
) {

    public record Entry(
            String id,
            List<Change> changes
    ) {
    }

    public record Change(
            String field,
            Map<String, Object> value
    ) {
    }
}
