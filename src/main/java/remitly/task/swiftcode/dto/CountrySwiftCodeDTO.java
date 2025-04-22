package remitly.task.swiftcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CountrySwiftCodeDTO {
    @JsonProperty("countryISO2")
    private String countryISO2;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("swiftCodes")
    private List<SwiftCodeDTO> swiftCodes;
}
