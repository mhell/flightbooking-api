package se.lexicon.flightbooking_api.service;

import reactor.core.publisher.Flux;
import se.lexicon.flightbooking_api.dto.ChatRequestDTO;

public interface FlightBookingAssistant {
    Flux<String> processChatQuery(ChatRequestDTO chatRequest);
}
