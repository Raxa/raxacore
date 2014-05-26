package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.*;
import org.springframework.beans.factory.annotation.Autowired;

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
}
