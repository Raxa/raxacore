package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.model.BahmniPersonAttributeType;

import java.util.List;

public interface PersonAttributeTypeDao {
	public List<BahmniPersonAttributeType> getAll();
}
