package eu.nasuta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ColumnDTO {
	@JsonProperty
	private String columnName;
	@JsonProperty
	private List<Object> values;

	public ColumnDTO(String columnName, List<Object> values) {
		this.columnName = columnName;
		this.values = values;
	}
}
