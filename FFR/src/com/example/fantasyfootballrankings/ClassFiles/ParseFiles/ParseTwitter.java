package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
/**
 * Handles the parsing of the twitter data
 * @author Jeff
 *
 */
public class ParseTwitter 
{
	/**
	 * The main function for handling twitter tweets
	 * @param url
	 * @return
	 */
	public static List<NewsObjects> twitterParse(String url)
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
	    try  
	    {  
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document doc = db.parse(new URL(url).openStream());
			NodeList nList = doc.getElementsByTagName("status");
			System.out.println("Size: " + nList.getLength());
			for(int i = 0; i < nList.getLength(); i++)
			{
				Node node = nList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element element = (Element) node;
					System.out.println(getValue("text", element));
					String date = getValue("created_at", element).split("\\+0000")[0];
					String header = getValue("name", element) + ": " + getValue("text", element);
					StringBuilder replySet = new StringBuilder(1000);
					List<String> replies = handleReplies(element);
					for(String reply: replies)
					{
						replySet.append("In reply to: " + reply + "\n");
					}
					if(replySet.length() < 5)
					{
						replySet.append(" ");
					}
					NewsObjects news = new NewsObjects(header, replySet.toString(), date);
					newsSet.add(news);
				}
			}
	    } 
	    catch (Exception e) 
	    {  
	        e.printStackTrace();  
	    } 
		return newsSet;
	}
	
	/**
	 * Handles getting replies iteratively
	 * @param element
	 * @return
	 * @throws ParserConfigurationException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<String> handleReplies(Element element) throws ParserConfigurationException, MalformedURLException, IOException, SAXException
	{
		List<String> replies = new ArrayList<String>();
		String urlBase = "https://api.twitter.com/1/statuses/show.xml?id=";
		int counter = 0;
		while(getValue("in_reply_to_status_id", element) != null && counter < 2)
		{
			counter++;
			System.out.println("had reply");
			String commentId = getValue("in_reply_to_status_id", element);
			String url = urlBase + commentId + "&include_entities=true";
			System.out.println(url);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document doc;
	    	
			doc = db.parse(new URL(url).openStream());
			NodeList nList = doc.getElementsByTagName("status");
			Node node = nList.item(0);
			if(node.getNodeType() == Node.ELEMENT_NODE) 
			{
				element = (Element) node;
				System.out.println(getValue("text", element));
				System.out.println(getValue("name", element));
				System.out.println(getValue("created_at", element));
				replies.add(getValue("name", element) + " (" + getValue("created_at",element) + "): "  
						+ getValue("text", element) + "\n");
			}
		}
		return replies;
	}
	
	/**
	 * Parses the data from the xml
	 * @param tag
	 * @param element
	 * @return
	 */
	private static String getValue(String tag, Element element) 
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		if(node == null)
		{
			return null;
		}
		return node.getNodeValue();
	}
	
}
