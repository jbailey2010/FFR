package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.conf.ConfigurationBuilder;

import android.util.Base64;

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
			for(int i = 0; i < nList.getLength(); i++)
			{
				Node node = nList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element element = (Element) node;
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
	    if(newsSet.size() == 0)
	    {
	    	newsSet.add(new NewsObjects("Request Limit Exceeded", "Check Back in an Hour", ""));
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
			String commentId = getValue("in_reply_to_status_id", element);
			String url = urlBase + commentId + "&include_entities=true";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document doc;
	    	
			doc = db.parse(new URL(url).openStream());
			NodeList nList = doc.getElementsByTagName("status");
			Node node = nList.item(0);
			if(node.getNodeType() == Node.ELEMENT_NODE) 
			{
				element = (Element) node;
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

	/**
	 * Parses the tweets from the individual accounts
	 */
	public static List<NewsObjects> parseTwitter4j(String accountName)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
        Paging paging = new Paging(1, 25);
        List<Status> statuses = new ArrayList<Status>();
        try {
			statuses = twitter.getUserTimeline(accountName, paging);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Status status : statuses)
		{
			String header = status.getUser().getName() + ": " + status.getText();
			String date = status.getCreatedAt().toString();
			StringBuilder replySet = new StringBuilder(1000);
			int counter = 0;
			while(status.getInReplyToStatusId() != -1L && counter < 4)
			{
				try {
					status = twitter.showStatus(status.getInReplyToStatusId());
				} catch (TwitterException e) {
					break;
				}
				replySet.append("In reply to: " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
						+ status.getText() + "\n\n");
				counter++;
			}
			if(replySet.length() < 5)
			{
				replySet.append(" ");
			}
			NewsObjects news = new NewsObjects(header, replySet.toString(), date);
			newsSet.add(news);
		}
        if(newsSet.size() < 10)
        {
        	newsSet.add(new NewsObjects("Rate limit exceeded, try again in a few minutes", " ", " "));
        } 
        return newsSet;
	}
	
	/**
	 * Handles twitter parsing for a list instead of a user
	 * @return
	 */
	public static List<NewsObjects> parseTwitter4jList()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
        Paging paging = new Paging(1, 25);
        try {
        	int id = -1;
			ResponseList<UserList> list = twitter.getUserLists("chriswesseling");
			for(UserList listIter : list)
			{
				if(listIter.getName().equals("Fantasy Football Writers"))
				{
					id = listIter.getId();
					break;
				}
			}
			ResponseList<Status> listStatuses = twitter.getUserListStatuses(id, paging);
			for(Status status : listStatuses)
			{
				String header = status.getUser().getName() + ": " + status.getText();
				String date = status.getCreatedAt().toString();
				StringBuilder replySet = new StringBuilder(1000);
				int counter = 0;
				while(status.getInReplyToStatusId() != -1L && counter < 4)
				{
					status = twitter.showStatus(status.getInReplyToStatusId());
					replySet.append("In reply to:  " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
							+ status.getText() + "\n\n");
					counter++;
				}
				if(replySet.length() < 5)
				{
					replySet.append(" ");
				}
				NewsObjects news = new NewsObjects(header, replySet.toString(), date);
				newsSet.add(news);
			}
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(newsSet.size() == 0)
        {
        	newsSet.add(new NewsObjects("Rate limit exceeded, try again in a few minutes", " ", " "));
        }
        return newsSet;
	}
	
	/**
	 * Parses twitter given a user's input query terms, returning relevant tweets
	 */
	public static List<NewsObjects> searchTweets(String query)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
        try {
        	Query queryObj = new Query(query);
	    	queryObj.setCount(30);
	        QueryResult result;
			result = twitter.search(queryObj);
			List<Status> statuses = result.getTweets();
		    for(Status status: statuses)
		    {
				String header = status.getUser().getName() + ": " + status.getText();
				String date = status.getCreatedAt().toString();
				StringBuilder replySet = new StringBuilder(1000);
				int counter = 0;
				while(status.getInReplyToStatusId() != -1L && counter < 3)
				{
					status = twitter.showStatus(status.getInReplyToStatusId());
					replySet.append("In reply to:  " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
							+ status.getText() + "\n\n");
					counter++;
				}
				if(replySet.length() < 5)
				{
					replySet.append(" ");
				}
				NewsObjects news = new NewsObjects(header, replySet.toString(), date);
				newsSet.add(news);
			}
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return newsSet;
	}
}
