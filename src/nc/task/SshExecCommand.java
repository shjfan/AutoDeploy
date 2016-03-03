package nc.task;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import nc.tool.FileUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class SshExecCommand extends SftpCommand implements ITaskCommand {

	private String command;
	
	public final static String PARA_COMMAND="command";
	
	public SshExecCommand(){
		super();
	}
	public SshExecCommand(Map<String, Object> para){
		super(para);
		FileUtil.log("实例化"+this.getClass().getName());
		this.command=para.get(PARA_COMMAND)==null?null:para.get(PARA_COMMAND).toString();
	}
	
	@Override
	public void process() throws Exception {
		FileUtil.log("sshexec start " + host + "&&" + port + "&&" + username
				+ "&&" + password + "&&" + command);
		Session sshSession = null;	 
        
        try {  
        	sshSession = this.connect(getHost(), getPort(), getUsername(), getPassword());
        	ChannelExec channelExec = (ChannelExec)sshSession.openChannel( "exec" );  
            channelExec.setCommand( command );  
            channelExec.setInputStream( null );  
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(outputStream );
            channelExec.setErrStream( outputStream );  
            InputStream in = channelExec.getInputStream();  
            channelExec.connect();  
            int res = -1;  
            StringBuffer buf = new StringBuffer( 1024 );  
            byte[] tmp = new byte[ 1024 ];  
            while ( true ) {  
                while ( in.available() > 0 ) {  
                    int i = in.read( tmp, 0, 1024 );  
                    if ( i < 0 ) break;  
                    buf.append( new String( tmp, 0, i ) );  
                }  
                if ( channelExec.isClosed() ) {  
                    res = channelExec.getExitStatus();  
                    FileUtil.log( format( "Exit-status: %d", res ) );  
                    break;  
                }  
                Thread.sleep(100);
            }  
            FileUtil.log( outputStream.toString() );  
            FileUtil.log( buf.toString() );
            //等上10秒
            FileUtil.log("等10秒");
            for(int i=1;i<=10;i++){
            	Thread.sleep(1000); 
            	FileUtil.log(i+"秒");
            }            
            channelExec.disconnect();  
        }  catch (Exception e) {  
			FileUtil.log(e);
			throw e;
        } finally{
        	try{
        		sshSession.disconnect();
        	}catch(Exception e){
        		
        	}
        }
        FileUtil.log("sshexec end " + host + "&&" + port + "&&" + username
				+ "&&" + password + "&&" + command);
	}

}
