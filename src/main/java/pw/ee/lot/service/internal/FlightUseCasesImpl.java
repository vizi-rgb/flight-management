package pw.ee.lot.service.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pw.ee.lot.domain.Flight;
import pw.ee.lot.domain.repository.FlightRepository;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;
import pw.ee.lot.dto.mapper.FlightMapper;
import pw.ee.lot.service.FlightUseCases;

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
    public Flight createFlight(CreateFlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            log.error("Flight with number {} already exists", request.flightNumber());
            throw new IllegalArgumentException("Flight with number " + request.flightNumber() + " already exists");
        }

        final var flight = flightMapper.mapCreateFlightRequestToFlight(request);
        log.info("Creating flight {}", flight.getFlightNumber());
        return flightRepository.save(flight);
    }

    @Override
    public void deleteFlight() {

    }

    @Override
    public void updateFlight() {

    }

    @Override
    public FlightResource getFlight(String flightNumber) {
        final var flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> {
                log.error("Flight with number {} not found", flightNumber);
                return new IllegalArgumentException("Flight not found");
            });

        return flightMapper.mapFlightToFlightResource(flight);
    }
}
