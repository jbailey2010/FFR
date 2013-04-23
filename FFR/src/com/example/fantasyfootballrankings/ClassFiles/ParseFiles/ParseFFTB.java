package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.net.MalformedURLException;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Handles parsing from fantasy football toolbox's rankings
 * @author Jeff
 *
 */
public class ParseFFTB 
{
	/**
	 * Parses each of the top four pages of the fftoolbox rankings
	 * @param holder
	 * @throws XPatherException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void parseFFTBRankingsWrapper(Storage holder) throws MalformedURLException, IOException, XPatherException
	{
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?page=1&pos=top200&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?page=2&pos=top200&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?page=3&pos=top200&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?page=4&pos=top200&teams=12&budget=200");
	}
	
	/**
	 * Does the individual, per page work
	 * @param holder the storage unit to write to
	 * @param url the url to be parsed
	 * @throws XPatherException 
	 * @throws IOException 
	 * @throws MalformedURLException  
	 */
	public static void parseFFTBPage(Storage holder, String url) throws MalformedURLException, IOException, XPatherException
	{
		String text = HandleBasicQueries.handleLists(url, "td");
		String[] brokenUp = text.split("\n");
		for(int i = 1; i < brokenUp.length; i+=2)
		{ 
			String name = brokenUp[i];
			String team = brokenUp[i++];
			String pos = brokenUp[i++];
			String val = brokenUp[i+=4];
			if(team.split(" ").length <= 2)
			{
				if(name.contains("Defense"))
				{
					name = name.replaceAll("Defense", "D/ST");
				}
				val = val.substring(1, val.length());
				ParseRankings.finalStretch(holder, name, Integer.parseInt(val), team, pos);
			}
		}
	}
}
