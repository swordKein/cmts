package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeSeries;
import com.kthcorp.cmts.util.DateUtils;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.SftpClient;
import com.kthcorp.cmts.util.XmlUtil;
import org.apache.directory.api.util.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;

@Service
public class SftpService implements SftpServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${property.serverid}")
    private String serverid;
    @Value("${ccube_sftp.ip}")
    private String ccube_sftp_ip;
    @Value("${ccube_sftp.port}")
    private Integer ccube_sftp_port;
    @Value("${ccube_sftp.user}")
    private String ccube_sftp_user;
    @Value("${ccube_sftp.passwd}")
    private String ccube_sftp_passwd;
    @Value("${ccube_sftp.workdir}")
    private String ccube_sftp_workdir;
    @Value("${ccube_sftp.downdir}")
    private String ccube_sftp_downdir;
    @Value("${ccube_sftp.downext}")
    private String ccube_sftp_downext;
    @Value("${ccube_sftp.end_after_movedir}")
    private String ccube_sftp_end_after_movedir;
    @Value("${ccube_sftp.uploaddir}")
    private String ccube_sftp_uploaddir;

    @Autowired
    private CcubeService ccubeService;

    @Autowired
    private ItemsService itemsService;

    @Value("${prism_sftp.ip}")
    private String prism_sftp_ip;
    @Value("${prism_sftp.port}")
    private Integer prism_sftp_port;
    @Value("${prism_sftp.user}")
    private String prism_sftp_user;
    @Value("${prism_sftp.passwd}")
    private String prism_sftp_passwd;
    @Value("${prism_sftp.workdir}")
    private String prism_sftp_workdir;
    @Value("${prism_sftp.downdir}")
    private String prism_sftp_downdir;
    @Value("${prism_sftp.uploaddir}")
    private String prism_sftp_uploaddir;

    @PostConstruct
    public void checkDirs() {
        logger.info("#Ccube_Sftp_check:: workdir:"+ccube_sftp_workdir);
        FileUtils.checkDirAndCreate(ccube_sftp_workdir);
    }
    @PostConstruct
    public void checkDirsForPrism() {
        logger.info("#Prism_Sftp_check:: workdir:"+prism_sftp_workdir);
        FileUtils.checkDirAndCreate(prism_sftp_workdir);
    }

    @Override
    public int pollingCcubeSftp() {
        logger.info("#SftpService:pollingCcubeSftp:: fromDir:"+ccube_sftp_downdir);

        return pollingSftpDirByExtAndAfterMove(ccube_sftp_ip, ccube_sftp_port, ccube_sftp_user, ccube_sftp_passwd
        , ccube_sftp_downdir, ccube_sftp_downext, ccube_sftp_workdir, ccube_sftp_end_after_movedir);
    }

    @Override
    public int manualCcubeSftp() {
        logger.info("#SftpService:pollingCcubeSftp:: fromDir:"+ccube_sftp_downdir);

        return manualSftpDirByExtAndAfterMove(ccube_sftp_ip, ccube_sftp_port, ccube_sftp_user, ccube_sftp_passwd
                , ccube_sftp_downdir, ccube_sftp_downext, ccube_sftp_workdir, ccube_sftp_end_after_movedir);
    }

    @Override
    public int pollingSftpDirByExtAndAfterMove(String ip, int port, String user, String passwd
            , String fromPath, String fileExt, String toPath, String movePath) {
        int rtcode = 0;

        SftpClient client = new SftpClient();
        client.setServer(ip);
        client.setPort(port);
        client.setLogin(user);
        client.setPassword(passwd);
        client.connect();

        try {
            //String fileName = SftpClient.getOneFileName(ccube_sftp_downdir, ccube_sftp_downext);
            List<String> fileNames = SftpClient.getLs(fromPath, fileExt);
            for (String fileName : fileNames) {
                fileName = fileName.trim();

                if (!"".equals(fileName)) {
                    System.out.println("#CcubeSftp::Start Download file::" + fileName);
                    int rt = SftpClient.retrieveFile(fromPath, fileName, toPath + fileName);
                    System.out.println("#CcubeSftp::Start Download result::" + rt);

                    // xml 파싱하여 ccube_contents_orig / ccube_contents,  or  ccube_series_orig / ccube_series 에 등록한다.
                    // _orig 테이블은 등록 당시 stat = Y ,  태깅 완료 후 sftp 전송 완료 후 stat = S
                    // ccube_contents, ccube_series 테이블은 등록 당시 stat = Y , items 테이블 등록 시 stat = S
                    rt = this.processDownloadMultipleXmlFileToDB(ccube_sftp_workdir + fileName);

                    if (rt > 0) {
                        SftpClient.move_by_rename_command(fromPath + "/" + fileName, movePath + "/" + fileNameAddOk(fileName));
                        rtcode++;
                    }
                }
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            client.disconnect();
        }

        return rtcode;
    }


    @Override
    public int manualSftpDirByExtAndAfterMove(String ip, int port, String user, String passwd
            , String fromPath, String fileExt, String toPath, String movePath) {
        int rtcode = 0;

        SftpClient client = new SftpClient();
        client.setServer(ip);
        client.setPort(port);
        client.setLogin(user);
        client.setPassword(passwd);
        client.connect();

        try {
            //String fileName = SftpClient.getOneFileName(ccube_sftp_downdir, ccube_sftp_downext);
            List<String> fileNames = SftpClient.getLs(fromPath, fileExt);
            for (String fileName : fileNames) {
                fileName = fileName.trim();

                if (!"".equals(fileName)) {
                    System.out.println("#CcubeSftp::Start Download file::" + fileName);
                    int rt = SftpClient.retrieveFile(fromPath, fileName, toPath + fileName);
                    System.out.println("#CcubeSftp::Start Download result::" + rt);

                    // xml 파싱하여 ccube_contents_orig / ccube_contents,  or  ccube_series_orig / ccube_series 에 등록한다.
                    // _orig 테이블은 등록 당시 stat = Y ,  태깅 완료 후 sftp 전송 완료 후 stat = S
                    // ccube_contents, ccube_series 테이블은 등록 당시 stat = Y , items 테이블 등록 시 stat = S
                    rt = this.manualDownloadMultipleXmlFileToDB(ccube_sftp_workdir + fileName);

                    if (rt > 0) {
                        SftpClient.move_by_rename_command(fromPath + "/" + fileName, movePath + "/" + fileNameAddOk(fileName));
                        rtcode++;
                    }
                }
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            client.disconnect();
        }

        return rtcode;
    }

    private String fileNameAddOk(String fileName) {
        String result = "";
        if (!"".equals(fileName) && fileName.endsWith("xml")) {
            String fns[] = fileName.trim().split(".xml");
            result = fns[0] + "_OK.xml";
        } else {
            result = fileName;
        }
        return result;
    }

    @Override
    public int processDownloadMultipleXmlFileToDB(String fileName) {
        int rt = 0;

        try {

            NodeList nodeList = null;
            if (fileName.toUpperCase().contains("CONTENT")) {
                nodeList = XmlUtil.readXmlFile(fileName, "CONTENT");

                List<CcubeContent> contentList = this.convertNodeListToCcubeContentList(nodeList);
                System.out.println("#contentList.size:"+contentList.size());

                for(CcubeContent cc : contentList) {
                    cc.setStat("Y");
                    rt = ccubeService.insCcubeContent(cc);
                }

            } else if (fileName.toUpperCase().contains("SERIES")) {
                nodeList = XmlUtil.readXmlFile(fileName, "SERIES");

                List<CcubeSeries> seriesList = this.convertNodeListToCcubeSeriesList(nodeList);
                System.out.println("#seriesList.size:"+seriesList.size());

                for(CcubeSeries cs : seriesList) {
                    cs.setStat("Y");
                    rt = ccubeService.insCcubeSeries(cs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rt;
    }


    @Override
    public int manualDownloadMultipleXmlFileToDB(String fileName) {
        int rt = 0;

        try {

            Timestamp file_date = null;
            try {

                System.out.println("#MLOG fileName:"+fileName);
                String fileNameStr[] = fileName.split(".xml");
                String fileNameStr2 = fileNameStr[0];
                String fileNameStr3[] = fileNameStr2.split("_");
                String fileNameStr4 = "";
                for (String fs : fileNameStr3) {
                    fileNameStr4 = fs;
                }
                //String fileNameStr3 = fileNameStr2.substring(fileNameStr2.length() - 6, fileNameStr2.length() -1);
                System.out.println("#MLOG fileNameDateStr:"+fileNameStr4);
                file_date = DateUtils.getTimeFromStr(DateUtils.getDateStr(fileNameStr4));
            } catch (Exception xe) { }

            NodeList nodeList = null;
            if (fileName.toUpperCase().contains("CONTENT")) {
                nodeList = XmlUtil.readXmlFile(fileName, "CONTENT");

                List<CcubeContent> contentList = this.convertNodeListToCcubeContentList(nodeList);
                System.out.println("#contentList.size:"+contentList.size());

                for(CcubeContent cc : contentList) {
                    cc.setStat("Y");
                    cc.setRegdate(file_date);
                    rt = ccubeService.insCcubeContentManual(cc);
                }

            } else if (fileName.toUpperCase().contains("SERIES")) {
                nodeList = XmlUtil.readXmlFile(fileName, "SERIES");

                List<CcubeSeries> seriesList = this.convertNodeListToCcubeSeriesList(nodeList);
                System.out.println("#seriesList.size:"+seriesList.size());

                for(CcubeSeries cs : seriesList) {
                    cs.setStat("Y");
                    cs.setRegdate(file_date);
                    rt = ccubeService.insCcubeSeriesManual(cs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rt;
    }

    private List<CcubeContent> convertNodeListToCcubeContentList(NodeList nList) {
        List<CcubeContent> resultList = null;
        try {
            resultList = new ArrayList();
            //System.out.println("# req.size:"+nList.getLength());

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    CcubeContent newItem = new CcubeContent();
                    Element eElement = (Element) nNode;

                    newItem.setMaster_content_id(eElement.getElementsByTagName("MASTER_CONTENT_ID").item(0).getTextContent());
                    newItem.setContent_id(eElement.getElementsByTagName("CONTENT_ID").item(0).getTextContent());
                    newItem.setPurity_title(eElement.getElementsByTagName("PURITY_TITLE").item(0).getTextContent());
                    newItem.setContent_title(eElement.getElementsByTagName("CONTENT_TITLE").item(0).getTextContent());

                    newItem.setEng_title(eElement.getElementsByTagName("ENG_TITLE").item(0).getTextContent());
                    newItem.setDirector(eElement.getElementsByTagName("DIRECTOR").item(0).getTextContent());
                    newItem.setYear(eElement.getElementsByTagName("YEAR").item(0).getTextContent());
                    newItem.setActors_display(eElement.getElementsByTagName("ACTORS_DISPLAY").item(0).getTextContent());
                    newItem.setPoster_url(eElement.getElementsByTagName("POSTER_URL").item(0).getTextContent());
                    newItem.setCountry_of_origin(eElement.getElementsByTagName("COUNTRY_OF_ORIGIN").item(0).getTextContent());
                    newItem.setSad_ctgry_id(eElement.getElementsByTagName("SAD_CTGRY_ID").item(0).getTextContent());
                    newItem.setSad_ctgry_nm(eElement.getElementsByTagName("SAD_CTGRY_NM").item(0).getTextContent());
                    newItem.setTitle_brief(eElement.getElementsByTagName("TITLE_BRIEF").item(0).getTextContent());
                    newItem.setDomestic_release_date(eElement.getElementsByTagName("DOMESTIC_RELEASE_DATE").item(0).getTextContent());
                    newItem.setKt_rating(eElement.getElementsByTagName("KT_RATING").item(0).getTextContent());
                    newItem.setRunning_time(eElement.getElementsByTagName("RUNNING_TIME").item(0).getTextContent());
                    newItem.setDetail_genre_display_cd(eElement.getElementsByTagName("DETAIL_GENRE_DISPLAY_CD").item(0).getTextContent());
                    newItem.setDetail_genre_display_nm(eElement.getElementsByTagName("DETAIL_GENRE_DISPLAY_NM").item(0).getTextContent());
                    newItem.setKmrb_id(eElement.getElementsByTagName("KMRB_ID").item(0).getTextContent());
                    newItem.setKmrb_title_nm(eElement.getElementsByTagName("KMRB_TITLE_NM").item(0).getTextContent());
                    newItem.setKmrb_year(eElement.getElementsByTagName("KMRB_YEAR").item(0).getTextContent());
                    newItem.setKmrb_domestic_release_date(eElement.getElementsByTagName("KMRB_DOMESTIC_RELEASE_DATE").item(0).getTextContent());
                    newItem.setKmrb_country_of_origin(eElement.getElementsByTagName("KMRB_COUNTRY_OF_ORIGIN").item(0).getTextContent());
                    newItem.setKmrb_director(eElement.getElementsByTagName("KMRB_DIRECTOR").item(0).getTextContent());
                    newItem.setKmrb_director_eng(eElement.getElementsByTagName("KMRB_DIRECTOR_ENG").item(0).getTextContent());
                    newItem.setKmrb_actors_display(eElement.getElementsByTagName("KMRB_ACTORS_DISPLAY").item(0).getTextContent());
                    newItem.setKmrb_actors_display_eng(eElement.getElementsByTagName("KMRB_ACTORS_DISPLAY_ENG").item(0).getTextContent());

                    if(eElement.getElementsByTagName("SUMMARY_LONG") != null && eElement.getElementsByTagName("SUMMARY_LONG").item(0) != null) {
                        newItem.setSummary_long(eElement.getElementsByTagName("SUMMARY_LONG").item(0).getTextContent());
                    }
                    if (eElement.getElementsByTagName("SUMMARY_MEDIUM") != null && eElement.getElementsByTagName("SUMMARY_MEDIUM").item(0) != null) {
                        newItem.setSummary_medium(eElement.getElementsByTagName("SUMMARY_MEDIUM").item(0).getTextContent());
                    }

                    resultList.add(newItem);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private List<CcubeSeries> convertNodeListToCcubeSeriesList(NodeList nList) {
        List<CcubeSeries> resultList = null;
        try {
            resultList = new ArrayList();
            //System.out.println("# req.size:"+nList.getLength());

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    CcubeSeries newItem = new CcubeSeries();
                    Element eElement = (Element) nNode;

                    newItem.setSeries_id(eElement.getElementsByTagName("SERIES_ID").item(0).getTextContent());
                    newItem.setPurity_title(eElement.getElementsByTagName("PURITY_TITLE").item(0).getTextContent());
                    newItem.setSeries_nm(eElement.getElementsByTagName("SERIES_NM").item(0).getTextContent());
                    newItem.setEng_title(eElement.getElementsByTagName("ENG_TITLE").item(0).getTextContent());
                    newItem.setDirector(eElement.getElementsByTagName("DIRECTOR").item(0).getTextContent());
                    newItem.setYear(eElement.getElementsByTagName("YEAR").item(0).getTextContent());
                    newItem.setActors_display(eElement.getElementsByTagName("ACTORS_DISPLAY").item(0).getTextContent());
                    newItem.setPoster_url(eElement.getElementsByTagName("POSTER_URL").item(0).getTextContent());
                    newItem.setCountry_of_origin(eElement.getElementsByTagName("COUNTRY_OF_ORIGIN").item(0).getTextContent());
                    newItem.setSad_ctgry_id(eElement.getElementsByTagName("SAD_CTGRY_ID").item(0).getTextContent());
                    newItem.setSad_ctgry_nm(eElement.getElementsByTagName("SAD_CTGRY_NM").item(0).getTextContent());
                    newItem.setKt_rating(eElement.getElementsByTagName("KT_RATING").item(0).getTextContent());

                    if(eElement.getElementsByTagName("SUMMARY_LONG") != null && eElement.getElementsByTagName("SUMMARY_LONG").item(0) != null) {
                        newItem.setSummary_long(eElement.getElementsByTagName("SUMMARY_LONG").item(0).getTextContent());
                    }
                    if (eElement.getElementsByTagName("SUMMARY_MEDIUM") != null && eElement.getElementsByTagName("SUMMARY_MEDIUM").item(0) != null) {
                        newItem.setSummary_medium(eElement.getElementsByTagName("SUMMARY_MEDIUM").item(0).getTextContent());
                    }

                    resultList.add(newItem);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public int uploadToCcube(String work_dir, String fileName) {
        int rt = 0;

        logger.info("#SftpService:uploadToCcube:: from:"+work_dir+fileName);
        try {
            rt = this.uploadSftpToCcubeOutput(ccube_sftp_ip, ccube_sftp_port, ccube_sftp_user, ccube_sftp_passwd
                    , ccube_sftp_uploaddir, work_dir, fileName);
        } catch (Exception e) {
            rt = -2;
            e.printStackTrace();
        }
        return rt;
    }

    @Override
    public int uploadSftpToCcubeOutput(String ip, int port, String user, String passwd
            , String upload_dir, String work_dir, String fileName) {
        int rt = 0;

        SftpClient client = new SftpClient();
        client.setServer(ip);
        client.setPort(port);
        client.setLogin(user);
        client.setPassword(passwd);
        client.connect();

        try {

            logger.info("#SftpService:uploadToCcube:: to:"+upload_dir+fileName);
            client.uploadFile(work_dir+fileName, upload_dir+fileName);
            rt = 1;
        } catch (Exception e) {
            rt = -1;
            logger.error("", e);
        } finally {
            client.disconnect();
        }

        return rt;
    }


    @Override
    public int uploadToPrismReq() {
        int rt = 0;

        logger.info("#SftpService:uploadToPrismReq start!");

        try {
            // 전일 입수된 아이템 기준으로 REQ dat 파일을 생성한다.
            // 데이터 조회
            String yesterDate = DateUtils.calculateDate_(GregorianCalendar.DATE, -1, DateUtils.getLocalDate("yyyyMMdd"));
            System.out.println("#SftpPrism getYesterday :: "+yesterDate);

            Map<String,Object> reqMap = new HashMap();
            String yesterDate2 = yesterDate + " 00:00:00";
            reqMap.put("regdate1", yesterDate2);

            List<Map<String, Object>> itemsList = itemsService.getItemsInfoForPrism(reqMap);
            System.out.println("#SftpPrism itemsList:"+itemsList.size());

            if (itemsList != null && itemsList.size() > 0) {
                // 전일자 디렉토리 생성 확인
                String yesterDate3 = yesterDate.replace("-","");
                FileUtils.checkDirAndCreate(prism_sftp_workdir);
                String dayDir = prism_sftp_workdir+"/"+yesterDate3;
                FileUtils.checkDirAndCreate(dayDir);
                dayDir += "/";

                // 파일 쓰기
                String fileContent = this.generatePrismReqFileContent(itemsList, yesterDate3);
                //System.out.println("#SftpPrism getFileContent:"+fileContent);
                String fileName = "OTVMETA_REQ_"+yesterDate3;
                FileUtils.writeFileFromStr(fileContent, dayDir, fileName+".dat", "utf-8");
                FileUtils.writeFileFromStr("", dayDir, fileName+".fin", "utf-8");

                // REQ dat 파일을 PRISM 경로/yyyymmdd/ 아래에 업로드한다.
                // 이후 REQ fin 파일을 업로드한다.
                // 일자별 디렉토리 생성 확인
                // dat -> fin 파일 순으로 업로드
                int rtu = 0;
                try {
                    rtu = this.uploadToPrism(yesterDate3, fileName);
                } catch (Exception ue) {
                    logger.error("#SftpPrism "+yesterDate+" :: upload fail:"+fileName+".dat  caused by "
                            +(ue.getMessage() != null ? ue.getMessage() : ue.getCause()));
                } finally {
                    logger.info("#SftpPrism "+yesterDate+" :: upload success:"+fileName+".dat & fin file");
                }

            }

        } catch (Exception e) {
            rt = -2;
            e.printStackTrace();
        }
        return rt;
    }

    public String generatePrismReqFileContent(List<Map<String,Object>> itemsList, String yesterDate) {
        String result = "";

        String seperator = "|";
        String lineFeed = "\n";
        String fromDate = DateUtils.calculateDate(GregorianCalendar.DATE, -180, yesterDate);

        int cnt = 1;
        for(Map<String, Object> item : itemsList) {
            if (item.get("idx") != null && item.get("title") != null) {
                String thisStr = item.get("idx").toString() + seperator
                        + item.get("title").toString().replace("\\","").replace("|","") + seperator
                        + "" + seperator
                        + fromDate + seperator
                        + yesterDate
                        + lineFeed;

                result += thisStr;
            }
            cnt++;
        }
        return result;
    }

    @Override
    public int uploadToPrism(String work_add_path, String fileName) {
        int rt = 0;

        logger.info("#SftpService:uploadToPrism:: from:"+prism_sftp_workdir+work_add_path+"/"+fileName);
        try {
            String work_dir2 = prism_sftp_workdir;
            String upload_dir2 = prism_sftp_uploaddir;
            if (!"".equals(work_add_path)) {
                work_dir2 += work_add_path + "/";
                upload_dir2 += work_add_path;
            }

            rt = this.uploadSftpToPrism(prism_sftp_ip, prism_sftp_port, prism_sftp_user, prism_sftp_passwd
                    , upload_dir2, work_dir2, work_add_path, fileName);
        } catch (Exception e) {
            rt = -2;
            e.printStackTrace();
        }
        return rt;
    }

    @Override
    public int uploadSftpToPrism(String ip, int port, String user, String passwd
            , String upload_dir, String work_dir, String work_add_path, String fileName) {
        int rt = 0;

        SftpClient client = new SftpClient();
        client.setServer(ip);
        client.setPort(port);
        client.setLogin(user);
        client.setPassword(passwd);
        client.connect();

        try {
            logger.info("#SftpService:uploadSftpToPrism:: to:"+upload_dir+fileName+".dat");

            //String upload_dir2 = upload_dir;
            //if (!"".equals(work_add_path)) {
            //    upload_dir2 += work_add_path;
            //}
            client.dirCheckAndCreate(upload_dir, work_add_path);

            client.uploadFile(work_dir+fileName+".dat", upload_dir+"/"+fileName+".dat");
            client.uploadFile(work_dir+fileName+".fin", upload_dir+"/"+fileName+".fin");
            rt = 1;
        } catch (Exception e) {
            rt = -1;
            logger.error("", e);
        } finally {
            client.disconnect();
        }

        return rt;
    }


    @Override
    public int pollingPrismSftp() {
        logger.info("#SftpService:pollingCcubeSftp:: fromDir:"+ccube_sftp_downdir);

        String yesterDate = DateUtils.calculateDate_(GregorianCalendar.DATE, -1, DateUtils.getLocalDate("yyyyMMdd"));
        String yesterDate3 = yesterDate.replace("-","");

        return pollingSftpDirByExtAndFin(prism_sftp_ip, prism_sftp_port, prism_sftp_user, prism_sftp_passwd
                , prism_sftp_downdir, prism_sftp_workdir, yesterDate3);
    }

    @Override
    public int pollingSftpDirByExtAndFin(String ip, int port, String user, String passwd
            , String fromPath, String toPath, String work_add_path) {
        int rtcode = 0;

        SftpClient client = new SftpClient();
        client.setServer(ip);
        client.setPort(port);
        client.setLogin(user);
        client.setPassword(passwd);
        client.connect();

        try {
            //String fileName = SftpClient.getOneFileName(ccube_sftp_downdir, ccube_sftp_downext);
            String fromPath2 = fromPath;
            String toPath2 = toPath;
            if (!"".equals(work_add_path)) {
                fromPath2 = fromPath2 + "/" + work_add_path;
                toPath2 = toPath2 + "/" + work_add_path + "/";
            }
            List<String> fileNames = SftpClient.getLs(fromPath2 , "fin");
            for (String fileName : fileNames) {
                fileName = fileName.trim();
                String dataFileName = fileName.replace(".fin",".dat");

                // fin 파일 확인하여 dat 파일을 다운로드 한다.
                // 파일명에 REQ가 없는 경우만 다운로드 한다.
                // 실제로는 API 연동을 통해 데이터 취득하므로 다운로드 한 파일을 처리하지는 않는다.

                if (!"".equals(fileName)) {
                    System.out.println("#PrismSftp::Start Download file::" + dataFileName);
                    int rt = SftpClient.retrieveFile(fromPath2, dataFileName, toPath2 + dataFileName);
                    System.out.println("#PrismSftp::Start Download result::" + rt);

                }
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            client.disconnect();
        }

        return rtcode;
    }
}
