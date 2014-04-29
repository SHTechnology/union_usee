import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class FileManager 
{
	private static final Date NULL = null;

	public boolean isFileExist(String strFileName) 
	{
		File file = new File(strFileName);
		if (!file.exists())
		{
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	public static void fileDelete(String from,boolean containFolder) throws Exception 
	{
		try 
		{  
            File dir = new File(from);  
            File[] files = dir.listFiles();// 将文件或文件夹放入文件集  
            if (files == null)// 判断文件集是否为空  
                return;  
 
            for (int i = 0; i < files.length; i++) 
            {
            	// 遍历文件集  
                if (files[i].isDirectory()) 
                {
                	if( containFolder==true)
                	{
                		fileDelete(files[i].getPath(),containFolder);// 递归移动文件  
                		files[i].delete();// 删除文件所在原目录
                	}
                }  
                else
                {
                	files[i].delete();  
                }
            }  
        } 
		catch (Exception e) 
		{  
            throw e;  
        }  
	}
	public static void fileMove(String from, String to,boolean containFolder) throws Exception 
	{
		try 
		{  
            File dir = new File(from);  
            File[] files = dir.listFiles();// 将文件或文件夹放入文件集  
            if (files == null)// 判断文件集是否为空  
                return;  
            File moveDir = new File(to);// 创建目标目录  
            if (!moveDir.exists())
            {
            	// 判断目标目录是否存在  
                moveDir.mkdirs();// 不存在则创建  
            }  
            String strsp = "\\";
            if(to.charAt(0)=='/')
            {
            	strsp = "/";
            }
            else
            {
            	strsp = "\\";
            }
            for (int i = 0; i < files.length; i++) 
            {
            	// 遍历文件集  
                if (files[i].isDirectory()) 
                {
                	if( containFolder==true)
                	{
                		// 如果是文件夹或目录,则递归调用fileMove方法，直到获得目录下的文件  
                		fileMove(files[i].getPath(), to + strsp+ files[i].getName(),containFolder);// 递归移动文件  
                		files[i].delete();// 删除文件所在原目录
                	}
                }  
                else
                {
                	// 将文件目录放入移动后的目录
                	File moveFile = new File(to + strsp + files[i].getName());  
                	if (moveFile.exists()) 
                	{
                		// 目标文件夹下存在的话，删除  
                		moveFile.delete();  
                	}  
                	files[i].renameTo(moveFile);// 移动文件  
                }
            }  
        } 
		catch (Exception e) 
		{  
            throw e;  
        }  
	}
	
	
	 // 递归取得文件夹（包括子目录）中所有文件的大小
    public long getFileSize(File f) throws Exception//取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    public String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
    public long getlist(File f){//递归求取目录文件个数
        long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }
    public long getDir(File f){//递归求取目录中文件（包括目录）的个数
        long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory())
            {
                size = size + getDir(flist[i]);
            }
        }
        return size;
    }
    /*
     * 获得当前文件或者目录时间并比较
     */
	public boolean changeFiletime(File f, String filename,Date lastFileDate) 
	{
		boolean isChange = false;
		File fileToChange = f;

		//读取文件的最后修改时间
		Date filetime = new Date(fileToChange.lastModified());
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//		System.out.println("数据处理时间:" + df.format(date));// new Date()为获取当前系统时间
		
		// 判断更新时间是否为上次更新时间
		if( lastFileDate!=NULL&&filetime!=NULL )
		{
			if( filetime.after(lastFileDate))
			{
				isChange = true;
			}
			else
			{
				isChange = false;
			}
		}
		else
		{
			if( lastFileDate==NULL)
			{
				isChange = true;
			}
		}
		return isChange;
	}
	 /**-----------------------------------------------------------------------
     *getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     *Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *@param cls为Class类型
     *@return 返回值为该类所在的Java程序运行的目录
     -------------------------------------------------------------------------*/
    public static String getAppPath(Class<?> cls)
    {
        //检查用户传入的参数是否为空
        if(cls==null) 
         throw new java.lang.IllegalArgumentException("参数不能为空！");
        ClassLoader loader=cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName=cls.getName()+".class";
        //获得传入参数所在的包
        Package pack=cls.getPackage();
        String path="";
        //如果不是匿名包，将包名转化为路径
        if(pack!=null){
            String packName=pack.getName();
           //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
           if(packName.startsWith("java.")||packName.startsWith("javax.")) 
              throw new java.lang.IllegalArgumentException("不要传送系统类！");
            //在类的名称中，去掉包名的部分，获得类的文件名
            clsName=clsName.substring(packName.length()+1);
            //判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if(packName.indexOf(".")<0) path=packName+"/";
            else{//否则按照包名的组成部分，将包名转换为路径
                int start=0,end=0;
                end=packName.indexOf(".");
                while(end!=-1){
                    path=path+packName.substring(start,end)+"/";
                    start=end+1;
                    end=packName.indexOf(".",start);
                }
                path=path+packName.substring(start)+"/";
            }
        }
        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url =loader.getResource(path+clsName);
        //从URL对象中获取路径信息
        String realPath=url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos=realPath.indexOf("file:");
        if(pos>-1) realPath=realPath.substring(pos+5);
        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos=realPath.indexOf(path+clsName);
        realPath=realPath.substring(0,pos-1);
        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if(realPath.endsWith("!"))
            realPath=realPath.substring(0,realPath.lastIndexOf("/"));
      /*------------------------------------------------------------
       ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
        中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
        的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
        中文及空格路径
      -------------------------------------------------------------*/
      try{
        realPath=java.net.URLDecoder.decode(realPath,"utf-8");
       }catch(Exception e){throw new RuntimeException(e);}
       return realPath;
    }//getAppPath定义结束
   //-----------------------------------------------------------------
}
