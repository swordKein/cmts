package com.kthcorp.cmts.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class SftpClient {

    private final static Logger logger = LoggerFactory.getLogger(SftpClient.class);

    private String server;
    private int port;
    private String login;
    private String password;

    public static JSch jsch = null;
    public static Session session = null;
    public static Channel channel = null;
    public static ChannelSftp c = null;

    public SftpClient(String server, int port, String login, String password) {
        this.server = server;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    /**
     * Connects to the server and does some commands.
     */
    public void connect() {
        try {
            logger.debug("#SftpClient::Initializing jsch");
            jsch = new JSch();
            session = jsch.getSession(login, server, port);

            // Java 6 version
            session.setPassword(password.getBytes(Charset.forName("ISO-8859-1")));

            // Java 5 version
            // session.setPassword(password.getBytes("ISO-8859-1"));

            logger.debug("#SftpClient::Jsch set to StrictHostKeyChecking=no");
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("Connecting to " + server + ":" + port);
            session.connect();
            logger.info("Connected !");

            // Initializing a channel
            logger.debug("#SftpClient::Opening a channel ...");
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            logger.debug("#SftpClient::Channel sftp opened");

        } catch (JSchException e) {
            logger.error("", e);
        }
    }

    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     * @throws Exception if connection and channel are not available or if an error occurs during upload.
     */
    public void uploadFile(String sourceFile, String destinationFile) throws Exception {
        if (c == null || session == null || !session.isConnected() || !c.isConnected()) {
            throw new Exception("Connection to server is closed. Open it first.");
        }

        try {
            logger.debug("#SftpClient::Uploading file to server");
            c.put(sourceFile, destinationFile);
            logger.info("Upload successfull.");
        } catch (SftpException e) {
            throw new Exception(e);
        }
    }

    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     * @throws Exception if connection and channel are not available or if an error occurs during download.
     */
    public static Integer retrieveFile(String filePath, String sourceFile, String destinationFile) throws Exception {
        if (c == null || session == null || !session.isConnected() || !c.isConnected()) {
            throw new Exception("Connection to server is closed. Open it first.");
        }
        int rtcode = -1;

        try {
            logger.debug("#SftpClient::Downloading file to server");
            c.cd(filePath);
            logger.debug("#SftpClient::cd to::"+c.pwd());
            c.get(sourceFile, destinationFile);
            logger.info("#SftpClient::Download successfull.");
            rtcode = 1;
        } catch (SftpException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }

        return rtcode;
    }

    public static void dirCheckAndCreate(String filePath, String add_path) throws Exception {
        if (c == null || session == null || !session.isConnected() || !c.isConnected()) {
            throw new Exception("Connection to server is closed. Open it first.");
        }

        try {
            //String currentDirectory = c.pwd();

            SftpATTRS attrs = null;
            try {
                c.cd(filePath);
                c.stat(filePath + "/" + add_path);
            } catch (Exception de) {
            }

            if (attrs != null) {
                System.out.println("#sftp dest DIR:" + attrs.isDir());
            } else {
                System.out.println("#sftp create DIR:" + filePath);
                try {
                    c.mkdir(filePath);
                } catch (Exception e) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }
    }

    public static String getOneFileNameFromlistFilePath(String filePath) throws Exception {
        if (c == null || session == null || !session.isConnected() || !c.isConnected()) {
            throw new Exception("Connection to server is closed. Open it first.");
        }

        String fileName = "";

        try {
            String com1 = "ls -tlr";
            logger.debug("#SftpClient::Listing file-list in path:"+filePath);

            c.cd(filePath);
            Vector filelist = c.ls(filePath);
            for (int i = 0; i < filelist.size(); i++) {
                System.out.println(filelist.get(i).toString());
            }
            if(filelist != null && filelist.size() > 2) {
                fileName = filelist.get(2).toString();
            }

            logger.info("Download successfull.");
        } catch (SftpException e) {
            throw new Exception(e.getMessage(), e);
        }

        return fileName;
    }

    public static String getOneFileName(String filePath, String ext) throws Exception {
        c.cd(filePath);
        String com1 = "ls -tr *." + ext +  " | grep -E '^[^d]' | head -1";
        System.out.println("#SftpClient:: excute command: "+com1);

        String fileName = excuteCommand(com1);

        return fileName;
    }

    public static List<String> getLs(String filePath, String ext) throws Exception {
        System.out.println("#SftpClient:: getList from filePath:: " + filePath + "/*." + ext);
        List<String> fileNames = new ArrayList();

        c.cd(filePath);
        Vector<ChannelSftp.LsEntry> flist = c.ls(filePath);

        for(ChannelSftp.LsEntry entry : flist) {
            if (entry != null && entry.getFilename() != null
                    && !entry.getFilename().equals(".") && !entry.getFilename().equals("..")
                    && entry.getFilename().indexOf(".") != 0
                    && entry.getFilename().endsWith(ext)
                    ){
                fileNames.add(entry.getFilename().trim());
            }
        }

        System.out.println("#SftpClient:: ls result: fileNames::"+fileNames.toString());
        return fileNames;
    }

    public static void move_by_rename_command(String sourceFile, String destFile) throws Exception {
        String com1 = "rename " + sourceFile + " " + destFile;
        System.out.println("#SftpClient:: move_by_rename_command: "+com1);

        //String result = excuteCommand(com1);
        c.rename(sourceFile, destFile);
    }

    public static String excuteCommand(String com1) throws Exception {
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(com1);
        channel.setInputStream(null);
        ((ChannelExec)channel).setErrStream(System.err);

        InputStream in=channel.getInputStream();
        channel.connect();
        byte[] tmp=new byte[1024];
        String fileName = "";
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                fileName = new String(tmp, 0, i);
                //System.out.print(new String(tmp, 0, i));
            }
            if(channel.isClosed()){
                //System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            //try{Thread.sleep(1000);}catch(Exception ee){}
        }
        channel.disconnect();

        System.out.println("#SftpCleint:: excute result:"+fileName);
        return fileName;
    }

    public static void disconnect() {
        if (c != null) {
            logger.debug("#SftpClient::Disconnecting sftp channel");
            c.disconnect();
        }
        if (channel != null) {
            logger.debug("#SftpClient::Disconnecting channel");
            channel.disconnect();
        }
        if (session != null) {
            logger.debug("#SftpClient::Disconnecting session");
            session.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        String filePath = "/home/daisy/service/cmts";

        SftpClient client = new SftpClient();
        client.setServer("14.63.170.72");
        client.setPort(22);
        client.setLogin("daisy");
        client.setPassword("daisy!");
        client.connect();

        try {
            //String fileName = getOneFileName(filePath, "yml");
            //fileName = fileName.trim();
            List<String> fileNames = getLs(filePath, "yml");

            for(String fileName : fileNames) {
                if (!"".equals(fileName)) {
                    String file1 = filePath + "/" + fileName;
                    System.out.println("#SftpClient::Start Download file::" + fileName);
                    retrieveFile(filePath, fileName, "e:\\" + fileName);
                }
            }

            //client.retrieveFile("/uploaded.txt", "target/downloaded.txt");
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            client.disconnect();
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SftpClient() {}
}