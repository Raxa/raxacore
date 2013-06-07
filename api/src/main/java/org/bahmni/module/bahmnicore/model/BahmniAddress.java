package org.bahmni.module.bahmnicore.model;

import java.util.LinkedHashMap;

public class BahmniAddress {
	
	private String address1;
	
	private String address2;
	
	private String address3;
	
	private String cityVillage;
	
	private String countyDistrict;
	
	private String stateProvince;

    public BahmniAddress(String address1, String address2, String address3, String cityVillage, String countyDistrict, String stateProvince) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.cityVillage = cityVillage;
        this.countyDistrict = countyDistrict;
        this.stateProvince = stateProvince;
    }

    public BahmniAddress(LinkedHashMap post) {
		SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
		address1 = extractor.extract("address1");
		address2 = extractor.extract("address2");
		address3 = extractor.extract("address3");
		cityVillage = extractor.extract("cityVillage");
		countyDistrict = extractor.extract("countyDistrict");
		stateProvince = extractor.extract("stateProvince");
	}
	
	public String getAddress1() {
		return address1;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public String getAddress3() {
		return address3;
	}
	
	public String getCityVillage() {
		return cityVillage;
	}
	
	public String getCountyDistrict() {
		return countyDistrict;
	}
	
	public String getStateProvince() {
		return stateProvince;
	}
}
