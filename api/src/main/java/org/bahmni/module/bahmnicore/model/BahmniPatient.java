package org.bahmni.module.bahmnicore.model;

import org.apache.log4j.Logger;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class BahmniPatient {
	private Date birthdate;
	private Integer age;
	private String centerName;
	private String identifier;
	private List<BahmniPersonAttribute> attributes = new ArrayList<BahmniPersonAttribute>();
	private List<BahmniAddress> addresses = new ArrayList<BahmniAddress>();
	private List<BahmniName> names = new ArrayList<BahmniName>();
	private String gender;
    private String image;
    private String uuid;
    private String balance;
    private Date dateOfRegistration;
    private static Logger logger = Logger.getLogger(BahmniPatient.class);

    public BahmniPatient() {
    }

    public BahmniPatient(SimpleObject post) throws ParseException {
		SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);

        balance = extractor.extract("balance");
		age = extractor.extract("age");
		identifier = extractor.extract("identifier");
		image = extractor.extract("image");
		gender = extractor.extract("gender");
		SimpleObjectExtractor centerNameExtractor = new SimpleObjectExtractor(extractor.<LinkedHashMap> extract("centerID"));
		centerName = centerNameExtractor.extract("name");
		
		try {
			birthdate = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String> extract("birthdate"));
		}
		catch (Exception e) {
			logger.warn(e);
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

        String extractedDateOfRegistration = extractor.extract("dateOfRegistration");
        if (extractedDateOfRegistration != null)
            dateOfRegistration = new SimpleDateFormat("dd-MM-yyyy").parse(extractedDateOfRegistration);
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
	
	public String getIdentifier() {
		return identifier;
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

    public double getBalance() {
        return balance == null ? Double.NaN : Double.parseDouble(balance);
    }

    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public String getFullName() {
        return names.get(0).getFullName();
    }

    public String getPatientName() {
        BahmniName patientName = getNames().get(0);
        return patientName.getGivenName() + " " + patientName.getFamilyName();
    }

    public boolean hasBalance() {
        return !Double.isNaN(getBalance()) && getBalance() > 0;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
