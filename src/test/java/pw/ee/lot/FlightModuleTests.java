package pw.ee.lot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.domain.repository.FlightRepository;
import pw.ee.lot.dto.flight.CreateFlightRequest;
import pw.ee.lot.service.FlightUseCases;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FlightModuleTests {

    private final static String flightsEndpoint = "/flights";
    private final ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
    @Autowired
    private FlightUseCases flightUseCases;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FlightRepository flightRepository;

    @BeforeEach
    public void setUp() {
        flightRepository.deleteAll();
    }

    @Test
    public void givenWrongFlightNumber_whenGetFlight_thenReturnHttpNotFound() throws Exception {
        // given
        String wrongFlightNumber = "WRONG_ID";

        // when and then
        mockMvc.perform(get(flightsEndpoint + "/" + wrongFlightNumber))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenFlightNumber_whenGetFlight_thenReturnHttpOk() throws Exception {
        // given
        Flight flight = Flight.builder()
            .flightNumber("LOT123")
            .departureTime(LocalDateTime.now())
            .availableSeats(100)
            .route(List.of("WAW", "JFK"))
            .build();
        flight = flightRepository.save(flight);

        // when and then
        mockMvc.perform(get(flightsEndpoint + "/" + flight.getFlightNumber()))
            .andExpect(status().isOk());
    }

    @Test
    public void givenCreateFlightRequest_whenCreateFlight_thenReturnHttpCreated() throws Exception {
        // given
        String flightNumber = "LOT123";
        LocalDateTime departureTime = LocalDateTime.now();
        int availableSeats = 100;
        List<String> route = List.of("WAW", "JFK");

        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
            flightNumber,
            departureTime,
            availableSeats,
            route
        );

        // when and then
        mockMvc.perform(post(flightsEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(createFlightRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenCreateFlightRequest_whenCreateFlightAndFlightAlreadyExists_thenReturnHttpBadRequest() throws Exception {
        // given
        Flight flight = Flight.builder()
            .flightNumber("LOT123")
            .departureTime(LocalDateTime.now())
            .availableSeats(100)
            .route(List.of("WAW", "JFK"))
            .build();
        flightRepository.save(flight);

        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
            flight.getFlightNumber(),
            flight.getDepartureTime(),
            flight.getAvailableSeats(),
            flight.getRoute()
        );

        // when and then
        mockMvc.perform(post(flightsEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(createFlightRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenFlightNumber_whenDeleteFlight_thenReturnHttpNoContent() throws Exception {
        // given
        Flight flight = Flight.builder()
            .flightNumber("LOT123")
            .departureTime(LocalDateTime.now())
            .availableSeats(100)
            .route(List.of("WAW", "JFK"))
            .build();
        flight = flightRepository.save(flight);

        // when and then
        mockMvc.perform(delete(flightsEndpoint + "/" + flight.getFlightNumber()))
            .andExpect(status().isNoContent());
    }

    @Test
    public void givenWrongFlightNumber_whenDeleteFlight_thenReturnHttpNotFound() throws Exception {
        // given
        String wrongFlightNumber = "WRONG_ID";

        // when and then
        mockMvc.perform(delete(flightsEndpoint + "/" + wrongFlightNumber))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenUpdateFlightRequest_whenUpdateFlight_thenReturnHttpNoContent() throws Exception {
        // given
        Flight flight = Flight.builder()
            .flightNumber("LOT123")
            .departureTime(LocalDateTime.now())
            .availableSeats(100)
            .route(List.of("WAW", "JFK"))
            .build();
        flight = flightRepository.save(flight);

        // when
        String newFlightNumber = "LOT124";
        LocalDateTime newDepartureTime = LocalDateTime.now().plusDays(1);
        int newAvailableSeats = 200;
        List<String> newRoute = List.of("WAW", "JFK", "LAX");

        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
            newFlightNumber,
            newDepartureTime,
            newAvailableSeats,
            newRoute
        );

        // when and then
        mockMvc.perform(patch(flightsEndpoint + "/" + flight.getFlightNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(createFlightRequest)))
            .andExpect(status().isNoContent());

        assertThat(flightRepository.findByFlightNumber(newFlightNumber)).isNotEmpty();
        assertThat(flightRepository.findByFlightNumber(flight.getFlightNumber())).isEmpty();
    }

    @Test
    public void givenWrongFlightNumber_whenUpdateFlight_thenReturnHttpNotFound() throws Exception {
        // given
        String wrongFlightNumber = "WRONG_ID";

        // when
        String newFlightNumber = "LOT124";
        LocalDateTime newDepartureTime = LocalDateTime.now().plusDays(1);
        int newAvailableSeats = 200;
        List<String> newRoute = List.of("WAW", "JFK", "LAX");

        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
            newFlightNumber,
            newDepartureTime,
            newAvailableSeats,
            newRoute
        );

        // when and then
        mockMvc.perform(patch(flightsEndpoint + "/" + wrongFlightNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(createFlightRequest)))
            .andExpect(status().isNotFound());
    }
}
