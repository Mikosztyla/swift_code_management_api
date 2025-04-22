package remitly.task.swiftcode.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "swifts")
public class Swift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "swift_code", nullable = false, unique = true)
    private String swiftCode;
    private String baseSwiftCode;

    private String address;
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "is_headquarter")
    private boolean isHeadquarter;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Swift(String swiftCode, String address, String bankName, boolean isHeadquarter, Country country) {
        this.swiftCode = swiftCode;
        this.address = address;
        this.bankName = bankName;
        this.isHeadquarter = isHeadquarter;
        this.country = country;
        baseSwiftCode = swiftCode.substring(0, 8);
    }
}
