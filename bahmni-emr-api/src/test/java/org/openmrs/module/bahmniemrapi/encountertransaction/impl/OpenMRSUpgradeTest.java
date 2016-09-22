package org.openmrs.module.bahmniemrapi.encountertransaction.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class OpenMRSUpgradeTest extends BaseModuleContextSensitiveTest {

	// Vitals (61) -> Pulse (62)
	//                Blood Pressure (63) -> Systolic (64)

	Obs vitals = null;
	Obs bp = null;
	Obs systolic = null;
	Obs pulse = null;

	@Before
	public void setUp() throws Exception {
		executeDataSet("openmrsUpgradeTestData.xml");

		Encounter encounter = Context.getEncounterService().getEncounter(7);
		vitals = createObs(Context.getConceptService().getConcept(61), null);
		bp = createObs(Context.getConceptService().getConcept(63), null);
		systolic = createObs(Context.getConceptService().getConcept(64), 120.0);
		pulse = createObs(Context.getConceptService().getConcept(62), 72.0);

		bp.addGroupMember(systolic);

		vitals.addGroupMember(pulse);
		vitals.addGroupMember(bp);

		encounter.addObs(vitals);
		Context.getEncounterService().saveEncounter(encounter);
	}

	@Test
	public void reproduceInconsistencyInGettingAllObsFromEncounter() throws ParseException {

		Encounter encounter = Context.getEncounterService().getEncounter(7);
		int beforeEviction = encounter.getAllObs(true).size();
		assertEquals(1, beforeEviction);

		Context.evictFromSession(encounter);

		encounter = Context.getEncounterService().getEncounter(7);
		int afterEviction = encounter.getAllObs(true).size();
		assertEquals(4, afterEviction);
	}

	@Test
	public void shouldUpdateTopLevelObsAndValidateCount() throws ParseException {
		Encounter encounter = Context.getEncounterService().getEncounter(7);

		Obs exisitingVitalsObs = encounter.getAllObs().iterator().next();
		Obs diastolicObs = createObs(Context.getConceptService().getConcept(65), 80.0);
		diastolicObs.setPerson(new Person(2));
		diastolicObs.setObsDatetime(new Date());
		exisitingVitalsObs.addGroupMember(diastolicObs); //Added a new obs to the top level obs.

		Context.getEncounterService().saveEncounter(encounter);

		encounter = Context.getEncounterService().getEncounter(7);

		Set<Obs> allObs = encounter.getAllObs(true);
		int afterEditing = encounter.getAllObs(true).size();

		//Full Obs hirearchy is re-created as there is change at the parent level.
		assertEquals(8, afterEditing);
	}

	@Test
	public void shouldUpdateChildLevelObsAndValidateCount() throws ParseException {
		Context.flushSession();
		Context.clearSession();


		Encounter encounter = Context.getEncounterService().getEncounter(7);

		int after = Context.getEncounterService().getEncounter(7).getAllObs(true).size();

		assertEquals(4, after);

		encounter = Context.getEncounterService().getEncounter(7);
		Obs bpObsInEncounter = Context.getObsService().getObservations(null, Arrays.asList(encounter),Arrays.asList(
				Context.getConceptService().getConcept(63)),null,null,null,null,1,null,null,null,false).get(0);

		Obs diastolicObs = createObs(Context.getConceptService().getConcept(65), 80.0);
		diastolicObs.setPerson(new Person(2));
		diastolicObs.setObsDatetime(new Date());
		bpObsInEncounter.addGroupMember(diastolicObs); //Added a new diastolic obs to the bpObsInEncounter

		encounter = Context.getEncounterService().saveEncounter(encounter);
		Context.evictFromSession(encounter);

		encounter = Context.getEncounterService().getEncounter(7);
		int afterEditing = encounter.getAllObs(true).size();

		//Full Obs hirearchy is re-created eventhough the change is at a child level
		assertEquals(1, afterEditing);
	}


	private Obs createObs(Concept concept, Double value) throws ParseException {
		Obs obs = new Obs();
		obs.setConcept(concept);
		if(value != null){
			obs.setValueNumeric(value);
		}

		return obs;
	}


}
