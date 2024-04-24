package pw.ee.lot.dto.flight;

import java.time.LocalDateTime;

public record FlightSearchCriteria(
    String flightNumber,
    LocalDateTime departureTimeFrom,
    LocalDateTime departureTimeTo,
    Integer availableSeatsFrom,
    String city
) {
}
