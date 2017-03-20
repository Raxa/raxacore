package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.dao.PatientDao;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class BahmniPatientDaoImplLuceneIT extends BaseIntegrationTest {
    @Autowired
    private PatientDao patientDao;
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet("apiTestData.xml");
        updateSearchIndex();
    }
    
    @Test
    public void shouldSearchByPatientPrimaryIdentifier() {
        String[] addressResultFields = {"city_village"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("GAN200001", "", null, "city_village", "", 100, 0, null,"",null,addressResultFields,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient.getUuid());
        assertEquals("GAN200001", patient.getIdentifier());
        assertEquals("Horatio", patient.getGivenName());
        assertEquals("Sinha", patient.getFamilyName());
        assertEquals("M", patient.getGender());
        assertEquals("1983-01-30 00:00:00.0", patient.getBirthDate().toString());
        assertEquals("{\"city_village\" : \"Ramgarh\"}", patient.getAddressFieldValue());
        assertEquals("2006-01-18 00:00:00.0", patient.getDateCreated().toString());
        assertEquals(null, patient.getDeathDate());
        assertEquals("{\"National ID\" : \"NAT100010\"}", patient.getExtraIdentifiers());
    }
    
    @Test
    public void shouldSearchByPatientExtraIdentifier() {
        String[] addressResultFields = {"city_village"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("100010", "", null, "city_village", "", 100, 0, null,"",null,addressResultFields,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, true);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient.getUuid());
        assertEquals("GAN200001", patient.getIdentifier());
        assertEquals("Horatio", patient.getGivenName());
        assertEquals("Sinha", patient.getFamilyName());
        assertEquals("M", patient.getGender());
        assertEquals("1983-01-30 00:00:00.0", patient.getBirthDate().toString());
        assertEquals("{\"city_village\" : \"Ramgarh\"}", patient.getAddressFieldValue());
        assertEquals("2006-01-18 00:00:00.0", patient.getDateCreated().toString());
        assertEquals(null, patient.getDeathDate());
        assertEquals("{\"National ID\" : \"NAT100010\"}", patient.getExtraIdentifiers());
    }
    
    @Test
    public void shouldSearchByPartialPatientIdentifier() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("02", "", null, "city_village", "", 100, 0, null,"",null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        
        assertEquals("GAN200002", patient.getIdentifier());
        assertNull(patient.getExtraIdentifiers());
    }
    
    @Test
    public void shouldSortResultsByCreationDate() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("GAN20000", "", null, "city_village", "", 100, 4, null,"",null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
//        assertEquals(, patients.size());
        assertEquals("2006-01-18 00:00:00.0", patients.get(0).getDateCreated().toString());
        assertEquals("2005-09-22 00:00:00.0", patients.get(1).getDateCreated().toString());
    }
    
    @Test
    public void shouldReturnResultAfterGivenOffset() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "Sinha", null, "city_village", "", 100, 1, null,"",null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        
        patients = patientDao.getPatientsUsingLuceneSearch("", "Sinha", null, "city_village", "", 100, 2, null,"",null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldFetchBasedOnPatientAttributeTypes() throws Exception {
        String[] patientAttributes = { "caste"};
        String[] patientResultFields = {"caste"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "testCaste1", "city_village", null, 100, 0, patientAttributes,"",null,null,patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(1, patients.size());
    }
    
    @Test
    public void shouldThrowErrorWhenPatientAttributesIsNotPresent() throws Exception {
        String[] patientAttributes = {"caste","nonExistingAttribute"};
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Attribute In Patient Attributes [caste, nonExistingAttribute]");
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "testCaste1", "city_village", null, 100, 0, patientAttributes, "", null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
    }
    
    @Test
    public void shouldThrowErrorWhenPatientAddressIsNotPresent() throws Exception {
        String[] patientAttributes = {"caste"};
        String addressField = "nonExistingAddressFiled";
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Address Filed nonExistingAddressFiled");
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "testCaste1", addressField, null, 100, 0, patientAttributes, "", null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
    }
    
    @Test
    public void shouldFetchPatientsByProgramAttributes(){
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "", "city_village", null, 100, 0, null,"Stage1","stage",null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse response = patients.get(0);
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("John",response.getGivenName());
        assertEquals("{\"stage\":\"Stage1\"}",response.getPatientProgramAttributeValue());
    }
    
    @Test
    public void shouldThrowErrorWhenProgramAttributesIsNotPresent() {
        String nonExistingAttribute = "nonExistingAttribute";
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Program Attribute nonExistingAttribute");
        patientDao.getPatientsUsingLuceneSearch("", "", "", "city_village", null, 100, 0, null, "Stage1",nonExistingAttribute, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
    }
    
    @Test
    public void shouldFetchPatientsByAllSearchParametersExceptIdentifier(){
        String[] addressResultFields = {"city_village"};
        String[] patientResultFields = {"caste"};
        
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "John", "testCaste1", "city_village", "Bilaspur", 100, 0, new String[]{"caste","givenNameLocal"},"Stage1","stage",addressResultFields,patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse response = patients.get(0);
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("df8ae447-6745-45be-b859-403241d9913d",response.getUuid());
        assertEquals(1026,response.getPersonId());
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("{ \"city_village\" : \"Bilaspur\"}",response.getAddressFieldValue());
        assertEquals("John",response.getGivenName());
        assertEquals("Peeter",response.getMiddleName());
        assertEquals("Sinha",response.getFamilyName());
        assertEquals("F",response.getGender());
        assertEquals("{\"stage\":\"Stage1\"}",response.getPatientProgramAttributeValue());
    }
    
    
    @Test
    @Ignore
    public void shouldFetchPatientsByCodedConcepts(){
        
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "John", "testCaste1", "city_village", "Bilaspur", 100, 0, new String[]{"caste"}, "Fac", "facility",null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse response = patients.get(0);
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("df8ae447-6745-45be-b859-403241d9913d",response.getUuid());
        assertEquals(1026,response.getPersonId());
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("Bilaspur",response.getAddressFieldValue());
        assertEquals("John",response.getGivenName());
        assertEquals("Peeter",response.getMiddleName());
        assertEquals("Sinha",response.getFamilyName());
        assertEquals("F",response.getGender());
        assertEquals("{\"caste\":\"testCaste1\"}",response.getCustomAttribute());
        assertEquals("{\"facility\":\"Facility1, City1, Country1\"}",response.getPatientProgramAttributeValue());
    }
    
    @Test
    public void shouldFetchPatientsByOnlyOneProgramAttribute(){
        String[] addressResultFields = {"city_village"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", null, "city_village", "", 100, 0, null,"Stage1","stage",addressResultFields,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse response = patients.get(0);
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("df8ae447-6745-45be-b859-403241d9913d",response.getUuid());
        assertEquals(1026,response.getPersonId());
        assertEquals("GAN200002",response.getIdentifier());
        assertEquals("{ \"city_village\" : \"Bilaspur\"}",response.getAddressFieldValue());
        assertEquals("John",response.getGivenName());
        assertEquals("Peeter",response.getMiddleName());
        assertEquals("Sinha",response.getFamilyName());
        assertEquals("F",response.getGender());
        assertEquals("{\"stage\":\"Stage1\"}",response.getPatientProgramAttributeValue());
    }
    
    @Test
    public void shouldSearchByPatientIdentifierWithAttributes() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "John", null, "city_village", "", 100, 0, null,"",null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(2, patients.size());
    }
    
    @Test
    public void shouldReturnAdmissionStatus() throws Exception{
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("200000", null, null, "city_village", null, 10, 0, null, null, null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse patient200000 = patients.get(0);
        assertFalse(patient200000.getHasBeenAdmitted());
        
        patients = patientDao.getPatientsUsingLuceneSearch("200002", null, null, "city_village", null, 10, 0, null, null, null,null,null, "8d6c993e-c2cc-11de-8d13-0040c6dffd0f", false, false);
        assertEquals(1, patients.size());
        PatientResponse patient200003 = patients.get(0);
        assertTrue(patient200003.getHasBeenAdmitted());
    }
    
    @Test
    public void shouldReturnAddressAndPatientAttributes() throws Exception{
        String[] addressResultFields = {"address3"};
        String[] patientResultFields = {"middleNameLocal"  ,  "familyNameLocal" ,"givenNameLocal"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("GAN200002", null, null, null, null, 100, 0, new String[]{"caste","givenNameLocal"},null,null,addressResultFields,patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        assertEquals(1, patients.size());
        PatientResponse patient200002 = patients.get(0);
        assertTrue("{\"givenNameLocal\":\"ram\",\"middleNameLocal\":\"singh\",\"familyNameLocal\":\"gond\"}".equals(patient200002.getCustomAttribute()));
        assertTrue("{ \"address3\" : \"Dindori\"}".equals(patient200002.getAddressFieldValue()));
    }
    
    @Test
    public void shouldSearchPatientByNameWithSingleQuote() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch(null, "na'me", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse patient = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("na'me",patient.getFamilyName());
        
    }
    
    @Test
    public void shouldSearchPatientByNameWithOneSingleQuoteInSearchString() throws Exception {
        List<PatientResponse>  patients = patientDao.getPatientsUsingLuceneSearch(null, "'", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse patientSearchWithJustSingleQuote = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("na'me",patientSearchWithJustSingleQuote.getFamilyName());
    }
    
    @Test
    public void shouldSearchPatientNameByMultipleSingleQuotesInSearchString() throws Exception {
        List<PatientResponse>  patients = patientDao.getPatientsUsingLuceneSearch(null, "'''", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldGiveEmptyResultIfPatientDoesnotExistWithGivenPatientName() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch(null, "ab'me", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldGiveAllThePatientsIfWeSearchWithPercentile() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch(null, "%", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(10, patients.size());
    }
    
    @Test
    public void shouldGiveAllThePatientsIfWeSearchWithPercentileAsIdentifier() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("%", null, null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(10, patients.size());
    }
    
    @Test
    public void shouldGiveThePatientsIfWeSearchBySpaceSeperatedString() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch(null, "special character", null, null, null, 10, 0, null, null, null, null, null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(2, patients.size());
    }
    
    @Test
    public void shouldFetchBasedOnPatientAttributeTypesWhenThereIsSingleQuoteInPatientAttribute() throws Exception {
        String[] patientAttributes = { "caste"};
        String[] patientResultFields = {"caste"};
        String[] addressResultFields = {"address3"};
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "go'nd", null, null, 100, 0, patientAttributes,null,null,addressResultFields, patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(1, patients.size());
        
        assertEquals("{\"caste\":\"go'nd\"}", patients.get(0).getCustomAttribute());
        
        assertTrue("{ \"address3\" : \"Dindori\"}".equals(patients.get(0).getAddressFieldValue()));
        
        
        patients = patientDao.getPatientsUsingLuceneSearch("", "", "'", null, null, 100, 0, patientAttributes,null,null,addressResultFields, patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse patientWithSingleQuoteInSearch = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("{\"caste\":\"go'nd\"}", patientWithSingleQuoteInSearch.getCustomAttribute());
        assertTrue("{ \"address3\" : \"Dindori\"}".equals(patientWithSingleQuoteInSearch.getAddressFieldValue()));
        
        
        patients = patientDao.getPatientsUsingLuceneSearch("", "", "'''", null, null, 100, 0, patientAttributes,null,null,addressResultFields, patientResultFields, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldFetchPatientsByProgramAttributesWhenThereIsSingleQuoteInProgramAttribute(){
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "", null, null, 100, 0, null,"Stage'12","stage",null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse response = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("{\"stage\":\"Stage'12\"}",response.getPatientProgramAttributeValue());
    }
    
    @Test
    public void shouldFetchPatientsByProgramAttributeWhenThereIsJustOneSingleQuoteInSearchString() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "", null, null, 100, 0, null,"'","stage",null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse response = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("{\"stage\":\"Stage'12\"}",response.getPatientProgramAttributeValue());
    }
    
    @Test
    public void shouldFetchPatientsByParogramAttributeWhenThreAreMultipleSingleQuotesInSearchString() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "", "", null, null, 100, 0, null,"''''","stage",null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldFetchPatientsByPatientIdentifierWhenThereIsSingleQuoteInPatientIdentifier(){
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("51'0003", "", "", null, null, 100, 0, null,null, null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse response = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("SEV51'0003", response.getIdentifier());
    }
    
    @Test
    public void shouldFetchPatientsByPatientIdentifierWhenThereIsJustOneSingleQuoteInPatientIdentifier() throws Exception {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("'", "", "", null, null, 100, 0, null,null, null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        PatientResponse response = patients.get(0);
        
        assertEquals(1, patients.size());
        assertEquals("SEV51'0003", response.getIdentifier());
    }
    
    @Test
    public void shouldSearchPatientsByPatientIdentifierWhenThereAreMultipleSinglesInSearchString() throws Exception {
        
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("'''", "", "", null, null, 100, 0, null,null, null,null,null, "c36006e5-9fbb-4f20-866b-0ece245615a1", false, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldNotReturnDuplicatePatientsEvenIfThereAreMultipleVisitsForThePatients() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "1058GivenName", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d34-0010c6affd0f", false, false);
        
        assertEquals(1, patients.size());
        PatientResponse patient1 = patients.get(0);
        
        assertEquals("1058GivenName", patient1.getGivenName());
    }
    
    @Test
    public void shouldReturnPatientEvenIfThereIsNoVisitForThePatientWhenFilterByVisitLocationIsFalse() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "1059NoVisit", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d34-0010c6affd0f", false, false);
        
        assertEquals(1, patients.size());
        PatientResponse patient1 = patients.get(0);
        
        assertEquals("1059NoVisit", patient1.getGivenName());
    }
    
    @Test
    public void shouldNotReturnPatientIfThereIsNoVisitForThePatientAndFilterByVisitLocationIsTrue() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "1059NoVisit", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d34-0010c6affd0f", true, false);
        
        assertEquals(0, patients.size());
    }
    
    @Test
    public void shouldReturnPatientsWithinVisitLocationOfGivenLoginLocationWhenFilterByVisitLocationIsTrue() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "someUnique", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d34-0010c6affd0f", true, false);
        assertEquals(1, patients.size());
        
        PatientResponse patient = patients.get(0);
        assertEquals("someUniqueName", patient.getGivenName());
    }
    
    @Test
    public void shouldReturnAllMatchingPatientsIrrespectiveOfVisitsWhenFilterByVisitLocationIsFalse() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "someUnique", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d34-0010c6affd0f", false, false);
        assertEquals(2, patients.size());
        
        PatientResponse patient1 = patients.get(0);
        PatientResponse patient2 = patients.get(1);
        assertEquals("someUniqueName", patient1.getGivenName());
        assertEquals("someUniqueOtherName", patient2.getGivenName());
    }
    
    @Test
    public void shouldReturnPatientsWithinVisitLocationWhenLocationProvidedIsChildLocationAndFilterByLocationIsTrue() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "someUnique", null, "city_village", "", 100, 0, null,"",null,null,null, "8d6c993e-c2cc-11de-8d13-0010c6addd0f", true, false);
        assertEquals(1, patients.size());
        
        PatientResponse patient = patients.get(0);
        assertEquals("someUniqueName", patient.getGivenName());
    }
    
    @Test
    public void shouldReturnPatientsWithinTheVisitLocationWhenTheLocationPassedIsVisitLocationAndFilterByVisitLocationIsTrue() {
        List<PatientResponse> patients = patientDao.getPatientsUsingLuceneSearch("", "someUnique", null, "city_village", "", 100, 0, null, "", null, null, null, "8d6c993e-c2cc-11de-8d13-0010c6aff12f", true, false);
        assertEquals(1, patients.size());
        
        PatientResponse patient = patients.get(0);
        assertEquals("someUniqueName", patient.getGivenName());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldReturnAllMatchingPatientsWhenLoginLocationIsNull() {
        patientDao.getPatientsUsingLuceneSearch("", "someUnique", null, "city_village", "", 100, 0, null,"",null,null,null, null, false, false);
        
    }
}
