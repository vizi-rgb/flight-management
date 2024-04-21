package pw.ee.lot.service;

import pw.ee.lot.domain.Flight;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;

public interface FlightUseCases {

    Flight createFlight(CreateFlightRequest request);

    void deleteFlight();

    void updateFlight();

    FlightResource getFlight(String flightNumber);
}
