package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Parse fantasy football auction functions
 * @author Jeff
 *
 */
public class ParseFFA 
{
	/**
	 * Calls most of the positions (ignoring d/st, stupid rankings)
	 * @param holder
	 * @throws IOException
	 */
	public static void parseFFAWrapper(Storage holder) throws IOException
	{
		parseFFAWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AmFZg72qUEYcdFZuaFN4dG9rOW9YcUVSWWlsUkY2eGc&gid=0");
		parseFFAWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AmFZg72qUEYcdFZuaFN4dG9rOW9YcUVSWWlsUkY2eGc&gid=1");
		parseFFAWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AmFZg72qUEYcdFZuaFN4dG9rOW9YcUVSWWlsUkY2eGc&gid=2");
		parseFFAWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AmFZg72qUEYcdFZuaFN4dG9rOW9YcUVSWWlsUkY2eGc&gid=3");
		parseFFAWorker(holder, "https://docs.google.com/spreadsheet/pub?key=0AmFZg72qUEYcdFZuaFN4dG9rOW9YcUVSWWlsUkY2eGc&gid=5");
	}
	
	/**
	 * Does the actual parsing
	 */
	public static void parseFFAWorker(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		for(int i = 21; i < td.length; i+=6)
		{
			if(i+1 >= td.length)
			{
				break;
			}
			String name = ParseRankings.fixDefenses(ParseRankings.fixNames(td[i+1]));
			int playerVal = Integer.parseInt(td[i+3].substring(1, td[i+3].length()));
			ParseRankings.finalStretch(holder, name, playerVal, "", "");
		}
	}
}
