package org.bahmni.module.bahmnicore.dao;

public interface AbstractDao {

    public <T> T getByUuid(String uuid, Class<T> className);
}
