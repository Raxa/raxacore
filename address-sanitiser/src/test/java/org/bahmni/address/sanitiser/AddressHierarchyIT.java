package org.bahmni.address.sanitiser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-address-sanitiser.xml"})
public class AddressHierarchyIT {
    @Autowired
    AddressHierarchy addressHierarchy;

    @Test
    public void shouldGetAllVillagesFromCache() {
        List<String> allVillages = addressHierarchy.getAllVillages();
        List<String> allVillagesFromCache = addressHierarchy.getAllVillages();

        assertEquals(allVillagesFromCache, allVillages);
    }
}
