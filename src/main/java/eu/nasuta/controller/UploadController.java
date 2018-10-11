package eu.nasuta.controller;


import eu.nasuta.model.IUser;
import eu.nasuta.model.SingleNumericDataFile;
import eu.nasuta.repository.SingleDataFileRepository;
import eu.nasuta.security.service.JsonWebTokenAuthenticationService;
import eu.nasuta.service.JsonParserService;
import eu.nasuta.service.ParserService;
import eu.nasuta.utilities.PrintList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@CrossOrigin
@RestController
@RequestMapping("/api/fileupload")
public class UploadController {

    Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    SingleDataFileRepository sDataRepository;

    @Autowired
    ParserService parserService;

    @Autowired
    JsonParserService jsonParserService;

    @Autowired
    JsonWebTokenAuthenticationService auth;

    //for testing
    @CrossOrigin(allowCredentials = "true")
    @GetMapping("/hallo")
    public ResponseEntity<?> hallo(){
        JSONObject json = new JSONObject();
        json.put("msg","hallo");
        return ResponseEntity.status(HttpStatus.OK).body(json.toJSONString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/xlsx/single")
    public ResponseEntity<?> xlxsSingleFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("token") String token,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("uuid") String uuid,
            @RequestParam("removeConstCols") boolean filterConstantCols) {

        IUser user = auth.getUserFromToken(auth.parseToken(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(":(");
        System.out.println("upload by user "+user.toString());
        String orginalFileName = file.getOriginalFilename();
        String fileLocation = null;
        System.out.println(token);

        try {
            InputStream in = file.getInputStream();
            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            fileLocation = path.substring(0, path.length() - 1) + file.getOriginalFilename();

            FileOutputStream f = new FileOutputStream(fileLocation);
            int ch = 0;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }
            f.flush();
            f.close();

        } catch (FileNotFoundException e) {
            logger.error("File not Found Error in xlxsSingleFile() [SecuredController]");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File Error");
        } catch (IOException e) {
            logger.error("File Upload error in xlxsSingleFile() [SecuredController]");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File Error");
        }

        Sheet sheet = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(fileLocation));
            Workbook wb = new XSSFWorkbook(fileInputStream);
            sheet = wb.getSheetAt(0);
        } catch (FileNotFoundException e) {
            logger.error("File not Found Error in xlxsSingleFile() [SecuredController]");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("exel converting error in xlxsSingleFile() [SecuredController]");
            e.printStackTrace();
        }

        SingleNumericDataFile data = parserService.parse(sheet,filterConstantCols);
        data.setName(name);
        data.setDescription(description);
        data.setUsername(user.getUsername());
        data.setOrginalFileName(orginalFileName);
        data.setUuid(uuid);

        PrintList.printSingleDataFile(data);
        System.out.println(data.toString());
        sDataRepository.save(data);
        System.out.println(data.getUuid());
        JSONArray json = jsonParserService.RawDataJson(data);
        return new ResponseEntity<>(json.toJSONString(), HttpStatus.OK);
    }

}
