package remitly.task.swiftcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SwiftCodeDTO {
    @JsonProperty("address")
    private String address;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("countryISO2")
    private String countryISO2;

    @JsonProperty("isHeadquarter")
    private boolean isHeadquarter;

    @JsonProperty("swiftCode")
    private String swiftCode;
}
