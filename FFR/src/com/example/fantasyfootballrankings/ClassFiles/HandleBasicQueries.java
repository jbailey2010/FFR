package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;


import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.string;
import android.util.Log;

/**
 * This class handles basic queries, returning basic info to be handled
 * by other functions
 * @author Jeff
 *
 */  
public class HandleBasicQueries 
{
	
	/**
	 * This is the query function for a list
	 * @param url the url to be parsed
	 * @return the text that was parsed
	 * @throws IOException
	 */
	public static String handleLists(String url, String params) throws IOException
	{
		StringBuilder result = new StringBuilder(5000);
		Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(params);

        for (Element element : links) 
        {
        	result.append(element.text() + "\n");
        }
        return result.toString();
	}
	
	/**
	 * This is the query function for a list
	 * called twice+ with the same page
	 * @param url the url to be parsed
	 * @return the text that was parsed
	 * @throws IOException
	 */
	public static String handleListsMulti(Document doc, String url, String params) throws IOException
	{
		StringBuilder result = new StringBuilder(5000);
        Elements links = doc.select(params);
        for (Element element : links) 
        {
        	result.append(element.text() + "\n");
        }
        return result.toString();
	}
	/**
	 * Handles the queries to a table
	 * @param url the url to be parsed
	 * @param params the class of the table to be parsed
	 * @return the text from the table
	 * @throws IOException
	 */
	public static String handleTables(String url, String params) throws IOException
	{
		StringBuilder result = new StringBuilder(5000);
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.getElementsByClass(params);
		for (Element el : els) 
		{
			result.append(el.text() + "\n");
		}
		return result.toString();
	}
	
	/**
	 * Handles the queries to a table called multiple times
	 * by some function
	 * @param url the url to be parsed
	 * @param params the class of the table to be parsed
	 * @return the text from the table
	 * @throws IOException
	 */
	public static String handleTablesMulti(Document doc,String url, String params) throws IOException
	{
		StringBuilder result = new StringBuilder(5000);
		Elements els = doc.getElementsByClass(params);
		for (Element el : els) 
		{
			result.append(el.text() + "\n");
		}
		return result.toString();
	}
}
