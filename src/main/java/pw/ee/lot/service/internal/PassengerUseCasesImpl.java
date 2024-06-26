package pw.ee.lot.service.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.ee.lot.domain.Passenger;
import pw.ee.lot.domain.repository.PassengerRepository;
import pw.ee.lot.dto.mapper.PassengerMapper;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;
import pw.ee.lot.dto.passenger.PassengerDetailsResource;
import pw.ee.lot.dto.passenger.UpdatePassengerRequest;
import pw.ee.lot.service.PassengerUseCases;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class PassengerUseCasesImpl implements PassengerUseCases {

    final PassengerRepository passengerRepository;
    final PassengerMapper passengerMapper;

    /**
     * Creates a new passenger.
     *
     * @param request the request to create a passenger
     * @return the created passenger
     */
    @Override
    @Transactional
    public Passenger createPassenger(CreatePassengerRequest request) {
        final var passenger = passengerMapper.mapCreatePassengerRequestToPassenger(request);
        return passengerRepository.save(passenger);
    }

    /**
     * Deletes a passenger.
     *
     * @param passengerId the passenger id to be deleted
     */
    @Override
    @Transactional
    public void deletePassenger(UUID passengerId) {
        final var passenger = passengerRepository.findByPassengerId(passengerId)
            .orElseThrow(() -> {
                log.error("Cannot delete passenger {} as it's not found", passengerId);
                return new NoSuchElementException("Passenger not found");
            });

        if (!passenger.getFlights().isEmpty()) {
            log.error("Cannot delete passenger {} as it's assigned to flights", passengerId);
            throw new IllegalArgumentException("Passenger is assigned to flights");
        }

        passengerRepository.delete(passenger);
    }

    /**
     * Updates a passenger.
     *
     * @param passengerId the passenger id to be updated
     * @param request     the request to update a passenger
     */
    @Override
    @Transactional
    public void updatePassenger(UUID passengerId, UpdatePassengerRequest request) {
        final var passenger = passengerRepository.findByPassengerId(passengerId)
            .orElseThrow(() -> {
                log.error("Cannot update passenger {} as it's not found", passengerId);
                return new NoSuchElementException("Passenger not found");
            });

        log.info("Updating passenger {}", passengerId);
        applyPartialUpdates(passenger, request);
        passengerRepository.save(passenger);
    }

    /**
     * Gets a passenger.
     *
     * @param passengerId the passenger id to be retrieved
     * @return the passenger
     */
    @Override
    @Transactional(readOnly = true)
    public PassengerDetailsResource getPassenger(UUID passengerId) {
        final var passenger = passengerRepository.findByPassengerId(passengerId)
            .orElseThrow(() -> {
                log.error("Cannot get passenger {} as it's not found", passengerId);
                return new NoSuchElementException("Passenger not found");
            });

        return passengerMapper.mapPassengerToPassengerDetailsResource(passenger);
    }

    private void applyPartialUpdates(Passenger passenger, UpdatePassengerRequest request) {
        if (request.firstName() != null) {
            if (request.firstName().length() < 2) {
                throw new IllegalArgumentException("First name must be at least 2 characters long");
            }

            passenger.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            if (request.lastName().length() < 2) {
                throw new IllegalArgumentException("Last name must be at least 2 characters long");
            }

            passenger.setLastName(request.lastName());
        }

        if (request.countryCode() != null) {
            passenger.getPhoneNumber().setCountryCode(request.countryCode());
        }

        if (request.phoneNumber() != null) {
            passenger.getPhoneNumber().setPhoneNumber(request.phoneNumber());
        }
    }
}
