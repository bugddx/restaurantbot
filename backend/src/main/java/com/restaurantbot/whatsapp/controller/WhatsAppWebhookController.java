package com.restaurantbot.whatsapp.controller;

import com.restaurantbot.whatsapp.dto.WhatsAppWebhookPayload;
import com.restaurantbot.whatsapp.service.WhatsAppWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final WhatsAppWebhookService webhookService;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        return ResponseEntity.ok(challenge == null ? "" : challenge);
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(
            @RequestBody WhatsAppWebhookPayload payload
    ) {
        webhookService.processWebhook(payload);

        return ResponseEntity.ok().build();
    }
}
