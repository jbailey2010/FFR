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
		String result = "";
		Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(params);

        for (Element element : links) 
        {
        	result += element.text() + "\n";
        }
        return result;
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
		String result = "";
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.getElementsByClass(params);
		for (Element el : els) 
		{
			result += el.text() + "\n";
		}
		return result;
	}
	/*below is an example of how to programmatically add a textview
	 * NOTE: NEED TO SET ID IN WHATEVER XML IS BEING USED
	 * SECOND NOTE: TO MAKE MULTIPLE PRINTS, USE LINEARLAYOUT
	View linearLayout =  findViewById(R.id.rankings);
    try 
    {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
		String text = HandleBasicQueries.handleParsing("http://fantasyfootballcalculator.com/adp_csv_ppr.php?teams=12", "pre");
	    String[] list = text.split("\n");
	    for(int i = 0; i < list.length; i++)
	    {
	    	TextView valueTV = new TextView(cont);
	    	valueTV.setText(list[i]);
	    	valueTV.setId(i);
		    ((RelativeLayout)linearLayout).addView(valueTV);			
	    }
    } 
    catch (MalformedURLException e1) 
    {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} 
    catch (IOException e1) 
    {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} 
    catch (XPatherException e1) 
    {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}	   */
}
