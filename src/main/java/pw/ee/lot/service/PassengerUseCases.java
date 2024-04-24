package pw.ee.lot.service;

import pw.ee.lot.domain.Passenger;
import pw.ee.lot.dto.passenger.CreatePassengerRequest;
import pw.ee.lot.dto.passenger.PassengerDetailsResource;
import pw.ee.lot.dto.passenger.UpdatePassengerRequest;

import java.util.UUID;

public interface PassengerUseCases {

    Passenger createPassenger(CreatePassengerRequest request);

    void deletePassenger(UUID passengerId);

    void updatePassenger(UUID passengerId, UpdatePassengerRequest request);

    PassengerDetailsResource getPassenger(UUID passengerId);

}
