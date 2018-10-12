package eu.nasuta.model.table;

public class ColumnDescription {
	private final String name;
	private final String type;

	public ColumnDescription(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
