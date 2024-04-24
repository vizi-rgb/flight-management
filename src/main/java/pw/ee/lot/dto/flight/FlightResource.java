package pw.ee.lot.dto.flight;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FlightResource(
    String flightNumber,
    LocalDateTime departureTime,
    Integer availableSeats,
    List<String> route
) {
}
