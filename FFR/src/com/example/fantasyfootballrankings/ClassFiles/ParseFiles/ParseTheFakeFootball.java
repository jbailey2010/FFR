package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
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
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		for(int i = 0; i < td.size(); i++)
		{
			if(td.get(i).contains("-") && i > 0)
			{
				min = i;
				break;
			}
		}
		int i = min;
		while(i < td.size())
		{
			String name = ParseRankings.fixDefenses(ParseRankings.fixNames(td.get(i).split(" - ")[0]));
			int val = 0;
			try{
				val = Integer.parseInt(td.get(i+1).replaceAll("\\$", ""));
			} catch(NumberFormatException e)
			{
				
			}
			ParseRankings.finalStretch(holder, name, val, "", "");
			try
			{
				while(!td.get(i+1).contains("-"))
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
