package org.raxa.module.raxacore.model;

import java.util.LinkedHashMap;

public class SimpleObjectExtractor {
	
	private LinkedHashMap post;
	
	public SimpleObjectExtractor(java.util.LinkedHashMap post) {
		this.post = post;
	}
	
	public <T> T extract(String key) {
		return (post == null || key == null) ? null : (T) post.get(key);
	}
}
