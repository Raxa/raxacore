package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.EpisodeDAO;
import org.bahmni.module.bahmnicore.model.Episode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EpisodeDAOImpl implements EpisodeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(Episode episode) {
        session().save(episode);
    }

    @Override
    public Episode get(Integer episodeId) {
        return (Episode) session().get(Episode.class, episodeId);
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
