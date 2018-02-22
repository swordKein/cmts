package com.kthcorp.cmts.service;

//@Service
public interface SftpServiceImpl {
    int pollingCcubeSftp();

    int pollingSftpDirByExtAndAfterMove(String ip, int port, String user, String passwd
            , String fromPath, String fileExt, String toPath, String movePath);
}
