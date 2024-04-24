package pw.ee.lot.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;
import pw.ee.lot.dto.passenger.PassengerDetailsResource;
import pw.ee.lot.dto.passenger.UpdatePassengerRequest;
import pw.ee.lot.service.PassengerUseCases;

import java.util.UUID;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    final PassengerUseCases passengerUseCases;

    @GetMapping("/{passengerId}")
    public ResponseEntity<PassengerDetailsResource> getPassenger(@PathVariable UUID passengerId) {
        final var passenger = passengerUseCases.getPassenger(passengerId);
        return ResponseEntity.ok(passenger);
    }

    @PostMapping
    public ResponseEntity<Void> createPassenger(@RequestBody @Valid CreatePassengerRequest request) {
        final var createdPassenger = passengerUseCases.createPassenger(request);

        final var location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{passengerId}")
            .buildAndExpand(createdPassenger.getPassengerId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{passengerId}")
    public ResponseEntity<Void> updatePassenger(@PathVariable UUID passengerId, @RequestBody @Valid UpdatePassengerRequest request) {
        passengerUseCases.updatePassenger(passengerId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{passengerId}")
    public ResponseEntity<Void> deletePassenger(@PathVariable UUID passengerId) {
        passengerUseCases.deletePassenger(passengerId);
        return ResponseEntity.noContent().build();
    }
}
