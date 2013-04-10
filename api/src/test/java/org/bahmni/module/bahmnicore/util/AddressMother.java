package org.bahmni.module.bahmnicore.util;

import org.openmrs.module.webservices.rest.SimpleObject;

public class AddressMother {
    public SimpleObject getSimpleObjectForAddress() {
        return new SimpleObject()
                .add("address1", "House No. 23")
                .add("address2", "8th cross")
                .add("address3", "3rd block")
                .add("cityVillage", "Bengaluru")
                .add("countyDistrict", "Bengaluru south")
                .add("stateProvince", "Karnataka");
    }
}
