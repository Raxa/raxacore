package org.bahmni.module.elisatomfeedclient.api.domain;

import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.TestOrder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;


public class OpenElisAccessionTest {
    private Encounter accessionNotesEncounter;
    private Concept accessionNotesConcept;
    private OpenElisAccession openElisAccessionWithNotes;

    @Test
    public void shouldGetDiffWhenAccessionHasNewOrder() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(1, diff.getAddedTestDetails().size());
        assertEquals(test2, diff.getAddedTestDetails().toArray()[0]);
    }

    @Test
    public void shouldGetDiffWhenAccessionHasRemovedOrderFromPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1", "test2", "test3");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Canceled").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        Set<OpenElisTestDetail> removedTestDetails = diff.getRemovedTestDetails();

        assertEquals(1, removedTestDetails.size());
        assertEquals(test3, removedTestDetails.toArray()[0]);

    }

    @Test
    public void shouldGetDiffWhenAccessionHasAddedTestToPreviousEncounterAndRemovedTestWithinElis() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Canceled").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(1, diff.getAddedTestDetails().size());
        assertEquals(test2, diff.getAddedTestDetails().toArray()[0]);
        assertEquals(0, diff.getRemovedTestDetails().size());
    }

    @Test
    public void shouldNotDiffIfThereAreNoAddedOrRemovedTests() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1", "test2");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(0, diff.getAddedTestDetails().size());
        assertEquals(0, diff.getRemovedTestDetails().size());

    }

    @Test
    public void shouldNotDiffIfThereAreTestsRemovedOnBothSides() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1", "test2");
        getOrderByName(previousEncounter, "test1").setVoided(true);
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").withStatus("Canceled").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(0, diff.getAddedTestDetails().size());
        assertEquals(0, diff.getRemovedTestDetails().size());
    }

    @Test
    public void shouldGetDiffIfCancelledTestIsReordered() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1", "test2");
        getOrderByName(previousEncounter, "test1").setVoided(true);
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").withStatus("Canceled").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test1ReOrdered = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test1ReOrdered))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(1, diff.getAddedTestDetails().size());
        assertEquals(test1ReOrdered, diff.getAddedTestDetails().toArray()[0]);
    }

    @Test
    public void shouldGetDiffIfNewPanelAreAdded() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").withPanelUuid("panel1").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withPanelUuid("panel1").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(2, diff.getAddedTestDetails().size());
        Assert.assertThat(diff.getAddedTestDetails(), hasItem(test2));
        Assert.assertThat(diff.getAddedTestDetails(), hasItem(test3));
    }

    @Test
    public void shouldGetDiffIfPanelAreRemoved() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("test1", "panel1");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").withStatus("Canceled").withPanelUuid("panel1").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Canceled").withPanelUuid("panel1").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(2, diff.getRemovedTestDetails().size());
        Assert.assertThat(diff.getRemovedTestDetails(), hasItem(test2));
        Assert.assertThat(diff.getRemovedTestDetails(), hasItem(test3));
    }

    @Test
    public void shouldNotGetDiffIfThereArePanelsRemovedOnBothSides() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("panel1", "test2");
        getOrderByName(previousEncounter, "panel1").setVoided(true);
        OpenElisTestDetail panel1 = new OpenElisTestDetailBuilder().withTestUuid("test1").withPanelUuid("panel1").withStatus("Canceled").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(panel1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(0, diff.getAddedTestDetails().size());
        assertEquals(0, diff.getRemovedTestDetails().size());
    }

    @Test
    public void shouldGetDiffIfCancelledPanelIsReordered() throws Exception {
        Encounter previousEncounter = getEncounterWithOrders("panel1", "test2");
        getOrderByName(previousEncounter, "panel1").setVoided(true);
        OpenElisTestDetail panel1 = new OpenElisTestDetailBuilder().withTestUuid("test1").withPanelUuid("panel1").withStatus("Canceled").build();
        OpenElisTestDetail panel2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail panel1ReOrdered = new OpenElisTestDetailBuilder().withTestUuid("test1").withPanelUuid("panel1").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(panel1, panel2, panel1ReOrdered))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());

        AccessionDiff diff = openElisAccession.getDiff(previousEncounter);

        assertEquals(1, diff.getAddedTestDetails().size());
        assertEquals(panel1ReOrdered, diff.getAddedTestDetails().toArray()[0]);
    }

    public void accessionNotesTestSetup() {
        accessionNotesEncounter = new Encounter(343);
        accessionNotesConcept = new Concept();
        accessionNotesConcept.setUuid("123");
        openElisAccessionWithNotes = new OpenElisAccessionBuilder().withAccessionNotes("note1", "note2").build();
    }

    @Test
    public void shouldReturnTheAccessionNotesToBeAdded() {
        accessionNotesTestSetup();
        AccessionDiff diff = openElisAccessionWithNotes.getAccessionNoteDiff(accessionNotesEncounter, accessionNotesConcept);
        assertNotNull(diff);
        assertEquals(2, diff.getAccessionNotesToBeAdded().size());
        assertEquals("note1", diff.getAccessionNotesToBeAdded().get(0));
        assertEquals("note2", diff.getAccessionNotesToBeAdded().get(1));
    }

    @Test
    public void shouldUpdateTheAccessionNotesToBeAdded() {
        accessionNotesTestSetup();
        Obs obs = createNewAccessionNotesObs("note1");
        accessionNotesEncounter.addObs(obs);
        AccessionDiff diff = openElisAccessionWithNotes.getAccessionNoteDiff(accessionNotesEncounter, accessionNotesConcept);
        assertNotNull(diff);
        assertEquals(1, diff.getAccessionNotesToBeAdded().size());
        assertEquals("note2", diff.getAccessionNotesToBeAdded().get(0));
    }

    @Test
    public void shouldntReturnDiffWhenNotesAlreadyExist() {
        accessionNotesTestSetup();
        Obs obs1 = createNewAccessionNotesObs("note1");
        Obs obs2 = createNewAccessionNotesObs("note2");
        accessionNotesEncounter.addObs(obs1);
        accessionNotesEncounter.addObs(obs2);
        AccessionDiff diff = openElisAccessionWithNotes.getAccessionNoteDiff(accessionNotesEncounter, accessionNotesConcept);
        assertNotNull(diff);
        assertEquals(0, diff.getAccessionNotesToBeAdded().size());
    }
    @Test
    public void shouldntReturnDiffWhenNotesAreAddedAndNoNotesExist() {
        accessionNotesTestSetup();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().build();
        AccessionDiff diff = openElisAccession.getAccessionNoteDiff(accessionNotesEncounter, accessionNotesConcept);
        assertNotNull(diff);
        assertEquals(0, diff.getAccessionNotesToBeAdded().size());
    }

    private Obs createNewAccessionNotesObs(String testText) {
        Obs obs = new Obs();
        obs.setConcept(accessionNotesConcept);
        obs.setValueText(testText);
        return obs;
    }

    private Order getOrderByName(Encounter encounter, String testUuid) {
        for (Order order : encounter.getOrders()) {
            if (order.getConcept().getUuid().equals(testUuid))
                return order;
        }
        return null;
    }

    private Encounter getEncounterWithOrders(String... testUuids) {
        Encounter encounter = new Encounter();
        for (String testUuid : testUuids) {
            TestOrder order = new TestOrder();
            Concept concept = new Concept();
            concept.setUuid(testUuid);
            order.setConcept(concept);
            encounter.addOrder(order);
        }
        return encounter;
    }
}
