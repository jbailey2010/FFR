package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Handles parsing of the fake football vals
 * @author Jeff
 *
 */
public class ParseTheFakeFootball {
	
	public static void parseTheFakeFootballVals(Storage holder) throws IOException
	{
		parseTheFakeFootballValsWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AopbSwnRivj7dGJUWkI3OWNad2xoTmN6OEhqaTdnZ2c&amp;output=html&amp;widget=true");
		parseTheFakeFootballValsWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AopbSwnRivj7dDctOVFhdU5SMlA3UUdTejFlR2U0Zmc&amp;output=html&amp;widget=true");
	}
	
	/**
	 * Does the actual parsing
	 * @param holder
	 * @throws IOException
	 */
	public static void parseTheFakeFootballValsWorker(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains("-") && i > 0)
			{
				min = i;
				break;
			}
		}
		int i = min;
		while(i < td.length)
		{
			String name = ParseRankings.fixDefenses(ParseRankings.fixNames(td[i].split(" - ")[0]));
			int val = 0;
			try{
				val = Integer.parseInt(td[i+1].replaceAll("\\$", ""));
			} catch(NumberFormatException e)
			{
				
			}
			ParseRankings.finalStretch(holder, name, val, "", "");
			try
			{
				while(!td[i+1].contains("-"))
				{
					i++;
				}
				i++;
			}catch(ArrayIndexOutOfBoundsException e)
			{
				break;
			}
		}
	}
}
