package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Order> getCompletedOrdersFrom(List<Order> allOrders) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.setProjection(Projections.groupProperty("order"));

        criteria.add(Restrictions.in("order", allOrders));
        return criteria.list();
    }

    @Override
    public List<DrugOrder> getActiveDrugOrders(Patient patient) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugOrder.class);

        Criterion notAutoExpired = Restrictions.or(Restrictions.ge("autoExpireDate", new Date()),
                Restrictions.isNull("autoExpireDate"));
        Criterion notDiscontinued = Restrictions.eq("discontinued", false);
        Criterion notVoided = Restrictions.eq("voided", false);

        Junction allConditions = Restrictions.conjunction()
                .add(notAutoExpired)
                .add(notDiscontinued)
                .add(notVoided);
        criteria.add(allConditions)
                .createCriteria("encounter")
                .add(Restrictions.eq("patient", patient));
        return criteria.list();
    }

    @Override
    public List<DrugOrder> getPrescribedDrugOrders(Patient patient, Integer numberOfVisits) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query queryVisitsWithDrugOrders = currentSession.createQuery("select v.visitId from DrugOrder d, Encounter e, Visit v where d.encounter = e.encounterId and e.visit = v.visitId and v.patient = (:patientId) " +
                "and d.voided = false and v.stopDatetime is not null and v.stopDatetime < :now group by v.visitId order by v.startDatetime desc");
        queryVisitsWithDrugOrders.setParameter("patientId", patient);
        queryVisitsWithDrugOrders.setParameter("now", new Date());
        if(numberOfVisits != null ) {
            queryVisitsWithDrugOrders.setMaxResults(numberOfVisits);
        }
        List<Integer> visitWithDrugOrderIds = (List<Integer>) queryVisitsWithDrugOrders.list();
        if(!visitWithDrugOrderIds.isEmpty()) {
            Query query = currentSession.createQuery("select d from DrugOrder d, Encounter e, Visit v where d.encounter = e.encounterId and e.visit = v.visitId and v.visitId in (:visitIds) " +
                    "and d.voided = false order by e.encounterDatetime desc");
            query.setParameterList("visitIds", visitWithDrugOrderIds);
            return (List<DrugOrder>) query.list();
        }
        return new ArrayList<>();
    }
}
