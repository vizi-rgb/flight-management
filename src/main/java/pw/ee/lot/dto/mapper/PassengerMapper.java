package pw.ee.lot.dto.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pw.ee.lot.domain.Passenger;
import pw.ee.lot.domain.PhoneNumber;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;
import pw.ee.lot.dto.passenger.PassengerDetailsResource;
import pw.ee.lot.dto.passenger.PassengerResource;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PassengerMapper {

    private final FlightMapper flightMapper;

    @Autowired
    public PassengerMapper(@Lazy FlightMapper flightMapper) {
        this.flightMapper = flightMapper;
    }


    public PassengerDetailsResource mapPassengerToPassengerDetailsResource(Passenger passenger) {
        return PassengerDetailsResource.builder()
            .passengerId(passenger.getPassengerId())
            .firstName(passenger.getFirstName())
            .lastName(passenger.getLastName())
            .countryCode(passenger.getPhoneNumber().getCountryCode())
            .phoneNumber(passenger.getPhoneNumber().getPhoneNumber())
            .flights(
                passenger.getFlights()
                    .stream()
                    .map(flightMapper::mapFlightToFlightResource)
                    .collect(Collectors.toSet())
            )
            .build();
    }

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
