package pw.ee.lot.service.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.domain.repository.FlightRepository;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;
import pw.ee.lot.dto.UpdateFlightRequest;
import pw.ee.lot.dto.mapper.FlightMapper;
import pw.ee.lot.service.FlightUseCases;

/**
 * Implementation of the {@link FlightUseCases} interface providing operations related to flights.
 * This service class handles the business logic for creating, updating, deleting, and retrieving flights.
 */
@Service
@Slf4j
@RequiredArgsConstructor
class FlightUseCasesImpl implements FlightUseCases {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    /**
     * Create a new flight
     *
     * @param request containing flight details
     * @return the created flight
     * @throws IllegalArgumentException if a flight with the same number already exists
     */
    @Override
    @Transactional
    public Flight createFlight(CreateFlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            log.error("Flight with number {} already exists", request.flightNumber());
            throw new IllegalArgumentException("Flight with number " + request.flightNumber() + " already exists");
        }

        final var flight = flightMapper.mapCreateFlightRequestToFlight(request);
        log.info("Creating flight {}", flight.getFlightNumber());
        return flightRepository.save(flight);
    }

    /**
     * Delete a flight by its flight number.
     *
     * @param flightNumber the flight number to delete
     * @throws IllegalArgumentException if the flight with the specified number is not found
     */
    @Override
    @Transactional
    public void deleteFlight(String flightNumber) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot delete flight with number {} as it's not found", flightNumber);
                return new IllegalArgumentException("Flight not found");
            });

        log.info("Deleting flight {}", flightNumber);
        flightRepository.delete(flight);
    }

    /**
     * Update a flight with the given flight number and details.
     *
     * @param flightNumber the flight number to update
     * @param request      containing updated flight details
     * @throws IllegalArgumentException if the flight with the specified number is not found
     */
    @Override
    @Transactional
    public void updateFlight(String flightNumber, UpdateFlightRequest request) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot update flight with number {} as it's not found", flightNumber);
                return new IllegalArgumentException("Flight not found");
            });

        log.info("Updating flight {}", flightNumber);
        applyPartialUpdates(flight, request);
        flightRepository.save(flight);
    }

    /**
     * Retrieve a flight resource by its flight number.
     *
     * @param flightNumber the flight number to retrieve
     * @return the flight resource
     * @throws IllegalArgumentException if the flight with the specified number is not found
     */
    @Override
    @Transactional(readOnly = true)
    public FlightResource getFlight(String flightNumber) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot get flight with number {} as it's not found", flightNumber);
                return new IllegalArgumentException("Flight not found");
            });

        return flightMapper.mapFlightToFlightResource(flight);
    }

    private void applyPartialUpdates(Flight flight, UpdateFlightRequest request) {
        if (request.flightNumber() != null) {
            flight.setFlightNumber(request.flightNumber());
        }

        if (request.departureTime() != null) {
            flight.setDepartureTime(request.departureTime());
        }
        if (request.availableSeats() != null) {
            flight.setAvailableSeats(request.availableSeats());
        }
        if (request.route() != null && !request.route().isEmpty()) {
            flight.setRoute(request.route());
        }
    }
}
