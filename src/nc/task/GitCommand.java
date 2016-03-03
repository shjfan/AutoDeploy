package nc.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nc.tool.FileUtil;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.BatchingProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

public class GitCommand implements ITaskCommand{

	// git远程地址
	// 如git@172.16.50.51:ieop_group/ieop_uapi.git
	private String url;
	// git本地目录
	private String dir;
	
	public static final String PARA_URL="url";
	public static final String PARA_DIR="dir";

	private static String refspec = "+refs/head/*:refs/remotes/origin/*";
	
	public GitCommand(){
		super();
	}
	
	public GitCommand(Map<String,Object> para){
		super();
		FileUtil.log("实例化"+this.getClass().getName());
		this.url=para.get(PARA_URL)==null?null:para.get(PARA_URL).toString();
		this.dir=para.get(PARA_DIR)==null?null:para.get(PARA_DIR).toString();
	}
	
	public GitCommand(String url, String dir) {
		super();
		FileUtil.log("实例化"+this.getClass().getName());
		this.url = url;
		this.dir = dir;
	}

	
	public void process() throws Exception {
		FileUtil.log(" git start "+url+" && "+dir);		
		if(url==null||dir==null){
			FileUtil.log("git地址和本地目录不能为空!");
			throw new Exception("git地址和本地目录不能为空!");
		}
		dir = new File(dir).getCanonicalPath();
		FileUtil.log(" CanonicalPath dir= "+dir);	
		try {
			if (existsGit(new File(dir))) {
				pullGit();
			} else {
				cloneGit();
			}

		} catch (IOException e) {
			FileUtil.log(e);
			throw e;
		} catch (InvalidRemoteException e) {
			FileUtil.log(e);
			throw e;
		} catch (TransportException e) {
			FileUtil.log(e);
			throw e;
		} catch (GitAPIException e) {
			FileUtil.log(e);
			throw e;
		}
		FileUtil.log(" git end "+url+" && "+dir);

	}

	/**
	 * git fetch
	 * 
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 * @throws IOException
	 */
	private void fetchGit() throws InvalidRemoteException, TransportException,
			GitAPIException, IOException {
		FileUtil.log(" git fetch start "+url+" && "+dir);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(dir))
				.readEnvironment().findGitDir().build();
		Git git = null;
		try {
			git = new Git(repository);
			FetchCommand fetch = git.fetch();			
			fetch.setRemote(url);
			fetch.setRefSpecs(new RefSpec(refspec));
			fetch.setProgressMonitor(new TextProgressMonitor());
			fetch.call();
		} finally {
			if (git != null) {
				try {
					git.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		FileUtil.log(" git fetch end "+url+" && "+dir);
	}
	
	/**
	 * git pull
	 * 
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 * @throws IOException
	 */
	private void pullGit() throws InvalidRemoteException, TransportException,
			GitAPIException, IOException {
		FileUtil.log(" git pull start "+url+" && "+dir);
//		FileRepositoryBuilder builder = new FileRepositoryBuilder();
//		Repository repository = builder.setGitDir(new File(dir))
//				.readEnvironment().findGitDir().build();
		Repository repository = new FileRepository(dir + "\\.git");
		
		Git git = null;
		try {
			git = new Git(repository);
			PullCommand pull = git.pull();
//			pull.setRemote(url);
//			pull.setRemoteBranchName("develop");
//			pull.setRebase(true);
			pull.setProgressMonitor(new TextProgressMonitor());
			pull.call();
		} finally {
			if (git != null) {
				try {
					git.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		FileUtil.log(" git pull end "+url+" && "+dir);
	}

	/**
	 * git clone
	 * 
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * @throws IOException 
	 */
	private void cloneGit() throws InvalidRemoteException, TransportException,
			GitAPIException, IOException {
		FileUtil.log(" git clone start "+url+" && "+dir);
		  
		CloneCommand clone = Git.cloneRepository();
		clone.setBare(false);
		//clone.setCloneAllBranches(true);
		clone.setBranch("develop");
		clone.setProgressMonitor(new GitTextProgressMonitor());
		clone.setDirectory(new File(dir)).setURI(url);		
		clone.call();
		FileUtil.log(" git clone end "+url+" && "+dir);
	}

	private boolean existsGit(File dirfile) {
		return dirfile != null && dirfile.exists()
				&& dirfile.listFiles().length != 0;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}
class GitTextProgressMonitor extends BatchingProgressMonitor{

	public GitTextProgressMonitor()
	{
		super();
	}

	protected void onUpdate(String taskName, int workCurr)
	{
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		send(s);
	}

	protected void onEndTask(String taskName, int workCurr)
	{
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		s.append("\n");
		send(s);
	}

	private void format(StringBuilder s, String taskName, int workCurr)
	{
		s.append("\r");
		s.append(taskName);
		s.append(": ");
		for (; s.length() < 25; s.append(' '));
		s.append(workCurr);
	}

	protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt)
	{
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		send(s);
	}

	protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt)
	{
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		s.append("\n");
		send(s);
	}

	private void format(StringBuilder s, String taskName, int cmp, int totalWork, int pcnt)
	{
		s.append("\r");
		s.append(taskName);
		s.append(": ");
		for (; s.length() < 25; s.append(' '));
		String endStr = String.valueOf(totalWork);
		String curStr;
		for (curStr = String.valueOf(cmp); curStr.length() < endStr.length(); curStr = (new StringBuilder()).append(" ").append(curStr).toString());
		if (pcnt < 100)
			s.append(' ');
		if (pcnt < 10)
			s.append(' ');
		s.append(pcnt);
		s.append("% (");
		s.append(curStr);
		s.append("/");
		s.append(endStr);
		s.append(")");
	}

	private void send(StringBuilder s)
	{
		FileUtil.log(s.toString());
	}
}
