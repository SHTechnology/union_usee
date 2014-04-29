import java.io.File;     
import java.util.Iterator;   
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;   
import org.dom4j.DocumentException;      
import org.dom4j.Element;   
import org.dom4j.io.SAXReader;  

public class Dom4jDemo implements XmlDocument  {

	@Override
	public void createXml(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parserXmlWithFile(File file) 
	{
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
			parserElement(root);
		} 
		catch (DocumentException e) 
		{
			System.out.println(e.getMessage());
		}
		System.out.println("dom4j parserXml"); 
	}
	@Override
	public void parserXml(String fileName) {
		// TODO Auto-generated method stub
		File inputXml = new File(fileName);
		parserXmlWithFile(inputXml);
	}
	public void parserElement(Element element) 
	{
		if( element==null)
			return;
		String elementName = element.getName();
		if( element.elements().size()<=0)
		{
			System.out.println("-node name:"+ elementName + "  text:"+ element.getText());
		}
		else
		{
			System.out.println("-node name:"+ elementName);
		}
	
		List<?> attrList = element.attributes();
		for (int i = 0; i < attrList.size(); i++) 
		{
		    //属性的取得
		    Attribute item = (Attribute)attrList.get(i);
		    System.out.println("------" + item.getName() + "=" + item.getValue());
		}
		
		for (Iterator<?> iter = element.elementIterator(); iter.hasNext();) 
		{
			Element subElement = (Element)iter.next();
			parserElement(subElement);
		}
	}
}
