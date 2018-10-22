package eu.nasuta.controller;


import com.codepoetics.protonpack.StreamUtils;
import eu.nasuta.model.table.VarTable;
import eu.nasuta.repository.DataSetRepository;
import eu.nasuta.util.QuickplotUtilities;
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
    public JSONArray scatterData(@PathVariable("xaxis") String xAxis, @PathVariable("uuid") String uuidString){

        VarTable<Double> numericTable = dataSetRepository
                .findById(UUID.fromString(uuidString)).get()
                .getData().toNumericTable();
        int xAxisIndex = numericTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Double>> tSortedNumericTable = QuickplotUtilities.transpose(
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
                                    .map(xVal -> new ScatterPoint(
                                            name,
                                            xVal.getValue(),
                                            tSortedNumericTable.get(colIndex).get((int) xVal.getIndex()),
                                            10
                                    )).collect(Collectors.toList())
                    ));
        }
        return new JSONArray(){{addAll(res);}};
    }

    @CrossOrigin
    @PostMapping(value = "/scatter/filter/{xaxis}/{uuid}",consumes="application/json",produces="application/json")
    public JSONArray ScatterFiltered(
            @PathVariable("xaxis") String xAxis,
            @PathVariable("uuid") String uuidString,
            @RequestBody String[] selectedCurves){
        VarTable<Double> numericTable = dataSetRepository
                .findById(UUID.fromString(uuidString)).get()
                .getData().toNumericTable();
        int xAxisIndex = numericTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Double>> tSortedNumericTable = QuickplotUtilities.transpose(
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(numericTable.iterator(), Spliterator.ORDERED), false)
                        .sorted(Comparator.comparingDouble(row->row.get(xAxisIndex)))
                        .collect(Collectors.toList())
        );

        List<ScatterCurve> res = new ArrayList<>();
        for (int i = 0; i < numericTable.columnDescriptions.size(); i++){
            final int colIndex = i;
            String name = numericTable.columnDescriptions.get(i).getName();
            if (Arrays.asList(selectedCurves).contains(name)){
                res.add(new ScatterCurve(
                        name,
                        StreamUtils.zipWithIndex(tSortedNumericTable.get(xAxisIndex).stream())
                                .map(xVal -> new ScatterPoint(
                                        name,
                                        xVal.getValue(),
                                        tSortedNumericTable.get(colIndex).get((int) xVal.getIndex()),
                                        10
                                )).collect(Collectors.toList())
                ));
            }
        }
        return new JSONArray(){{addAll(res);}};
    }

    @CrossOrigin
    @GetMapping("/multi/{xaxis}/{uuid}")
    public JSONArray multiData(@PathVariable("xaxis") String xAxis, @PathVariable("uuid") String uuidString){

        VarTable<Object> multiTable = dataSetRepository
                .findById(UUID.fromString(uuidString)).get()
                .getData().toMultiTable(xAxis);
        int xAxisIndex = multiTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Object>> tMultiTable = QuickplotUtilities.transpose(multiTable.data);

        List<MultiData> res = new ArrayList<>();
        for (int i = 0; i < multiTable.columnDescriptions.size(); i++){
            final int colIndex = i;
            String name = multiTable.columnDescriptions.get(i).getName();
            try {
                res.add(new MultiData(
                        name,
                        StreamUtils.zipWithIndex(tMultiTable.get(xAxisIndex).stream())
                                .map(xVal -> new JData(
                                        xVal.getValue().toString(),
                                        Double.parseDouble(
                                                tMultiTable.get(colIndex).get((int) xVal.getIndex()).toString()
                                        )
                                )).collect(Collectors.toList())
                ));
            }
            catch (Exception e){
                System.out.println("probalby parsing error:" + e);
            }

        }

        return new JSONArray(){{addAll(res);}};
    }

    @CrossOrigin
    @PostMapping(value = "/multi/filter/{xaxis}/{uuid}",consumes="application/json",produces="application/json")
    public JSONArray multiDataFiltered(
            @PathVariable("xaxis") String xAxis,
            @PathVariable("uuid") String uuidString,
            @RequestBody String[] selectedCurves){
        Arrays.asList(selectedCurves).forEach(System.out::println);
        VarTable<Object> multiTable = dataSetRepository
                .findById(UUID.fromString(uuidString)).get()
                .getData().toMultiTable(xAxis);
        int xAxisIndex = multiTable.columnDescriptions.stream().map(e->e.getName()).collect(Collectors.toList()).indexOf(xAxis);

        List<List<Object>> tMultiTable = QuickplotUtilities.transpose(multiTable.data);

        List<MultiData> res = new ArrayList<>();
        for (int i = 0; i < multiTable.columnDescriptions.size(); i++){
            final int colIndex = i;
            String name = multiTable.columnDescriptions.get(i).getName();
            if (Arrays.asList(selectedCurves).contains(name)){
                try {
                    res.add(new MultiData(
                            name,
                            StreamUtils.zipWithIndex(tMultiTable.get(xAxisIndex).stream())
                                    .map(xVal -> new JData(
                                            xVal.getValue().toString(),
                                            Double.parseDouble(
                                                    tMultiTable.get(colIndex).get((int) xVal.getIndex()).toString()
                                            )
                                    )).collect(Collectors.toList())
                    ));
                }
                catch (Exception e){
                    //ignore double parsing errors
                }
            }
        }

        return new JSONArray(){{addAll(res);}};
    }

    @Data
    @AllArgsConstructor
    private class ScatterCurve{
        String name;
        List<ScatterPoint> series;
    }

    @Data
    @AllArgsConstructor
    private class ScatterPoint {
        String name;
        double x,y,r;
    }

    @Data
    @AllArgsConstructor
    private class MultiData{
        String name; //x val | see ngx/ chart js documentation
        List<JData> series;
    }

    @Data
    @AllArgsConstructor
    private class JData{
        String name; // curve name
        double value; //y val
    }
}
