package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPostSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.OrderWithUrgency;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderPostSaveCommandImpl implements EncounterDataPostSaveCommand {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Encounter currentEncounter, EncounterTransaction updatedEncounterTransaction) {
        for (EncounterTransaction.Order savedOrder : updatedEncounterTransaction.getOrders()) {
            String urgency = getUrgencyForOrderHavingConcept(savedOrder.getConceptUuid(), bahmniEncounterTransaction);
            if(urgency != null) {
                String orderUuid = savedOrder.getUuid();
                Query query = sessionFactory.getCurrentSession().createSQLQuery("update orders set urgency=:urgency where uuid=:orderUuid");
                query.setParameter("urgency", urgency);
                query.setParameter("orderUuid", orderUuid);
                query.executeUpdate();
            }
        }
        return updatedEncounterTransaction;
    }

    private String getUrgencyForOrderHavingConcept(String conceptUuid, BahmniEncounterTransaction bahmniEncounterTransaction) {
        for (OrderWithUrgency orderWithUrgency : bahmniEncounterTransaction.getOrdersWithUrgency()) {
            if (orderWithUrgency.getConceptUuid().equals(conceptUuid)) {
                return orderWithUrgency.getUrgency();
            }
        }
        return null;
    }
}
