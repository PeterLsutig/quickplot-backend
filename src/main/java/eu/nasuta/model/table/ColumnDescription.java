package eu.nasuta.model.table;


import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ColumnDescription {
	private final String name;
	private final String type;
}
