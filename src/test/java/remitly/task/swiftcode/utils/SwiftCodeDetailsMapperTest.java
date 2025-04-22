package remitly.task.swiftcode.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import remitly.task.swiftcode.dto.BranchSwiftCodeDTO;
import remitly.task.swiftcode.dto.CountrySwiftCodeDTO;
import remitly.task.swiftcode.dto.HeadquarterSwiftCodeDTO;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.model.Swift;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwiftCodeDetailsMapperTest {
    private static final String SWIFT_CODE_1 = "AAISALTRXXX";
    private static final String SWIFT_CODE_2 = "AAISALTRYYY";
    private static final String ADDRESS_1 = "Some Address";
    private static final String ADDRESS_2 = "Another Address";
    private static final String BANK_NAME_1 = "Some Bank";
    private static final String BANK_NAME_2 = "Another Bank";
    private static final String COUNTRY_ISO2 = "AL";
    private static final String COUNTRY_NAME = "Albania";

    @Mock
    private Swift swift;
    @Mock private Country country;
    private List<Swift> swiftList;
    private SwiftCodeDetailsMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        country = mock(Country.class);
        when(country.getIso2()).thenReturn(COUNTRY_ISO2);
        when(country.getName()).thenReturn(COUNTRY_NAME);
        swift = mock(Swift.class);
        when(swift.getSwiftCode()).thenReturn(SWIFT_CODE_1);
        when(swift.getAddress()).thenReturn(ADDRESS_1);
        when(swift.getBankName()).thenReturn(BANK_NAME_1);
        when(swift.getCountry()).thenReturn(country);
        when(swift.isHeadquarter()).thenReturn(false);
        Swift swift1 = mock(Swift.class);
        when(swift1.getSwiftCode()).thenReturn(SWIFT_CODE_2);
        when(swift1.getAddress()).thenReturn(ADDRESS_2);
        when(swift1.getBankName()).thenReturn(BANK_NAME_2);
        when(swift1.getCountry()).thenReturn(country);
        when(swift1.isHeadquarter()).thenReturn(false);
        swiftList = Arrays.asList(swift, swift1);
        mapper = new SwiftCodeDetailsMapper();
    }

    @Test
    void testMapToBranchSwiftCodeDTO() {
        // given
        when(swift.isHeadquarter()).thenReturn(false);
        // when
        BranchSwiftCodeDTO branchDto = mapper.mapToBranchSwiftCodeDTO(swift);
        // then
        assertNotNull(branchDto);
        assertEquals(SWIFT_CODE_1, branchDto.getSwiftCode());
        assertEquals(ADDRESS_1, branchDto.getAddress());
        assertEquals(BANK_NAME_1, branchDto.getBankName());
        assertEquals(COUNTRY_ISO2, branchDto.getCountryISO2());
        assertFalse(branchDto.isHeadquarter());
    }

    @Test
    void testMapToHeadquarterSwiftCodeDTO() {
        // given
        when(swift.isHeadquarter()).thenReturn(true);
        // when
        HeadquarterSwiftCodeDTO headquarterDto = mapper.mapToHeadquarterSwiftCodeDTO(swift, swiftList);
        // then
        assertNotNull(headquarterDto);
        assertEquals(SWIFT_CODE_1, headquarterDto.getSwiftCode());
        assertEquals(ADDRESS_1, headquarterDto.getAddress());
        assertEquals(BANK_NAME_1, headquarterDto.getBankName());
        assertEquals(COUNTRY_ISO2, headquarterDto.getCountryISO2());
        assertTrue(headquarterDto.isHeadquarter());
        assertEquals(2, headquarterDto.getBranches().size());
    }

    @Test
    void testMapToSwiftCodeDTOs() {
        // given
        when(swift.isHeadquarter()).thenReturn(false);
        // when
        List<SwiftCodeDTO> swiftCodeDtos = mapper.mapToSwiftCodeDTOs(swiftList);
        // then
        assertNotNull(swiftCodeDtos);
        assertEquals(2, swiftCodeDtos.size());
        assertEquals(SWIFT_CODE_1, swiftCodeDtos.get(0).getSwiftCode());
        assertEquals(SWIFT_CODE_2, swiftCodeDtos.get(1).getSwiftCode());
    }

    @Test
    void testMapToCountrySwiftCodeDTO() {
        // given
        when(swift.isHeadquarter()).thenReturn(false);
        // when
        CountrySwiftCodeDTO countryDto = mapper.mapToCountrySwiftCodeDTO(country, swiftList);
        // then
        assertNotNull(countryDto);
        assertEquals(COUNTRY_ISO2, countryDto.getCountryISO2());
        assertEquals(COUNTRY_NAME, countryDto.getCountryName());
        assertEquals(2, countryDto.getSwiftCodes().size());
    }
}