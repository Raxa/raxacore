package org.bahmni.jss.registration;

import org.bahmni.address.sanitiser.AddressSanitiser;
import org.bahmni.address.sanitiser.SanitizerPersonAddress;

public class NonSanitizingSanitizer extends AddressSanitiser {
    public NonSanitizingSanitizer() {
        super(null, null);
    }

    @Override
    public SanitizerPersonAddress sanitiseByVillageAndTehsil(SanitizerPersonAddress personAddress) {
        return personAddress;
    }
}
