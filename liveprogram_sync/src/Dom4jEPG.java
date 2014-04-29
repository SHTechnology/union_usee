
   
import java.io.File;     
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;   
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;   
import org.dom4j.DocumentException;      
import org.dom4j.Element;   
import org.dom4j.io.SAXReader;   
   
/**  
*   
* @author hongliang.dinghl  
* Dom4j 生成XML文档与解析XML文档  
*/ 

public class Dom4jEPG implements XmlDocument 
{
	private List<LiveProgramData> data= null;
	private XmlCallback callback;
	public String channelName;
	public long createEPGTime;;
	
	
	
	static final String broadcastDataElementName = "BroadcastData";
	static final String broadcastDataCreationtimeAttribute = "creationtime";
	static final String broadcastDataVersionAttribute = "version";
	static final String broadcastDataCodeAttribute = "code";

	static final String schedulerDataElementName = "SchedulerData";
	static final String channelElementName = "Channel";
	static final String channelTextElementName = "ChannelText";
	static final String channelNameElementName = "ChannelName";
	
	static final String eventElementName = "Event"; // 节目元素节点
	static final String eventBegintimeAttribute = "begintime"; // 节目开始时间
	static final String eventDurationAttribute = "duration";   // 节目时间长度
	static final String eventIdAttribute = "eventid"; 		  // 节目id（忽略）
	static final String eventTypeAttribute = "eventtype";      // 节目类型（）
	
	static final String eventTextElementName = "EventText";    // 节目名称节点
	static final String eventTextNameElementName = "Name";     // 节目名称
	static final String eventTextDescriptionElementName = "ShortDescription"; // 节目描述
	
	private static final File NULL = null;
	
	public Dom4jEPG()
	{
		data = new ArrayList<LiveProgramData>();
	}
	public void createXml(String fileName) {
		// TODO Auto-generated method stub
		
	}
	public void parserXmlWithFile(File file) 
	{
		if( file==NULL)
		{
			return;
		}
		File inputXml = file;
		
		try 
		{
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(inputXml);
			// root element
			Element root = document.getRootElement();
			parserEPGElement(root);
		} 
		catch (DocumentException e) 
		{
			System.out.println(e.getMessage());
		}
		
		if( this.callback!=null)
		{
			this.callback.parserFinished((Object)this);
		}
	}
	@Override
	public void parserXml(String fileName) {
		// TODO Auto-generated method stub
		File inputXml = new File(fileName);
		parserXmlWithFile(inputXml);
	}
	
	public void parserEPGElement(Element root) 
	{
		if( root==NULL)
			return;
		String elementName = root.getName();
		List<?> attrList = null;
		if( elementName.compareTo(broadcastDataElementName)==0)
		{
			// System.out.println(elementName+":");
			// 获得root 属性
			attrList = root.attributes();
			for (int i = 0; i < attrList.size(); i++) 
			{
			    //属性的取得
			    Attribute item = (Attribute)attrList.get(i);
			    if( item.getName().compareTo(broadcastDataCreationtimeAttribute)==0)
			    {
			    	try 
					{
						SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss" );
				    	Date date;
						date = format.parse(item.getValue());
						this.createEPGTime = date.getTime();
					} catch (ParseException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    //System.out.println("------" + item.getName() + "=" + item.getValue());
			}
		}
		
		for (Iterator<?> iter = root.elementIterator(); iter.hasNext();) 
		{
			Element subElement = (Element)iter.next();
			elementName = subElement.getName();
			// 判断是否频道
			if( elementName.compareTo(schedulerDataElementName)==0)
			{
				for (Iterator<?> scheduler = subElement.elementIterator(); scheduler.hasNext();) 
				{
					Element schedulerElement = (Element)scheduler.next();
					if( schedulerElement.getName().compareTo(channelElementName)==0)
					{
						parserEPGChannelElement(schedulerElement);
					}
				}
			}
		}
	}
	public void parserEPGChannelElement(Element element)
	{
		if( element==null)
			return;
		String elementName = element.getName();
		//System.out.println(elementName);
		
		for (Iterator<?> iter = element.elementIterator(); iter.hasNext();) 
		{
			Element subElement = (Element)iter.next();
			elementName = subElement.getName();
			// 判断是否频道
			if( elementName.compareTo(eventElementName)==0)
			{
				parserEPGEventElement(subElement);
			}
			else if( elementName.compareTo(channelTextElementName)==0)
			{
				for (Iterator<?> channelText = subElement.elementIterator(); channelText.hasNext();) 
				{
					Element channelTextElement = (Element)channelText.next();
					String channelTextElementName = channelTextElement.getName();
					if (channelTextElementName.compareTo(channelNameElementName) == 0)
					{
						this.channelName = channelTextElement.getText();
						//System.out.println("------" + channelTextElementName + ":" + channelTextElement.getText());
					} 
				}
			}
		}
	}
	
	public void parserEPGEventElement(Element element)
	{
		if( element==null)
			return;
		String elementName = element.getName();
		//System.out.println(elementName);
		List<?> attrList = element.attributes();
		LiveProgramData lpitem = new LiveProgramData();
		data.add(lpitem);
		for (int i = 0; i < attrList.size(); i++) 
		{
		    //属性的取得
		    Attribute item = (Attribute)attrList.get(i);
		    String itemName = item.getName();

		    if( itemName.compareTo(eventBegintimeAttribute) == 0)
		    {
		    	// 20140321003000
				try 
				{
					SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss" );
			    	Date date;
					date = format.parse(item.getValue());
					lpitem.lpBeginTime = date.getTime();
					if( lpitem.lpEndTime>200 )
					{
						lpitem.lpEndTime += lpitem.lpBeginTime;
					}
				} catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    else if( itemName.compareTo(eventDurationAttribute) == 0)
		    {
		    	try 
				{
					SimpleDateFormat format = new SimpleDateFormat( "HHmmss" );
			    	Date date;
					date = format.parse(item.getValue());
					lpitem.lpEndTime = lpitem.lpBeginTime + date.getTime();
				} catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    else if( itemName.compareTo(eventIdAttribute) == 0)
		    {
		    	
		    }
		    else if( itemName.compareTo(eventTypeAttribute) == 0)
		    {
		    	
		    }
		    	
		    //System.out.println("------" + item.getName() + "=" + item.getValue());
		}
		
		for (Iterator<?> iter = element.elementIterator(); iter.hasNext();) 
		{
			Element subElement = (Element)iter.next();
			elementName = subElement.getName();
			// 判断是否频道
			if (elementName.compareTo(eventTextElementName) == 0)
			{
				for (Iterator<?> eventText = subElement.elementIterator(); eventText.hasNext();) 
				{
					Element eventTextElement = (Element)eventText.next();
					String eventTextElementName = eventTextElement.getName();
					if (eventTextElementName.compareTo(eventTextNameElementName) == 0)
					{
						lpitem.lpName = eventTextElement.getText();
						//System.out.println("------" + eventTextElementName + ":" + eventTextElement.getText());
					} 
				}
			} 
			else if (elementName.compareTo(eventTextDescriptionElementName) == 0) 
			{
				//System.out.println("------" + elementName + ":" + subElement.getText());
				lpitem.lpDescription = subElement.getText();
			}
		}
	}
	public void setCallback(XmlCallback callback) {
		this.callback = callback;
	}
	public XmlCallback getCallback() {
		return callback;
	}
	public List<LiveProgramData> getData() {
		return data;
	}
	public void setData(List<LiveProgramData> data) {
		this.data = data;
	}
}
