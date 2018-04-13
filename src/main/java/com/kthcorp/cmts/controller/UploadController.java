package com.kthcorp.cmts.controller;

import com.kthcorp.cmts.model.NlpProgs;
import com.kthcorp.cmts.model.UploadModel;
import com.kthcorp.cmts.service.TestService;
import com.kthcorp.cmts.service.UtilServiceImpl;
import com.kthcorp.cmts.util.*;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.cluster.routing.allocation.decider.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Conditional(CheckAdminProfiles.class)
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private TestService testService;
	@Autowired
	private UtilServiceImpl utilService;

	@Value("${spring.static.resource.location}")
	private String UPLOAD_DIR;

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {

		logger.debug("Upload {}", "testing");

		//String r1 = testService.getTest();
		String r2 = testService.getTest();
		System.out.println("#DB result:"+r2);
		model.put("message", "Upload Test");
		return "upload";
	}

	@RequestMapping("/words")
	public String getWords(Map<String, Object> model) {

		logger.debug("words {}", "testing");

		List<NlpProgs> nlpProgsList = null;
		try {
			nlpProgsList = testService.getNlpProgs();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//String r1 = testService.getTest();
		//String r2 = testService.getTest();
		//System.out.println("#DB result:"+r2);
		model.put("message", "getWords");
		model.put("nlpProgsList", nlpProgsList);
		return "words";
	}


	@PostMapping("/api/upload")
	public ResponseEntity<?> uploadFile(
			@RequestParam("file") MultipartFile uploadfile) {

		logger.debug("Single file upload!");

		if (uploadfile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}

		try {

			FileUtils.saveUploadedFiles(Arrays.asList(uploadfile),UPLOAD_DIR);

		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity("Successfully uploaded - " +
				uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

	}

	@PostMapping("/api/upload/multi")
	public ResponseEntity<?> uploadFileMulti(
			@RequestParam("extraField") String extraField,
			@RequestParam("files") MultipartFile[] uploadfiles) {

		logger.debug("Multiple file upload!");

		String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
				.filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

		if (StringUtils.isEmpty(uploadedFileName)) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}

		try {

			FileUtils.saveUploadedFiles(Arrays.asList(uploadfiles),UPLOAD_DIR);

		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity("Successfully uploaded - "
				+ uploadedFileName, HttpStatus.OK);

	}

	// maps html form to a Model
	@PostMapping("/api/upload/multi/model")
	public ResponseEntity<?> multiUploadFileModel(@ModelAttribute UploadModel model) {

		logger.debug("Multiple file upload! With UploadModel");

		try {

			FileUtils.saveUploadedFiles(Arrays.asList(model.getFiles()),UPLOAD_DIR);

		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity("Successfully uploaded!", HttpStatus.OK);

	}

	@PostMapping("/api/detect")
	public ResponseEntity<?> detectFileCharset(
			@RequestParam("file") MultipartFile uploadfile) {

		logger.debug("Single file upload!");

		if (uploadfile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}

		try {

			FileUtils.saveUploadedFiles(Arrays.asList(uploadfile), UPLOAD_DIR);

		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity("Successfully uploaded - " +
				uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

	}



	@PostMapping("/api/read")
	@ResponseBody
	public HashMap<String, Object> readFile(
			@RequestParam("files") MultipartFile uploadfile) {
		logger.debug("Single file Read start!");

		byte[] readByte = null;
		String result_utf8 = "";
		String result_ms949 = "";
		String detCharset = "";

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		if (uploadfile.isEmpty()) {
			//return new ResponseEntity("please select a file!", HttpStatus.OK);
			return null;
		}

		try {
			readByte = FileUtils.readUploadedFiles(Arrays.asList(uploadfile));
			int byteSize = readByte.length;
			int viewSize = 0;

			System.out.println("#byteSize::"+byteSize);

			result_utf8 = FileUtils.convertByteToString(readByte, "utf-8", 30);
			result_ms949 = FileUtils.convertByteToString(readByte, "ms949", 30);

			InputStream fis = new ByteArrayInputStream(readByte);
			//FileInputStream fis = new FileInputStream(uploadfile.getOriginalFilename());
			detCharset = DetectEncoding.guessEncoding(fis);
			System.out.println("# DetectEncoding.detectJchardet result::"+detCharset);

		} catch (Exception e) {
			//return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			e.printStackTrace();
		}

		//return new ResponseEntity("Successfully uploaded - " +
		//		uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

		resultMap.put("readbyte", readByte);
		resultMap.put("strutf8", result_utf8);
		resultMap.put("strms949", result_ms949);
		resultMap.put("detCharset", detCharset);
		return resultMap;
	}



	@PostMapping("/api/read_words")
	@ResponseBody
	public HashMap<String, Object> readFileForWords(
			@RequestParam("files") MultipartFile uploadfile) {
		logger.debug("Single file Read start!");

		List<String> str_id = null;
		List<String> str_orig = null;
		List<String> str_parsed = null;

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		if (uploadfile.isEmpty()) {
			//return new ResponseEntity("please select a file!", HttpStatus.OK);
			return null;
		}

		Map<String, Object> resultMap1 = null;

		try {
			resultMap1 = testService.processFileToNlpsSome(uploadfile, 50);

			//testService.processFileToNlpsResult(uploadfile);
			int rtutil = utilService.runJobTask(testService,"processFileToNlpsResult", uploadfile);
		} catch (Exception e) {
			//return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			e.printStackTrace();
		}

		//return new ResponseEntity("Successfully uploaded - " +
		//		uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

		if (resultMap1 != null) {
			if(resultMap1.get("resultIdArr") != null) str_id = (List<String>) resultMap1.get("resultIdArr");
			if(resultMap1.get("resultOrigArr") != null) str_orig = (List<String>) resultMap1.get("resultOrigArr");
			if(resultMap1.get("resultParsedArr") != null) str_parsed = (List<String>) resultMap1.get("resultParsedArr");
		}

		resultMap.put("str_id", str_id);
		resultMap.put("str_orig", str_orig);
		resultMap.put("str_parsed", str_parsed);
		return resultMap;
	}
}