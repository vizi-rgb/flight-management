package pw.ee.lot.dto.passenger;

import lombok.Builder;
import pw.ee.lot.dto.flight.FlightResource;

import java.util.Set;
import java.util.UUID;

@Builder
public record PassengerDetailsResource(
    UUID passengerId,
    String firstName,
    String lastName,
    String countryCode,
    String phoneNumber,
    Set<FlightResource> flights
) {
}
