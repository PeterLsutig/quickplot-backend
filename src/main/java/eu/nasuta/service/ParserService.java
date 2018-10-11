package eu.nasuta.service;

import com.google.common.collect.Streams;
import eu.nasuta.model.ColumHeader;
import eu.nasuta.model.SingleNumericDataFile;
import eu.nasuta.utilities.PrintList;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParserService {

    Logger logger = LoggerFactory.getLogger(ParserService.class);

    public SingleNumericDataFile parse(Sheet sheet, boolean removeConstantCols){

        List<Row> rows = new ArrayList<>();
        for (Row row: sheet)rows.add(row);

        Row firstRow = rows.get(0);
        rows.remove(0);

        List<ColumHeader> columHeaders = Streams.stream(firstRow.cellIterator())
                .filter(cell -> !cell.getStringCellValue().isEmpty())
                .map(cell -> new ColumHeader(cell.getStringCellValue(),cell.getColumnIndex(),null))
                .collect(Collectors.toList());

        PrintList.printList(columHeaders.stream().map(ch->ch.getColName()).collect(Collectors.toList()));

        //filter blank rows (at the end of the file for example) //note all cols shall have same size and not be empty by definiton
        rows=rows.stream()
                .filter(row->!(row.getCell(columHeaders.get(0).getColIndex()).getCellTypeEnum() == CellType.BLANK))
                .collect(Collectors.toList());

        Map<String, List<Double>> dataMap = new HashMap<>();
        try {
            for (ColumHeader columHeader: columHeaders){
                List<Double> stringColData = rows.stream()
                        .map(row->row.getCell(columHeader.getColIndex()))
                        .map(Cell::getNumericCellValue)
                        .collect(Collectors.toList());
                dataMap.put(columHeader.getColName(),stringColData);
            }
        }
        catch (Exception e){
            System.out.println("tables does not contain only Numeric values!");
            throw new IllegalArgumentException();
        }
        if (removeConstantCols){
            try{
                List<String> keysToRemove = new ArrayList<>();
                for (String colKey: dataMap.keySet())
                    if (dataMap.get(colKey).stream().distinct().count() <=1)
                        keysToRemove.add(colKey);
                for (String key: keysToRemove) dataMap.remove(key);
            }
            catch (Exception e){
                e.printStackTrace();
                logger.error("removing zero cols error");
            }
        }
        SingleNumericDataFile singleNumericDataFile = new SingleNumericDataFile();
        singleNumericDataFile.setData(dataMap);
        return singleNumericDataFile;
    }
}

