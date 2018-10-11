package eu.nasuta.controller;


import eu.nasuta.model.SingleNumericDataFile;
import eu.nasuta.repository.SingleDataFileRepository;
import eu.nasuta.security.service.JsonWebTokenAuthenticationService;
import eu.nasuta.service.JsonParserService;
import eu.nasuta.service.ParserService;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class DataController {

    Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    SingleDataFileRepository sDataRepository;

    @Autowired
    ParserService parserService;

    @Autowired
    JsonParserService jsonParserService;

    @Autowired
    JsonWebTokenAuthenticationService auth;

    @CrossOrigin
    @PostMapping("/scatter")
    public ResponseEntity<?> xlsxScatterData(
            @RequestParam("x") String xAxis,
            @RequestParam("uuid") String uuid
    ){
        System.out.println("scatter");
        //Todo User check
        SingleNumericDataFile data = sDataRepository.findById(uuid).get();
        if (data == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(":(");


        JSONArray json = null;
        try {
            json = jsonParserService.ScatterDataJson(data,xAxis);
        }catch (Exception e){
            System.out.println("xlsxScatterData parsing error");
        }
        return ResponseEntity.status(HttpStatus.OK).body(json.toJSONString());
    }
}
