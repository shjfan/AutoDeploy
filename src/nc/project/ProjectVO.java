package nc.project;

import java.util.LinkedList;
import java.util.List;

public class ProjectVO {

	private List<Task> taskList = new LinkedList<Task>();

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public void addTask(Task task) {
		taskList.add(task);
	}

}


