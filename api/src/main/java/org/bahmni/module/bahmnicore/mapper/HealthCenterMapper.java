package org.bahmni.module.bahmnicore.mapper;

import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.raxa.module.raxacore.mapper.PatientMapper;
import org.raxa.module.raxacore.model.BahmniPatient;

import java.util.Collection;
import java.util.List;

public class HealthCenterMapper {
	
	public void addHealthCenter(Person person, BahmniPatient bahmniPatient, PatientMapper patientMapper) {
		LocationService locationService = Context.getLocationService();
		List<Location> allLocations = locationService.getAllLocations();
		String center = bahmniPatient.getCenterName();
		
		List<LocationAttributeType> allLocationAttributeTypes = locationService.getAllLocationAttributeTypes();
		LocationAttributeType identifierSourceName = findIdentifierSourceName(allLocationAttributeTypes);
		
		for (Location location : allLocations) {
			Collection<LocationAttribute> activeAttributes = location.getActiveAttributes();
			for (LocationAttribute attribute : activeAttributes) {
				addHealthCenter(person, center, identifierSourceName, location, attribute);
			}
		}
	}
	
	private LocationAttributeType findIdentifierSourceName(List<LocationAttributeType> allLocationAttributeTypes) {
		LocationAttributeType identifierSourceName = null;
		for (LocationAttributeType attributeType : allLocationAttributeTypes) {
			if (attributeType.getName().equals("IdentifierSourceName")) {
				identifierSourceName = attributeType;
				break;
			}
		}
		return identifierSourceName;
	}
	
	private void addHealthCenter(Person person, String center, LocationAttributeType identifierSourceName,
	        Location location, LocationAttribute attribute) {
		if (attribute.getAttributeType().equals(identifierSourceName) && attribute.getValue().toString().equals(center)) {
			PersonAttribute locationAttribute = new PersonAttribute();
			locationAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
			locationAttribute.setValue(location.getId().toString());
			person.getAttributes().add(locationAttribute);
		}
	}
}
