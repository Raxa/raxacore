package org.raxa.module.raxacore.web.v1_0.model;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import static junit.framework.Assert.assertEquals;

public class PersonAttributeTest {
    @Test
    public void shouldCreatePersonAttributeFromSimpleObject() {
        String value = "someCaste";
        String attributeUUId = "casteAttributeUUId";
        SimpleObject personAttributeObject = new SimpleObject().add("attributetype", attributeUUId).add("value", value);

        PersonAttribute personAttribute = new PersonAttribute(personAttributeObject);

        assertEquals(attributeUUId, personAttribute.getPersonAttributeUuid());
        assertEquals(value, personAttribute.getValue());
    }
}
