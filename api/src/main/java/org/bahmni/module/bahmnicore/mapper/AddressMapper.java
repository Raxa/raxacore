package org.bahmni.module.bahmnicore.mapper;

import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.bahmni.module.bahmnicore.model.BahmniAddress;

import java.util.List;

public class AddressMapper {
	
	public Patient map(Patient patient, List<BahmniAddress> addresses) {
		for (BahmniAddress address : addresses) {
			PersonAddress personAddress = new PersonAddress();
			if (address.getAddress1() != null) {
				personAddress.setAddress1(address.getAddress1());
			}
			if (address.getAddress2() != null) {
				personAddress.setAddress2(address.getAddress2());
			}
			if (address.getAddress3() != null) {
				personAddress.setAddress3(address.getAddress3());
			}
			if (address.getCityVillage() != null) {
				personAddress.setCityVillage(address.getCityVillage());
			}
			if (address.getCountyDistrict() != null) {
				personAddress.setCountyDistrict(address.getCountyDistrict());
			}
			if (address.getStateProvince() != null) {
				personAddress.setStateProvince(address.getStateProvince());
			}
			personAddress.setPreferred(true);
			patient.addAddress(personAddress);
		}
		return patient;
	}
	
}