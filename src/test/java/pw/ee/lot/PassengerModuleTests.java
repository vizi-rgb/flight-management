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
import pw.ee.lot.domain.Passenger;
import pw.ee.lot.domain.PhoneNumber;
import pw.ee.lot.domain.repository.PassengerRepository;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PassengerModuleTests {

    private final static String passengersEndpoint = "/passengers";
    private final ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        passengerRepository.deleteAll();
    }

    @Test
    public void givenWrongPassengerId_whenGetPassenger_thenReturnHttpNotFound() throws Exception {
        // given
        String wrongPassengerId = UUID.randomUUID().toString();

        // when and then
        mockMvc.perform(get(passengersEndpoint + "/" + wrongPassengerId))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenPassengerId_whenGetPassenger_thenReturnHttpOk() throws Exception {
        // given
        Passenger passenger = Passenger.builder()
            .passengerId(UUID.randomUUID())
            .firstName("John")
            .lastName("Smith")
            .phoneNumber(
                PhoneNumber.builder()
                    .countryCode("48")
                    .phoneNumber("123456789")
                    .build()
            )
            .build();
        passengerRepository.save(passenger);

        // when and then
        mockMvc.perform(get(passengersEndpoint + "/" + passenger.getPassengerId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    public void givenCreatePassengerRequest_whenCreatePassenger_thenReturnHttpCreated() throws Exception {
        // given
        CreatePassengerRequest createPassengerRequest = new CreatePassengerRequest(
            "John",
            "Smith",
            "48",
            "123456789"
        );

        // when and then
        mockMvc.perform(post(passengersEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(createPassengerRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenUpdatePassengerRequest_whenUpdatePassenger_thenReturnHttpNoContent() throws Exception {
        // given
        Passenger passenger = Passenger.builder()
            .passengerId(UUID.randomUUID())
            .firstName("John")
            .lastName("Smith")
            .phoneNumber(
                PhoneNumber.builder()
                    .countryCode("48")
                    .phoneNumber("123456789")
                    .build()
            )
            .build();
        passengerRepository.save(passenger);

        // when
        String updatedFirstName = "Jane";
        String updatedLastName = "Doe";
        String updatedCountryCode = "49";
        String updatedPhoneNumber = "987654321";
        CreatePassengerRequest updatePassengerRequest = new CreatePassengerRequest(
            updatedFirstName,
            updatedLastName,
            updatedCountryCode,
            updatedPhoneNumber
        );

        // when and then
        mockMvc.perform(patch(passengersEndpoint + "/" + passenger.getPassengerId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(updatePassengerRequest)))
            .andExpect(status().isNoContent());

        final Passenger updatedPassenger = passengerRepository.findByPassengerId(passenger.getPassengerId()).get();
        assertThat(updatedPassenger.getFirstName()).isEqualTo(updatedFirstName);
        assertThat(updatedPassenger.getLastName()).isEqualTo(updatedLastName);
        assertThat(updatedPassenger.getPhoneNumber().getCountryCode()).isEqualTo(updatedCountryCode);
        assertThat(updatedPassenger.getPhoneNumber().getPhoneNumber()).isEqualTo(updatedPhoneNumber);
    }

    @Test
    public void givenPassengerId_whenDeletePassenger_thenReturnHttpNoContent() throws Exception {
        // given
        Passenger passenger = Passenger.builder()
            .passengerId(UUID.randomUUID())
            .firstName("John")
            .lastName("Smith")
            .phoneNumber(
                PhoneNumber.builder()
                    .countryCode("48")
                    .phoneNumber("123456789")
                    .build()
            )
            .build();
        passengerRepository.save(passenger);

        // when and then
        mockMvc.perform(delete(passengersEndpoint + "/" + passenger.getPassengerId().toString()))
            .andExpect(status().isNoContent());
        assertThat(passengerRepository.findByPassengerId(passenger.getPassengerId())).isEmpty();
    }

    @Test
    public void givenWrongPassengerId_whenDeletePassenger_thenReturnHttpNotFound() throws Exception {
        // given
        String wrongPassengerId = UUID.randomUUID().toString();

        // when and then
        mockMvc.perform(delete(passengersEndpoint + "/" + wrongPassengerId))
            .andExpect(status().isNotFound());
    }
}
