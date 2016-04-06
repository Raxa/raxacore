package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.EpisodeDAO;
import org.bahmni.module.bahmnicore.model.Episode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.PatientProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EpisodeDAOImpl implements EpisodeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Episode episode) {
        session().save(episode);
    }

    @Override
    public Episode get(Integer episodeId) {
        return (Episode) session().get(Episode.class, episodeId);
    }

    @Override
    public Episode getEpisodeForPatientProgram(PatientProgram patientProgram) {
        return (Episode) session().createQuery(
                "SELECT e FROM Episode e " +
                        "INNER JOIN e.patientPrograms pp " +
                        "WHERE pp = :patientProgram")
                .setParameter("patientProgram", patientProgram)
                .uniqueResult();
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
