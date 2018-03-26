package com.kthcorp.cmts.service;

import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.SftpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SftpService implements SftpServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cmts.property.serverid}")
    private String serverid;
    @Value("${cmts.property.ccube_sftp.ip}")
    private String ccube_sftp_ip;
    @Value("${cmts.property.ccube_sftp.port}")
    private Integer ccube_sftp_port;
    @Value("${cmts.property.ccube_sftp.user}")
    private String ccube_sftp_user;
    @Value("${cmts.property.ccube_sftp.passwd}")
    private String ccube_sftp_passwd;
    @Value("${cmts.property.ccube_sftp.workdir}")
    private String ccube_sftp_workdir;
    @Value("${cmts.property.ccube_sftp.downdir}")
    private String ccube_sftp_downdir;
    @Value("${cmts.property.ccube_sftp.downext}")
    private String ccube_sftp_downext;
    @Value("${cmts.property.ccube_sftp.end_after_movedir}")
    private String ccube_sftp_end_after_movedir;
    @Value("${cmts.property.ccube_sftp.uploaddir}")
    private String ccube_sftp_uploaddir;

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
                    int rt = SftpClient.retrieveFile(fromPath, fileName, toPath + "/" + fileName);
                    System.out.println("#CcubeSftp::Start Download result::" + rt);

                    if (rt > 0) {
                        SftpClient.move_by_rename_command(fromPath + "/" + fileName, movePath + "/" + fileName);
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
