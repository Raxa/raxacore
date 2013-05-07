package org.bahmni.address.sanitiser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-address-sanitiser.xml"})
public class AddressHierarchyIT extends BaseModuleContextSensitiveTest {
    @Autowired
    AddressHierarchy addressHierarchy;

    @Test
    public void testsantitiseAddressWithJustOneMatchWithVillageMatch() throws Exception {

        executeDataSet("apiTest.xml");

        LavensteinsDistance lavensteinsDistance = new LavensteinsDistance();
        AddressHierarchy addressHierarchy = new AddressHierarchy();
        AddressSanitiser addressSanitiser = new AddressSanitiser(lavensteinsDistance, addressHierarchy);
        PersonAddress personAddressAfterSanitise;

        // correct output should be Chattisgarh	Anuppur	PUSHPRAJGARH	AMARKANTAK
        PersonAddress personAddress1 = new PersonAddress("AMARKANTAKU","PUSHPRAJGARHI","Anuppur","Chattisgarh");
        personAddressAfterSanitise = addressSanitiser.sanitise(personAddress1);
        assertEquals("Chattisgarh",personAddressAfterSanitise.getState());
        assertEquals("Anuppur",personAddressAfterSanitise.getDistrict());
        assertEquals("PUSHPRAJGARH",personAddressAfterSanitise.getTehsil());
        assertEquals("AMARKANTAK",personAddressAfterSanitise.getVillage());

    }

    @Test
    public void testsantitiseAddressWithJustMoreThanONeMatchWithVillageMatchAndDiffTehsils() throws Exception {

        executeDataSet("apiTest.xml");

        LavensteinsDistance lavensteinsDistance = new LavensteinsDistance();
        AddressHierarchy addressHierarchy = new AddressHierarchy();
        AddressSanitiser addressSanitiser = new AddressSanitiser(lavensteinsDistance, addressHierarchy);
        PersonAddress personAddressAfterSanitise;

        // correct output should be Chattisgarh	Anuppur	PUSHPRAJGARH	AMARKANTAK
        PersonAddress personAddress1 = new PersonAddress("Bilaspuri","Bilaspura","Anuppur","Chattisgarh");
        personAddressAfterSanitise = addressSanitiser.sanitise(personAddress1);
        assertEquals("Chattisgarh",personAddressAfterSanitise.getState());
        assertEquals("Anuppur",personAddressAfterSanitise.getDistrict());
        assertEquals("Bilaspur",personAddressAfterSanitise.getTehsil());
        assertEquals("Bilaspur",personAddressAfterSanitise.getVillage());

        // correct output should be Chattisgarh	Anuppur	Bilaspur AMARKANTAK
        PersonAddress personAddress2 = new PersonAddress("Bilaspuri","Champaka","Anuppur","Chattisgarh");
        personAddressAfterSanitise = addressSanitiser.sanitise(personAddress2);
        assertEquals("Chattisgarh",personAddressAfterSanitise.getState());
        assertEquals("Anuppur",personAddressAfterSanitise.getDistrict());
        assertEquals("Champak",personAddressAfterSanitise.getTehsil());
        assertEquals("Bilaspur",personAddressAfterSanitise.getVillage());
    }

    @Test
    public void testsantitiseAddressWithJustSimilarVillageNames() throws Exception {

        executeDataSet("apiTest.xml");

        LavensteinsDistance lavensteinsDistance = new LavensteinsDistance();
        AddressHierarchy addressHierarchy = new AddressHierarchy();
        AddressSanitiser addressSanitiser = new AddressSanitiser(lavensteinsDistance, addressHierarchy);
        PersonAddress personAddressAfterSanitise;

        // correct output should be Chattisgarh	Anuppur	PUSHPRAJGARH	AMARKANTAK
        PersonAddress personAddress1 = new PersonAddress("Bilasppur","Bilaspura","Anuppur","Chattisgarh");
        personAddressAfterSanitise = addressSanitiser.sanitise(personAddress1);
        assertEquals("Chattisgarh",personAddressAfterSanitise.getState());
        assertEquals("Anuppur",personAddressAfterSanitise.getDistrict());
        assertEquals("Bilaspur",personAddressAfterSanitise.getTehsil());
        assertEquals("Bilaspur",personAddressAfterSanitise.getVillage());

        // correct output should be Chattisgarh	Anuppur	Bilaspur AMARKANTAK
        PersonAddress personAddress2 = new PersonAddress("Bilaspuri","Bilaspura","Anuppur","Chattisgarh");
        personAddressAfterSanitise = addressSanitiser.sanitise(personAddress2);
        assertEquals("Chattisgarh",personAddressAfterSanitise.getState());
        assertEquals("Anuppur",personAddressAfterSanitise.getDistrict());
        assertEquals("Bilaspur",personAddressAfterSanitise.getTehsil());
        assertEquals("Bilaspur",personAddressAfterSanitise.getVillage());
    }

}
