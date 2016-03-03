package test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import nc.task.GitCommand;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class TestJGit {
	public static void main(String[] args) {
		//Repository repo = null;

		String path = "d:/testjgit"; // 设置git仓库的路径
		String url = "git@172.16.50.51:ieop_group/ieop_uapi.git";
		GitCommand git = new GitCommand(url,path);
		try{
			git.process();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(1==1){
			return;
		}
		/*InitCommand init = new InitCommand();
		// 设置路径
		init.setBare(false).setDirectory(new File(path));
		// 执行git init ，创建仓库
		Git git;
		try {
			git = init.call();// 创建仓库
			repo = git.getRepository();
			System.out.println("create reposuccess");
		} catch (GitAPIException e) {

			e.printStackTrace();
		}
		// 执行 git remote add 命令
		// 实例化一个RemoteConfig 对象，用户配置远端仓库
		StoredConfig config = repo.getConfig();*/
		try {
/*			RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
			// 设置你的远端地址
			URIish uri = new URIish("git@172.16.50.51:ieop_group/ieop_uapi.git");
			// 设置哪个分支
			RefSpec refSpec = new RefSpec("+refs/head/*:refs/remotes/origin/*");
			// 更新配置
			remoteConfig.addFetchRefSpec(refSpec);
			remoteConfig.addPushRefSpec(refSpec);
			remoteConfig.addURI(uri);
			remoteConfig.addPushURI(uri);
			// 更新配置
			remoteConfig.update(config);
			// 保存到本地文件中
			config.save();*/
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setGitDir(new File(path)).readEnvironment().findGitDir().build();
			Git git1 =new Git(repository);
			CloneCommand clone = git1.cloneRepository();
			FetchCommand fetch= git1.fetch();
			fetch.setRemote("git@172.16.50.51:ieop_group/ieop_uapi.git");
			fetch.setRefSpecs(new RefSpec("+refs/head/*:refs/remotes/origin/*"));
			fetch.setProgressMonitor(new TextProgressMonitor());
			fetch.call();
//			if(1==1){
//				return;
//			}
			clone.setBare(false);
			clone.setCloneAllBranches(true);
			clone.setProgressMonitor(new TextProgressMonitor());
			
			
			clone.setDirectory(new File(path)).setURI("git@172.16.50.51:ieop_group/ieop_uapi.git");
			//UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(login, password);                
			//clone.setCredentialsProvider(user);
			clone.call(); 
			System.out.println("git remote addsuccess.");
		} /*catch (URISyntaxException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/ catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
