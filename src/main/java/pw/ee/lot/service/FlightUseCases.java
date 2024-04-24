package pw.ee.lot.service;

import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.flight.CreateFlightRequest;
import pw.ee.lot.dto.flight.FlightResource;
import pw.ee.lot.dto.flight.UpdateFlightRequest;

import java.util.UUID;

public interface FlightUseCases {

    Flight createFlight(CreateFlightRequest request);

    void deleteFlight(String flightNumber);

    void updateFlight(String flightNumber, UpdateFlightRequest request);

    FlightResource getFlight(String flightNumber);

    void addPassengerToFlight(String flightNumber, UUID passengerId);

    void removePassengerFromFlight(String flightNumber, UUID passengerId);
}
