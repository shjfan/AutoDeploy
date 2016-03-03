package nc.task.sftp;

import java.util.concurrent.atomic.AtomicInteger;

public class SftpTaskEnv {
	private static SftpTaskEnv instance = new SftpTaskEnv();
	private AtomicInteger count = new AtomicInteger(0);
	private SftpTaskEnv(){
		
	}	
	public static SftpTaskEnv getInstance(){
		return instance;
	}	
	public void init(){
		count.set(0);
	}
	public int increment(){
		return count.incrementAndGet();
	}
	public int decrement(){
		return count.decrementAndGet();
	}
	public boolean isFinished(){
		return count.compareAndSet(0, 0);
	}
}
