package org.raxa.module.raxacore.dao;

import org.raxa.module.raxacore.model.ResultList;

public interface PersonNameDao {
	
	public ResultList getUnique(String key, String query);
}
