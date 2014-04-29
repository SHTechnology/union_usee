import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class liveprogram_sync implements XmlCallback 
{
	private static final File NULL = null;
	PrintWriter logout;
	private Timer timer;
	
	public long lastDate;
	public String folderPath = "/Users/qinwei/Desktop/EPG/";
	public String tempFolderPath = "/Users/qinwei/Desktop/EPG/temp/";
	public String logFilePath = "/Users/qinwei/Desktop/";
	public String configFileName = "config.xml";
	public long timerDelay = 1000;
	public long timerPeriod = ( 1000 * 60 * 10 );

	public static void main(final String arg[]) 
	{
		liveprogram_sync sync = new liveprogram_sync();
		
		Dom4jConfig config = new Dom4jConfig();
		String path = System.getProperty("user.dir");
		path += ("/"+sync.configFileName);
		config.parserXml(path);
		
		String value = config.getLogFolder();
		if( value.length()>0)
		{
			sync.logFilePath = value;
		}
		value = config.getEpgFolder();
		if( value.length()>0)
		{
			sync.folderPath = value;
		}
		Long lvalue = config.getUpdatePeriod();
		if( lvalue>=60)
		{
			sync.timerPeriod = lvalue*1000;
		}
		value = config.getJdbcUrl();
		if( value.length()>0)
		{
			Union_useeDB.jdbcUrl = value;
		}
		value = config.getDbuser_name();
		if( value.length()>0)
		{
			Union_useeDB.dbName = value;
		}
		value = config.getDbuser_passsword();
		
		if( value.length()>0)
		{
			Union_useeDB.dbPassword = value;
		}
		
		if( Union_useeDB.getConnection() != null )
		{
			try 
			{
				sync.tempFolderPath = sync.folderPath + "/temp/";
				FileManager.fileMove(sync.folderPath, sync.tempFolderPath, false);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sync.folderTimer(sync.timerDelay,sync.timerPeriod);
		}
		else
		{
			System.out.println("数据库连接失败，请检查配置文件，重新启动同步程序");
		}
	}
	
	/*
	 * 构造方法
	 */
	public liveprogram_sync()
	{
		lastDate = 0;
	}

	public void parserFinished(Object object)
	{
		if ( object instanceof Dom4jEPG  )
		{
			Dom4jEPG dom = (Dom4jEPG)object;
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			Date date = new Date(dom.createEPGTime);
			
			List<LiveProgramData> data = dom.getData();
			
			String sdata = "";
			int count = data.size();
			for (int i = 0; i < count; i++) 
			{
				//属性的取得
				LiveProgramData item = (LiveProgramData)data.get(i);
				sdata += (item.lpName + "," + item.lpBeginTime + "," + item.lpBeginTime + ","+item.lpDescription);
				if( i<(count-1))
					sdata += "##";
			}     
			
			String rs = (String) Union_useeDB.executeProcedure(dom.channelName,sdata);
			String printStr = "<<--\n";
			printStr += "--Dom4jEPG parserFinished,channelName:" + dom.channelName +
							  " createTime:" + sdf.format(date) + 
							   "\n--" + rs;
			printStr += "\n-->>";
			System.out.println(printStr);
			logout.println(printStr);
		}
	} 
	/*
	 * 获得日志文件路径
	 */
	private String getCurrentLogFileName()
	{
		String path = logFilePath;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
		path = path + df.format(new Date()) + ".log";
		File dir = new File(logFilePath);// 创建目标目录  
        if (!dir.exists())
        {
        	// 判断目标目录是否存在  
        	dir.mkdirs();// 不存在则创建  
        }  
		return path;
	}
	
	public void folderTimer(long delay, long period)
	{
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() 
			{
		        try
		        {
		        	logout = new PrintWriter(new FileOutputStream(getCurrentLogFileName()));
					long startTime = System.currentTimeMillis();
		            long l = 0;
		            String path = tempFolderPath;
		            FileManager fileManager = new FileManager();
		            File file = new File(path);
		           
		            
		            boolean isChange = fileManager.changeFiletime(file,path,new Date(lastDate));
		            Date filetime = new Date(file.lastModified());
		            System.out.println("#########################################################");
					logout.println("#########################################################");
		    		System.out.println("更新的目录：" + folderPath + " 目录更新时间:"+ filetime.toString()); 
			        logout.println("更新的目录：" + folderPath + " 目录更新时间:"+ filetime.toString());
			        
		            if( isChange )
					{
						System.out.println("开始更新EPG数据...");
						logout.println("开始更新EPG数据...");
					}
					else
					{
						System.out.println("没有可更新EPG数据.");
						logout.println("没有可更新EPG数据.");
					}
		            
		    		
		            if (file.isDirectory())
		            { 
		            	//如果路径是文件夹的时候
		                System.out.println("文件个数           " + fileManager.getlist(file));		                
		                System.out.println("文件和目录总个数           " + fileManager.getDir(file));
		            
		                l = fileManager.getFileSize(file);
		                System.out.println("目录的大小为：" + fileManager.FormetFileSize(l));
		                
		                if( isChange )
		                {
		                	// 目录下文件枚举 and 解析xml文件数据
		                	enumEPGRootDir(file);
		                	logout.println("目录的大小为：" + fileManager.FormetFileSize(l) + 
		                			"\n更新文件个数为：" + fileManager.getlist(file));
		                }
		            } 	
		            
		            long endTime = System.currentTimeMillis();
			        System.out.println("总共花费时间为：" + (endTime - startTime) + "毫秒...");
			        logout.println("总共花费时间为：" + (endTime - startTime) + "毫秒..." );
			        
			        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		            String dateString = sdf.format(new Date());
		            System.out.println("同步时间为：" + dateString);
		            logout.println("同步时间为：" + dateString); 
		            
			        System.out.println("#########################################################");
			        logout.println("#########################################################");
					// 设置下一次比较时间点
					lastDate = startTime;
					// 记录文件
					logout.flush();
					logout.close();
					Union_useeDB.closeConnection();
					FileManager.fileDelete(path, false);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
			}
		}, delay, period);
	}
	/**
	 * 枚举EPG目录
	 * 
	 */
	public void enumEPGRootDir(File f)
	{
		if(f==NULL)
		{
			return;
		}
		//递归枚举目录下的文件和文件夹
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) 
        {
            if (flist[i].isFile()) 
            {
            	String filename = flist[i].getAbsolutePath();
            	System.out.println("解析文件名称:" + filename);
    			logout.println("解析文件名称:" + filename);
            	// 解析EPG数据
            	parseEPGDataWithFile(flist[i]);
            }
        }
	}
	/**
	 * 解析EPG数据
	 * 
	 * @throws IOException
	 */
	public void parseEPGDataWithFile(File file)
	{
		try 
		{
			Dom4jEPG demo = new Dom4jEPG();
			demo.setCallback(this);
			// 解析xml
			demo.parserXmlWithFile(file);
			Thread.sleep ( 50 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从控制对接收一行字符串，然后输出到控制台
	 * 
	 * @throws IOException
	 */
	public void printConsoleLine() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		System.out.println("Enter your value:");
		str = br.readLine();
		System.out.println("your value is :" + str);
	}

	/**
	 * 从控制台接收一个字符 然后打印到控制台上
	 * 
	 * @throws IOException
	 */
	public void printConsoleChar() throws IOException {
		System.out.print("Enter a Char:");
		System.out.println("");
		char i = (char) System.in.read();
		System.out.println("your char is :" + i);
	}
}
