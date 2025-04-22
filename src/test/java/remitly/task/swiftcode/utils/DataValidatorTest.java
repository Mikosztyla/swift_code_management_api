package remitly.task.swiftcode.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataValidatorTest {
    private static final String VALID_SWIFT_CODE = "ABCDUS33XXX";
    private static final String INVALID_SWIFT_CODE_SHORT = "ABC123";
    private static final String INVALID_SWIFT_CODE_SPECIAL = "ABC@123!XYZ";
    private static final String VALID_ISO2 = "US";
    private static final String INVALID_ISO2_LENGTH = "USA";
    private static final String INVALID_ISO2_LOWER = "us";
    private static final String INVALID_ISO2_SYMBOLS = "U$";
    private static final String EMPTY_CODE = " ";

    private DataValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DataValidator();
    }

    @Test
    void shouldPassValidationForValidSwiftCode() {
        //given //when //then
        assertDoesNotThrow(() -> validator.validateSwiftCode(VALID_SWIFT_CODE));
    }

    @Test
    void shouldThrowForNullSwiftCode() {
        //given //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateSwiftCode(null));
        assertEquals("SwiftCode must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowForEmptySwiftCode() {
        //given //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateSwiftCode(EMPTY_CODE));
        assertEquals("SwiftCode must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowForInvalidSwiftFormat() {
        //given //when //then
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> validator.validateSwiftCode(INVALID_SWIFT_CODE_SHORT));
        assertEquals("Invalid swiftCode format. Expected 11 uppercase letters/numbers", ex1.getMessage());
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> validator.validateSwiftCode(INVALID_SWIFT_CODE_SPECIAL));
        assertEquals("Invalid swiftCode format. Expected 11 uppercase letters/numbers", ex2.getMessage());
    }

    @Test
    void shouldPassValidationForValidCountryISO2() {
        //given //when //then
        assertDoesNotThrow(() -> validator.validateCountryISO2(VALID_ISO2));
    }

    @Test
    void shouldThrowForNullCountryISO2() {
        //given //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateCountryISO2(null));
        assertEquals("Country ISO2 code must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowForEmptyCountryISO2() {
        //given //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateCountryISO2(EMPTY_CODE));
        assertEquals("Country ISO2 code must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowForInvalidCountryISO2Format() {
        //given //when //then
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> validator.validateCountryISO2(INVALID_ISO2_LENGTH));
        assertEquals("Invalid country ISO2 code. Expected 2 uppercase letters", ex1.getMessage());
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> validator.validateCountryISO2(INVALID_ISO2_LOWER));
        assertEquals("Invalid country ISO2 code. Expected 2 uppercase letters", ex2.getMessage());
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> validator.validateCountryISO2(INVALID_ISO2_SYMBOLS));
        assertEquals("Invalid country ISO2 code. Expected 2 uppercase letters", ex3.getMessage());
    }
}