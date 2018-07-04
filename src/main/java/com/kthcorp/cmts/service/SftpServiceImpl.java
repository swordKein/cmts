package com.kthcorp.cmts.service;

//@Service
public interface SftpServiceImpl {
    int pollingCcubeSftp();

    int manualCcubeSftp();

    int pollingSftpDirByExtAndAfterMove(String ip, int port, String user, String passwd
            , String fromPath, String fileExt, String toPath, String movePath);

    int manualSftpDirByExtAndAfterMove(String ip, int port, String user, String passwd
            , String fromPath, String fileExt, String toPath, String movePath);

    int processDownloadMultipleXmlFileToDB(String fileName);

    int manualDownloadMultipleXmlFileToDB(String fileName);

    int uploadToCcube(String work_dir, String fileName);

    int uploadSftpToCcubeOutput(String ip, int port, String user, String passwd
            , String upload_dir, String work_dir, String fileName);

    int uploadToPrismReq();

    int uploadToPrism(String work_add_path, String fileName);

    int uploadSftpToPrism(String ip, int port, String user, String passwd
            , String upload_dir, String work_dir, String work_add_path, String fileName);

    int pollingPrismSftp();
    int pollingSftpDirByExtAndFin(String ip, int port, String user, String passwd
            , String fromPath, String toPath, String work_add_path);
}
