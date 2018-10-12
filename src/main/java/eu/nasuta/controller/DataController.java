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

        System.out.println("scatter, " +xAxis+" , "+uuidString);

        DataSet dataSet = dataSetRepository.findById(UUID.fromString(uuidString)).get();

        VarTable<Double> numericTable= dataSet.getData().toNumericTable();

        int xAxisIndex = numericTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Double>> sortedTable= StreamSupport.stream(Spliterators.spliteratorUnknownSize(numericTable.iterator(), Spliterator.ORDERED), false)
        .sorted(Comparator.comparingDouble(row->row.get(xAxisIndex)))
        .collect(Collectors.toList());

        VarTable<Double> sortedNumericTable = new VarTable<>(numericTable.columnDescriptions,sortedTable);

        sortedNumericTable.printTable();

        List<ScatterCurve> scatterCurves = numericTable.columnDescriptions.stream()
                .map(c->new ScatterCurve(c.getName(),new ArrayList<Serie>()))
                .collect(Collectors.toList());

        for (int i = 0; i<sortedNumericTable.size(); i++){
            for (ScatterCurve curve: scatterCurves){
                curve.series.add(new Serie(
                        curve.name,
                        sortedNumericTable.getColumn(xAxisIndex).get(i),
                        sortedNumericTable.getColumn(curve.name).get(i),
                        10
                        ));
            }
        }

        List<ScatterCurve> scatterCurves2 = numericTable.columnDescriptions.stream()
                .map(c->new ScatterCurve(c.getName(),new ArrayList<Serie>()))
                .collect(Collectors.toList());

        List<List<Double>> sortedTable2= StreamSupport.stream(Spliterators.spliteratorUnknownSize(numericTable.iterator(), Spliterator.ORDERED), false)
                .sorted(Comparator.comparingDouble(row->row.get(xAxisIndex)))
                .peek(row->{
                    for (int i = 0; i< scatterCurves2.size();i++){
                        scatterCurves2.get(i).series.add(
                                new Serie(
                                        scatterCurves2.get(i).name,
                                        row.get(xAxisIndex),
                                        row.get(i),
                                        10)
                        );
                    }
                })
                .collect(Collectors.toList());

        /*
        StreamUtils.zipWithIndex(sortedNumericTable.columnDescriptions.stream())
                .map(col-> new ScatterCurve(
                        col.getValue().getName(),
                        sortedNumericTable.data.get(col.getIndex()).stream()
                        .map(cell-> new Serie(
                                col.getValue().getName(),
                                sortedNumericTable.getRow()
                                ))
                ))
                */


        return new JSONArray();
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
}
