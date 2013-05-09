package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.StrictMode;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Handle the parsing of the permanent data
 * (pff data from last year)
 * @author Jeff
 *
 */
public class ParsePermanentData 
{
	/**
	 * Parses the percentage of time there were 8+ men
	 * in the box for a running back
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static Map<String, String> parseMenInBox(Storage holder) throws IOException
	{
		String url = "https://www.profootballfocus.com/blog/2013/05/08/facing-eight-in-the-box/";
		String text = HandleBasicQueries.handleLists(url, "td");
		String[] brokenData = text.split("\n");
		Map<String, String> players = new HashMap<String, String>();
		String name = "";
		String value = "";
		for(int i = 0; i < brokenData.length; i++)
		{
			if((i-1)%6 == 0)
			{
				name = brokenData[i];
				String validated = ParseRankings.fixNames(name);
				name = Storage.nameExists(holder, validated);
			}
			else if((i-5)%6 == 0)
			{
				value = brokenData[i];
				players.put(name, value);
				name = "";
				value = "";
			}
		}
		return players;
	}
	
	public static Map<String, String> parseRunPassRatio() throws IOException
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		String url = "https://www.profootballfocus.com/blog/2013/05/08/facing-eight-in-the-box/";
		String text = HandleBasicQueries.handleLists(url, "td");
		String[] brokenData = text.split("\n");
		Map<String, String> players = new HashMap<String, String>();
		String name = "";
		String value = "";
		for(int i = 0; i < brokenData.length; i++)
		{
			if((i-1)%6 == 0)
			{
				name = brokenData[i];
			}
			else if((i-5)%6 == 0)
			{
				value = brokenData[i];
				players.put(name, value);
				name = "";
				value = "";
			}
		}
		return players;
		return null;
	}
}
