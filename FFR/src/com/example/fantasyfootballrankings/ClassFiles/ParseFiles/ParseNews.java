package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseRotoWorldNews;
import AsyncTasks.ParsingAsyncTask.ParseTwitterFeeds;
import AsyncTasks.ParsingAsyncTask.ParseTwitterSearch;
import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadRotoNews;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.TwitterWork;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
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
			String newsObj = data[i];
			String date = newsObj.split("\\(")[1].split("\\)")[0];
			String news = newsObj.substring(newsObj.indexOf(")") + 2, newsObj.length());
			String header = newsObj.split("by")[0];
			news = news.split("Analysis:")[0];
			NewsObjects newNews = new NewsObjects(header, news, date);
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
		String date = HandleBasicQueries.handleListsMulti(doc, url, "ul li div span");
		String names = HandleBasicQueries.handleListsMulti(doc, url, "ul li div strong a");
		String[] reportSet = report.split("\n");
		String[] impactSet = impact.split("\n");
		String[] dateSet = date.split("\n");
		String[] namesSet = names.split("\n");
		int min = Math.min(reportSet.length, impactSet.length);
		min = Math.min(min, dateSet.length);
		min = Math.min(min, namesSet.length);
		for(int i = 0; i < min; i++)
		{
			NewsObjects news = new NewsObjects(namesSet[i].replace(":", "-") + "\n\n" + reportSet[i], impactSet[i], dateSet[i]);
			newsSet.add(news);
		}
		return newsSet;
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
	 * @param obj 
	 */
	public static void startTwitterAsync(Context cont, String selection, TwitterWork obj)
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseTwitterFeeds news = stupid.new ParseTwitterFeeds(cont);
		news.execute(cont, selection, obj);	
	}
	
	/**
	 * Calls the actual searching function
	 * @param cont
	 * @param search
	 * @param obj 
	 */
	public static void startTwitterSearchAsync(Context cont, String search, String header, boolean flag, String query, TwitterWork obj)
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseTwitterSearch news = stupid.new ParseTwitterSearch(cont, flag, query, obj);
		news.execute(cont, search, header, obj);	
	}
}
