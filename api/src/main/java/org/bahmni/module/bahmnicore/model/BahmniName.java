package org.bahmni.module.bahmnicore.model;

import java.util.LinkedHashMap;

public class BahmniName {
	
	private String givenName;
	
	private String middleName;
	
	private String familyName;
	
	public BahmniName(LinkedHashMap post) {
		SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
		givenName = extractor.extract("givenName");
		middleName = extractor.extract("middleName");
		familyName = extractor.extract("familyName");
	}
	
	public String getGivenName() {
		return givenName;
	}
	
	public String getMiddleName() {
		return middleName;
	}
	
	public String getFamilyName() {
		return familyName;
	}
}
