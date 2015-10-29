package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.BahmniConceptDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public class BahmniConceptDaoImpl implements BahmniConceptDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Collection<Concept> searchByQuestion(Concept questionConcept, String searchQuery) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "select answer " +
                        "from ConceptAnswer as answer " +
                        "join answer.answerConcept.names  as answerConceptNames " +
                        "where answer.concept = :questionConcept " +
                        "and lower(answerConceptNames.name) like :searchQuery");
        query.setEntity("questionConcept", questionConcept);
        query.setString("searchQuery", "%" + searchQuery.toLowerCase() + "%");
        List<ConceptAnswer> answers = query.list();
        HashSet<Concept> resultConcepts = new HashSet<>();
        for (ConceptAnswer answer : answers) {
            resultConcepts.add(answer.getAnswerConcept());
        }
        return resultConcepts;
    }
}
