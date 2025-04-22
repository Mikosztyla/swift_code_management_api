package remitly.task.swiftcode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import remitly.task.swiftcode.model.Swift;
import remitly.task.swiftcode.model.Country;
import remitly.task.swiftcode.repository.SwiftRepository;
import remitly.task.swiftcode.repository.CountryRepository;

import java.io.InputStream;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {
    private final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);
    private final CountryRepository countryRepo;
    private final SwiftRepository branchRepo;

    @PostConstruct
    public void loadDataFromExcel() {
        if (branchRepo.count() > 0 && countryRepo.count() > 0) {
            LOGGER.info("Data already initialized — skipping initialization.");
            return;
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.xlsx")) {
            if (inputStream == null) throw new RuntimeException("Excel file not found");
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String iso2 = row.getCell(0).getStringCellValue();
                String swiftCode = row.getCell(1).getStringCellValue();
                String bankName = row.getCell(3).getStringCellValue();
                String address = row.getCell(4).getStringCellValue();
                String countryName = row.getCell(6).getStringCellValue();
                boolean isHeadquarter = !swiftCode.endsWith("XXX");
                Country country = countryRepo.findByIso2(iso2)
                        .orElseGet(() -> countryRepo.save(new Country(countryName, iso2)));
                Swift swift = new Swift(swiftCode, address, bankName, isHeadquarter, country);
                branchRepo.save(swift);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("Initialized {} branches and {} countries", branchRepo.count(), countryRepo.count());
        }
    }
}
