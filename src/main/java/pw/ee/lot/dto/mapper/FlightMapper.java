package pw.ee.lot.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.flight.CreateFlightRequest;
import pw.ee.lot.dto.flight.FlightDetailsResource;
import pw.ee.lot.dto.flight.FlightResource;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlightMapper {

    private final PassengerMapper passengerMapper;

    public Flight mapCreateFlightRequestToFlight(CreateFlightRequest request) {
        return Flight.builder()
            .flightNumber(request.flightNumber())
            .departureTime(request.departureTime())
            .availableSeats(request.availableSeats())
            .route(request.route())
            .build();
    }

    public FlightResource mapFlightToFlightResource(Flight flight) {
        return FlightResource.builder()
            .flightNumber(flight.getFlightNumber())
            .departureTime(flight.getDepartureTime())
            .availableSeats(flight.getAvailableSeats())
            .route(flight.getRoute())
            .build();
    }

    public FlightDetailsResource mapFlightToFlightDetailsResource(Flight flight) {
        return FlightDetailsResource.builder()
            .flightNumber(flight.getFlightNumber())
            .departureTime(flight.getDepartureTime())
            .availableSeats(flight.getAvailableSeats())
            .route(flight.getRoute())
            .passengers(
                flight.getPassengers()
                    .stream()
                    .map(passengerMapper::mapPassengerToPassengerResource)
                    .collect(Collectors.toSet())
            )
            .build();
    }
}
