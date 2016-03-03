package nc.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import nc.task.sftp.SftpProgressMonitorImpl;
import nc.tool.FileUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpCommand {
	protected String host;
	protected int port;
	protected String username;
	protected String password;

	public static final String PARA_HOST = "host";
	public static final String PARA_PORT = "port";
	public static final String PARA_USERNAME = "username";
	public static final String PARA_PASSWORD = "password";

	public static final String OS_WINDOWS = "windows";
	public static final String OS_LINUX = "linux";
	
	//上传或下载的最长时间-秒
	public static final int MAX_SECOND = 600;

	public SftpCommand() {
		super();
	}

	public SftpCommand(Map<String, Object> para) {
		super();
		FileUtil.log("实例化"+this.getClass().getName());
		this.host = para.get(PARA_HOST) == null ? null : para.get(PARA_HOST)
				.toString();
		this.port = para.get(PARA_PORT) == null ? 22 : Integer.parseInt(para
				.get(PARA_PORT).toString());
		this.username = para.get(PARA_USERNAME) == null ? null : para.get(
				PARA_USERNAME).toString();
		this.password = para.get(PARA_PASSWORD) == null ? null : para.get(
				PARA_PASSWORD).toString();
	}

	/**
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public Session connect(String host, int port, String username,
			String password) throws Exception {
		Session sshSession = null;
		try {
			FileUtil.log("sftp connect start "+host+"&&"+port+"&&"+username+"&&"+password);
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			sshSession = jsch.getSession(username, host, port);
			FileUtil.log("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			FileUtil.log("Session connected.");
			FileUtil.log("sftp connect end "+host+"&&"+port+"&&"+username+"&&"+password);
		} catch (Exception e) {
			FileUtil.log(e);
			throw e;
		}
		return sshSession;
	}

	public ChannelSftp getChannelSftp(Session sshSession)throws Exception {
		ChannelSftp sftp = null;
		try {
			FileUtil.log("sftp getChannelSftp start ");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			FileUtil.log("sftp getChannelSftp end ");
		} catch (Exception e) {
			FileUtil.log(e);
			throw e;
		}
		return sftp;
	}

	/**
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * @param sftp
	 */
	public void upload(String directory, String uploadFile, ChannelSftp sftp)throws Exception {
		try {
			FileUtil.log("sftp upload start "+directory+"&&"+uploadFile);
			sftp.cd(directory);
			File file = new File(uploadFile);
			sftp.put(new FileInputStream(file), file.getName());
			//sftp.put(new FileInputStream(file), file.getName(),new SftpProgressMonitorImpl("sftp upload "+directory+"&&"+uploadFile));

		} catch (Exception e) {
			FileUtil.log(e);
			throw e;
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 * @param sftp
	 */
	public void download(String directory, String downloadFile,
			String saveFile, ChannelSftp sftp) throws Exception{
		try {
			FileUtil.log("sftp upload start "+directory+"&&"+downloadFile);
			sftp.cd(directory);
			File file = new File(saveFile);
			sftp.get(downloadFile, new FileOutputStream(file));
			//sftp.get(downloadFile, new FileOutputStream(file),new SftpProgressMonitorImpl("sftp downlaod "+directory+"&&"+downloadFile+"&&"+saveFile));
			Thread.sleep(1000);
		} catch (Exception e) {
			FileUtil.log(e);
			throw e;
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * @param sftp
	 */
	public void delete(String directory, String deleteFile, ChannelSftp sftp)throws Exception {
		try {
			FileUtil.log("sftp delete start "+directory+"&&"+deleteFile);
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			FileUtil.log(e);
			throw e;
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	public Vector listFiles(String directory, ChannelSftp sftp)
			throws SftpException {
		return sftp.ls(directory);
	}

	/**
	 * create Directory
	 * 
	 * @param filepath
	 * @param sftp
	 */
	public void createDir(String parentpath,String dirname, ChannelSftp sftp)throws Exception {
		try {
				sftp.cd(parentpath);
				sftp.mkdir(dirname);
		} catch (SftpException e) {
			FileUtil.log("sftp createDir failed :" + parentpath +"&&"+dirname);
			FileUtil.log(e);
			throw e;
		}

	}

	/**
	 * 分割符
	 * 
	 * @param ostype
	 * @return
	 */
	public String getSeperator(String ostype) {
		if (this.OS_WINDOWS.equalsIgnoreCase(ostype)) {
			return "\\";
		} else if (this.OS_LINUX.equalsIgnoreCase(ostype)) {
			return "/";
		}
		return "/";
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

