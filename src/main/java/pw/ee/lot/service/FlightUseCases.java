package pw.ee.lot.service;

import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;
import pw.ee.lot.dto.UpdateFlightRequest;

public interface FlightUseCases {

    Flight createFlight(CreateFlightRequest request);

    void deleteFlight(String flightNumber);

    void updateFlight(String flightNumber, UpdateFlightRequest request);

    FlightResource getFlight(String flightNumber);
}
