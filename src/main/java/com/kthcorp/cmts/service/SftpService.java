package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeSeries;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.SftpClient;
import com.kthcorp.cmts.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

    @PostConstruct
    public void checkDirs() {
        logger.info("#Ccube_Sftp_check:: workdir:"+ccube_sftp_workdir);
        FileUtils.checkDirAndCreate(ccube_sftp_workdir);
    }

    @Override
    public int pollingCcubeSftp() {
        logger.info("#SftpService:pollingCcubeSftp:: fromDir:"+ccube_sftp_downdir);

        return pollingSftpDirByExtAndAfterMove(ccube_sftp_ip, ccube_sftp_port, ccube_sftp_user, ccube_sftp_passwd
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
}
