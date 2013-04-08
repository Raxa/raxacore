package org.raxa.module.raxacore.util;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Arrays;

public class SimpleObjectMother {
	
	public static SimpleObject getSimpleObjectWithAllFields() {
		return new SimpleObject().add("birthdate", "01-01-2012").add("age", 21).add("gender", "M").add(
		    "attributes",
		    Arrays.asList(new SimpleObject().add("attributeType", "b3b6d540-a32e-44c7-91b3-292d97667518").add("value",
		        "someCaste"))).add("addresses", Arrays.asList(new SimpleObject().add("address1", "7143 Koramangala"))).add(
		    "centerID", new SimpleObject().add("name", "Ganiyari")).add("names",
		    Arrays.asList(new SimpleObject().add("givenName", "first").add("familyName", "Last"))).add("patientIdentifier",
		    "someIdentifier");
	}
	
}
