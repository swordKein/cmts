package com.kthcorp.cmts.util;

import com.kthcorp.cmts.model.MovieCine21;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileUtilsTest {

	@Test
	public void testFileReadTest() throws Exception{
		String fileName = "wan.txt";
		String filePath = "e:\\upload\\";

		List<MultipartFile> files = new ArrayList<MultipartFile>();
		MultipartFile file = FileUtils.convertFileToMultipart(filePath,fileName);
		files.add(file);

		File inFile = new File(filePath);
		FileInputStream fileStream = new FileInputStream(inFile);
		String detectCharset = DetectEncoding.guessEncoding(fileStream);
		System.out.println("#Result orig: charset::"+detectCharset);

		byte[] result = FileUtils.readUploadedFiles(files);

		String detCharset2 = DetectEncoding.detectUniversalDetector(result);
		System.out.println("#Result orig: detectJchardet2::"+detCharset2);


		String str = new String (result);
		System.out.println("#Result orig: byte::"+result+" | size:"+str.length()+" | str:"+str.substring(0,40));

		String strUtf8 = FileUtils.convertByteToString(result, "utf-8", 40);
		System.out.println("#Result utf8 str:"+strUtf8);

		boolean isCrack = strUtf8.contains("�");
		System.out.println("#Result utf8 is_CRACK?:"+isCrack);

		String strCp949 = FileUtils.convertByteToString(result, "ms949", 40);
		System.out.println("#Result cp949 str:"+strCp949);
	}


	@Test
	public void test_fileNm() {
		String fileName = "test_csv.txt";
//		String[] fileNames = fileName.split("\\.");
//		String fileNm = fileNames[0];
//		String addNm = DateUtils.getLocalDateTime();
//		String toFileNm = fileNm+"_" + "added"+ "_" + addNm;
//		String extNm = "." + fileNames[fileNames.length-1];
//		toFileNm += extNm;
		String toFileNm = FileUtils.getGenFileName(fileName, "added");


		System.out.println("#toFileNm:"+toFileNm);
	}


	@Test
	public void tes_getCine21Data() {
		try {
			List<MovieCine21> res = FileUtils.getCine21Data();
			System.out.println("#result:"+res.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test_getFileSeperatedDataToArray() {
		try {
			ArrayList<ArrayList<String>> result = FileUtils.getFileSeperatedDataToArray("e:\\dic_not_use.txt", "\\t", 0);
			ArrayList<ArrayList<String>> result2 = new ArrayList();

			for (ArrayList<String> sarr : result) {
				//System.out.println("#result:" + sarr.toString()+" || ");
				if (sarr != null && sarr.size() == 3 && "●".equals(sarr.get(2).trim())) {
					System.out.println("#xresult:" + sarr.toString());

					result2.add(sarr);
				}
			}
			System.out.println("#result.size:"+result2.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
