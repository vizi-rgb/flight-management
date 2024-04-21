package pw.ee.lot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateFlightRequest(
    @NotNull
    @Size(min = 4)
    String flightNumber,

    @NotNull
    LocalDateTime departureTime,

    @NotNull
    @Positive
    Integer availableSeats,

    @NotEmpty
    List<String> route
) {
}
