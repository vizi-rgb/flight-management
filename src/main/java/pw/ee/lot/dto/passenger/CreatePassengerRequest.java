package pw.ee.lot.dto.passenger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePassengerRequest(
    @NotNull
    @Size(min = 2)
    String firstName,

    @NotNull
    @Size(min = 2)
    String lastName,

    @NotNull
    String countryCode,

    @NotNull
    String phoneNumber
) {
}
