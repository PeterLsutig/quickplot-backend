package eu.nasuta.model.fitting;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.nasuta.controller.DataController;
import lombok.Data;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ScatterCurve {

    String name;
    List<ScatterPoint> series;

    @JsonCreator
    public ScatterCurve(){

    }

    public List<WeightedObservedPoint> getWOPlist (){
        return this.series.stream().map(ScatterPoint::getAsWOP).collect(Collectors.toList());
    }
}
