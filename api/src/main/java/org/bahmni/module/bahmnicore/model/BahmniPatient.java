package org.bahmni.module.bahmnicore.model;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class BahmniPatient {
	
	private Date birthdate;
	
	private Integer age;
	
	private String centerName;
	
	private String patientIdentifier;
	
	private List<BahmniPersonAttribute> attributes = new ArrayList<BahmniPersonAttribute>();
	
	private List<BahmniAddress> addresses = new ArrayList<BahmniAddress>();
	
	private List<BahmniName> names = new ArrayList<BahmniName>();
	
	private String gender;

    private String image;

    private String uuid;
	
	public BahmniPatient(SimpleObject post) {
		SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
		
		age = extractor.extract("age");
		patientIdentifier = extractor.extract("patientIdentifier");
		gender = extractor.extract("gender");
		SimpleObjectExtractor centerNameExtractor = new SimpleObjectExtractor(extractor.<LinkedHashMap> extract("centerID"));
		centerName = centerNameExtractor.extract("name");
		
		try {
			birthdate = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String> extract("birthdate"));
		}
		catch (Exception e) {
			//do something
		}
		
		List<LinkedHashMap> nameList = extractor.extract("names");
		for (LinkedHashMap name : nameList) {
			names.add(new BahmniName(name));
		}
		
		List<LinkedHashMap> addressList = extractor.extract("addresses");
		for (LinkedHashMap address : addressList) {
			addresses.add(new BahmniAddress(address));
		}
		
		List<LinkedHashMap> attributeList = extractor.extract("attributes");
		for (LinkedHashMap attribute : attributeList) {
			attributes.add(new BahmniPersonAttribute(attribute));
		}
	}
	
	public Date getBirthdate() {
		return birthdate;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public List<BahmniAddress> getAddresses() {
		return addresses;
	}
	
	public List<BahmniName> getNames() {
		return names;
	}
	
	public String getPatientIdentifier() {
		return patientIdentifier;
	}
	
	public String getCenterName() {
		return centerName;
	}
	
	public List<BahmniPersonAttribute> getAttributes() {
		return attributes;
	}
	
	public String getGender() {
		return gender;
	}

    public String getImage() {
        return image;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
