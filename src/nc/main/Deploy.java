package nc.main;

import java.util.List;

import nc.project.Project;
import nc.project.ProjectVO;
import nc.project.Task;
import nc.task.ITaskCommand;
import nc.task.TaskFactory;
import nc.tool.FileUtil;

public class Deploy {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String xmlfile = args[0];
		if(xmlfile==null){
			FileUtil.log("请输入对应deploy文件！");
			return;
		}
		ProjectVO projectvo=Project.getProject(xmlfile);
		List<Task> tasklist= projectvo.getTaskList();
		for(int i=0;i<tasklist.size();i++){
			Task task = tasklist.get(i);
			ITaskCommand taskcommand= TaskFactory.getTaskCommand(task.getType(), task.getPara());
			taskcommand.process();
		}
	}

}
