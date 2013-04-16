package org.bahmni.jss.registration;

import java.io.IOException;

public class AllStates extends AllLookupValues {
    private AllLookupValues allDistricts;

    public AllStates(String csvLocation, String fileName, AllLookupValues allDistricts) throws IOException {
        super(csvLocation, fileName);
        this.allDistricts = allDistricts;
    }

    @Override
    public String getLookUpValue(String key) {
        String stateId = allDistricts.getLookUpValue(key);
        return allDistricts.getLookUpValue(stateId);
    }
}