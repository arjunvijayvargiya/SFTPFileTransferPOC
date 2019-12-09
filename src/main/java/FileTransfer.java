import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


import java.util.Vector;

public class FileTransfer {


    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String SFTPHOST = "localhost";
        int SFTPPORT = 22;
        String SFTPUSER = "user1";
        String SFTPPASS = "user123";
        String SFTPWORKINGDIR = "/files/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

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
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            Vector filelist = channelSftp.ls(SFTPWORKINGDIR);
            for (int i = 0; i < filelist.size(); i++) {
                System.out.println(filelist.get(i).toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}