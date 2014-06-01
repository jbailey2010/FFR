package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.io.IOException;



import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class handles basic queries, returning basic info to be handled
 * by other functions
 * @author Jeff
 *
 */  
public class HandleBasicQueries 
{
	public static String ua = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.2 Safari/537.36"; 

	/**
	 * This is the query function for a list
	 * @param url the url to be parsed
	 * @return the text that was parsed
	 * @throws IOException
	 */
	public static List<String> handleLists(String url, String params) throws IOException
	{
		List<String> elems = new ArrayList<String>();
		Document doc = Jsoup.connect(url).userAgent(ua).get();
        Elements links = doc.select(params);

        for (Element element : links) 
        {
        	elems.add(element.text());
        }
        return elems;
	}
	
	public static List<String> handleListsNoUA(String url, String params) throws IOException
	{
		List<String> elems = new ArrayList<String>();
		Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(params);

        for (Element element : links) 
        {
        	elems.add(element.text());
        }
        return elems;
	}
	
	/**
	 * This is the query function for a list
	 * called twice+ with the same page
	 * @param url the url to be parsed
	 * @return the text that was parsed
	 * @throws IOException
	 */
	public static List<String> handleListsMulti(Document doc, String url, String params) throws IOException
	{
		List<String> elems = new ArrayList<String>();
        Elements links = doc.select(params);
        for (Element element : links) 
        {
        	elems.add(element.text());
        }
        return elems;
	}
	/**
	 * Handles the queries to a table
	 * @param url the url to be parsed
	 * @param params the class of the table to be parsed 
	 * @return the text from the table
	 * @throws IOException
	 */
	public static List<String> handleTables(String url, String params) throws IOException
	{
		List<String> elems = new ArrayList<String>();
		Document doc = Jsoup.connect(url).userAgent(ua).timeout(0).get();
		Elements els = doc.getElementsByClass(params);
		for (Element el : els) 
		{
			elems.add(el.text());
		}
		return elems;
	}
	 
	/**
	 * Handles the queries to a table called multiple times
	 * by some function
	 * @param url the url to be parsed
	 * @param params the class of the table to be parsed
	 * @return the text from the table
	 * @throws IOException
	 */
	public static List<String> handleTablesMulti(Document doc,String url, String params) throws IOException
	{
		List<String> elems = new ArrayList<String>();
		Elements els = doc.getElementsByClass(params);
		for (Element el : els)  
		{
			elems.add(el.text());
		}
		return elems;
	}
}
