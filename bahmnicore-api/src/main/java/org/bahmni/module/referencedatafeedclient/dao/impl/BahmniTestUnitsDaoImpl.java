package org.bahmni.module.referencedatafeedclient.dao.impl;

import org.bahmni.module.referencedatafeedclient.dao.BahmniTestUnitsDao;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.ConceptNumeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BahmniTestUnitsDaoImpl implements BahmniTestUnitsDao {

    private SessionFactory sessionFactory;

    @Autowired
    public BahmniTestUnitsDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void updateUnitsForTests(String newUnit, String oldUnit) {

        Session session = sessionFactory.getCurrentSession();
        String queryString = "select con from Concept con " +
                "inner join con.datatype dat " +
                "inner join con.conceptClass conclass " +
                "where dat.name = 'Numeric' " +
                "and conclass.name = 'Test' " +
                "and con.units = :oldUnit";

        List<ConceptNumeric> conceptList = (List<ConceptNumeric>) session.createQuery(queryString)
                .setParameter("oldUnit", oldUnit)
                .list();

        for (ConceptNumeric conceptToBeSaved : conceptList) {
            conceptToBeSaved.setUnits(newUnit);
            session.saveOrUpdate(conceptToBeSaved);
        }
    }
}
