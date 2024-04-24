package pw.ee.lot.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.ee.lot.dto.flight.*;
import pw.ee.lot.service.FlightUseCases;

import java.util.UUID;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightUseCases flightUseCases;

    @PostMapping()
    public ResponseEntity<Void> createFlight(@RequestBody @Valid CreateFlightRequest request) {
        final var createdFlight = flightUseCases.createFlight(request);

        final var location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{flightNumber}")
            .buildAndExpand(createdFlight.getFlightNumber()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{flightNumber}")
    public ResponseEntity<Void> updateFlight(@PathVariable String flightNumber, @RequestBody @Valid UpdateFlightRequest request) {
        flightUseCases.updateFlight(flightNumber, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        flightUseCases.deleteFlight(flightNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightDetailsResource> getFlight(@PathVariable String flightNumber) {
        final var flight = flightUseCases.getFlight(flightNumber);
        return ResponseEntity.ok(flight);
    }

    @GetMapping
    public ResponseEntity<Page<FlightResource>> getFlights(Pageable pageable) {
        final var flights = flightUseCases.getFlights(pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FlightResource>> getFlights(Pageable pageable, FlightSearchCriteria criteria) {
        final var flights = flightUseCases.searchFlights(pageable, criteria);
        return ResponseEntity.ok(flights);
    }


    @PostMapping("/{flightNumber}/{passengerId}")
    public ResponseEntity<Void> addPassengerToFlight(@PathVariable String flightNumber, @PathVariable UUID passengerId) {
        flightUseCases.addPassengerToFlight(flightNumber, passengerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{flightNumber}/{passengerId}")
    public ResponseEntity<Void> removePassengerFromFlight(@PathVariable String flightNumber, @PathVariable UUID passengerId) {
        flightUseCases.removePassengerFromFlight(flightNumber, passengerId);
        return ResponseEntity.noContent().build();
    }

}
