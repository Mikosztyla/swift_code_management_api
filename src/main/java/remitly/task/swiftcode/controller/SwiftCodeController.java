package remitly.task.swiftcode.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import remitly.task.swiftcode.dto.SwiftCodeDTO;
import remitly.task.swiftcode.service.SwiftCodeService;

import java.util.Map;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwiftCodeController.class);
    private final SwiftCodeService swiftCodeService;

    @Autowired
    public SwiftCodeController(SwiftCodeService swiftCodeService) {
        this.swiftCodeService = swiftCodeService;
    }

    @GetMapping("/{swiftCode}")
    public ResponseEntity<Object> getBySwiftCode(@PathVariable String swiftCode) {
        LOGGER.info("Getting record for SWIFT code: {}", swiftCode);
        try {
            return ResponseEntity.ok(swiftCodeService.getSwiftCodeDetails(swiftCode));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<Object> getByCountry(@PathVariable String countryISO2) {
        LOGGER.info("Getting all SWIFT codes for country: {}", countryISO2);
        try {
            return ResponseEntity.ok(swiftCodeService.getSwiftCodesByCountry(countryISO2));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> addSwiftCode(@RequestBody SwiftCodeDTO requestDTO) {
        LOGGER.info("Adding SWIFT code: {}", requestDTO.getSwiftCode());
        try {
            swiftCodeService.addSwiftCode(requestDTO);
            return ResponseEntity.ok().body(Map.of("message", "SWIFT code added successfully"));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error adding swift code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<Object> deleteSwiftCode(@PathVariable String swiftCode) {
        LOGGER.info("Deleting SWIFT code: {}", swiftCode);
        try {
            swiftCodeService.deleteSwiftCode(swiftCode);
            return ResponseEntity.ok().body(Map.of("message", "SWIFT code deleted successfully"));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error deleting swift code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
