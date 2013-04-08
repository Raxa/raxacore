package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPersonAttribute;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import java.util.List;

public class PersonAttributeMapper {
	
	private PersonService personService;
	
	public Patient map(Patient patient, List<BahmniPersonAttribute> attributes) {
		for (BahmniPersonAttribute attribute : attributes) {
			if (attribute.getPersonAttributeUuid() == null || attribute.getValue() == null)
				continue;
			
			PersonAttribute personAttribute = new PersonAttribute();
			personAttribute.setAttributeType(getPersonService().getPersonAttributeTypeByUuid(
			    attribute.getPersonAttributeUuid().toString()));
			personAttribute.setValue(attribute.getValue().toString());
			patient.addAttribute(personAttribute);
		}
		return patient;
	}
	
	public PersonService getPersonService() {
		if (personService == null)
			personService = Context.getPersonService();
		return personService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
}
