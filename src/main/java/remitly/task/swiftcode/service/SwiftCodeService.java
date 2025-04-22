package remitly.task.swiftcode.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import remitly.task.swiftcode.dto.CountrySwiftCodeDTO;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.model.Swift;
import remitly.task.swiftcode.repository.CountryRepository;
import remitly.task.swiftcode.repository.SwiftRepository;
import remitly.task.swiftcode.utils.DataValidator;
import remitly.task.swiftcode.utils.SwiftCodeDetailsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftRepository swiftRepository;
    private final CountryRepository countryRepo;
    private final DataValidator dataValidator;
    private final SwiftCodeDetailsMapper mapper;

    public Object getSwiftCodeDetails(String swiftCode) {
        dataValidator.validateSwiftCode(swiftCode);
        Swift swift = swiftRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new IllegalArgumentException("No records for provided swiftCode"));
        if (!swift.isHeadquarter()) return mapper.mapToBranchSwiftCodeDTO(swift);
        List<Swift> branches = swiftRepository.findAllByBaseSwiftCodeAndIsHeadquarterFalse(swift.getBaseSwiftCode());
        return mapper.mapToHeadquarterSwiftCodeDTO(swift, branches);
    }

    public CountrySwiftCodeDTO getSwiftCodesByCountry(String countryISO2) {
        dataValidator.validateCountryISO2(countryISO2);
        Country country = countryRepo.findByIso2(countryISO2.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("No country found for code: " + countryISO2));
        List<Swift> swiftCodes = swiftRepository.findAllByCountryIso2IgnoreCase(countryISO2);
        return mapper.mapToCountrySwiftCodeDTO(country, swiftCodes);
    }

    public void addSwiftCode(SwiftCodeDTO dto) {
        dataValidator.validateSwiftCode(dto.getSwiftCode());
        dataValidator.validateCountryISO2(dto.getCountryISO2());
        if (swiftRepository.findBySwiftCode(dto.getSwiftCode()).isPresent()) {
            throw new IllegalArgumentException("SWIFT code already exists: " + dto.getSwiftCode());
        }
        Country country = countryRepo.findByIso2(dto.getCountryISO2().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("No country found for code: " + dto.getCountryISO2()));
        Swift swift = new Swift(dto.getSwiftCode(), dto.getAddress(), dto.getBankName(), dto.isHeadquarter(), country);
        swiftRepository.save(swift);
    }

    public void deleteSwiftCode(String swiftCode) throws IllegalArgumentException {
        dataValidator.validateSwiftCode(swiftCode);
        Swift swift = swiftRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new IllegalArgumentException("No SWIFT code found with the provided code: " + swiftCode));
        swiftRepository.delete(swift);
    }
}
