package eu.nasuta.model.fitting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

public class ScatterPoint extends WeightedObservedPoint {

    @JsonCreator
    public ScatterPoint(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("r") double weight){
        super(weight,x,y);
    }

    public WeightedObservedPoint getAsWOP(){
        return new WeightedObservedPoint(this.getWeight(),this.getX(),this.getY());
    }

}
