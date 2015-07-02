package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.DrugOrderUtil;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.service.OrderMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.bahmniemrapi.encountertransaction.matcher.EncounterSessionMatcher.DEFAULT_SESSION_DURATION_IN_MINUTES;

@Component
public class TestOrderSaveCommandImpl implements EncounterDataPreSaveCommand {

    private OrderMetadataService orderMetadataService;
    private ConceptService conceptService;

    Comparator<EncounterTransaction.DrugOrder> drugOrderStartDateComparator = new Comparator<EncounterTransaction.DrugOrder>() {
        @Override
        public int compare(EncounterTransaction.DrugOrder o1, EncounterTransaction.DrugOrder o2) {
            Date date1 = o1.getScheduledDate();
            Date date2 = o2.getScheduledDate();
            if(date1 == null){
                date1 = new Date();
            }
            if(date2 == null){
                date2 = new Date();
            }
            return date1.compareTo(date2);
        }
    };
    private AdministrationService adminService;


    @Autowired
    public TestOrderSaveCommandImpl(OrderMetadataService orderMetadataService, ConceptService conceptService,@Qualifier("adminService") AdministrationService administrationService) {
        this.orderMetadataService = orderMetadataService;
        this.conceptService = conceptService;
        this.adminService = administrationService;
    }

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int encounterSessionDuration = configuredSessionDuration != null ? Integer.parseInt(configuredSessionDuration) : DEFAULT_SESSION_DURATION_IN_MINUTES;

        for (EncounterTransaction.TestOrder testOrder : bahmniEncounterTransaction.getTestOrders()) {
            if(testOrder.getAutoExpireDate() == null){
                testOrder.setAutoExpireDate(DateTime.now().plusMinutes(encounterSessionDuration).toDate());
            }
        }
        return bahmniEncounterTransaction;
    }



}
