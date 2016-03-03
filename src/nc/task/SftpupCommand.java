package nc.task;

import java.io.File;
import java.util.Map;

import nc.task.sftp.SftpTaskEnv;
import nc.tool.FileUtil;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

public class SftpupCommand extends SftpCommand implements ITaskCommand {

	private String fromdir;
	private String todir;
	private String fromos;
	private String toos;

	public static final String PARA_FROMDIR = "fromdir";
	public static final String PARA_TODIR = "todir";
	public static final String PARA_FROMOS = "fromos";
	public static final String PARA_TOOS = "toos";

	public SftpupCommand() {
		super();
	}

	public SftpupCommand(Map<String, Object> para) {
		super(para);
		FileUtil.log("实例化"+this.getClass().getName());
		this.fromdir = para.get(PARA_FROMDIR) == null ? null : para.get(
				PARA_FROMDIR).toString();
		this.todir = para.get(PARA_TODIR) == null ? null : para.get(PARA_TODIR)
				.toString();
		this.fromos = para.get(PARA_FROMOS) == null ? null : para.get(
				PARA_FROMOS).toString();
		this.toos = para.get(PARA_TOOS) == null ? null : para.get(PARA_TOOS)
				.toString();
	}

	@Override
	public void process() throws Exception {
		FileUtil.log("sftpup start " + host + "&&" + port + "&&" + username
				+ "&&" + password + "&&" + fromdir + "&&" + todir + "&&"
				+ fromos + "&&" + toos);
		Session sshSession = null;
		try {
			sshSession = this.connect(getHost(), getPort(), getUsername(),
					getPassword());				
			ChannelSftp sftp = this.getChannelSftp(sshSession);
			//初始化任务计数
			SftpTaskEnv.getInstance().init();
			up(this.fromdir, this.todir, sftp);
			
			//等上5秒
    		Thread.sleep(5000);
    		
			//等待任务执行完毕，关闭session
			//如果超时则报错
    		boolean isovertime = true;
    		for(int i=0;i<MAX_SECOND*2;i++){
    			if(SftpTaskEnv.getInstance().isFinished()){
    				sshSession.disconnect();
    				isovertime = false;
    				break;
    			}
    			Thread.sleep(500);
    		}
    		if(isovertime){
    			throw new Exception("sftpup error overtime "+host+"&&"+port+"&&"+username+"&&"+password+"&&"+fromdir+"&&"+todir+"&&"+fromos+"&&"+toos);
    		}
			
		} catch (Exception e) {
			if(sshSession!=null){
				try{
					sshSession.disconnect();
				}catch(Exception ee){
					
				}
			}
			FileUtil.log(e);
			throw e;
		}
		FileUtil.log("sftpup start " + host + "&&" + port + "&&" + username
				+ "&&" + password + "&&" + fromdir + "&&" + todir + "&&"
				+ fromos + "&&" + toos);

	}

	/**
	 * 上传目录
	 * 
	 * @param fromdir
	 * @param todir
	 * @param sftp
	 */
	private void up(String fromdir, String todir, ChannelSftp sftp) throws Exception{
		File fromfile = new File(fromdir);
		File[] files = fromfile.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				try{
					sftp.cd(todir + getSeperator(toos) + file.getName());
				}catch(Exception e){
					createDir(todir, file.getName(), sftp);
				}
				up(fromdir + getSeperator(fromos) + file.getName(), todir
						+ getSeperator(toos) + file.getName(), sftp);
			} else {
				upload(todir, fromdir + getSeperator(fromos) + file.getName(),
						sftp);
			}
		}

	}

	public String getFromdir() {
		return fromdir;
	}

	public void setFromdir(String fromdir) {
		this.fromdir = fromdir;
	}

	public String getTodir() {
		return todir;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

	public String getFromos() {
		return fromos;
	}

	public void setFromos(String fromos) {
		this.fromos = fromos;
	}

	public String getToos() {
		return toos;
	}

	public void setToos(String toos) {
		this.toos = toos;
	}

}
