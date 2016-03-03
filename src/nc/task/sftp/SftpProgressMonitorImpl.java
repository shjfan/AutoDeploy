package nc.task.sftp;

import nc.tool.FileUtil;

import com.jcraft.jsch.SftpProgressMonitor;

public class SftpProgressMonitorImpl implements SftpProgressMonitor {
	
	private String note;
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public SftpProgressMonitorImpl(){
		super();
	}
	
	public SftpProgressMonitorImpl(String note){
		super();
		this.note=note;
		int count = SftpTaskEnv.getInstance().increment();
		FileUtil.log(" doing sftp task count :"+count);
	}

	@Override
	public boolean count(long arg0) {
		return false;
	}

	@Override
	public void end() {
		FileUtil.log("sftp end; "+note);
		int count = SftpTaskEnv.getInstance().decrement();
		FileUtil.log(" doing sftp task count :"+count);
	}

	@Override
	public void init(int arg0, String arg1, String arg2, long arg3) {

	}

}
