package remitly.task.swiftcode.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remitly.task.swiftcode.dto.BranchSwiftCodeDTO;
import remitly.task.swiftcode.dto.CountrySwiftCodeDTO;
import remitly.task.swiftcode.dto.HeadquarterSwiftCodeDTO;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.model.Swift;
import remitly.task.swiftcode.repository.CountryRepository;
import remitly.task.swiftcode.repository.SwiftRepository;
import remitly.task.swiftcode.utils.DataValidator;
import remitly.task.swiftcode.utils.SwiftCodeDetailsMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SwiftCodeServiceTest {
    private static final String SWIFT_CODE = "TESTUS33TTT";
    private static final String COUNTRY_CODE = "US";
    private static final String BANK_NAME = "Bank";
    private static final String ADDRESS = "Address";
    private static final String SWIFT_CODE_EXISTS_MSG = "SWIFT code already exists: ";
    private static final String SWIFT_CODE_CREATED_MSG = "SWIFT code added successfully";
    private static final String SWIFT_CODE_DELETED_MSG = "SWIFT code deleted successfully";
    private static final String INVALID_SWIFT_CODE_FORMAT_MSG = "Invalid swiftCode format. Expected 11 uppercase letters/numbers";
    private static final String INVALID_ISO2_CODE_FORMAT_MSG = "Invalid country ISO2 code. Expected 2 uppercase letters";
    private static final String SWIFT_CODE_NOT_FOUND_MSG = "No records for provided swiftCode";
    private static final String COUNTRY_NOT_FOUND_MSG = "No country found for code: ";
    private static final String SWIFT_CODE_NOT_FOUND = "No SWIFT code found with the provided code: ";
    private static final String SWIFT_CODE_MISSING_MSG = "SwiftCode must not be null or empty";

    private final SwiftRepository swiftRepository = mock(SwiftRepository.class);
    private final CountryRepository countryRepository = mock(CountryRepository.class);
    private final DataValidator validator = mock(DataValidator.class);
    private final SwiftCodeDetailsMapper mapper = mock(SwiftCodeDetailsMapper.class);
    private SwiftCodeService service;

    private Swift swift;
    private Country country;

    @BeforeEach
    void setUp() {
        service = new SwiftCodeService(swiftRepository, countryRepository, validator, mapper);
        country = new Country("United States", COUNTRY_CODE);
        swift = new Swift(SWIFT_CODE, ADDRESS, BANK_NAME, true, country);
    }

    @Test
    void shouldReturnBranchDtoWhenSwiftIsBranch() {
        //given
        swift = new Swift(SWIFT_CODE, ADDRESS, BANK_NAME, false, country);
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swift));
        BranchSwiftCodeDTO branchDto = BranchSwiftCodeDTO.builder().swiftCode(SWIFT_CODE).isHeadquarter(false).build();
        when(mapper.mapToBranchSwiftCodeDTO(swift)).thenReturn(branchDto);
        //when
        Object result = service.getSwiftCodeDetails(SWIFT_CODE);
        //then
        assertInstanceOf(BranchSwiftCodeDTO.class, result);
        assertEquals(SWIFT_CODE, ((BranchSwiftCodeDTO) result).getSwiftCode());
    }

    @Test
    void shouldReturnHeadquarterDtoWhenSwiftIsHeadquarter() {
        //given
        List<Swift> branches = List.of(new Swift("BRANCH12345", "branch addr", "branch bank", false, country));
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swift));
        when(swiftRepository.findAllByBaseSwiftCodeAndIsHeadquarterFalse(swift.getBaseSwiftCode())).thenReturn(branches);
        HeadquarterSwiftCodeDTO hqDto = HeadquarterSwiftCodeDTO.builder().swiftCode(SWIFT_CODE).branches(List.of()).isHeadquarter(true).build();
        when(mapper.mapToHeadquarterSwiftCodeDTO(swift, branches)).thenReturn(hqDto);
        //when
        Object result = service.getSwiftCodeDetails(SWIFT_CODE);
        //then
        assertInstanceOf(HeadquarterSwiftCodeDTO.class, result);
        assertEquals(SWIFT_CODE, ((HeadquarterSwiftCodeDTO) result).getSwiftCode());
    }

    @Test
    void shouldThrowWhenSwiftCodeNotFound() {
        //given
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());
        //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getSwiftCodeDetails(SWIFT_CODE));
        assertEquals(SWIFT_CODE_NOT_FOUND_MSG, ex.getMessage());
    }

    @Test
    void shouldReturnCountrySwiftCodes() {
        //given
        List<Swift> codes = List.of(swift);
        CountrySwiftCodeDTO expectedDto = mock(CountrySwiftCodeDTO.class);
        when(countryRepository.findByIso2(COUNTRY_CODE)).thenReturn(Optional.of(country));
        when(swiftRepository.findAllByCountryIso2IgnoreCase(COUNTRY_CODE)).thenReturn(codes);
        when(mapper.mapToCountrySwiftCodeDTO(country, codes)).thenReturn(expectedDto);
        //when
        CountrySwiftCodeDTO result = service.getSwiftCodesByCountry(COUNTRY_CODE);
        //then
        assertEquals(expectedDto, result);
    }

    @Test
    void shouldThrowWhenCountryNotFound() {
        //given
        when(countryRepository.findByIso2(COUNTRY_CODE)).thenReturn(Optional.empty());
        //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getSwiftCodesByCountry(COUNTRY_CODE));
        assertEquals(COUNTRY_NOT_FOUND_MSG + COUNTRY_CODE, ex.getMessage());
    }

    @Test
    void shouldReturnEmptyListWhenNoSwiftCodesInCountry() {
        //given
        when(countryRepository.findByIso2(COUNTRY_CODE)).thenReturn(Optional.of(country));
        when(swiftRepository.findAllByCountryIso2IgnoreCase(COUNTRY_CODE)).thenReturn(List.of());
        CountrySwiftCodeDTO expectedDto = mock(CountrySwiftCodeDTO.class);
        when(mapper.mapToCountrySwiftCodeDTO(country, List.of())).thenReturn(expectedDto);
        //when
        CountrySwiftCodeDTO result = service.getSwiftCodesByCountry(COUNTRY_CODE);
        //then
        assertNotNull(result);
        assertTrue(result.getSwiftCodes().isEmpty());
    }

    @Test
    void shouldAddNewSwiftCode() {
        //given
        SwiftCodeDTO dto = new SwiftCodeDTO(ADDRESS, BANK_NAME, COUNTRY_CODE, true, SWIFT_CODE);
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());
        when(countryRepository.findByIso2(COUNTRY_CODE)).thenReturn(Optional.of(country));
        //when
        service.addSwiftCode(dto);
        //then
        verify(swiftRepository).save(any(Swift.class));
    }

    @Test
    void shouldThrowWhenAddingExistingSwiftCode() {
        //given
        SwiftCodeDTO dto = new SwiftCodeDTO(ADDRESS, BANK_NAME, COUNTRY_CODE, true, SWIFT_CODE);
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swift));
        //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addSwiftCode(dto));
        assertEquals("SWIFT code already exists: " + SWIFT_CODE, ex.getMessage());
    }

    @Test
    void shouldThrowWhenCountryMissingOnAdd() {
        //given
        SwiftCodeDTO dto = new SwiftCodeDTO(ADDRESS, BANK_NAME, COUNTRY_CODE, true, SWIFT_CODE);
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());
        when(countryRepository.findByIso2(COUNTRY_CODE)).thenReturn(Optional.empty());
        //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addSwiftCode(dto));
        assertEquals("No country found for code: " + COUNTRY_CODE, ex.getMessage());
    }

    @Test
    void shouldDeleteExistingSwiftCode() {
        //given
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swift));
        //when
        service.deleteSwiftCode(SWIFT_CODE);
        //then
        verify(swiftRepository).delete(swift);
    }

    @Test
    void shouldThrowWhenDeletingUnknownSwiftCode() {
        //given
        when(swiftRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());
        //when //then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.deleteSwiftCode(SWIFT_CODE));
        assertEquals("No SWIFT code found with the provided code: " + SWIFT_CODE, ex.getMessage());
    }
}