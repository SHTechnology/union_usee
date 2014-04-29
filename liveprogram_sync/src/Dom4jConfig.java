import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class Dom4jConfig implements XmlDocument {
	private String logFolder = "";
	private String epgFolder = "";
	private long lastSyncTime = 1000*60*30;
	private long updatePeriod = 0;
	private String jdbcUrl = "";
	private String dbuser_name = "";
	private String dbuser_passsword = "";
	
	static final String logFolderElementName = "logFolder";
	static final String epgFolderElementName = "EPGFolder";
	static final String lastSyncTimeElementName = "lastSyncTime";
	static final String updatePeriodElementName = "updatePeriod";
	static final String jdbcUrlElementName = "jdbcUrl";
	static final String dbuser_nameElementName = "dbuser_name";
	static final String dbuser_passswordElementName = "dbuser_passsword";
	
	@Override
	public void createXml(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parserXml(String fileName) {
		// TODO Auto-generated method stub
		File inputXml = new File(fileName);
		parserXmlWithFile(inputXml);
	}

	@Override
	public void parserXmlWithFile(File file) {
		// TODO Auto-generated method stub
		
		
		if( file==null)
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

			for (Iterator<?> iter = root.elementIterator(); iter.hasNext();) 
			{
				Element element = (Element)iter.next();
				String name = element.getName();
				String value = element.getStringValue();
				if( name.compareTo(logFolderElementName)==0)
				{
					logFolder = value;
				}
				else if( name.compareTo(epgFolderElementName)==0)
				{
					epgFolder = value;
				}
				else if( name.compareTo(lastSyncTimeElementName)==0)
				{
					if( value.length()>0)
					lastSyncTime = Long.parseLong(value);
				}
				else if( name.compareTo(updatePeriodElementName)==0)
				{
					if( value.length()>0)
					updatePeriod = Long.parseLong(value);
				}
				else if( name.compareTo(jdbcUrlElementName)==0)
				{
					jdbcUrl = value;
				}
				else if( name.compareTo(dbuser_nameElementName)==0)
				{
					dbuser_name = value;
				}
				else if( name.compareTo(dbuser_passswordElementName)==0)
				{
					dbuser_passsword = value;
				}
			}
		} 
		catch (DocumentException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	public String getLogFolder() {
		return logFolder;
	}

	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
	}

	public String getEpgFolder() {
		return epgFolder;
	}

	public void setEpgFolder(String epgFolder) {
		this.epgFolder = epgFolder;
	}

	public long getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(long lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public long getUpdatePeriod() {
		return updatePeriod;
	}

	public void setUpdatePeriod(long updatePeriod) {
		this.updatePeriod = updatePeriod;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDbuser_name() {
		return dbuser_name;
	}

	public void setDbuser_name(String dbuser_name) {
		this.dbuser_name = dbuser_name;
	}

	public String getDbuser_passsword() {
		return dbuser_passsword;
	}

	public void setDbuser_passsword(String dbuser_passsword) {
		this.dbuser_passsword = dbuser_passsword;
	}

	
}
