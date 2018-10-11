package eu.nasuta.service;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import eu.nasuta.model.SingleNumericDataFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsonParserService {

    //Frontend Scater Format: {name:string, series: {name: string, x: number, y: number, r: number}[] }[]

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

    @Data
    @AllArgsConstructor
    private class RawData {
        String key;
        List<Double> val;
    }

    @Data
    @AllArgsConstructor
    private class Pair{
        double num;
        int index;
    }

    public JSONArray RawDataJson(SingleNumericDataFile sdf){
        if (sdf == null) throw new IllegalArgumentException();
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(sdf
                .getData()
                .keySet()
                .stream()
                .map(key->new RawData(key, sdf.getData().get(key)))
                .collect(Collectors.toList()));
        return jsonArray;
    }

    public JSONArray ScatterDataJson(SingleNumericDataFile data, String xAxis){
        List<Double> xOriginalData = data.getData().get(xAxis);
        Set<String> yColNames = data.getData().keySet(); yColNames.remove(xAxis);

        if (xOriginalData == null || xOriginalData.isEmpty() || yColNames.isEmpty()) throw new IllegalArgumentException();

        final List<Pair> sortedXPairs = StreamUtils.zipWithIndex(xOriginalData.stream())
                .map(num->new Pair(num.getValue(),(int)num.getIndex()))
                .sorted(Comparator.comparingDouble(Pair::getNum))
                .collect(Collectors.toList());

        JSONArray json = new JSONArray();
        List<ScatterCurve> curves = yColNames.parallelStream()
                .map(yColName -> new ScatterCurve(
                        yColName,
                        sortedXPairs.stream().map(xPair->new Serie(
                                yColName,
                                xPair.getNum(),
                                data.getData().get(yColName).get(xPair.getIndex()),
                                10))
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
        json.addAll(curves);

        /*
        final List<Integer> sortedXIndices = StreamUtils.zipWithIndex(xOriginalData.stream())
                .sorted(indexedDouble ->Comparator.comparingDouble(indexedDouble.getValue()))
                .map(indexedDouble->indexedDouble.getIndex())
                .collect(Collectors.toList());
                */


        return json;
    }
}
