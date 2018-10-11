package eu.nasuta.controller;


import eu.nasuta.dto.ColumnDTO;
import eu.nasuta.model.DataSet;
import eu.nasuta.model.User;
import eu.nasuta.model.table.VarTable;
import eu.nasuta.repository.DataSetRepository;
import eu.nasuta.security.service.JsonWebTokenAuthenticationService;
import eu.nasuta.service.ParserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/api/fileupload")
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	private DataSetRepository dataSetRepository;
	private ParserService parserService;
	private JsonWebTokenAuthenticationService auth;

	@Autowired
	public UploadController(DataSetRepository dataSetRepository, ParserService parserService, JsonWebTokenAuthenticationService auth) {
		this.dataSetRepository = dataSetRepository;
		this.parserService = parserService;
		this.auth = auth;
	}


	//for testing
	@CrossOrigin(allowCredentials = "true")
	@GetMapping("/hallo")
	public ResponseEntity<?> hallo() {
		JSONObject json = new JSONObject();
		json.put("msg", "hallo");
		return ResponseEntity.status(HttpStatus.OK).body(json.toJSONString());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/xlsx/single")
	public List<ColumnDTO> xlxsSingleFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam("token") String token,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("uuid") String uuid,
			@RequestParam("removeConstCols") boolean filterConstantCols) {

		User user = auth.getUserFromToken(auth.parseToken(token));
		if (user == null) throw new RuntimeException("Du HUUUURENSOHN!");
		System.out.println("upload by user " + user.toString());
		String originalFileName = file.getOriginalFilename();
		System.out.println(token);
		Workbook wb;
		try (InputStream in = file.getInputStream()){
			wb = new XSSFWorkbook(in);
		} catch (IOException e) {
			logger.error("Failed to read in file as Excel sheet.", e);
			throw new RuntimeException("Failed to read in file as Excel sheet.", e);
		}
		Sheet sheet = wb.getSheetAt(0);

		VarTable<Object> table = parserService.parse(sheet, filterConstantCols);
		DataSet data = new DataSet();
		data.setName(name);
		data.setDescription(description);
		data.setOwner(user);
		data.setFileName(originalFileName);
		data.setUuid(UUID.fromString(uuid));

		System.out.println(data);
		dataSetRepository.save(data);
		System.out.println(data.getUuid());



		List<ColumnDTO> columns = new ArrayList<>();
		for (int i = 0; i < table.size(); i++) {
			columns.add(new ColumnDTO(table.getColumnDescription(i).getName(), table.getColumn(i)));
		}


		return columns;
	}

}
