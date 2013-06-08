package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.io.StringReader;



import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseRotoWorldNews;
import AsyncTasks.ParsingAsyncTask.ParseTwitterFeeds;
import AsyncTasks.ParsingAsyncTask.ParseTwitterSearch;
import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadRotoNews;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
/**
 * A library to handle the parsing of news
 * @author Jeff
 *
 */
public class ParseNews 
{
	/**
	 * Parses rotoworld forums for news
	 * @return
	 * @throws IOException
	 */
	public static List<NewsObjects> parseNewsRoto(String url) throws IOException
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		Document doc = Jsoup.connect(url).timeout(0).get();
		String report = HandleBasicQueries.handleListsMulti(doc, url, "div.report");
		String impact = HandleBasicQueries.handleListsMulti(doc, url, "div.impact");
		String date = HandleBasicQueries.handleListsMulti(doc, url, "div.date");
		String[] reportSet = report.split("\n");
		String[] impactSet = impact.split("\n");
		String[] dateSet = date.split("\n");
		for(int i = 0; i < reportSet.length; i++)
		{
			NewsObjects news = new NewsObjects(reportSet[i], impactSet[i+1], dateSet[i]);
			newsSet.add(news);
		}
		return newsSet;
	}
	
	/**
	 * Handles the huddle news parsing
	 * @return
	 * @throws IOException
	 */
	public static List<NewsObjects> parseNewsHuddle() throws IOException
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String url = "http://www.thehuddle.com/fantasy_football_news.php";
		Document doc = Jsoup.connect(url).timeout(0).get();
		String report = HandleBasicQueries.handleTablesMulti(doc, url, "news-item");
		String impact = HandleBasicQueries.handleTablesMulti(doc, url, "news-impact");
		String date = HandleBasicQueries.handleTablesMulti(doc, url, "news-date");
		String[] reportSet = report.split("\n");
		String[] impactSet = impact.split("\n");
		String[] dateSet = date.split("\n");
		for(int i = 0; i < reportSet.length; i++)
		{
			NewsObjects news = new NewsObjects(reportSet[i], impactSet[i].replace("Huddle Up: ", ""), dateSet[i]);
			newsSet.add(news);
		}
		return newsSet;
	}
	
	/**
	 * Parses the cbs player news
	 * @return
	 * @throws IOException
	 */ 
	public static List<NewsObjects> parseCBS() throws IOException
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String url = "http://fantasynews.cbssports.com/fantasyfootball/playernews";
		Document doc = Jsoup.connect(url).timeout(0).get();
		String report = HandleBasicQueries.handleListsMulti(doc, url, "div#newsPage1 table tbody tr td");
		String[] data = report.split("\n");
		for(int i = 0; i < data.length; i+=4)
		{
			String date = data[i];
			String name = data[i+1];
			String news = data[i+3].split("News:")[1];
			String header = data[i+3].split("News:")[0];
			String impact = news.split("Analysis:")[1];
			news = news.split("Analysis:")[0];
			NewsObjects newNews = new NewsObjects(name + "\n\n" + header + "\n\n" + news.substring(1, news.length()), 
					impact.substring(1, impact.length()), date);
			newsSet.add(newNews);
		}
		return newsSet;
	}
	
	/**
	 * SI news parsing
	 * @return
	 * @throws IOException
	 */
	public static List<NewsObjects> parseSI() throws IOException
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String url = "http://sportsillustrated.cnn.com/fantasy/player_news/nfl/";
		Document doc = Jsoup.connect(url).timeout(0).get();
		String report = HandleBasicQueries.handleListsMulti(doc, url, "dt");
		String impact = HandleBasicQueries.handleListsMulti(doc, url, "dd");
		String date = HandleBasicQueries.handleListsMulti(doc, url, "li div span");
		String names = HandleBasicQueries.handleListsMulti(doc, url, "li div strong");
		String[] reportSet = report.split("\n");
		String[] impactSet = impact.split("\n");
		String[] dateSet = date.split("\n");
		String[] namesSet = names.split("\n");
		for(int i = 0; i < dateSet.length; i++)
		{
			NewsObjects news = new NewsObjects(namesSet[i].replace(":", "-") + " " + reportSet[i+2], impactSet[i+2], dateSet[i]);
			newsSet.add(news);
		}
		return newsSet;
	}
	
	/**
	 *Twitter parsing
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public static List<NewsObjects> parseTwitter(String url) throws IOException, SAXException, ParserConfigurationException
	{
		return ParseTwitter.twitterParse(url);
	}

	/**
	 * Handles conditionally reading news
	 * from file
	 * @param cont
	 */
	public static void startNewsReading(Context cont) 
	{
		StorageAsyncTask stupid = new StorageAsyncTask();
		ReadRotoNews news = stupid.new ReadRotoNews(cont);
		news.execute(cont);
	}

	/**
	 * Handles conditionally fetching the news
	 * @param cont
	 */
	public static void startNewsAsync(Context cont, boolean rh, boolean rp, boolean th, boolean cbs, boolean si) 
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseRotoWorldNews news = stupid.new ParseRotoWorldNews(cont);
		news.execute(cont, rh, rp, th, cbs, si);
	}
	
	/**
	 * Handles conditionally fetching twitter data
	 * @param cont
	 * @param selection
	 */
	public static void startTwitterAsync(Context cont, String selection)
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseTwitterFeeds news = stupid.new ParseTwitterFeeds(cont);
		news.execute(cont, selection);	
	}
	
	/**
	 * Calls the actual searching function
	 * @param cont
	 * @param search
	 */
	public static void startTwitterSearchAsync(Context cont, String search, String header)
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseTwitterSearch news = stupid.new ParseTwitterSearch(cont);
		news.execute(cont, search, header);	
	}
}
