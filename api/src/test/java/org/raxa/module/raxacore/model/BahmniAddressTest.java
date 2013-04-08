package org.raxa.module.raxacore.model;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import static junit.framework.Assert.assertEquals;

public class BahmniAddressTest {
	
	@Test
	public void shouldCreateAddressFromSimpleObject() {
		String stateProvince = "somestateProvince";
		String countyDistrict = "somecountyDistrict";
		String cityVillage = "somecityVillage";
		String address3 = "someAddress3";
		String address2 = "someAddress2";
		String address1 = "someAddress1";
		SimpleObject addressObject = new SimpleObject().add("address1", address1).add("address2", address2).add("address3",
		    address3).add("cityVillage", cityVillage).add("countyDistrict", countyDistrict).add("stateProvince",
		    stateProvince);
		
		BahmniAddress address = new BahmniAddress(addressObject);
		
		assertEquals(address1, address.getAddress1());
		assertEquals(address2, address.getAddress2());
		assertEquals(address3, address.getAddress3());
		assertEquals(cityVillage, address.getCityVillage());
		assertEquals(countyDistrict, address.getCountyDistrict());
		assertEquals(stateProvince, address.getStateProvince());
	}
}
