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
	/**
	 * Does the actual parsing
	 * @param holder
	 * @throws IOException
	 */
	public static void parseTheFakeFootballVals(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("https://docs.google.com/spreadsheet/pub?key=0AopbSwnRivj7dGJUWkI3OWNad2xoTmN6OEhqaTdnZ2c&amp;output=html&amp;widget=true", "td");
		String[] td = html.split("\n");
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
