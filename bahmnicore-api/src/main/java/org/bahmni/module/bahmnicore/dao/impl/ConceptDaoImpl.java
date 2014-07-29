package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ConceptDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConceptDaoImpl implements ConceptDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Concept> conceptFor(List<String> conceptNames) {
        List<String> lowerCaseConceptNames = new ArrayList<>();
        for (String conceptName : conceptNames) {
            lowerCaseConceptNames.add(conceptName.toLowerCase());
        }

        // Concept.hbm takes care of sorting by weight
        Query conceptsSortedByWeightQuery = sessionFactory.getCurrentSession().createQuery(
                "select c " +
                        " from Concept as c, ConceptName as cn " +
                        " where cn.concept = c.conceptId " +
                        " and lower(cn.name) in (:lowerCaseConceptNames) " +
                        " and cn.voided=0 " +
                        " and cn.conceptNameType = :conceptNameType");
        conceptsSortedByWeightQuery.setParameterList("lowerCaseConceptNames", lowerCaseConceptNames.toArray());
        conceptsSortedByWeightQuery.setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED);
        return conceptsSortedByWeightQuery.list();
    }
}
