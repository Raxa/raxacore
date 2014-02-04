package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class OpenElisAccessionEventWorker implements EventWorker {
    public static final String VOID_REASON = "updated since by lab technician";
    private ElisAtomFeedProperties atomFeedProperties;
    private HttpClient httpClient;
    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private ConceptService conceptService;
    private AccessionMapper accessionMapper;
    private EncounterTransactionMapper encounterTransactionMapper;
    private VisitService visitService;
    private ProviderService providerService;

    private static Logger logger = Logger.getLogger(OpenElisAccessionEventWorker.class);

    public OpenElisAccessionEventWorker(ElisAtomFeedProperties atomFeedProperties, HttpClient httpClient, EncounterService encounterService, EmrEncounterService emrEncounterService, ConceptService conceptService, AccessionMapper accessionMapper, EncounterTransactionMapper encounterTransactionMapper, VisitService visitService, ProviderService providerService) {

        this.atomFeedProperties = atomFeedProperties;
        this.httpClient = httpClient;
        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.conceptService = conceptService;
        this.accessionMapper = accessionMapper;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.visitService = visitService;
        this.providerService = providerService;
    }

    @Override
    public void process(Event event) {
        String accessionUrl = atomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + accessionUrl);
        try {
            OpenElisAccession openElisAccession = httpClient.get(accessionUrl, OpenElisAccession.class);
            Encounter previousEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
            Encounter encounterFromAccession = null;
            if (previousEncounter != null) {
                AccessionDiff diff = openElisAccession.getDiff(previousEncounter);
                if (diff.hasDifference()) {
                    logger.info("openelisatomfeedclient:updating encounter for accession : " + accessionUrl);
                    encounterFromAccession = accessionMapper.mapToExistingEncounter(openElisAccession, diff, previousEncounter);
                }
            } else {
                logger.info("openelisatomfeedclient:creating new encounter for accession : " + accessionUrl);
                encounterFromAccession = accessionMapper.mapToNewEncounter(openElisAccession);
            }

            if (encounterFromAccession != null) {
                EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounterFromAccession, true);
                emrEncounterService.save(encounterTransaction);
            }
            associateTestResultsToOrder(openElisAccession);

        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + accessionUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read accession data", e);
        } catch (ParseException pe) {
            logger.error("openelisatomfeedclient:error processing lab results. Invalid result data type : " + accessionUrl + pe.getMessage(), pe);
            throw new OpenElisFeedException("could not read accession data. Invalid result data type.", pe);
        }
    }

    protected void associateTestResultsToOrder(OpenElisAccession openElisAccession) throws ParseException {
        Encounter orderEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
        Visit visit = orderEncounter.getVisit(); //TODO:visit wont hav the latest encounters in them
        final EncounterType labResultEncounterType = encounterService.getEncounterType("LAB_RESULT");
        final Set<OpenElisTestDetail> allTests = openElisAccession.getTestDetails();

        ResultObsCreator resultObsCreator = new ResultObsCreator(conceptService.getConceptByName("LABRESULTS_CONCEPT"));
        List<Provider> labResultProviders = new ArrayList<>();
        for (OpenElisTestDetail testDetail : allTests) {
            if (StringUtils.isNotBlank(testDetail.getResult())) {
                Encounter resultEncounter = identifyResultEncounter(visit, labResultEncounterType, testDetail);
                Order testOrder = identifyOrder(orderEncounter, testDetail);
                Provider testProvider = getProviderForResults(labResultProviders, testDetail.getProviderUuid());
                boolean isResultUpdated = true;

                if (resultEncounter != null) {
                    Obs prevObs = identifyResultObs(resultEncounter, testDetail);
                    isResultUpdated = !isSameDate(prevObs.getObsDatetime(), DateTime.parse(testDetail.getDateTime()).toDate());
                    if (isResultUpdated) {
                        prevObs.setVoided(true);
                        prevObs.setVoidReason(VOID_REASON);
                    }
                }

                if (isResultUpdated) {
                    resultEncounter = findOrCreateEncounter(openElisAccession, visit, testDetail, labResultEncounterType, testProvider);
                    resultEncounter.addObs(resultObsCreator.createNewObsForOrder(testDetail, testOrder, resultEncounter));
                    visit.addEncounter(resultEncounter);
                }
            }
        }

        visitService.saveVisit(visit);
    }

    private boolean isSameDate(Date date1, Date date2) {
        return date1.getTime() == date2.getTime();
    }

    private Obs identifyResultObs(Encounter resultEncounter, OpenElisTestDetail testDetail) {
        boolean isPanel = StringUtils.isNotBlank(testDetail.getPanelUuid());
        final Set<Obs> obsAtTopLevel = resultEncounter.getObsAtTopLevel(false);
        for (Obs obs : obsAtTopLevel) {
            if (isPanel && obs.getConcept().getUuid().equals(testDetail.getPanelUuid())) {
                for (Obs member : obs.getGroupMembers()) {
                    if (member.getConcept().getUuid().equals(testDetail.getTestUuid())) {
                        return member;
                    }
                }
            } else if (obs.getConcept().getUuid().equals(testDetail.getTestUuid())) {
                return obs;
            }
        }
        return null;
    }

    private Order identifyOrder(Encounter orderEncounter, OpenElisTestDetail testDetail) {
        for (Order order : orderEncounter.getOrders()) {
            String testConceptUuid = StringUtils.isBlank(testDetail.getPanelUuid()) ? testDetail.getTestUuid() : testDetail.getPanelUuid();
            if (order.getConcept().getUuid().equals(testConceptUuid)) {
                return order;
            }
        }
        return null; //this should never be the case.
    }

    private class ResultObsCreator {
        public static final String LAB_RESULT = "LAB_RESULT";
        public static final String LAB_ABNORMAL = "LAB_ABNORMAL";
        public static final String LAB_MINNORMAL = "LAB_MINNORMAL";
        public static final String LAB_MAXNORMAL = "LAB_MAXNORMAL";
        public static final String LAB_NOTES = "LAB_NOTES";
        private Concept labConcepts = null;
        private static final String RESULT_TYPE_NUMERIC = "N";

        private ResultObsCreator(Concept labResultsConcept) {
            this.labConcepts = labResultsConcept;
        }

        private Concept getLabConceptByName(String name) {
            final List<Concept> members = this.labConcepts.getSetMembers();
            for (Concept concept : members) {
                if (concept != null && concept.getName().getName().equals(name)) {
                    return concept;
                }
            }
            return null;
        }

        public Obs createNewTestObsForOrder(OpenElisTestDetail testDetail, Order order, Concept concept, Date obsDate) throws ParseException {
            Obs labObs = newParentObs(order, concept, obsDate);
            labObs.addGroupMember(newChildObs(order, obsDate, concept, testDetail.getResult()));
            labObs.addGroupMember(newChildObs(order, obsDate, LAB_ABNORMAL, testDetail.getAbnormal().toString()));
            if(testDetail.getResultType().equals(RESULT_TYPE_NUMERIC)) {
                labObs.addGroupMember(newChildObs(order, obsDate, LAB_MINNORMAL, testDetail.getMinNormal().toString()));
                labObs.addGroupMember(newChildObs(order, obsDate, LAB_MAXNORMAL, testDetail.getMaxNormal().toString()));
            }
            final Set<String> notes = testDetail.getNotes();
            if (notes != null) {
                for (String note : notes) {
                    if (StringUtils.isNotBlank(note)) {
                        labObs.addGroupMember(newChildObs(order, obsDate, LAB_NOTES, note));
                    }
                }
            }

            return labObs;
        }

        private Obs newChildObs(Order order, Date obsDate, String conceptName, String value) throws ParseException {
            Concept concept = getLabConceptByName(conceptName);
            Obs resultObs = newChildObs(order, obsDate, concept, value);
            return resultObs;
        }

        private Obs newChildObs(Order order, Date obsDate, Concept concept, String value) throws ParseException {
            Obs resultObs = new Obs();
            resultObs.setConcept(concept);
            resultObs.setValueAsString(value);
            resultObs.setObsDatetime(obsDate);
            resultObs.setOrder(order);
            return resultObs;
        }

        public Obs createNewObsForOrder(OpenElisTestDetail testDetail, Order testOrder, Encounter resultEncounter) throws ParseException {
            Date obsDate = DateTime.parse(testDetail.getDateTime()).toDate();
            if(testDetail.getPanelUuid() != null) {
                Obs panelObs = createOrFindPanelObs(testDetail, testOrder, resultEncounter, obsDate);
                Concept testConcept = conceptService.getConceptByUuid(testDetail.getTestUuid());
                panelObs.addGroupMember(createNewTestObsForOrder(testDetail, testOrder, testConcept, obsDate));
                return panelObs;
            } else {
                return createNewTestObsForOrder(testDetail, testOrder, testOrder.getConcept(), obsDate);
            }
        }

        private Obs createOrFindPanelObs(OpenElisTestDetail testDetail, Order testOrder, Encounter resultEncounter, Date obsDate) {
            Obs panelObs = null;
            for (Obs obs : resultEncounter.getAllObs()) {
                if(obs.getConcept().getUuid().equals(testDetail.getPanelUuid())){
                    panelObs = obs;
                    break;
                }
            }
            return panelObs != null ? panelObs : newParentObs(testOrder, testOrder.getConcept(), obsDate);
        }

        private Obs newParentObs(Order order, Concept concept, Date obsDate) {
            Obs labObs = new Obs();
            labObs.setConcept(concept);
            labObs.setOrder(order);
            labObs.setObsDatetime(obsDate);
            return labObs;
        }
    }

    private Encounter findOrCreateEncounter(OpenElisAccession openElisAccession, Visit visit,
                                            OpenElisTestDetail testDetail, EncounterType labResultEncounterType, Provider testProvider) {
        Encounter labResultEncounter = getEncounterByEncounterTypeProviderAndVisit(labResultEncounterType, testProvider, visit);

        if (labResultEncounter == null) {
            labResultEncounter = newEncounterInstance(openElisAccession, visit, visit.getPatient(), testProvider, labResultEncounterType);
        }

        return  labResultEncounter;
    }

    private Provider getProviderForResults(List<Provider> labResultProviders, String providerUuid) {
        for (Provider labResultProvider : labResultProviders) {
            if (labResultProvider.getUuid().equals(providerUuid)) {
                return labResultProvider;
            }
        }

        Provider provider = null;
        if (StringUtils.isNotBlank(providerUuid)) {
            provider =  providerService.getProviderByUuid(providerUuid);
        }

        //the lab results provider may not be register as provider in MRS,
        //hence instead of failing, get the system provider
        if (provider == null) {
            provider = providerService.getProviderByIdentifier("system");
        }

        labResultProviders.add(provider);
        return provider;
    }

    private Encounter getEncounterByEncounterTypeProviderAndVisit(EncounterType labResultEncounterType, Provider provider, Visit visit) {
        for (Encounter encounter : visit.getEncounters()) {
            if(hasSameEncounterType(labResultEncounterType, encounter) && hasSameProvider(provider, encounter)) {
                return encounter;
            }
        }
        return null;
    }

    private boolean hasSameEncounterType(EncounterType labResultEncounterType, Encounter encounter) {
        return encounter.getEncounterType().getUuid().equals(labResultEncounterType.getUuid());
    }

    private boolean hasSameProvider(Provider provider, Encounter encounter) {
        return encounter.getEncounterProviders().iterator().next().getProvider().getUuid().equals(provider.getUuid());
    }

    private Encounter newEncounterInstance(OpenElisAccession openElisAccession, Visit visit, Patient patient, Provider labSystemProvider, EncounterType encounterType) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(encounterType);
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(openElisAccession.fetchDate());
        EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        encounter.setProvider(encounterRole, labSystemProvider);
        encounter.setVisit(visit);
        return encounter;
    }

    /**
     * For a given test/panel result, there ought to be only one encounter containing non voided corresponding observation
     * @param visit
     * @param labResultEncounterType
     * @param testDetail
     * @return
     */
    private Encounter identifyResultEncounter(Visit visit, EncounterType labResultEncounterType, OpenElisTestDetail testDetail) {
        for (Encounter encounter : visit.getEncounters()) {
            if (!encounter.getEncounterType().equals(labResultEncounterType)) continue;

            final Obs resultObs = identifyResultObs(encounter, testDetail);
            if (resultObs != null) {
               return encounter;
            }
        }
        return null;
    }

    @Override
    public void cleanUp(Event event) {

    }
}
