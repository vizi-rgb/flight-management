package pw.ee.lot.dto.flight;

import lombok.Builder;
import pw.ee.lot.dto.passenger.PassengerResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record FlightDetailsResource(
    String flightNumber,
    LocalDateTime departureTime,
    Integer availableSeats,
    List<String> route,
    Set<PassengerResource> passengers
) {
}
