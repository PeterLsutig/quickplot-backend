package eu.nasuta.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleNumericDataFile {

    @Id
    String uuid;
    String username;
    String name;
    String orginalFileName;
    String description;
    Date date;
    Map<String,List<Double>> data;
}
