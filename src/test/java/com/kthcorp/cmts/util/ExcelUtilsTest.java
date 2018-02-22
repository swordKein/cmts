package com.kthcorp.cmts.util;

import com.kthcorp.cmts.model.MovieCine21;
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
public class ExcelUtilsTest {

	@Test
	public void testFileReadTest() throws Exception {
		String fileName = "메타확장_태깅_씨네21_urg02.xlsx";
		String filePath = "E:\\CINE21_1600\\";

		String outFileName = "메타확장_태깅_씨네21_urg02.xlsx.tsv";

		File inputFile = new File(filePath + fileName);
		File outputFile = new File(filePath + outFileName);
	}

}
