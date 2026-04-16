package se.lexicon.flightbooking_api.service;

import reactor.core.publisher.Flux;

public interface FlightBookingAssistant {
    Flux<String> processChatQuery(String chatId, String chatQuery);
}
