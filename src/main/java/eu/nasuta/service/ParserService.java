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

		class ColumnMetaData {
			String name;
			CellType cellType = null;
			Object lastSeenValue = null;
			boolean isConst = true;

			public ColumnMetaData(String name) {
				this.name = name;
			}

			public void setCellType(CellType cellType) {
				if (this.cellType != null
                        && ( !Objects.equals(this.cellType, cellType)
                && (
                        (!Objects.equals(this.cellType, CellType.NUMERIC) && (!Objects.equals(this.cellType, CellType.FORMULA)))
                        && (!Objects.equals(cellType, CellType.NUMERIC) && (!Objects.equals(cellType, CellType.FORMULA)))

                        )
                )
                        ) {
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
				if (value != null) {
				    metaData.compareValue(value);
                    metaData.setCellType(type);

                }
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

}

