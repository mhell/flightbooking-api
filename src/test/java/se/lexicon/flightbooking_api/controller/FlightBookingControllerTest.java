package se.lexicon.flightbooking_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.lexicon.flightbooking_api.entity.FlightBooking;
import se.lexicon.flightbooking_api.entity.FlightStatus;
import se.lexicon.flightbooking_api.repository.FlightBookingRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlightBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightBookingRepository flightBookingRepository;

    @BeforeEach
    void setUp() {
        flightBookingRepository.deleteAll();
    }

    @Test
    void getAllFlightsReturnsEveryFlight() throws Exception {
        flightBookingRepository.save(createFlight("FL001", FlightStatus.AVAILABLE, null, null, "London"));
        flightBookingRepository.save(createFlight("FL002", FlightStatus.BOOKED, "Jane Doe", "jane@example.com", "Paris"));

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].flightNumber").exists())
                .andExpect(jsonPath("$[1].flightNumber").exists());
    }

    @Test
    void getAvailableFlightsReturnsOnlyAvailableFlights() throws Exception {
        flightBookingRepository.save(createFlight("FL001", FlightStatus.AVAILABLE, null, null, "London"));
        flightBookingRepository.save(createFlight("FL002", FlightStatus.BOOKED, "Jane Doe", "jane@example.com", "Paris"));

        mockMvc.perform(get("/api/flights/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("FL001"));
    }

    @Test
    void bookFlightBooksAnAvailableFlight() throws Exception {
        FlightBooking availableFlight = flightBookingRepository.save(
                createFlight("FL001", FlightStatus.AVAILABLE, null, null, "London")
        );

        mockMvc.perform(post("/api/flights/{flightId}/book", availableFlight.getId())
                        .contentType("application/json")
                        .content("""
                                {
                                  "passengerName": "Jane Doe",
                                  "passengerEmail": "jane@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(availableFlight.getId()))
                .andExpect(jsonPath("$.passengerName").value("Jane Doe"))
                .andExpect(jsonPath("$.passengerEmail").value("jane@example.com"))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        FlightBooking savedFlight = flightBookingRepository.findById(availableFlight.getId()).orElseThrow();
        assertThat(savedFlight.getStatus()).isEqualTo(FlightStatus.BOOKED);
        assertThat(savedFlight.getPassengerEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void getBookingsByEmailReturnsOnlyBookedFlightsForEmail() throws Exception {
        flightBookingRepository.save(createFlight("FL001", FlightStatus.BOOKED, "Jane Doe", "jane@example.com", "London"));
        flightBookingRepository.save(createFlight("FL002", FlightStatus.AVAILABLE, null, "jane@example.com", "Paris"));
        flightBookingRepository.save(createFlight("FL003", FlightStatus.BOOKED, "John Doe", "john@example.com", "Rome"));

        mockMvc.perform(get("/api/flights/bookings").param("email", "jane@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("FL001"))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }

    @Test
    void cancelFlightMakesFlightAvailableAndClearsPassengerData() throws Exception {
        FlightBooking bookedFlight = flightBookingRepository.save(
                createFlight("FL001", FlightStatus.BOOKED, "Jane Doe", "jane@example.com", "London")
        );

        mockMvc.perform(delete("/api/flights/{flightId}/cancel", bookedFlight.getId())
                        .param("email", "jane@example.com"))
                .andExpect(status().isNoContent());

        FlightBooking savedFlight = flightBookingRepository.findById(bookedFlight.getId()).orElseThrow();
        assertThat(savedFlight.getStatus()).isEqualTo(FlightStatus.AVAILABLE);
        assertThat(savedFlight.getPassengerName()).isNull();
        assertThat(savedFlight.getPassengerEmail()).isNull();
    }

    @Test
    void cancelFlightReturnsBadRequestWhenFlightIsNotBooked() throws Exception {
        FlightBooking availableFlight = flightBookingRepository.save(
                createFlight("FL001", FlightStatus.AVAILABLE, null, null, "London")
        );

        mockMvc.perform(delete("/api/flights/{flightId}/cancel", availableFlight.getId())
                        .param("email", "jane@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Flight is not currently booked"));
    }

    private FlightBooking createFlight(
            String flightNumber,
            FlightStatus status,
            String passengerName,
            String passengerEmail,
            String destination
    ) {
        LocalDateTime departureTime = LocalDateTime.of(2026, 5, 1, 10, 0);
        return FlightBooking.builder()
                .flightNumber(flightNumber)
                .passengerName(passengerName)
                .passengerEmail(passengerEmail)
                .departureTime(departureTime)
                .arrivalTime(departureTime.plusHours(2))
                .status(status)
                .destination(destination)
                .price(199.99)
                .build();
    }
}
