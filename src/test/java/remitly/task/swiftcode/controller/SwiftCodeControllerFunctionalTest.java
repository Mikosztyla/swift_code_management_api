package remitly.task.swiftcode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.model.Swift;
import remitly.task.swiftcode.repository.CountryRepository;
import remitly.task.swiftcode.repository.SwiftRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SwiftCodeControllerFunctionalTest {
    private static final String SWIFT_CODE_URL = "/v1/swift-codes";
    private static final String COUNTRY_SWIFT_CODE_URL = "/v1/swift-codes/country/";
    private static final String BANK_NAME = "Bank name";
    private static final String ADDRESS_1 = "Taunusanlage 12";
    private static final String ADDRESS_2 = "Test address 99";
    private static final String VALID_SWIFT_CODE_1 = "DEUTDEFF500";
    private static final String VALID_SWIFT_CODE_2 = "DEUTDEFF501";
    private static final String VALID_SWIFT_CODE_3 = "DEUTDEFFXXX";
    private static final String VALID_SWIFT_CODE_4 = "EXISTUS1XXX";
    private static final String INVALID_SWIFT_CODE = "invalidSWIFT";
    private static final String NONEXISTENT_SWIFT_CODE = "NONEXISTENT";
    private static final String EMPTY_SWIFT_CODE = " ";
    private static final String COUNTRY_ISO_DE = "DE";
    private static final String COUNTRY_ISO_US = "US";
    private static final String COUNTRY_ISO_XY = "XY";
    private static final String COUNTRY_GERMANY = "Germany";
    private static final String COUNTRY_US = "United States";
    private static final String INVALID_COUNTRY_ISO = "--";
    private static final String NEW_SWIFT_CODE = "TESTUS12345";
    private static final String SWIFT_CODE_EXISTS_MSG = "SWIFT code already exists: ";
    private static final String SWIFT_CODE_CREATED_MSG = "SWIFT code added successfully";
    private static final String SWIFT_CODE_DELETED_MSG = "SWIFT code deleted successfully";
    private static final String INVALID_SWIFT_CODE_FORMAT_MSG = "Invalid swiftCode format. Expected 11 uppercase letters/numbers";
    private static final String INVALID_ISO2_CODE_FORMAT_MSG = "Invalid country ISO2 code. Expected 2 uppercase letters";
    private static final String SWIFT_CODE_NOT_FOUND_MSG = "No records for provided swiftCode";
    private static final String COUNTRY_NOT_FOUND_MSG = "No country found for code: ";
    private static final String SWIFT_CODE_NOT_FOUND = "No SWIFT code found with the provided code: ";
    private static final String SWIFT_CODE_MISSING_MSG = "SwiftCode must not be null or empty";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SwiftRepository swiftRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Country germany = new Country(COUNTRY_GERMANY, COUNTRY_ISO_DE);
        Country us = new Country(COUNTRY_US, COUNTRY_ISO_US);
        countryRepository.saveAll(List.of(germany, us));
        swiftRepository.saveAll(List.of(
                new Swift(VALID_SWIFT_CODE_1, ADDRESS_1, BANK_NAME, true, germany),
                new Swift(VALID_SWIFT_CODE_2, ADDRESS_2, BANK_NAME, true, germany),
                new Swift(VALID_SWIFT_CODE_3, ADDRESS_1, BANK_NAME, false, us),
                new Swift(VALID_SWIFT_CODE_4, ADDRESS_2, BANK_NAME, false, us)
        ));
    }

    @AfterEach
    void tearDown() {
        swiftRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void shouldReturnSwiftDetailsForValidSwiftCodeHeadquarter() throws Exception {
        //when //then
        mockMvc.perform(get(SWIFT_CODE_URL + "/" + VALID_SWIFT_CODE_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value(VALID_SWIFT_CODE_1))
                .andExpect(jsonPath("$.bankName").value(BANK_NAME))
                .andExpect(jsonPath("$.address").value(ADDRESS_1))
                .andExpect(jsonPath("$.countryISO2").value(COUNTRY_ISO_DE))
                .andExpect(jsonPath("$.countryName").value(COUNTRY_GERMANY))
                .andExpect(jsonPath("$.isHeadquarter").value(true))
                .andExpect(jsonPath("$.branches.length()").value(1))
                .andExpect(jsonPath("$.branches[0].address").value(ADDRESS_1))
                .andExpect(jsonPath("$.branches[0].bankName").value(BANK_NAME))
                .andExpect(jsonPath("$.branches[0].countryISO2").value(COUNTRY_ISO_US))
                .andExpect(jsonPath("$.branches[0].isHeadquarter").value(false))
                .andExpect(jsonPath("$.branches[0].swiftCode").value(VALID_SWIFT_CODE_3));
    }

    @Test
    void shouldReturnSwiftDetailsForValidSwiftCodeBranch() throws Exception {
        //when //then
        mockMvc.perform(get(SWIFT_CODE_URL + "/" + VALID_SWIFT_CODE_4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value(VALID_SWIFT_CODE_4))
                .andExpect(jsonPath("$.bankName").value(BANK_NAME))
                .andExpect(jsonPath("$.address").value(ADDRESS_2))
                .andExpect(jsonPath("$.countryISO2").value(COUNTRY_ISO_US))
                .andExpect(jsonPath("$.countryName").value(COUNTRY_US))
                .andExpect(jsonPath("$.isHeadquarter").value(false));
    }

    @Test
    void shouldReturn400WhenGettingInvalidSwiftCodeFormat() throws Exception {
        //when //then
        mockMvc.perform(get(SWIFT_CODE_URL + "/" + INVALID_SWIFT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(INVALID_SWIFT_CODE_FORMAT_MSG));
    }

    @Test
    void shouldReturn400WhenSwiftCodeDoesNotExist() throws Exception {
        //when //then
        mockMvc.perform(get(SWIFT_CODE_URL + "/" + NONEXISTENT_SWIFT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(SWIFT_CODE_NOT_FOUND_MSG));
    }

    @Test
    void shouldReturn400WhenSwiftCodeIsNullOrEmpty() throws Exception {
        //when //then
        mockMvc.perform(get(SWIFT_CODE_URL + "/" + EMPTY_SWIFT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(SWIFT_CODE_MISSING_MSG));
    }

    @Test
    void shouldReturnAllSwiftCodesForCountry() throws Exception {
        //when //then
        mockMvc.perform(get(COUNTRY_SWIFT_CODE_URL + COUNTRY_ISO_DE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value(COUNTRY_ISO_DE))
                .andExpect(jsonPath("$.countryName").value(COUNTRY_GERMANY))
                .andExpect(jsonPath("$.swiftCodes.length()").value(2))
                .andExpect(jsonPath("$.swiftCodes[0].address").value(ADDRESS_1))
                .andExpect(jsonPath("$.swiftCodes[0].bankName").value(BANK_NAME))
                .andExpect(jsonPath("$.swiftCodes[0].countryISO2").value(COUNTRY_ISO_DE))
                .andExpect(jsonPath("$.swiftCodes[0].isHeadquarter").value(true))
                .andExpect(jsonPath("$.swiftCodes[0].swiftCode").value(VALID_SWIFT_CODE_1))
                .andExpect(jsonPath("$.swiftCodes[1].address").value(ADDRESS_2))
                .andExpect(jsonPath("$.swiftCodes[1].bankName").value(BANK_NAME))
                .andExpect(jsonPath("$.swiftCodes[1].countryISO2").value(COUNTRY_ISO_DE))
                .andExpect(jsonPath("$.swiftCodes[1].isHeadquarter").value(true))
                .andExpect(jsonPath("$.swiftCodes[1].swiftCode").value(VALID_SWIFT_CODE_2));
    }

    @Test
    void shouldReturn400WhenCountryNotFound() throws Exception {
        //when //then
        mockMvc.perform(get(COUNTRY_SWIFT_CODE_URL + COUNTRY_ISO_XY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(COUNTRY_NOT_FOUND_MSG + COUNTRY_ISO_XY));
    }

    @Test
    void shouldReturn400WhenCountryISOInvalidFormat() throws Exception {
        //when //then
        mockMvc.perform(get(COUNTRY_SWIFT_CODE_URL + INVALID_COUNTRY_ISO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(INVALID_ISO2_CODE_FORMAT_MSG));
    }

    @Test
    void shouldCreateNewSwiftCode() throws Exception {
        //given
        SwiftCodeDTO newSwiftCode = new SwiftCodeDTO(ADDRESS_2, BANK_NAME, COUNTRY_ISO_US, false, NEW_SWIFT_CODE);
        //when //then
        mockMvc.perform(post(SWIFT_CODE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSwiftCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SWIFT_CODE_CREATED_MSG));
        assertTrue(swiftRepository.findBySwiftCode(NEW_SWIFT_CODE).isPresent());
    }

    @Test
    void shouldReturn400WhenSwiftCodeAlreadyExists() throws Exception {
        //given
        SwiftCodeDTO existingSwiftCode = new SwiftCodeDTO(ADDRESS_1, BANK_NAME, COUNTRY_ISO_US, false, VALID_SWIFT_CODE_1);
        //when //then
        mockMvc.perform(post(SWIFT_CODE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingSwiftCode)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(SWIFT_CODE_EXISTS_MSG + VALID_SWIFT_CODE_1));
    }

    @Test
    void shouldReturn400WhenPostingInvalidSwiftCodeFormat() throws Exception {
        //given
        SwiftCodeDTO existingSwiftCode = new SwiftCodeDTO(ADDRESS_1, BANK_NAME, COUNTRY_ISO_US, false, INVALID_SWIFT_CODE);
        //when //then
        mockMvc.perform(post(SWIFT_CODE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingSwiftCode)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_SWIFT_CODE_FORMAT_MSG));
    }

    @Test
    void shouldReturn400WhenPostingInvalidISO2CodeFormat() throws Exception {
        //given
        SwiftCodeDTO existingSwiftCode = new SwiftCodeDTO(ADDRESS_1, BANK_NAME, INVALID_COUNTRY_ISO, false, NEW_SWIFT_CODE);
        //when //then
        mockMvc.perform(post(SWIFT_CODE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingSwiftCode)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_ISO2_CODE_FORMAT_MSG));
    }

    @Test
    void shouldReturn400WhenPostingNonExistentCountry() throws Exception {
        //given
        SwiftCodeDTO existingSwiftCode = new SwiftCodeDTO(ADDRESS_1, BANK_NAME, COUNTRY_ISO_XY, false, NEW_SWIFT_CODE);
        //when //then
        mockMvc.perform(post(SWIFT_CODE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingSwiftCode)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(COUNTRY_NOT_FOUND_MSG + COUNTRY_ISO_XY));
    }

    @Test
    void shouldDeleteSwiftCode() throws Exception {
        //when //then
        mockMvc.perform(delete(SWIFT_CODE_URL + "/" + VALID_SWIFT_CODE_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SWIFT_CODE_DELETED_MSG));
        assertFalse(swiftRepository.findBySwiftCode(VALID_SWIFT_CODE_1).isPresent());
    }

    @Test
    void shouldReturn400WhenDeletingNonExistentSwiftCode() throws Exception {
        //when //then
        mockMvc.perform(delete(SWIFT_CODE_URL + "/" + NONEXISTENT_SWIFT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(SWIFT_CODE_NOT_FOUND + NONEXISTENT_SWIFT_CODE));
    }

    @Test
    void shouldReturn400WhenDeletingInvalidSwiftCodeFormat() throws Exception {
        //when //then
        mockMvc.perform(delete(SWIFT_CODE_URL + "/" + INVALID_SWIFT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_SWIFT_CODE_FORMAT_MSG));
    }
}