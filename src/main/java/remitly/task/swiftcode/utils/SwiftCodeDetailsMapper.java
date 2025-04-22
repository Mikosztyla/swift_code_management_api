package remitly.task.swiftcode.utils;

import org.springframework.stereotype.Component;
import remitly.task.swiftcode.dto.BranchSwiftCodeDTO;
import remitly.task.swiftcode.dto.CountrySwiftCodeDTO;
import remitly.task.swiftcode.dto.HeadquarterSwiftCodeDTO;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.model.Swift;

import java.util.List;

@Component
public class SwiftCodeDetailsMapper {

    public BranchSwiftCodeDTO mapToBranchSwiftCodeDTO(Swift swift) {
        return BranchSwiftCodeDTO.builder()
                .swiftCode(swift.getSwiftCode())
                .address(swift.getAddress())
                .bankName(swift.getBankName())
                .countryISO2(swift.getCountry().getIso2())
                .countryName(swift.getCountry().getName())
                .isHeadquarter(swift.isHeadquarter())
                .build();
    }

    public HeadquarterSwiftCodeDTO mapToHeadquarterSwiftCodeDTO(Swift swift, List<Swift> branches) {
        return HeadquarterSwiftCodeDTO.builder()
                .swiftCode(swift.getSwiftCode())
                .address(swift.getAddress())
                .bankName(swift.getBankName())
                .countryISO2(swift.getCountry().getIso2())
                .countryName(swift.getCountry().getName())
                .isHeadquarter(swift.isHeadquarter())
                .branches(mapToSwiftCodeDTOs(branches))
                .build();
    }

    public List<SwiftCodeDTO> mapToSwiftCodeDTOs(List<Swift> swifts) {
        return swifts.stream()
                .map(swift -> SwiftCodeDTO.builder()
                        .swiftCode(swift.getSwiftCode())
                        .address(swift.getAddress())
                        .bankName(swift.getBankName())
                        .countryISO2(swift.getCountry().getIso2())
                        .isHeadquarter(swift.isHeadquarter())
                        .build())
                .toList();
    }

    public CountrySwiftCodeDTO mapToCountrySwiftCodeDTO(Country country, List<Swift> swiftCodes ) {
        return CountrySwiftCodeDTO.builder()
                .countryISO2(country.getIso2())
                .countryName(country.getName())
                .swiftCodes(mapToSwiftCodeDTOs(swiftCodes))
                .build();
    }
}
