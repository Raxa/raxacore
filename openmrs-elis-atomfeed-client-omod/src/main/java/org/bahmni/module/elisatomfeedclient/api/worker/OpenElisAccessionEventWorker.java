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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class OpenElisAccessionEventWorker implements EventWorker {
    public static final String SYSTEM_PROVIDER_IDENTIFIER = "system";
    private ElisAtomFeedProperties atomFeedProperties;
    private HttpClient httpClient;
    private EncounterService encounterService;
    private ConceptService conceptService;
    private AccessionMapper accessionMapper;
    private ProviderService providerService;

    private static Logger logger = Logger.getLogger(OpenElisAccessionEventWorker.class);

    public OpenElisAccessionEventWorker(ElisAtomFeedProperties atomFeedProperties,
                                        HttpClient httpClient,
                                        EncounterService encounterService,
                                        ConceptService conceptService,
                                        AccessionMapper accessionMapper,
                                        ProviderService providerService) {

        this.atomFeedProperties = atomFeedProperties;
        this.httpClient = httpClient;
        this.encounterService = encounterService;
        this.conceptService = conceptService;
        this.accessionMapper = accessionMapper;
        this.providerService = providerService;
    }

    @Override
    public void process(Event event) {
        String accessionUrl = atomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + accessionUrl);
        try {
            OpenElisAccession openElisAccession = httpClient.get(accessionUrl, OpenElisAccession.class);
            Encounter orderEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
            boolean shouldSaveOrderEncounter = false;
            if (orderEncounter != null) {
                AccessionDiff diff = openElisAccession.getDiff(orderEncounter);
                if (diff.hasDifference()) {
                    logger.info("openelisatomfeedclient:updating encounter for accession : " + accessionUrl);
                    accessionMapper.addOrVoidOrderDifferences(openElisAccession, diff, orderEncounter);
                    shouldSaveOrderEncounter = true;
                }
            } else {
                logger.info("openelisatomfeedclient:creating new encounter for accession : " + accessionUrl);
                orderEncounter = accessionMapper.mapToNewEncounter(openElisAccession);
                shouldSaveOrderEncounter = true;
            }

            if (shouldSaveOrderEncounter) {
                encounterService.saveEncounter(orderEncounter);
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
        final EncounterType labResultEncounterType = encounterService.getEncounterType("LAB_RESULT");
        final Set<OpenElisTestDetail> allTests = openElisAccession.getTestDetails();

        List<Encounter> labResultEncounters = encounterService.getEncounters(orderEncounter.getPatient(),
                null, orderEncounter.getEncounterDatetime(), null, null,
                Arrays.asList(labResultEncounterType),
                null, null, null, false);

        Set<Encounter> updatedEncounters = new HashSet<>();
        ResultObsHelper resultObsHelper = new ResultObsHelper(conceptService);
        List<Provider> labResultProviders = new ArrayList<>();
        Visit resultVisit = accessionMapper.findOrCreateVisit(orderEncounter.getPatient(), new Date());
        for (OpenElisTestDetail testDetail : allTests) {
            if (StringUtils.isNotBlank(testDetail.getDateTime())) {
                Encounter resultEncounterForTest = identifyResultEncounter(labResultEncounters, testDetail);
                Order testOrder = identifyOrder(orderEncounter, testDetail);
                Provider testProvider = getProviderForResults(labResultProviders, testDetail.getProviderUuid());
                boolean isResultUpdated = true;

                if (resultEncounterForTest != null) {
                    Obs prevObs = identifyResultObs(resultEncounterForTest, testDetail);
                    final Date testDate = DateTime.parse(testDetail.getDateTime()).toDate();
                    isResultUpdated = !isSameDate(prevObs.getObsDatetime(), testDate);
                    if (isResultUpdated) {
                        resultObsHelper.voidObs(prevObs, testDate);
                        updatedEncounters.add(resultEncounterForTest);
                    }
                }

                if (isResultUpdated) {
                    resultEncounterForTest = findOrCreateEncounter(resultVisit, testProvider, labResultEncounterType);
                    resultEncounterForTest.addObs(resultObsHelper.createNewObsForOrder(testDetail, testOrder, resultEncounterForTest));
                    resultVisit.addEncounter(resultEncounterForTest);
                    updatedEncounters.add(resultEncounterForTest);
                }
            }
        }

        for (Encounter updatedEncounter : updatedEncounters) {
            encounterService.saveEncounter(updatedEncounter);
        }
    }


    /**
     * For a given test/panel result, there ought to be only one encounter containing non voided corresponding observation
     *
     * @param labResultEncounters
     * @param testDetail
     * @return
     */
    private Encounter identifyResultEncounter(List<Encounter> labResultEncounters, OpenElisTestDetail testDetail) {
        for (Encounter encounter : labResultEncounters) {
            final Obs resultObs = identifyResultObs(encounter, testDetail);
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
     *
     * However, for future multi-value tests, in both the cases (panel and indiv test), it would need go to one more
     * level down and return the matching observation.
     * @param resultEncounter
     * @param testDetail
     * @return
     */
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
            provider = providerService.getProviderByIdentifier(SYSTEM_PROVIDER_IDENTIFIER);
        }

        labResultProviders.add(provider);
        return provider;
    }

    private Encounter findOrCreateEncounter(Visit resultVisit,
                                            Provider testProvider, EncounterType labResultEncounterType) {

        Encounter labResultEncounter = getEncounterByProviderAndEncounterType(testProvider, labResultEncounterType, resultVisit.getEncounters());
        if (labResultEncounter == null) {
            labResultEncounter = accessionMapper.newEncounterInstance(resultVisit, resultVisit.getPatient(), testProvider, labResultEncounterType, new Date());
        }
        return  labResultEncounter;
    }

    private Encounter getEncounterByProviderAndEncounterType(Provider provider, EncounterType labResultEncounterType, Set<Encounter> labResultEncounters) {
        for (Encounter encounter : labResultEncounters) {
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
        if(encounter.getEncounterProviders().size() > 0) {
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
