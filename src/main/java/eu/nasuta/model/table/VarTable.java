package eu.nasuta.model.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class VarTable<T> implements Iterable<List<T>> {

	public List<ColumnDescription> columnDescriptions;
	public List<List<T>> data;

	public VarTable(List<ColumnDescription> columnDescriptions, List<? extends List<T>> data) {
		this.columnDescriptions = columnDescriptions;
		this.data = new ArrayList<>(data);
	}

	public ColumnDescription getColumnDescription(int index) {
		return columnDescriptions.get(index);
	}

	public List<T> getRow(int index) {
		return data.get(index);
	}

	public List<T> getColumn(int index) {
		return data.stream().map(l -> l.get(index)).collect(Collectors.toList());
	}

	public List<T> getColumn(String name) {
		for (int i = 0; i < columnDescriptions.size(); i++) {
			ColumnDescription columnDescription = columnDescriptions.get(i);
			if (name.equals(columnDescription.getName())) {
				return getColumn(i);
			}
		}
		throw new IllegalArgumentException("Unknown column name: " + name);
	}

	public int size() {
		return data.size();
	}

	public Iterator<List<T>> iterator() {
		return data.iterator();
	}

}
