package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.impl.HealthCenterFilterRule;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OpenElisAccessionEventWorker implements EventWorker {
    public static final String LAB_VISIT = "LAB_VISIT";
    public static final String LAB_MANAGER_NOTES = "Lab Manager Notes";
    private static final String LAB_ORDER_TYPE = "Lab Order";
    public static final String LAB_MANAGER_IDENTIFIER = "LABMANAGER";
    private static Logger logger = Logger.getLogger(OpenElisAccessionEventWorker.class);
    private final OrderService orderService;
    private final OrdersHelper ordersHelper;
    private final VisitService visitService;
    private final EncounterHelper encounterHelper;
    private ElisAtomFeedProperties atomFeedProperties;
    private HttpClient httpClient;
    private EncounterService encounterService;
    private ConceptService conceptService;
    private AccessionHelper accessionMapper;
    private ProviderService providerService;
    private HealthCenterFilterRule healthCenterFilterRule;

    //TODO : add the new service classes to bean initialization
    public OpenElisAccessionEventWorker(ElisAtomFeedProperties atomFeedProperties,
                                        HttpClient httpClient,
                                        EncounterService encounterService,
                                        ConceptService conceptService,
                                        AccessionHelper accessionMapper,
                                        ProviderService providerService,
                                        OrderService orderService,
                                        VisitService visitService, HealthCenterFilterRule healthCenterFilterRule) {

        this.atomFeedProperties = atomFeedProperties;
        this.httpClient = httpClient;
        this.encounterService = encounterService;
        this.conceptService = conceptService;
        this.accessionMapper = accessionMapper;
        this.providerService = providerService;
        this.visitService = visitService;
        this.healthCenterFilterRule = healthCenterFilterRule;
        this.orderService = orderService;
        this.ordersHelper = new OrdersHelper(orderService, conceptService, encounterService);
        this.encounterHelper = new EncounterHelper(encounterService, this.visitService);
    }

    @Override
    public void process(Event event) {
        String accessionUrl = atomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("Processing event : " + accessionUrl);
        try {
            OpenElisAccession openElisAccession = httpClient.get(accessionUrl, OpenElisAccession.class);

            if (!healthCenterFilterRule.passesWith(openElisAccession.getHealthCenter())) {
                logger.info("Skipping. Event " + accessionUrl + " will not be persisted");
                return;
            }

            Encounter orderEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());


            boolean shouldSaveOrderEncounter = false;
            if (orderEncounter != null) {
                AccessionDiff diff = openElisAccession.getDiff(orderEncounter);
                if (diff.hasDifference()) {
                    logger.info("updating encounter for accession : " + accessionUrl);
                    accessionMapper.addOrVoidOrderDifferences(openElisAccession, diff, orderEncounter);
                    shouldSaveOrderEncounter = true;
                }
            } else {
                logger.info("creating new encounter for accession : " + accessionUrl);
                orderEncounter = accessionMapper.mapToNewEncounter(openElisAccession, LAB_VISIT);
                shouldSaveOrderEncounter = true;
            }

            if (shouldSaveOrderEncounter) {
                //will save new visit as well
                encounterService.saveEncounter(orderEncounter);
            }
            if(openElisAccession.getAccessionNotes() != null && !openElisAccession.getAccessionNotes().isEmpty()){
                processAccessionNotes(openElisAccession, orderEncounter);
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

    private void processAccessionNotes(OpenElisAccession openElisAccession, Encounter orderEncounter) {
        Order labMangerNoteOrder = ordersHelper.getOrderByConceptName(orderEncounter, LAB_MANAGER_NOTES);
        Provider labManagerProvider = providerService.getProviderByIdentifier(LAB_MANAGER_IDENTIFIER);
        EncounterType labResultEncounterType = getLabResultEncounterType();
        Encounter encounter = null;
        if (labMangerNoteOrder == null) {
            labMangerNoteOrder = ordersHelper.createOrderInEncounter(orderEncounter, LAB_ORDER_TYPE, LAB_MANAGER_NOTES);
        } else {
            encounter = encounterHelper.getEncounterByObservationLinkedToOrder(labMangerNoteOrder, orderEncounter.getPatient(), labManagerProvider, labResultEncounterType);
        }

        if (encounter == null) {
            encounter = encounterHelper.createNewEncounter(labResultEncounterType, labManagerProvider, orderEncounter.getPatient());
        }
        saveAccessionNoteInEncounter(encounter, openElisAccession, labMangerNoteOrder);
    }

    private void saveAccessionNoteInEncounter(Encounter encounter, OpenElisAccession openElisAccession, Order labMangerNoteOrder) {
        AccessionDiff accessionNoteDiff = openElisAccession.getAccessionNoteDiff(encounter, labMangerNoteOrder.getConcept());
        List<String> accessionNotes = accessionNoteDiff.getAccessionNotesToBeAdded();
        if (accessionNotes != null && !accessionNotes.isEmpty()) {
            for (String accessionNote : accessionNotes) {
                encounter.addObs(createObsWith(accessionNote, labMangerNoteOrder));
            }
            encounterService.saveEncounter(encounter);
        }
    }

    private Obs createObsWith(String accessionNote, Order labMangerNoteOrder) {
        Obs observation = new Obs();
        observation.setConcept(labMangerNoteOrder.getConcept());
        observation.setValueText(accessionNote);
        observation.setOrder(labMangerNoteOrder);
        return observation;
    }

    protected void associateTestResultsToOrder(OpenElisAccession openElisAccession) throws ParseException {
        Encounter orderEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
        final EncounterType labResultEncounterType = getLabResultEncounterType();
        final Set<OpenElisTestDetail> allTests = openElisAccession.getTestDetails();

        List<Encounter> labResultEncounters = findVisitEncountersOfType(orderEncounter.getVisit(), labResultEncounterType);
        HashSet<Encounter> resultEncounters = new HashSet<>(labResultEncounters);
        Set<Encounter> updatedEncounters = new HashSet<>();
        ResultObsHelper resultObsHelper = new ResultObsHelper(conceptService);
        List<Provider> labResultProviders = new ArrayList<>();
        Visit resultVisit = orderEncounter.getVisit();
        for (OpenElisTestDetail testDetail : allTests) {
            if (testDetail.isReferredOut() && StringUtils.isBlank(testDetail.getDateTime())) {
                testDetail.setDateTime(openElisAccession.getDateTime());
            }
            if (StringUtils.isNotBlank(testDetail.getDateTime())) {
                Order testOrder = identifyOrder(orderEncounter, testDetail);
                Encounter resultEncounterForTest = identifyResultEncounter(resultEncounters, testDetail, testOrder);
                Provider testProvider = getProviderForResults(labResultProviders, testDetail.getProviderUuid());
                boolean isResultUpdated = true;

                Date testDate = DateTime.parse(testDetail.getDateTime()).toDate();
                if (resultEncounterForTest != null) {
                    Obs prevObs = identifyResultObs(resultEncounterForTest, testDetail, testOrder);
                    isResultUpdated = !isSameDate(prevObs.getObsDatetime(), testDate);
                    if (isResultUpdated) {
                        resultObsHelper.voidObs(prevObs, testDate);
                    }
                }

                if (isResultUpdated) {
                    resultEncounterForTest = findOrInitializeEncounter(resultVisit, testProvider, labResultEncounterType, orderEncounter.getEncounterDatetime());
                    resultEncounterForTest.addObs(resultObsHelper.createNewObsForOrder(testDetail, testOrder, resultEncounterForTest));
                    resultVisit.addEncounter(resultEncounterForTest);
                    updatedEncounters.add(resultEncounterForTest);
                    labResultEncounters.add(resultEncounterForTest);
                }
            }
        }

        for (Encounter updatedEncounter : updatedEncounters) {
            encounterService.saveEncounter(updatedEncounter);
        }
    }

    private List<Encounter> findVisitEncountersOfType(Visit visit, EncounterType encounterType) {
        List<Encounter> encounters = new ArrayList<>();
        for (Encounter encounter : visit.getEncounters()) {
            if (encounter.getEncounterType().equals(encounterType)) {
                encounters.add(encounter);
            }
        }
        return encounters;
    }

    private EncounterType getLabResultEncounterType() {
        String resultEncounterType = atomFeedProperties.getEncounterTypeForInvestigation();
        return encounterService.getEncounterType(resultEncounterType);
    }

    /**
     * For a given test/panel result, there ought to be only one encounter containing non voided corresponding observation
     *
     * @param labResultEncounters
     * @param testDetail
     * @param testOrder
     * @return
     */
    private Encounter identifyResultEncounter(HashSet<Encounter> labResultEncounters, OpenElisTestDetail testDetail, Order testOrder) {
        for (Encounter encounter : labResultEncounters) {
            final Obs resultObs = identifyResultObs(encounter, testDetail, testOrder);
            if (resultObs != null) {
                return encounter;
            }
        }
        return null;
    }

    /**
     * This method currenly checks at the topLevel Obs.
     * if its a panel, then it goes through the next level and identifes a test by the concept at the next level
     * If its a test, then it just checks at the top level concept
     * <p/>
     * However, for future multi-value tests, in both the cases (panel and indiv test), it would need go to one more
     * level down and return the matching observation.
     *
     * @param resultEncounter
     * @param testDetail
     * @param testOrder
     * @return
     */
    private Obs identifyResultObs(Encounter resultEncounter, OpenElisTestDetail testDetail, Order testOrder) {
        boolean isPanel = StringUtils.isNotBlank(testDetail.getPanelUuid());
        final Set<Obs> obsAtTopLevel = resultEncounter.getObsAtTopLevel(false);
        for (Obs obs : obsAtTopLevel) {
            if (isPanel && obs.getConcept().getUuid().equals(testDetail.getPanelUuid())) {
                for (Obs member : obs.getGroupMembers()) {
                    if (member.getConcept().getUuid().equals(testDetail.getTestUuid())
                            && member.getOrder().getId().equals(testOrder.getId())) {
                        return member;
                    }
                }
            } else if (obs.getConcept().getUuid().equals(testDetail.getTestUuid())
                    && obs.getOrder().getId().equals(testOrder.getId())) {
                return obs;
            }
        }
        return null;
    }

    private Order identifyOrder(Encounter orderEncounter, OpenElisTestDetail testDetail) {
        for (Order order : orderEncounter.getOrders()) {
            String testConceptUuid = StringUtils.isBlank(testDetail.getPanelUuid()) ? testDetail.getTestUuid() : testDetail.getPanelUuid();
            if (order.getConcept() != null && order.getConcept().getUuid().equals(testConceptUuid)) {
                return order;
            }
        }
        return null; //this should never be the case.
    }

    private Provider getProviderForResults(List<Provider> labResultProviders, String providerUuid) {
        for (Provider labResultProvider : labResultProviders) {
            if (labResultProvider.getUuid().equals(providerUuid)) {
                return labResultProvider;
            }
        }

        Provider provider = null;
        if (StringUtils.isNotBlank(providerUuid)) {
            provider = providerService.getProviderByUuid(providerUuid);
        }

        //the lab results provider may not be register as provider in MRS,
        //hence instead of failing, get the system provider
        if (provider == null) {
            provider = providerService.getProviderByIdentifier(atomFeedProperties.getLabSystemIdentifier());
        }

        labResultProviders.add(provider);
        return provider;
    }

    private Encounter findOrInitializeEncounter(Visit resultVisit, Provider testProvider, EncounterType labResultEncounterType, Date encounterDate) {
        Encounter labResultEncounter = getEncounterByProviderAndEncounterType(testProvider, labResultEncounterType, resultVisit.getEncounters());
        if (labResultEncounter == null) {
            labResultEncounter = accessionMapper.newEncounterInstance(resultVisit, resultVisit.getPatient(), testProvider, labResultEncounterType, encounterDate);
        }
        return labResultEncounter;
    }

    private Encounter getEncounterByProviderAndEncounterType(Provider provider, EncounterType labResultEncounterType, Set<Encounter> labResultEncounters) {
        for (Encounter encounter : labResultEncounters) {
            if (hasSameEncounterType(labResultEncounterType, encounter) && hasSameProvider(provider, encounter)) {
                return encounter;
            }
        }
        return null;
    }

    private boolean hasSameEncounterType(EncounterType labResultEncounterType, Encounter encounter) {
        return encounter.getEncounterType().getUuid().equals(labResultEncounterType.getUuid());
    }

    private boolean hasSameProvider(Provider provider, Encounter encounter) {
        if (encounter.getEncounterProviders().size() > 0) {
            return encounter.getEncounterProviders().iterator().next().getProvider().getUuid().equals(provider.getUuid());
        }
        return false;
    }

    private boolean isSameDate(Date date1, Date date2) {
        return date1.getTime() == date2.getTime();
    }

    @Override
    public void cleanUp(Event event) {

    }
}
