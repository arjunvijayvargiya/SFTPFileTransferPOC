import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.util.Collection;
import java.util.Vector;

public class FileTransfer {

    private static final Logger logger = LoggerFactory.getLogger(FileTransfer.class);

    String SFTPHOST = "localhost";
    int SFTPPORT = 22;
    String SFTPUSER = "user1";
    String SFTPPASS = "user123";
    private static final String SFTPWORKINGDIR = "/files/";

    Session session = null;
    Channel channel = null;
    ChannelSftp sftp = null;

    public void connect(){
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
//            sftp.cd(SFTPWORKINGDIR);
//            Vector filelist = channelSftp.ls(SFTPWORKINGDIR);
//            for (int i = 0; i < filelist.size(); i++) {
//                System.out.println(filelist.get(i).toString());
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws SftpException {
        FileTransfer fileTransfer = new FileTransfer();
        fileTransfer.connect();
        //fileTransfer.downloadFiles(SFTPWORKINGDIR, "D:\\sandbox\\SpringBootProjects\\SFTPFileTransfer\\src\\main\\resources");
        //fileTransfer.uploadFiles("D:\\sandbox\\SpringBootProjects\\SFTPFileTransfer\\src\\main\\resources", SFTPWORKINGDIR);
        fileTransfer.removeFiles(SFTPWORKINGDIR);
        fileTransfer.destroy();
    }

    public void downloadFiles(String remoteFolder, String localFolder) throws SftpException {
        File localFile = new File(localFolder);
        if (localFile.exists()) {
            Vector<ChannelSftp.LsEntry> fileList = sftp.ls(remoteFolder);
            File destFile;
            for (ChannelSftp.LsEntry file : fileList) {
                if (isRealFile(file.getFilename())) {
                    destFile = new File(localFolder, file.getFilename());
                    if (destFile.exists()) { // if image file already exist
                        logger.info("file already exist " + destFile.getAbsolutePath());
                    }
                    sftp.get(remoteFolder + file.getFilename(), localFolder);
                    logger.info("download " + file.getFilename());
                }
            }
        } else {
            logger.error("local folder \"" + localFile.getAbsolutePath() + "\" does not exist");
        }
    }

    public void removeFiles(String remoteFolder) throws SftpException {
            Vector<ChannelSftp.LsEntry> fileList = sftp.ls(remoteFolder);
            File destFile;
            for (ChannelSftp.LsEntry file : fileList) {
                if (isRealFile(file.getFilename())) {
                    sftp.rm(remoteFolder + file.getFilename());
                    logger.info("deleting " + file.getFilename());
                }
            }
    }

    /**
     * upload the files from the local folder to the remote folder
     * @param localFolder
     * @param remoteFolder
     * @throws SftpException
     */
    public void uploadFiles(String localFolder, String remoteFolder) throws SftpException {
        File localFile = new File(localFolder);
        if (localFile.exists()) {
            for (File file : localFile.listFiles()) {
                if (isRealFile(file.getName())) {
                    sftp.put(file.getAbsolutePath(), remoteFolder);
                    logger.info("upload " + file.getName());
                }
            }
        } else {
            logger.error("local folder \"" + localFile.getAbsolutePath() + "\" does not exist");
        }
    }

    public  boolean isRealFile(String filename) {
        return (!filename.equals("..") && !filename.equals("."));
    }


    /**
     * close and destroy the sftp connection
     */
    public void destroy() {
        sftp.exit();
        sftp.disconnect();
        session.disconnect();
    }
}