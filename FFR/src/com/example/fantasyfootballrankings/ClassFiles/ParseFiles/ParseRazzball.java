package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Parses Razzball's rankings
 * @author Jeff
 *
 */
public class ParseRazzball 
{
	/**
	 * Calls the worker for each page of the sheet
	 * @param holder
	 * @throws IOException
	 */
	public static void parseRazzballWrapper(Storage holder) throws IOException
	{
		parseRazzballWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0ArfGomt41ZrzdC05bTA2amhKaUxMN2cyQnRxLWFBTGc&gid=0");
		parseRazzballWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0ArfGomt41ZrzdC05bTA2amhKaUxMN2cyQnRxLWFBTGc&gid=1");
		parseRazzballWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0ArfGomt41ZrzdC05bTA2amhKaUxMN2cyQnRxLWFBTGc&gid=2");
		parseRazzballWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0ArfGomt41ZrzdC05bTA2amhKaUxMN2cyQnRxLWFBTGc&gid=3");
	}
	
	/**
	 * Does the individual, per-page parsing
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void parseRazzballWorker(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		for(int i = 9; i < td.length; i+=3)
		{
			String name = ParseRankings.fixNames(td[i]);
			if(i + 1 >= td.length)
			{
				break;
			}
			int val = Integer.parseInt(td[i+1]);
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
