package pw.ee.lot.dto.mapper;

import org.springframework.stereotype.Component;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;

@Component
public class FlightMapper {

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
}
