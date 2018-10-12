package eu.nasuta.model.table;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.JSONArray;

import java.util.*;
import java.util.stream.Collectors;


public class VarTable<T> implements Iterable<List<T>> {

	public List<ColumnDescription> columnDescriptions;
	public List<List<T>> data;

	public VarTable(List<ColumnDescription> columnDescriptions, List<? extends List<T>> data) {
		this.columnDescriptions = columnDescriptions;
		this.data = new ArrayList<>(data.stream().filter(row -> !row.contains(null)).collect(Collectors.toList()));
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
			addAll(StreamUtils.zipWithIndex(transpose(data).stream())
					.peek(l->printList(l.getValue()))
					.map(indexedList -> new RawData(
							columnDescriptions.get((int) indexedList.getIndex()).getName(),
							indexedList.getValue()
					))
					.collect(Collectors.toList()));
		}};
	}

	private static <T> List<List<T>> transpose(List<List<T>> table) {
		List<List<T>> ret = new ArrayList<List<T>>();
		final int N = table.get(0).size();
		for (int i = 0; i < N; i++) {
			List<T> col = new ArrayList<T>();
			for (List<T> row : table) {
				col.add(row.get(i));
			}
			ret.add(col);
		}
		return ret;
	}


	public void printTable(){
		System.out.print("[");
		columnDescriptions.stream().map(cd->cd.getName()).forEach(name-> System.out.print(name+","));
		System.out.println("]");
		data.forEach(VarTable::printList);
	}

	private static void printList(List<?> list) {
		StringJoiner stringJoiner = new StringJoiner("/\t", "[", "]");
		for (Object thing : list) {
			stringJoiner.add(String.valueOf(thing));
		}
		System.out.println(stringJoiner);
	}

	public boolean contains (String curve){
		return columnDescriptions.stream().parallel().map(ch->ch.getName()).anyMatch(colname ->Objects.equals(colname,curve));
	}

	public boolean containsNumericCurve (String curve){
		return columnDescriptions.stream().parallel().anyMatch(cd->Objects.equals(cd, new ColumnDescription(curve,"Double")));
	}

	public VarTable<Double> toNumericTable(){
		List<List<T>> data = transpose(this.data);
		List<List<Double>> res = new ArrayList<>();
		List<ColumnDescription> resDescription = new ArrayList<>();
		for (int i = 0; i < columnDescriptions.size(); i++) {
			ColumnDescription cd = columnDescriptions.get(i);
			if (!Objects.equals(cd.getType(),"Double")) {
				columnDescriptions.remove(i);
				data.remove(i);
				i--;
				continue;
			}
			resDescription.add(columnDescriptions.get(i));
			res.add((List<Double>)data.get(i));
		}
		return new VarTable<Double>(resDescription,transpose(res));
	}

}
