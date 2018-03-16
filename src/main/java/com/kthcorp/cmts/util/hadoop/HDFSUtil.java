package com.kthcorp.cmts.util.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import java.security.PrivilegedExceptionAction;

/**
 * Created by shadowcat on 2015. 7. 30..
 */
public class HDFSUtil {

    public static void main(String[] args) {
        writeFile("phr", "210.122.36.199:8020", null);
    }

    public static void writeFile(String user , String hadoopUrl, byte[] bytes ) {

        try {
            UserGroupInformation ugi
                    = UserGroupInformation.createRemoteUser(user);

            ugi.doAs(new PrivilegedExceptionAction<Void>() {

                public Void run() throws Exception {

                    Path filenamePath = new Path("/user/phr/freq/test1");
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", hadoopUrl);
//                    conf.set("fs.defaultFS", "hdfs://daisy-st01:8020");
//                    String conxUrl = String.format("webhdfs://%s:%s", hadoopUrl, 8020);
//                    conf.set("fs.defaultFS", conxUrl);

                    FileSystem fs = WebHdfsFileSystem.get(conf);

                    System.out.println("Home Path : " + fs.getHomeDirectory());
                    System.out.println("Work Path : " + fs.getWorkingDirectory());


                    FSDataOutputStream out = fs.create(filenamePath);
                    fs.setPermission(filenamePath,new FsPermission(FsAction.ALL,FsAction.NONE,FsAction.NONE));
                    //out.write(bytes);
                    out.close();

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
