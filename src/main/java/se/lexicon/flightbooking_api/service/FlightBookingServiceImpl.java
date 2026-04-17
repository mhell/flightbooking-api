package se.lexicon.flightbooking_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.flightbooking_api.dto.AvailableFlightDTO;
import se.lexicon.flightbooking_api.dto.BookFlightRequestDTO;
import se.lexicon.flightbooking_api.dto.FlightBookingDTO;
import se.lexicon.flightbooking_api.dto.FlightListDTO;
import se.lexicon.flightbooking_api.entity.FlightBooking;
import se.lexicon.flightbooking_api.entity.FlightStatus;
import se.lexicon.flightbooking_api.exception.FlightBookingException;
import se.lexicon.flightbooking_api.exception.ResourceNotFoundException;
import se.lexicon.flightbooking_api.mapper.FlightBookingMapper;
import se.lexicon.flightbooking_api.repository.FlightBookingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FlightBookingServiceImpl implements FlightBookingService {

    private final FlightBookingRepository flightBookingRepository;
    private final FlightBookingMapper mapper;


    @Override
    @Tool(description = "Book a flight using flight ID and passenger details (name and email). Marks the flight as booked and returns booking confirmation")
    public FlightBookingDTO bookFlight(
            @ToolParam(description = "The unique ID of the flight to book") Long flightId,
            @ToolParam(description = "Booking details with the passenger's name and email") BookFlightRequestDTO bookingRequest) {
        FlightBooking flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        if (flight.getStatus() != FlightStatus.AVAILABLE) {
            throw new FlightBookingException("Flight is not available");
        }

        flight.setPassengerName(bookingRequest.passengerName());
        flight.setPassengerEmail(bookingRequest.passengerEmail());
        flight.setStatus(FlightStatus.BOOKED);

        FlightBooking savedFlight = flightBookingRepository.save(flight);
        return mapper.toDTO(savedFlight);
    }

    @Override
    @Tool(description = "Cancel a passenger's flight booking using flight ID and the passenger's email")
    public void cancelFlight(
            @ToolParam(description = "The unique ID of the flight to cancel") Long flightId,
            @ToolParam(description = "The passenger's email address") String passengerEmail) {
        FlightBooking flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        if (!flight.getPassengerEmail().equals(passengerEmail)) {
            throw new FlightBookingException("Passenger email does not match");
        }

        flight.setStatus(FlightStatus.AVAILABLE);
        flightBookingRepository.save(flight);
    }

    @Override
    @Tool(description = "Find all available flights (non booked). Returns a list of objects describing the available flights")
    public List<AvailableFlightDTO> findAvailableFlights() {
        return flightBookingRepository.findByStatus(FlightStatus.AVAILABLE)
                .stream()
                .map(mapper::toAvailableFlightDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Tool(description = "Find all booked flights of a passenger using his or hers email. Returns a list of objects describing the booked flights")
    public List<FlightBookingDTO> findBookingsByEmail(
            @ToolParam(description = "The passenger's email address") String email) {
        return flightBookingRepository.findByPassengerEmail(email)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Tool(description = "Find all flights (both booked and available). Returns a list of objects describing the flights")
    public List<FlightListDTO> findAll() {
        return flightBookingRepository.findAll()
                .stream()
                .map(mapper::toListDTO)
                .collect(Collectors.toList());
    }

}