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
		for(int i = 20; i < td.length; i+=9)
		{
			String name = ParseRankings.fixNames(td[i].split(" - ")[0]);
			int val= Integer.parseInt(td[i+1].substring(1, td[i+1].length()));
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
