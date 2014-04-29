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
			System.out.println("���ݿ�����ʧ�ܣ����������ļ�����������ͬ������");
		}
	}
	
	/*
	 * ���췽��
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
				//���Ե�ȡ��
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
	 * �����־�ļ�·��
	 */
	private String getCurrentLogFileName()
	{
		String path = logFilePath;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");//�������ڸ�ʽ
		path = path + df.format(new Date()) + ".log";
		File dir = new File(logFilePath);// ����Ŀ��Ŀ¼  
        if (!dir.exists())
        {
        	// �ж�Ŀ��Ŀ¼�Ƿ����  
        	dir.mkdirs();// �������򴴽�  
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
		    		System.out.println("���µ�Ŀ¼��" + folderPath + " Ŀ¼����ʱ��:"+ filetime.toString()); 
			        logout.println("���µ�Ŀ¼��" + folderPath + " Ŀ¼����ʱ��:"+ filetime.toString());
			        
		            if( isChange )
					{
						System.out.println("��ʼ����EPG����...");
						logout.println("��ʼ����EPG����...");
					}
					else
					{
						System.out.println("û�пɸ���EPG����.");
						logout.println("û�пɸ���EPG����.");
					}
		            
		    		
		            if (file.isDirectory())
		            { 
		            	//���·�����ļ��е�ʱ��
		                System.out.println("�ļ�����           " + fileManager.getlist(file));		                
		                System.out.println("�ļ���Ŀ¼�ܸ���           " + fileManager.getDir(file));
		            
		                l = fileManager.getFileSize(file);
		                System.out.println("Ŀ¼�Ĵ�СΪ��" + fileManager.FormetFileSize(l));
		                
		                if( isChange )
		                {
		                	// Ŀ¼���ļ�ö�� and ����xml�ļ�����
		                	enumEPGRootDir(file);
		                	logout.println("Ŀ¼�Ĵ�СΪ��" + fileManager.FormetFileSize(l) + 
		                			"\n�����ļ�����Ϊ��" + fileManager.getlist(file));
		                }
		            } 	
		            
		            long endTime = System.currentTimeMillis();
			        System.out.println("�ܹ�����ʱ��Ϊ��" + (endTime - startTime) + "����...");
			        logout.println("�ܹ�����ʱ��Ϊ��" + (endTime - startTime) + "����..." );
			        
			        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		            String dateString = sdf.format(new Date());
		            System.out.println("ͬ��ʱ��Ϊ��" + dateString);
		            logout.println("ͬ��ʱ��Ϊ��" + dateString); 
		            
			        System.out.println("#########################################################");
			        logout.println("#########################################################");
					// ������һ�αȽ�ʱ���
					lastDate = startTime;
					// ��¼�ļ�
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
	 * ö��EPGĿ¼
	 * 
	 */
	public void enumEPGRootDir(File f)
	{
		if(f==NULL)
		{
			return;
		}
		//�ݹ�ö��Ŀ¼�µ��ļ����ļ���
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) 
        {
            if (flist[i].isFile()) 
            {
            	String filename = flist[i].getAbsolutePath();
            	System.out.println("�����ļ�����:" + filename);
    			logout.println("�����ļ�����:" + filename);
            	// ����EPG����
            	parseEPGDataWithFile(flist[i]);
            }
        }
	}
	/**
	 * ����EPG����
	 * 
	 * @throws IOException
	 */
	public void parseEPGDataWithFile(File file)
	{
		try 
		{
			Dom4jEPG demo = new Dom4jEPG();
			demo.setCallback(this);
			// ����xml
			demo.parserXmlWithFile(file);
			Thread.sleep ( 50 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ӿ��ƶԽ���һ���ַ�����Ȼ�����������̨
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
	 * �ӿ���̨����һ���ַ� Ȼ���ӡ������̨��
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
