package se.lexicon.flightbooking_api.service;

import se.lexicon.flightbooking_api.dto.ChatRequestDTO;
import se.lexicon.flightbooking_api.dto.ChatResponseDTO;

public interface FlightBookingAssistant {
    ChatResponseDTO processChatQuery(ChatRequestDTO chatRequest);
}
