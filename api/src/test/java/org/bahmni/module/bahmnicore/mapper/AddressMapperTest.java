package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniAddress;
import org.bahmni.module.bahmnicore.util.SimpleObjectMother;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class AddressMapperTest {

    @Test
    public void shouldNotAddAddressIfItIsTheSame() {
        Patient patient = new Patient();
        SimpleObject address = SimpleObjectMother.getSimpleObjectForAddress();
        AddressMapper addressMapper = new AddressMapper();

        addressMapper.map(patient, Arrays.asList(new BahmniAddress(address)));
        assertEquals(patient.getAddresses().size(), 1);

        addressMapper.map(patient, Arrays.asList(new BahmniAddress(address)));
        assertEquals(patient.getAddresses().size(), 1);
    }
}
