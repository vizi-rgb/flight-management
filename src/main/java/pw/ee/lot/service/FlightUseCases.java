package pw.ee.lot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.flight.*;

import java.util.UUID;

public interface FlightUseCases {

    Flight createFlight(CreateFlightRequest request);

    void deleteFlight(String flightNumber);

    void updateFlight(String flightNumber, UpdateFlightRequest request);

    FlightDetailsResource getFlight(String flightNumber);

    Page<FlightResource> getFlights(Pageable pageable);

    void addPassengerToFlight(String flightNumber, UUID passengerId);

    void removePassengerFromFlight(String flightNumber, UUID passengerId);

    Page<FlightResource> searchFlights(Pageable pageable, FlightSearchCriteria criteria);
}
