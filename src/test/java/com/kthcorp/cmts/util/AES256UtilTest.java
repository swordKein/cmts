package com.kthcorp.cmts.util;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URLEncoder;

import static org.junit.Assert.*;
 
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AES256UtilTest {

    @Test
    public void testEncDecTest() throws Exception {
    	String key = "sdjnfio2390dsvjklwwe90jf2";
    	AES256Util aes256 = new AES256Util(key);
    	
    	String orgText = "ollehmeta_";
    	String orgDate = DateUtils.getLocalDate2();
    	orgText += orgDate;

    	/*  result ::   orgText : ollehmeta_2018-01-29 :: encText : hGavIsRSA/0YUOXytZeDhfIFh+28rCsj6IKLWDkD+7g= :: decText : ollehmeta_2018-01-29 */

    	/* hGavIsRSA%2F0YUOXytZeDhfIFh%2B28rCsj6IKLWDkD%2B7g%3D */

    	String encText = aes256.aesEncode(orgText);
    	String urlencode_encText = URLEncoder.encode(encText, "UTF-8");

    	String decText = aes256.aesDecode(encText);

    	System.out.println(" result ::   orgText : "+orgText+" :: encText : "+encText+" || urlencode_encText : "+urlencode_encText+" :: decText : "+decText);
    }
    
    @Test
    public void testGetPassTime() throws Exception {
    	String sDate = "";
    	String eDate = "20151019112159";
    	long diffTime = DateUtils.getPassTime2(sDate, eDate);
    	System.out.println(" result txt :: " + diffTime);
    }
}