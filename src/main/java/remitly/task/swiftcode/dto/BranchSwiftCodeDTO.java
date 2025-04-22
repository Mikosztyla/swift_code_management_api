package remitly.task.swiftcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchSwiftCodeDTO {
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
}
