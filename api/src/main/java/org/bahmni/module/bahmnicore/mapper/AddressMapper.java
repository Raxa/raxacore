package org.bahmni.module.bahmnicore.mapper;

import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.bahmni.module.bahmnicore.model.BahmniAddress;

import java.util.List;
import java.util.Set;

public class AddressMapper {

    public Patient map(Patient patient, List<BahmniAddress> addresses) {
        PersonAddress personAddress = getNonVoidedAddress(patient);

        if (personAddress == null) {
            personAddress = new PersonAddress();
            populateAddress(personAddress, addresses);
            patient.addAddress(personAddress);
        } else {
            populateAddress(personAddress, addresses);
        }

        return patient;
    }

    private PersonAddress getNonVoidedAddress(Patient patient) {
        PersonAddress personAddress = null;
        Set<PersonAddress> patientAddresses = patient.getAddresses();
        for (PersonAddress address : patientAddresses) {
            if (!address.isVoided())  personAddress = address;
        }
        return personAddress;
    }

    private void populateAddress(PersonAddress personAddress1, List<BahmniAddress> addresses) {
        PersonAddress personAddress = personAddress1;

        for (BahmniAddress address : addresses) {
            personAddress.setAddress1(address.getAddress1());
            personAddress.setAddress2(address.getAddress2());
            personAddress.setAddress3(address.getAddress3());
            personAddress.setCityVillage(address.getCityVillage());
            personAddress.setCountyDistrict(address.getCountyDistrict());
            personAddress.setStateProvince(address.getStateProvince());
            personAddress.setPreferred(true);
        }
    }
}