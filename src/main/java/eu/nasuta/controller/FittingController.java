package eu.nasuta.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.nasuta.model.fitting.ScatterCurve;
import eu.nasuta.model.fitting.ScatterPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONArray;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/fitting")
public class FittingController {

    @CrossOrigin
    @PostMapping(value = "/scatter/{curvetype}",consumes="application/json",produces="application/json")
    public JSONArray scatterFitting(
            @PathVariable("curvetype") String curvetype,
            @RequestBody ScatterCurve[] scatterCurves) {
        System.out.println(curvetype);
        Arrays.stream(scatterCurves).forEach(System.out::println);
        scatterCurves[0].getSeries().stream().forEach(sp-> System.out.println("x :"+ sp.getX() +" \tY: " +sp.getY()));

        JSONArray json = new JSONArray();
        PolynomialCurveFitter fitter;
        for (ScatterCurve scatterCurve: scatterCurves){
            fitter =PolynomialCurveFitter.create(1);
            double[] coeffs = fitter.fit(scatterCurve.getWOPlist());
            System.out.println(coeffs);
            System.out.println(coeffs[0]+"\t"+coeffs[1]);
            json.add(new ReturnFormat(scatterCurve.getName(),"affine",coeffs));
        }

        return json;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class ReturnFormat{
        String name;
        String type;
        double[] params;
    }
}
