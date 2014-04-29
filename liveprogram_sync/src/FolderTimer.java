
import java.text.SimpleDateFormat;
import java.util.*;
public class FolderTimer
{
	/**
	 * 简易定时器
	 * @param delay  多久后开始执行。毫秒
	 * @param period 执行的间隔时间。毫秒
	 */
	private Timer timer;
	public void test(long delay, long period)
	{
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() 
			{
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
				//System.out.println(System.currentTimeMillis());
			}
		}, delay, period);
	}
}




	
