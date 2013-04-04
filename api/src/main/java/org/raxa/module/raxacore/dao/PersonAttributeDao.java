package org.raxa.module.raxacore.dao;

import org.raxa.module.raxacore.model.ResultList;

public interface PersonAttributeDao {
	
	public ResultList getUnique(String personAttribute, String query);
}
