package pw.ee.lot.service.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.domain.repository.FlightRepository;
import pw.ee.lot.domain.repository.PassengerRepository;
import pw.ee.lot.dto.flight.*;
import pw.ee.lot.dto.mapper.FlightMapper;
import pw.ee.lot.service.FlightUseCases;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Implementation of the {@link FlightUseCases} interface providing operations related to flights.
 * This service class handles the business logic for creating, updating, deleting, and retrieving flights.
 */
@Service
@Slf4j
@RequiredArgsConstructor
class FlightUseCasesImpl implements FlightUseCases {

    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
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
     * @throws NoSuchElementException if the flight with the specified number is not found
     */
    @Override
    @Transactional
    public void deleteFlight(String flightNumber) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot delete flight with number {} as it's not found", flightNumber);
                return new NoSuchElementException("Flight not found");
            });

        log.info("Deleting flight {}", flightNumber);
        flightRepository.delete(flight);
    }

    /**
     * Update a flight with the given flight number and details.
     *
     * @param flightNumber the flight number to update
     * @param request      containing updated flight details
     * @throws NoSuchElementException if the flight with the specified number is not found
     */
    @Override
    @Transactional
    public void updateFlight(String flightNumber, UpdateFlightRequest request) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot update flight with number {} as it's not found", flightNumber);
                return new NoSuchElementException("Flight not found");
            });

        log.info("Updating flight {}", flightNumber);
        applyPartialUpdates(flight, request);
        flightRepository.save(flight);
    }

    /**
     * Retrieve a flight details resource by its flight number.
     *
     * @param flightNumber the flight number to retrieve
     * @return the flight resource
     * @throws NoSuchElementException if the flight with the specified number is not found
     */
    @Override
    @Transactional(readOnly = true)
    public FlightDetailsResource getFlight(String flightNumber) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot get flight with number {} as it's not found", flightNumber);
                return new NoSuchElementException("Flight not found");
            });

        return flightMapper.mapFlightToFlightDetailsResource(flight);
    }

    /**
     * Retrieve a page of flight resources.
     *
     * @param pageable the page request
     * @return a page of flight resources
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FlightResource> getFlights(Pageable pageable) {
        return flightRepository.findAll(pageable)
            .map(flightMapper::mapFlightToFlightResource);
    }

    /**
     * Search for flights based on the given search criteria.
     *
     * @param pageable the page request
     * @param criteria the search criteria
     * @return a page of flight resources
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FlightResource> searchFlights(Pageable pageable, FlightSearchCriteria criteria) {
        Specification<Flight> spec = Specification.where(null);

        if (criteria.flightNumber() != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("flightNumber"), criteria.flightNumber() + "%")));
        }

        if (criteria.departureTimeFrom() != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("departureTime"), criteria.departureTimeFrom())));
        }

        if (criteria.departureTimeTo() != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("departureTime"), criteria.departureTimeTo())));
        }

        if (criteria.availableSeatsFrom() != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("availableSeats"), criteria.availableSeatsFrom())));
        }

        if (criteria.city() != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.isMember(criteria.city(), root.get("route"))));
        }

        return flightRepository.findAll(spec, pageable)
            .map(flightMapper::mapFlightToFlightResource);
    }

    /**
     * Add a passenger to a flight by their passenger ID.
     *
     * @param flightNumber the flight number to add the passenger to
     * @param passengerId  the passenger ID to add to the flight
     * @throws NoSuchElementException   if the flight with the specified number is not found
     * @throws NoSuchElementException   if the passenger with the specified ID is not found
     * @throws IllegalArgumentException if the passenger is already on the flight
     * @throws IllegalArgumentException if the flight is full
     */
    @Override
    @Transactional
    public void addPassengerToFlight(String flightNumber, UUID passengerId) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot add passenger to flight with number {} as it's not found", flightNumber);
                return new NoSuchElementException("Flight not found");
            });

        final var passenger = passengerRepository.findByPassengerId(passengerId)
            .orElseThrow(() -> {
                log.error("Cannot add passenger with id {} to flight {} as passenger is not found", passengerId, flightNumber);
                return new NoSuchElementException("Passenger not found");
            });

        if (flight.getPassengers().contains(passenger)) {
            log.error("Passenger with id {} is already on flight {}", passengerId, flightNumber);
            throw new IllegalArgumentException("Passenger is already on the flight");
        }

        var availableSeats = flight.getAvailableSeats();
        if (availableSeats <= 0) {
            log.error("Flight {} is full, cannot add passenger with id {}", flightNumber, passengerId);
            throw new IllegalArgumentException("Flight is full");
        }

        log.info("Adding passenger {} to flight {}", passengerId, flightNumber);
        flight.getPassengers().add(passenger);
        flight.setAvailableSeats(availableSeats - 1);
        flightRepository.save(flight);
    }

    /**
     * Remove a passenger from a flight by their passenger ID.
     *
     * @param flightNumber the flight number to remove the passenger from
     * @param passengerId  the passenger ID to remove from the flight
     * @throws NoSuchElementException   if the flight with the specified number is not found
     * @throws NoSuchElementException   if the passenger with the specified ID is not found
     * @throws IllegalArgumentException if the passenger is not on the flight
     */
    @Override
    @Transactional
    public void removePassengerFromFlight(String flightNumber, UUID passengerId) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Cannot remove passenger from flight with number {} as it's not found", flightNumber);
                return new NoSuchElementException("Flight not found");
            });

        final var passenger = passengerRepository.findByPassengerId(passengerId)
            .orElseThrow(() -> {
                log.error("Cannot remove passenger with id {} from flight {} as passenger is not found", passengerId, flightNumber);
                return new NoSuchElementException("Passenger not found");
            });

        if (!flight.getPassengers().contains(passenger)) {
            log.error("Passenger with id {} is not on flight {}", passengerId, flightNumber);
            throw new IllegalArgumentException("Passenger is not on the flight");
        }

        var availableSeats = flight.getAvailableSeats();

        log.info("Removing passenger {} from flight {}", passengerId, flightNumber);
        flight.getPassengers().remove(passenger);
        flight.setAvailableSeats(availableSeats + 1);
        flightRepository.save(flight);
    }

    private void applyPartialUpdates(Flight flight, UpdateFlightRequest request) {
        if (request.flightNumber() != null) {
            if (request.flightNumber().length() < 4) {
                log.error("Flight number {} is too short", request.flightNumber());
                throw new IllegalArgumentException("Flight number is too short");
            }

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
