package org.korsakow.services.finder;

import java.sql.ResultSet;

import javax.xml.xpath.XPathExpressionException;

import org.korsakow.ide.DataRegistry;
import org.korsakow.ide.NodeListResultSet;
import org.korsakow.services.tdg.VideoTDG;
import org.w3c.dom.Element;

public class VideoFinder
{
	public static ResultSet find(long id) throws XPathExpressionException{
		Element element = DataRegistry.getHelper().findElementByIdTag(id);
		ListNodeList nodeList = new ListNodeList();
		if (element != null) {
			if (!element.getTagName().equals(VideoTDG.NODE_NAME))
				throw new XPathExpressionException("Expected '" + VideoTDG.NODE_NAME + "', found '" + element.getTagName() + "'");
			nodeList.add(element);
		}
		return new NodeListResultSet(nodeList);
	}
	public static ResultSet findAll() throws XPathExpressionException{
		return DataRegistry.getHelper().xpathAsResultSet("/korsakow/videos/Video");
	}
}
