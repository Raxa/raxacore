package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.model.Episode;

public interface EpisodeDAO {
    public void save(Episode episode);

    public Episode get(Integer episodeId);
}
