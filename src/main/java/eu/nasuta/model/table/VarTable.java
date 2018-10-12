package eu.nasuta.model.table;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class VarTable<T> implements Iterable<List<T>> {

	public List<ColumnDescription> columnDescriptions;
	public List<List<T>> data;

	public VarTable(List<ColumnDescription> columnDescriptions, List<? extends List<T>> data) {
		this.columnDescriptions = columnDescriptions;
		List<List<T>> filteredData = new ArrayList<>();
		for (int i =0; i < columnDescriptions.size(); i++){
			filteredData.add(data.get(i).stream().filter(e->e != null).collect(Collectors.toList()));
		}
		this.data = new ArrayList<>(filteredData);
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


	@Data
	@AllArgsConstructor
	private class RawData {
		String key;
		List<T> val;
	}

	public JSONArray rawDataArray(){
		return new JSONArray(){{
			addAll(StreamUtils.zipWithIndex(data.stream())
					.peek(l->printList(l.getValue()))
					.map(indexedList -> new RawData(
							columnDescriptions.get((int) indexedList.getIndex()).getName(),
							indexedList.getValue()
					))
					.collect(Collectors.toList()));
		}};
	}

	public void printTable(){
		for (int i = 0; i< columnDescriptions.size(); i++){
			System.out.println("col: "+ columnDescriptions.get(i).getName() +" type: " + columnDescriptions.get(i).getType());
			printList(data.get(i));
		}
	}

	private static void printList(List<?> list) {
		StringJoiner stringJoiner = new StringJoiner("/", "[", "]");
		for (Object thing : list) {
			stringJoiner.add(String.valueOf(thing));
		}
		System.out.println(stringJoiner);
	}

}
