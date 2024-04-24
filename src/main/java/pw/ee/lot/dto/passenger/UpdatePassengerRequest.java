package pw.ee.lot.dto.passenger;

public record UpdatePassengerRequest(
    String firstName,
    String lastName,
    String countryCode,
    String phoneNumber
) {
}
