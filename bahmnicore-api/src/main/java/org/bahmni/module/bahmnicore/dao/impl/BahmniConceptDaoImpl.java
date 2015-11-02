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

    private static final String WHITE_SPACE = "\\s+";
    private static final String WILD_CARD = "%";
    private static final String BASE_SEARCH_QUERY = "select answer " +
            "from ConceptAnswer as answer " +
            "join answer.answerConcept.names  as answerConceptNames " +
            "where answer.concept = :questionConcept " +
            " and answerConceptNames.voided = false ";
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Collection<Concept> searchByQuestion(Concept questionConcept, String searchQuery) {
        String[] queryArray = searchQuery.split(WHITE_SPACE);
        StringBuffer queryStringBuffer = new StringBuffer(BASE_SEARCH_QUERY);
        appendSearchQueriesToBase(queryArray, queryStringBuffer);

        Query query = sessionFactory.getCurrentSession().createQuery(
                queryStringBuffer.toString());

        query.setEntity("questionConcept", questionConcept);
        for (int i = 0; i < queryArray.length; i++) {
            query.setString("query"+ i, searchBothSidesOf(queryArray[i]));
        }

        List<ConceptAnswer> answers = query.list();
        HashSet<Concept> resultConcepts = new HashSet<>();
        for (ConceptAnswer answer : answers) {
            resultConcepts.add(answer.getAnswerConcept());
        }
        return resultConcepts;
    }

    private void appendSearchQueriesToBase(String[] queryArray, StringBuffer queryStringBuffer) {
        for (int i = 0; i < queryArray.length; i++) {
            queryStringBuffer.append(" and lower(answerConceptNames.name) like :query" + i);
        }
    }

    private String searchBothSidesOf(String searchString) {
        return WILD_CARD + searchString.trim().toLowerCase() + WILD_CARD;
    }
}
