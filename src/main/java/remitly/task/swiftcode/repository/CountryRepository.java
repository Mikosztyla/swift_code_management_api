package remitly.task.swiftcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import remitly.task.swiftcode.model.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByIso2(String isoCode);
}
