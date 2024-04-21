package pw.ee.lot.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class PhoneNumber {

    @NotNull
    private String countryCode;

    @NotNull
    private String phoneNumber;
}
