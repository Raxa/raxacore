package org.raxa.module.raxacore.web.v1_0.model;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import static junit.framework.Assert.assertEquals;

public class NameTest {
   @Test
   public void shouldCreateNameFromSimpleObject() {
       String givenName = "SomeGivenName";
       String middleName = "SomeMiddleName";
       String familyName = "SomeFamilyName";
       SimpleObject nameObject = new SimpleObject().add("givenName", givenName).add("middleName", middleName).add("familyName", familyName);

       Name name = new Name(nameObject);

       assertEquals(givenName, name.getGivenName());
       assertEquals(middleName, name.getMiddleName());
       assertEquals(familyName, name.getFamilyName());
   }
}
