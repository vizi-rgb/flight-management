package pw.ee.lot.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PhoneNumber {

    @NotNull
    private String countryCode;

    @NotNull
    private String phoneNumber;
}
