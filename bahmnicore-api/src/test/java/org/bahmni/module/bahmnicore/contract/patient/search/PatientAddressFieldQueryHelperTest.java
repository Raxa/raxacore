package org.bahmni.module.bahmnicore.contract.patient.search;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatientAddressFieldQueryHelperTest {

	@Test
	public void shouldReturnWhereClauseWhenAddressFieldValueIsAvailable(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("city_village", "Bilaspur");
		String whereClause = patientAddressFieldQueryHelper.appendToWhereClause("where test='1234'");
		assertEquals("where test='1234' and ( city_village like '%Bilaspur%')", whereClause);
	}

	@Test
	public void shouldReturnWhereClauseWhenAddressFieldValueIsNotAvailable(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("city_village", "");
		String whereClause = patientAddressFieldQueryHelper.appendToWhereClause("where test='1234'");
		assertEquals("where test='1234'", whereClause);
	}

	@Test
	public void ensureThatScalarQueryResultIsConfigured(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("city_village", "Bilaspur");
		Map<String,Type> map = patientAddressFieldQueryHelper.addScalarQueryResult();
		assertTrue(map.containsKey("addressFieldValue"));
		assertEquals(StandardBasicTypes.STRING,map.get("addressFieldValue"));
	}

	@Test
	public void ensureThatGroupByClauseIsConfiguredAndIsNotEmpty(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("city_village", "Bilaspur");
		String groupBy = patientAddressFieldQueryHelper.appendToGroupByClause("something");
		assertEquals("something,city_village,p.person_id, p.uuid , pi.identifier , pn.given_name , pn.middle_name , pn.family_name , p.gender , p.birthdate , p.death_date , p.date_created , v.uuid",groupBy);
	}

	@Test
	public void ensureThatGroupByClauseIsConfiguredAndIsEmpty(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("city_village", "Bilaspur");
		String groupBy = patientAddressFieldQueryHelper.appendToGroupByClause("");
		assertEquals("city_village,p.person_id, p.uuid , pi.identifier , pn.given_name , pn.middle_name , pn.family_name , p.gender , p.birthdate , p.death_date , p.date_created , v.uuid",groupBy);
	}

	@Test
	public void shouldReturnSelectClauseWithAddressFieldValue(){
		PatientAddressFieldQueryHelper patientAddressFieldQueryHelper = new PatientAddressFieldQueryHelper("addressFieldName", null);
		String selectClause = patientAddressFieldQueryHelper.selectClause("select someFields");
		assertEquals("select someFields,pa.addressFieldName as addressFieldValue", selectClause);
	}

}
