package eu.nasuta.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColumHeader {

    String colName;
    int colIndex;
    String valuesClass;
}
