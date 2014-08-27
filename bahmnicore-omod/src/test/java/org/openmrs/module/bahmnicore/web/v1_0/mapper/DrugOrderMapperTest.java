package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.mapper.builder.DrugOrderBuilder;
import org.bahmni.module.bahmnicore.mapper.builder.EncounterBuilder;
import org.bahmni.module.bahmnicore.mapper.builder.PersonBuilder;
import org.bahmni.module.bahmnicore.mapper.builder.VisitBuilder;
import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.bahmni.module.bahmnicore.util.CustomDateSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(LocaleUtility.class)
@RunWith(PowerMockRunner.class)
public class DrugOrderMapperTest {

    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(LocaleUtility.class);
        when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<Locale> (Arrays.asList(Locale.getDefault())));
    }

    @Test
    public void shouldMapToResponseForFreeTextOrderDetails() throws Exception {
        DrugOrderBuilder drugBuilder = new DrugOrderBuilder();
        Date visitDate, dateActivated ;
        visitDate = dateActivated = new Date();
        Date dateScheduled = DateUtils.addDays(dateActivated, 2);
        Date expireDate = DateUtils.addDays(dateActivated, 20);
        int duration = 2;

        Person person = new PersonBuilder().withUUID("puuid").build();
        Encounter encounter = new EncounterBuilder().build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(visitDate).withEncounter(encounter).build();

        DrugOrder drugOrder1 = drugBuilder.withDrugName("Paracetamol 120mg/5ml 60ml")
                .withDosingType(FreeTextDosingInstructions.class)
                .withDrugForm("Capsule")
                .withScheduledDate(dateScheduled)
                .withDateActivated(dateActivated)
                .withDuration(duration)
                .withDurationUnits("Week")
                .withDosingInstructions("2.0 Tablet")
                .withVisit(visit)
                .withAutoExpireDate(expireDate).build();

        List<DrugOrder> drugOrderList = new ArrayList<>();
        drugOrderList.add(drugOrder1);

        List<BahmniDrugOrder> mappedDrugOrders = new DrugOrderMapper().mapToResponse(drugOrderList);
        assertEquals(1,mappedDrugOrders.size());
        BahmniDrugOrder mappedOrder = mappedDrugOrders.get(0);

        assertEquals("Paracetamol 120mg/5ml 60ml",mappedOrder.getDrugName());
        assertEquals("Capsule",mappedOrder.getDrugForm());
        assertEquals(2.0, mappedOrder.getDose(), 0);
        assertEquals("Tablet",mappedOrder.getDoseUnits());
        assertEquals(CustomDateSerializer.serializeDate(dateScheduled), mappedOrder.getEffectiveStartDate());
        assertEquals(CustomDateSerializer.serializeDate(expireDate), mappedOrder.getEffectiveStopDate());
        assertEquals(duration, mappedOrder.getDuration(),0);
        assertEquals("Days", mappedOrder.getDurationUnits());
        assertEquals("vuuid", mappedOrder.getVisit().getUuid());
        assertEquals(CustomDateSerializer.serializeDate(visitDate), mappedOrder.getVisit().getStartDateTime());
    }

    @Test
    public void shouldMapToResponseForSimpleOrderDetails() throws Exception {
        DrugOrderBuilder drugBuilder = new DrugOrderBuilder();

        Date dateActivated, visitDate;
        dateActivated= visitDate = new Date();
        Date dateScheduled = DateUtils.addDays(dateActivated, 2);
        Date expireDate = DateUtils.addDays(dateActivated, 20);
        Person person = new PersonBuilder().withUUID("puuid").build();
        Encounter encounter = new EncounterBuilder().build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(visitDate).withEncounter(encounter).build();

        int duration = 2;
        String dosingInstructions = "{\"instructions\": \"Before meals\", \"notes\": \"Take before waking up\"}";
        DrugOrder drugOrder1 = drugBuilder.withDrugName("Paracetamol 120mg/5ml 60ml")
                .withDosingType(SimpleDosingInstructions.class)
                .withDosingInstructions(dosingInstructions)
                .withDrugForm("Tablet")
                .withDateActivated(dateActivated)
                .withDuration(duration)
                .withDurationUnits("Week")
                .withDose(2.0)
                .withVisit(visit)
                .withFrequency("Once a day")
                .withRoute("Orally")
                .withAutoExpireDate(expireDate)
                .withDoseUnits("Capsule").build();

        List<DrugOrder> drugOrderList = new ArrayList<>();
        drugOrderList.add(drugOrder1);

        List<BahmniDrugOrder> mappedDrugOrders = new DrugOrderMapper().mapToResponse(drugOrderList);
        assertEquals(1,mappedDrugOrders.size());
        BahmniDrugOrder mappedOrder = mappedDrugOrders.get(0);

        assertEquals("Paracetamol 120mg/5ml 60ml",mappedOrder.getDrugName());
        assertEquals("Tablet",mappedOrder.getDrugForm());
        assertEquals(2.0, mappedOrder.getDose(), 0);
        assertEquals("Capsule",mappedOrder.getDoseUnits());
        assertEquals(CustomDateSerializer.serializeDate(dateActivated), mappedOrder.getEffectiveStartDate());
        assertEquals(CustomDateSerializer.serializeDate(expireDate), mappedOrder.getEffectiveStopDate());
        assertEquals(duration, mappedOrder.getDuration(),0);
        assertEquals("Week", mappedOrder.getDurationUnits());
        assertEquals("Before meals", mappedOrder.getDosingInstructions().getInstructions());
        assertEquals("Take before waking up", mappedOrder.getDosingInstructions().getNotes());
        assertEquals("Once a day", mappedOrder.getFrequency());
        assertEquals("Orally", mappedOrder.getRoute());
        assertEquals("vuuid", mappedOrder.getVisit().getUuid());
        assertEquals(CustomDateSerializer.serializeDate(visitDate), mappedOrder.getVisit().getStartDateTime());
    }

}
