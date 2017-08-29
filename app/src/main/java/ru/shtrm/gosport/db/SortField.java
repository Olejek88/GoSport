package ru.shtrm.gosport.db;

public class SortField {

	private String title;
	private String field;

	public SortField(String title, String field) {
		this.title = title;
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public String toString() {
		return title;
	}
}
