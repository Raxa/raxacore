package org.bahmni.module.bahmnicore.model;

import java.util.LinkedHashMap;

public class BahmniPersonAttribute {
	
	private String personAttributeUuid;
	
	private String value;
	
	public BahmniPersonAttribute(LinkedHashMap post) {
		SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
		personAttributeUuid = extractor.extract("attributeType");
		value = extractor.extract("value");
	}
	
	public String getPersonAttributeUuid() {
		return personAttributeUuid;
	}
	
	public String getValue() {
		return value;
	}
}
