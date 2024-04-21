package pw.ee.lot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw.ee.lot.domain.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
