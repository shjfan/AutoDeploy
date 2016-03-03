package nc.task;

import java.util.Map;

public class TaskFactory {
	public static final String TASK_TYPE_GIT = "git";
	public static final String TASK_TYPE_ANT = "ant";
	public static final String TASK_TYPE_SSHEXEC = "sshexec";
	public static final String TASK_TYPE_SFTPUP = "sftpup";
	public static final String TASK_TYPE_SFTPDOWN = "sftpdown";

	public static ITaskCommand getTaskCommand(String tasktype,
			Map<String, Object> para) {
		switch (tasktype) {
		case TASK_TYPE_GIT:
			return new GitCommand(para);
		case TASK_TYPE_ANT:
			return new AntCommand(para);
		case TASK_TYPE_SSHEXEC:
			return new SshExecCommand(para);
		case TASK_TYPE_SFTPUP:
			return new SftpupCommand(para);
		case TASK_TYPE_SFTPDOWN:
			return new SftpdownCommand(para);
		}
		return null;
	}
}
