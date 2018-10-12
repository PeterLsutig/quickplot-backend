package eu.nasuta.controller;


import eu.nasuta.model.DataSet;
import eu.nasuta.model.table.VarTable;
import eu.nasuta.repository.DataSetRepository;
import eu.nasuta.security.service.JsonWebTokenAuthenticationService;
import eu.nasuta.service.ParserService;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private DataSetRepository dataSetRepository;
    private ParserService parserService;
    private JsonWebTokenAuthenticationService auth;

    @Autowired
    public DataController(DataSetRepository dataSetRepository, ParserService parserService, JsonWebTokenAuthenticationService auth) {
        this.dataSetRepository = dataSetRepository;
        this.parserService = parserService;
        this.auth = auth;
    }

    @CrossOrigin
    @GetMapping("/scatter/{xaxis}/{uuid}")
    public JSONArray ScatterData(
            @PathVariable("xaxis") String xAxis,
            @PathVariable("uuid") String uuidString
    ){
        System.out.println("scatter, " +xAxis+" , "+uuidString);
        DataSet dataSet = dataSetRepository.findById(UUID.fromString(uuidString)).get();
        System.out.println(dataSet.getOwner().getUsername());

        return new JSONArray();
    }
}
