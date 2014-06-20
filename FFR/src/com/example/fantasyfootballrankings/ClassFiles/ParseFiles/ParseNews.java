package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseRotoWorldNews;
import AsyncTasks.ParsingAsyncTask.ParseTwitterFeeds;
import AsyncTasks.ParsingAsyncTask.ParseTwitterSearch;
import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadRotoNews;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.Utils.TwitterWork;
import com.example.fantasyfootballrankings.Pages.Home;
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
		List<String> reportSet = HandleBasicQueries.handleListsMulti(doc, url, "div.report");
		List<String> impactSet = HandleBasicQueries.handleListsMulti(doc, url, "div.impact");
		List<String> dateSet = HandleBasicQueries.handleListsMulti(doc, url, "div.date");
		for(int i = 0; i < reportSet.size(); i++)
		{
			NewsObjects news = new NewsObjects(reportSet.get(i), impactSet.get(i+1), dateSet.get(i));
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
		List<String> reportSet = HandleBasicQueries.handleTablesMulti(doc, url, "news-item");
		List<String> impactSet = HandleBasicQueries.handleTablesMulti(doc, url, "news-impact");
		List<String> dateSet = HandleBasicQueries.handleTablesMulti(doc, url, "news-date");
		for(int i = 0; i < reportSet.size(); i++)
		{
			NewsObjects news = new NewsObjects(reportSet.get(i), impactSet.get(i).replace("Huddle Up: ", ""), dateSet.get(i));
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
		List<String> data = HandleBasicQueries.handleListsMulti(doc, url, "div#newsPage1 table tbody tr td");
		for(int i = 0; i < data.size(); i+=4)
		{
			String newsObj = data.get(i);
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
		List<String> reportSet = HandleBasicQueries.handleListsMulti(doc, url, "dt");
		List<String> impactSet = HandleBasicQueries.handleListsMulti(doc, url, "dd");
		List<String> dateSet = HandleBasicQueries.handleListsMulti(doc, url, "ul li div span");
		List<String> namesSet = HandleBasicQueries.handleListsMulti(doc, url, "ul li div strong a");
		int min = Math.min(reportSet.size(), impactSet.size());
		min = Math.min(min, dateSet.size());
		min = Math.min(min, namesSet.size());
		for(int i = 0; i < min; i++)
		{
			NewsObjects news = new NewsObjects(namesSet.get(i).replace(":", "-") + " - " + reportSet.get(i), impactSet.get(i), dateSet.get(i));
			newsSet.add(news);
		}
		return newsSet;
	}
	
	public static List<NewsObjects> parseMFL() throws IOException 
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String url = "http://football.myfantasyleague.com/" + Home.yearKey + "/news_articles";
		Document doc = Jsoup.connect(url).timeout(0).get();
		List<String> title = HandleBasicQueries.handleListsMulti(doc, url, "td.headline b a");
		Elements elems = doc.select("tr.oddtablerow");
		Elements elems2 = doc.select("tr.eventablerow");
		List<String> news = new ArrayList<String>();
		for(int i = 0; i < elems.size(); i++){
			Element odd = elems.get(i).child(2);
			Element even= elems2.get(i).child(2);
			news.add(odd.text());
			news.add(even.text());
		}
		List<String> time = HandleBasicQueries.handleListsMulti(doc, url, "td.timestamp");
		for(int i = 0; i < 75; i++){
			String newsStr = news.get(i);
			newsStr = newsStr.substring(newsStr.indexOf(")") + 2);
			if(newsStr.contains("... (More)")){
				newsStr = newsStr.split("\\(More\\)")[0];
			}
			NewsObjects newsObj = new NewsObjects(title.get(i), newsStr, time.get(i) + " ago");
			newsSet.add(newsObj);
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
	 * @param b 
	 */
	public static void startNewsAsync(Context cont, boolean rh, boolean rp, boolean th, boolean cbs, boolean si, boolean b) 
	{
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseRotoWorldNews news = stupid.new ParseRotoWorldNews(cont);
		news.execute(cont, rh, rp, th, cbs, si, b);
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
