package pw.ee.lot.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.ee.lot.dto.CreateFlightRequest;
import pw.ee.lot.dto.FlightResource;
import pw.ee.lot.service.FlightUseCases;

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

    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightResource> getFlight(@PathVariable String flightNumber) {
        final var flight = flightUseCases.getFlight(flightNumber);
        return ResponseEntity.ok(flight);
    }

}
