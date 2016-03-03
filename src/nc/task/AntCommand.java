package nc.task;

import java.io.File;
import java.util.Map;

import nc.tool.FileUtil;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntCommand implements ITaskCommand{

	private String buildFilePath;
	public static final String PARA_BUILDFILEPATH="buildFilePath";

	public AntCommand() {
		super();
	}
	
	public AntCommand(Map<String,Object> para){
		super();
		FileUtil.log("实例化"+this.getClass().getName());
		this.buildFilePath = para.get(PARA_BUILDFILEPATH)==null?null:para.get(PARA_BUILDFILEPATH).toString();
		FileUtil.log("参数buildFilePath="+buildFilePath);
	}
	
	public AntCommand(String buildFilePath) {
		super();
		FileUtil.log("实例化"+this.getClass().getName());
		this.buildFilePath = buildFilePath;
		FileUtil.log("参数buildFilePath="+buildFilePath);
	}

	public void process() throws Exception {
		FileUtil.log(" ant start "+buildFilePath);
		// 创建一个ANT项目
		Project p = new Project();
		// 创建一个默认的监听器,监听项目构建过程中的日志操作
		AntLogger consoleLogger = new AntLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
		try {
			p.fireBuildStarted();
			// 初始化该项目
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			// 解析项目的构建文件
			helper.parse(p, new File(buildFilePath));
			// 执行项目的某一个目标
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
			if(consoleLogger.isError()){
				throw new Exception("ant fail "+buildFilePath);
			}
			FileUtil.log(" ant end "+buildFilePath);
		} catch (BuildException be) {
			p.fireBuildFinished(be);
			FileUtil.log(be);
			throw be;
		}catch(Exception e){
			FileUtil.log(e);
			throw e;
		}
	}

	public String getBuildFilePath() {
		return buildFilePath;
	}

	public void setBuildFilePath(String buildFilePath) {
		this.buildFilePath = buildFilePath;
	}

}

class AntLogger extends DefaultLogger{
	//是否构建失败
	private boolean iserror =false;
	public boolean  isError(){
		return iserror;
	}
	
	protected void log(String s)
	{	
		super.log(s);
		FileUtil.log(s);
	}
	public void buildFinished(BuildEvent event){
		super.buildFinished(event);
		Throwable error = event.getException();
		if(error!=null){
			iserror = true;
		}
		
	}
}
