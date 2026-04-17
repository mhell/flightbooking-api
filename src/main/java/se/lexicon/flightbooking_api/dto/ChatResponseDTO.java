package se.lexicon.flightbooking_api.dto;

import java.util.List;
import java.util.Optional;

public record ChatResponseDTO(
        String chatResponse,
        Optional<FlightBookingDTO> bookedFlight,
        List<FlightBookingDTO> flights
){}
