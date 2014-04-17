package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonObsDaoImpl implements PersonObsDao {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public List<Obs> getObsByPerson(String personUUID) {
        Query query = sessionFactory
                .getCurrentSession().createQuery(
                        "select obs from Obs as obs inner join fetch " +
                                "obs.concept as concept inner join fetch " +
                                "concept.datatype as datatype inner join " +
                                "obs.person as person " +
                                "where datatype.hl7Abbreviation= 'NM' and person.uuid= :personUUID");
        query.setString("personUUID", personUUID);
        return query.list();

    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        Query query = sessionFactory
                .getCurrentSession().createQuery(
                        "select concept from Obs as obs inner join " +
                                "obs.concept as concept inner join " +
                                "concept.datatype as datatype inner join " +
                                "obs.person as person " +
                                "where datatype.hl7Abbreviation= 'NM' and person.uuid= :personUUID");
        query.setString("personUUID", personUUID);
        return query.list();

    }
}
