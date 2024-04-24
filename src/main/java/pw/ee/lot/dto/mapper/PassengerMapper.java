package pw.ee.lot.dto.mapper;

import org.springframework.stereotype.Component;
import pw.ee.lot.domain.Passenger;
import pw.ee.lot.domain.PhoneNumber;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;
import pw.ee.lot.dto.passenger.PassengerResource;

import java.util.UUID;

@Component
public class PassengerMapper {

    public PassengerResource mapPassengerToPassengerResource(Passenger passenger) {
        return PassengerResource.builder()
            .passengerId(passenger.getPassengerId())
            .firstName(passenger.getFirstName())
            .lastName(passenger.getLastName())
            .countryCode(passenger.getPhoneNumber().getCountryCode())
            .phoneNumber(passenger.getPhoneNumber().getPhoneNumber())
            .build();
    }

    public Passenger mapCreatePassengerRequestToPassenger(CreatePassengerRequest request) {
        return Passenger.builder()
            .passengerId(UUID.randomUUID())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(
                PhoneNumber.builder()
                    .countryCode(request.countryCode())
                    .phoneNumber(request.phoneNumber())
                    .build()
            )
            .build();

    }
}
