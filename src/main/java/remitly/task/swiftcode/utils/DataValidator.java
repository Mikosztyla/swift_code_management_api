package remitly.task.swiftcode.utils;

import org.springframework.stereotype.Component;

@Component
public class DataValidator {

    public void validateSwiftCode(String swiftCode) {
        if (swiftCode == null || swiftCode.trim().isEmpty()) {
            throw new IllegalArgumentException("SwiftCode must not be null or empty");
        }
        if (!swiftCode.matches("^[A-Z0-9]{11}$")) {
            throw new IllegalArgumentException("Invalid swiftCode format. Expected 11 uppercase letters/numbers");
        }
    }

    public void validateCountryISO2(String countryISO2) {
        if (countryISO2 == null || countryISO2.trim().isEmpty()) {
            throw new IllegalArgumentException("Country ISO2 code must not be null or empty");
        }
        if (!countryISO2.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("Invalid country ISO2 code. Expected 2 uppercase letters");
        }
    }
}
