package se.lexicon.flightbooking_api.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDTO(
        @NotBlank(message = "Chat ID is required")
        String chatId,
        @NotBlank(message = "Chat message is required")
        String message
) {}
