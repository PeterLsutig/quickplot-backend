package eu.nasuta.model.table;

public class ColumnDescription {
	private final String name;
	private final Class<?> type;

	public ColumnDescription(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}
}
