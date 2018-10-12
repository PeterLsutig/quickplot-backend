package eu.nasuta.controller;


import com.codepoetics.protonpack.StreamUtils;
import eu.nasuta.model.DataSet;
import eu.nasuta.model.table.ColumnDescription;
import eu.nasuta.model.table.VarTable;
import eu.nasuta.repository.DataSetRepository;
import eu.nasuta.security.service.JsonWebTokenAuthenticationService;
import eu.nasuta.service.ParserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private DataSetRepository dataSetRepository;

    @Autowired
    public DataController(DataSetRepository dataSetRepository) {
        this.dataSetRepository = dataSetRepository;
    }

    @CrossOrigin
    @GetMapping("/scatter/{xaxis}/{uuid}")
    public JSONArray ScatterData(@PathVariable("xaxis") String xAxis, @PathVariable("uuid") String uuidString){

        VarTable<Double> numericTable = dataSetRepository
                .findById(UUID.fromString(uuidString)).get()
                .getData().toNumericTable();
        int xAxisIndex = numericTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Double>> tSortedNumericTable = transpose(
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(numericTable.iterator(), Spliterator.ORDERED), false)
                        .sorted(Comparator.comparingDouble(row->row.get(xAxisIndex)))
                        .collect(Collectors.toList())
        );

        List<ScatterCurve> res = new ArrayList<>();
        for (int i = 0; i < numericTable.columnDescriptions.size(); i++){
            final int colIndex = i;
            String name = numericTable.columnDescriptions.get(i).getName();
            res.add(new ScatterCurve(
                            name,
                            StreamUtils.zipWithIndex(tSortedNumericTable.get(xAxisIndex).stream())
                                    .map(xVal -> new Serie(
                                            name,
                                            xVal.getValue(),
                                            tSortedNumericTable.get(colIndex).get((int) xVal.getIndex()),
                                            10
                                    )).collect(Collectors.toList())
                    ));
        }
        return new JSONArray(){{addAll(res);}};
    }

    @Data
    @AllArgsConstructor
    private class ScatterCurve{
        String name;
        List<Serie> series;
    }

    @Data
    @AllArgsConstructor
    private class Serie {
        String name;
        double x,y,r;
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
}
