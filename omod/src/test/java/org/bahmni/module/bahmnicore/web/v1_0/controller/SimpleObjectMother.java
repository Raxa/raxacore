package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Arrays;

public class SimpleObjectMother {
	
	public static SimpleObject getSimpleObjectWithAllFields() {
		return new SimpleObject().add("birthdate", "01-01-2012").add("age", 21).add("gender", "M").add(
		    "attributes",
		    Arrays.asList(new SimpleObject().add("attributeType", "9671845a-968f-11e2-a7d2-83c44c300eb3").add("value",
		        "someCaste"))).add("addresses", Arrays.asList(new SimpleObject().add("address1", "7143 Koramangala"))).add(
		    "centerID", new SimpleObject().add("name", "Ganiyari")).add("names",
		    Arrays.asList(new SimpleObject().add("givenName", "first").add("familyName", "Last"))).add("patientIdentifier",
		    "GAN123");
	}
	
	public static SimpleObject getAnotherSimpleObjectWithAllFields() {
		return new SimpleObject().add("birthdate", "01-01-2012").add("age", 21).add("gender", "M").add(
		    "attributes",
		    Arrays.asList(new SimpleObject().add("attributeType", "9671845a-968f-11e2-a7d2-83c44c300eb3").add("value",
		        "someOtherCaste"))).add("addresses", Arrays.asList(new SimpleObject().add("address2", "7143 Koramangala")))
		        .add("centerID", new SimpleObject().add("name", "Ganiyari")).add("names",
		            Arrays.asList(new SimpleObject().add("givenName", "first").add("familyName", "Last1"))).add(
		            "patientIdentifier", "GAN123");
	}
	
}