package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openmrs.EncounterType;
import org.openmrs.api.APIException;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmnimapping.services.BahmniLocationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationBasedEncounterTypeIdentifierTest {

    @Mock
    private BahmniLocationService bahmniLocationService;
    private LocationBasedEncounterTypeIdentifier identifier;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        identifier = new LocationBasedEncounterTypeIdentifier(bahmniLocationService);
    }

    @Test
    public void shouldPopulateEncounterTypeUuidWhenEncounterTypeUuidAndNameIsNotSet() throws Exception {
        BahmniEncounterTransaction encounterTransaction = new BahmniEncounterTransaction();
        String locationUuid = UUID.randomUUID().toString();
        encounterTransaction.setEncounterTypeUuid(null);
        encounterTransaction.setEncounterType(null);
        encounterTransaction.setLocationUuid(locationUuid);
        EncounterType encounterTypeMappedToLocation = new EncounterType();
        encounterTypeMappedToLocation.setUuid(UUID.randomUUID().toString());
        when(bahmniLocationService.getEncounterType(locationUuid)).thenReturn(encounterTypeMappedToLocation);

        identifier.populateEncounterType(encounterTransaction);

        assertEquals(encounterTypeMappedToLocation.getUuid(), encounterTransaction.getEncounterTypeUuid());
    }

    @Test
    public void shouldNotChangeEncounterTypeUuidWhenEncounterTypeUuidIsAlreadySet() throws Exception {
        BahmniEncounterTransaction encounterTransaction = new BahmniEncounterTransaction();
        String existingEncounterTypeUuid = UUID.randomUUID().toString();
        String locationUuid = UUID.randomUUID().toString();
        encounterTransaction.setEncounterTypeUuid(existingEncounterTypeUuid);
        encounterTransaction.setEncounterType(null);
        encounterTransaction.setLocationUuid(locationUuid);

        identifier.populateEncounterType(encounterTransaction);

        assertEquals(existingEncounterTypeUuid, encounterTransaction.getEncounterTypeUuid());
        verifyZeroInteractions(bahmniLocationService);
    }

    @Test
    public void shouldNotPopulateEncounterTypeWhenEncounterTypeNameIsSet() throws Exception {
        BahmniEncounterTransaction encounterTransaction = new BahmniEncounterTransaction();
        String locationUuid = UUID.randomUUID().toString();
        encounterTransaction.setEncounterTypeUuid(null);
        encounterTransaction.setEncounterType("Consultation");
        encounterTransaction.setLocationUuid(locationUuid);

        identifier.populateEncounterType(encounterTransaction);

        assertEquals(null, encounterTransaction.getEncounterTypeUuid());
        verifyZeroInteractions(bahmniLocationService);
    }

    @Test
    public void shouldNotPopulateEncounterTypeWhenLocationIsNotSet() throws Exception {
        BahmniEncounterTransaction encounterTransaction = new BahmniEncounterTransaction();
        encounterTransaction.setEncounterTypeUuid(null);
        encounterTransaction.setEncounterType(null);
        encounterTransaction.setLocationUuid(null);

        identifier.populateEncounterType(encounterTransaction);

        assertEquals(null, encounterTransaction.getEncounterTypeUuid());
    }

    @Test
    public void shouldNotPopulateEncounterTypeWhenLocationIsNotMappedToEncounterType() throws Exception {
        BahmniEncounterTransaction encounterTransaction = new BahmniEncounterTransaction();
        String locationUuid = UUID.randomUUID().toString();
        encounterTransaction.setEncounterTypeUuid(null);
        encounterTransaction.setEncounterType(null);
        encounterTransaction.setLocationUuid(locationUuid);
        when(bahmniLocationService.getEncounterType(locationUuid)).thenReturn(null);

        identifier.populateEncounterType(encounterTransaction);

        assertEquals(null, encounterTransaction.getEncounterTypeUuid());
    }
}