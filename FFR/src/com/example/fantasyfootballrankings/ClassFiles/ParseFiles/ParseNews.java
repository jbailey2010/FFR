package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
	public static List<NewsObjects> parseNewsRoto() throws IOException
	{
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String url = "http://www.rotoworld.com/headlines/nfl/0/football-headlines";
		Document doc = Jsoup.connect(url).get();
		String report = HandleBasicQueries.handleListsMulti(doc, url, "div.report");
		String impact = HandleBasicQueries.handleListsMulti(doc, url, "div.impact");
		String date = HandleBasicQueries.handleListsMulti(doc, url, "div.date");
		String source = HandleBasicQueries.handleListsMulti(doc,url,"div.source");
		String[] reportSet = report.split("\n");
		String[] impactSet = impact.split("\n");
		String[] dateSet = impact.split("\n");
		String[] sourceSet = source.split("\n");
		for(int i = 0; i < reportSet.length; i++)
		{
			NewsObjects news = new NewsObjects(reportSet[i], impactSet[i], dateSet[i], sourceSet[i]);
			newsSet.add(news);
		}
		return newsSet;
	}
}
