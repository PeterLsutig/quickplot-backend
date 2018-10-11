package eu.nasuta.service;

import eu.nasuta.model.table.ColumnDescription;
import eu.nasuta.model.table.VarTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParserService {

	Logger logger = LoggerFactory.getLogger(ParserService.class);

	public VarTable<Object> parse(Sheet sheet, boolean removeConstantCols) {

//		List<Row> rows = new ArrayList<>();
//		for (Row row : sheet) rows.add(row);
//
//		Row headerRow = rows.get(0);
//		rows.remove(0);

		class ColumnMetaData {
			String name;
			CellType cellType = CellType._NONE;
			Object lastSeenValue = null;
			boolean isConst = true;

			public ColumnMetaData(String name) {
				this.name = name;
			}

			public void setCellType(CellType cellType) {
				if (this.cellType != null && !Objects.equals(this.cellType, cellType)) {
					throw new IllegalArgumentException("Column with name: `" + name + "` was set to type `" + this.cellType + "` which is different to `" + cellType + "`.");
				}
				this.cellType = cellType;
			}

			public void compareValue(Object value) {
				if (lastSeenValue == null) {
					lastSeenValue = value;
				}
				isConst = isConst && Objects.equals(lastSeenValue, value);
			}
		}

		final Iterator<Row> rowIterator = sheet.iterator();
		Row headerRow = rowIterator.next();
		List<ColumnMetaData> columnMetaData = new ArrayList<>();
		List<List<Object>> table = new ArrayList<>();
		for (Cell cell : headerRow) {
			columnMetaData.add(new ColumnMetaData(cell.getStringCellValue()));
			table.add(new ArrayList<>());
		}

		//column based table
		rowIterator.forEachRemaining(row -> {
			int index = 0;
			for (Cell cell : row) {
				List<Object> column = table.get(index);
				final ColumnMetaData metaData = columnMetaData.get(index);
				final CellType type = cell.getCellTypeEnum();
				metaData.setCellType(type);
				Object value;
				switch (type) {
					case STRING:
						value = cell.getStringCellValue();
						break;
					case ERROR:
					case BLANK:
						value = null;
						break;
					case NUMERIC:
					case FORMULA:
						value = cell.getNumericCellValue();
						break;
					case BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					default:
						throw new RuntimeException("Unknown instance of CellType: " + type);
				}
				metaData.compareValue(value);
				column.add(value);
				index++;
			}
		});

		//remove empty columns
		for (int i = 0; i < columnMetaData.size(); i++) {
			ColumnMetaData columnMetaDatum = columnMetaData.get(i);
			if (columnMetaDatum.isConst && table.get(i).contains(null)) {
				columnMetaData.remove(i);
				table.remove(i);
				i--;
			}
		}

		final List<ColumnDescription> columnDescriptions = columnMetaData.stream()
				.map(cmd -> new ColumnDescription(cmd.name, typeToClass(cmd.cellType)))
				.collect(Collectors.toList());

		return new VarTable<>(columnDescriptions, table);
	}

	private Class<?> typeToClass(CellType cellType) {
		switch (cellType) {
			case FORMULA:
			case NUMERIC:
				return Double.class;
			case STRING:
				return String.class;
			case BOOLEAN:
				return Boolean.class;
			default:
				throw new IllegalArgumentException("Celltype `" + cellType + "` is not supported.");
		}
	}

//	List<ColumHeader> columHeaders = Streams.stream(firstRow.cellIterator())
//			.filter(cell -> !cell.getStringCellValue().isEmpty())
//			.map(cell -> new ColumHeader(cell.getStringCellValue(), cell.getColumnIndex(), null))
//			.collect(Collectors.toList());
//
//		PrintList.printList(columHeaders.stream().map(ch -> ch.getColName()).collect(Collectors.toList()));
//
//	//filter blank rows (at the end of the file for example) //note all cols shall have same size and not be empty by definiton
//	rows = rows.stream()
//			.filter(row -> !(row.getCell(columHeaders.get(0).getColIndex()).getCellTypeEnum() == CellType.BLANK))
//			.collect(Collectors.toList());
//
//	Map<String, List<Double>> dataMap = new HashMap<>();
//		try {
//		for (ColumHeader columHeader : columHeaders) {
//			List<Double> stringColData = rows.stream()
//					.map(row -> row.getCell(columHeader.getColIndex()))
//					.map(Cell::getNumericCellValue)
//					.collect(Collectors.toList());
//			dataMap.put(columHeader.getColName(), stringColData);
//		}
//	} catch (Exception e) {
//		System.out.println("tables does not contain only Numeric values!");
//		throw new IllegalArgumentException();
//	}
//		if (removeConstantCols) {
//		try {
//			List<String> keysToRemove = new ArrayList<>();
//			for (String colKey : dataMap.keySet())
//				if (dataMap.get(colKey).stream().distinct().count() <= 1)
//					keysToRemove.add(colKey);
//			for (String key : keysToRemove) dataMap.remove(key);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("removing zero cols error");
//		}
//	}
//	SingleNumericDataFile singleNumericDataFile = new SingleNumericDataFile();
//		singleNumericDataFile.setData(dataMap);
//		return singleNumericDataFile;
}

