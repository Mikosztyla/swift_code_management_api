package remitly.task.swiftcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HeadquarterSwiftCodeDTO {
    @JsonProperty("address")
    private String address;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("countryISO2")
    private String countryISO2;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("isHeadquarter")
    private boolean isHeadquarter;

    @JsonProperty("swiftCode")
    private String swiftCode;

    @JsonProperty("branches")
    private List<SwiftCodeDTO> branches;
}
