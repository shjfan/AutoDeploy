package nc.tool;
import java.io.*;
import java.util.Date;

/**
 * 
 * @author fansj1
 *
 */
public class FileUtil
{
	public static String LOGPATH=".\\log";
    /**
     * 取得文件内容
     *
     * @return String
     */
    public static String getFileCnt(String filePath)
    {
        StringBuffer content = new StringBuffer();
        try
        {
            File temFile = new File(filePath);
            FileReader fileReader = new FileReader(temFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);
            String str = null;
            while ((str = bufferReader.readLine()) != null)
            {
                content.append(str);
            	content.append("\n");
            }
            bufferReader.close();
            fileReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 覆盖文件的内容
     */
    public static boolean setFileCnt(String filePath, String newContent)
    {
        try
        {
            File file = new File(filePath);
            if (!file.exists()) file.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(file));
            out.print(newContent);
            out.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("update template false:" + e.toString());
            return false;
        }
    }
    
    /**
     * 覆盖文件的内容
     */
    public static boolean appendFileCnt(String filePath, String newContent)
    {
        try
        {
            File file = new File(filePath);
            if (!file.exists()) file.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(file,true));
            out.print(newContent);
            out.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("update template false:" + e.toString());
            return false;
        }
    }
    /**
     * 记录日志
     * @param newContent
     * @return
     */
    public static boolean log(String newContent){
    	newContent = getNowNano()+":"+ newContent;
    	System.out.println(newContent);

    	return appendFileCnt(getLogPath(),newContent+"\n");
    }
    
	/**
	 * 获取当前时间(精确到毫秒), 格式为: yyyy-mm-dd hh:mm:ss.nnn
	 * 
	 * @return String 当前时间
	 */
	public static String getNowNano() {
		return new java.sql.Timestamp(System.currentTimeMillis()).toString();
	}
	
	private static String log_path = null;
	public static String getLogPath(){
		if(log_path == null){
			log_path = LOGPATH+"\\"+getDate()+".log";
		}
		return log_path;
	}
	public static String getDate(){
		Date date = new Date();
		int  month = date.getMonth()+1;
		String month_str = (month<10?"0":"" )+month;
		int day = date.getDate();
		String day_str = (day<10?"0":"") +day;
		return month_str+day_str;
	}
	
	/**
	 * 记录异常信息
	 * @param e
	 */
	public static void log(Exception e){
		StringBuffer str = new StringBuffer(e.getMessage()+"\n");
		StackTraceElement[] se = e.getStackTrace();
		for(int i=0;i<se.length;i++){
			str.append(se[i]+"\n");
		}
		log(str.toString());	
	}
    /**
	 * * 递归删除目录下的所有文件及子目录下所有文件 *
	 * 
	 * @param dir
	 *            将要删除的文件目录 *
	 * @return boolean Returns "true" if all deletions were successful. * If a
	 *         deletion fails, the method stops attempting to * delete and
	 *         returns "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

}
