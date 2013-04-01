package org.raxa.module.raxacore.dao;

import org.raxa.module.raxacore.model.ResultList;

public interface PersonAttributeDoa {
	
	public ResultList getUnique(String personAttribute, String query);
}
