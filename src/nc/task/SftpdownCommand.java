package nc.task;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import nc.task.sftp.SftpTaskEnv;
import nc.tool.FileUtil;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpdownCommand extends SftpCommand implements ITaskCommand {
	
	private String fromdir;
	private String todir;
	private String fromos;
	private String toos;
	
	
	public static final String PARA_FROMDIR="fromdir";
	public static final String PARA_TODIR="todir";
	public static final String PARA_FROMOS="fromos";
	public static final String PARA_TOOS="toos";
	
	public SftpdownCommand(){
		super();
	}
	
	public SftpdownCommand(Map<String,Object> para){
		super(para);
		FileUtil.log("实例化"+this.getClass().getName());
		this.fromdir=para.get(PARA_FROMDIR)==null?null:para.get(PARA_FROMDIR).toString();
		this.todir=para.get(PARA_TODIR)==null?null:para.get(PARA_TODIR).toString();
		this.fromos=para.get(PARA_FROMOS)==null?null:para.get(PARA_FROMOS).toString();
		this.toos=para.get(PARA_TOOS)==null?null:para.get(PARA_TOOS).toString();
	}
	
	@Override
	public void process() throws Exception {
		FileUtil.log("sftpdown start "+host+"&&"+port+"&&"+username+"&&"+password+"&&"+fromdir+"&&"+todir+"&&"+fromos+"&&"+toos);
		Session sshSession = null;	         
        try {  
        	sshSession = this.connect(getHost(), getPort(), getUsername(), getPassword());
    		ChannelSftp sftp = this.getChannelSftp(sshSession);
    		//初始化任务计数
			SftpTaskEnv.getInstance().init();
			createDir(todir);
    		downLoad(this.fromdir,this.todir,sftp);
    		
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
    			throw new Exception("error sftpdown overtime "+host+"&&"+port+"&&"+username+"&&"+password+"&&"+fromdir+"&&"+todir+"&&"+fromos+"&&"+toos);
    		}
        }  catch (Exception e) { 
			if(sshSession!=null){
				try{
					sshSession.disconnect();
				}catch(Exception ee){
					
				}
			}
			FileUtil.log(e);
			throw e; 
        }  
        FileUtil.log("sftpdown end "+host+"&&"+port+"&&"+username+"&&"+password+"&&"+fromdir+"&&"+todir+"&&"+fromos+"&&"+toos);
	}
	
	/**
	 * 下载目录
	 * @param fromdir
	 * @param todir
	 * @param sftp
	 * @throws SftpException
	 */
	private void downLoad(String fromdir,String todir,ChannelSftp sftp) throws Exception{
		Vector filevector = listFiles(fromdir, sftp);
		for(int i=0;i<filevector.size();i++){
			LsEntry file = (LsEntry)filevector.get(i);
			if(".".equalsIgnoreCase(file.getFilename())||"..".equalsIgnoreCase(file.getFilename())){
				continue;
			}
			if(file.getAttrs().isDir()){
				String tofilename = todir+getSeperator(toos)+file.getFilename();
				if(!new File(tofilename).exists() || !new File(tofilename).isDirectory()){
					createDir(tofilename);
				}				
				downLoad(fromdir+getSeperator(fromos)+file.getFilename(),tofilename,sftp);
			}else{
				download(fromdir, file.getFilename(), todir+getSeperator(toos)+file.getFilename(), sftp);
			}
		}
		
	}
	
	/**
	 * 创建本地目录
	 * @param dir
	 */
	private void createDir(String dir){
		File f = new File(dir);
		if(!f.exists()){
			f.mkdirs();
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
