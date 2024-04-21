package pw.ee.lot.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateFlightRequest(
    String flightNumber,
    LocalDateTime departureTime,
    List<String> route,

    @PositiveOrZero
    Integer availableSeats
) {
}
