package pw.ee.lot.dto.passenger;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PassengerResource(
    UUID passengerId,
    String firstName,
    String lastName,
    String countryCode,
    String phoneNumber
) {
}
