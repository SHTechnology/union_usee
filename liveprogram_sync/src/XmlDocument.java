import java.io.File;


/** 
* 
* @author hongliang.dinghl 
* ����XML�ĵ�����������Ľӿ� 
*/ 
public interface XmlDocument 
{
	/** 
	* ����XML�ĵ� 
	* @param fileName �ļ�ȫ·������ 
	*/ 
	public void createXml(String fileName); 
	/** 
	* ����XML�ĵ� 
	* @param fileName �ļ�ȫ·������ 
	*/ 
	public void parserXml(String fileName); 
	/** 
	* ����XML�ĵ� 
	* @param file �ļ���� 
	*/
	public void parserXmlWithFile(File file); 
}
