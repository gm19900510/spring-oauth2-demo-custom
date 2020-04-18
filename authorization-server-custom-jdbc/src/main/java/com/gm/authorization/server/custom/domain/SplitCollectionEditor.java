package com.gm.authorization.server.custom.domain;

import org.springframework.beans.propertyeditors.CustomCollectionEditor;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public class SplitCollectionEditor extends CustomCollectionEditor {

	private final Class<? extends Collection> collectionType;
	private final String splitRegex;

	public SplitCollectionEditor(Class<? extends Collection> collectionType, String splitRegex) {
		super(collectionType, true);
		this.collectionType = collectionType;
		this.splitRegex = splitRegex;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text.isEmpty()) {
			super.setValue(super.createCollection(this.collectionType, 0));
		} else {
			super.setValue(text.split(splitRegex));
		}
	}
}
