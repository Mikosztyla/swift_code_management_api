package remitly.task.swiftcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import remitly.task.swiftcode.model.Swift;

import java.util.List;
import java.util.Optional;

public interface SwiftRepository extends JpaRepository<Swift, Long> {
    List<Swift> findAllByBaseSwiftCodeAndIsHeadquarterFalse(String baseSwiftCode);
    Optional<Swift> findBySwiftCode(String swiftCode);
    List<Swift> findAllByCountryIso2IgnoreCase(String iso2);
}
