package org.raxa.module.raxacore.model;

import java.util.ArrayList;
import java.util.List;

public class NameList {

	private List<String> names;

	public NameList(List<String> names) {
		this.names = names == null ? new ArrayList<String>() : names;
	}

	public List<String> getNames() {
		return names;
	}

	public int size() {
		return names.size();
	}
}
