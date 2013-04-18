package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A little library to handle the parsing of the gridiron experts
 * rankings
 * @author Jeff
 *
 */
public class ParseGE 
{ 
	/**
	 * Does all of the work. Gets the values, ignores headers,
	 * splits name from value, makes magic happen
	 * @param holder
	 * @throws IOException
	 */
	public static void geRankings(Storage holder) throws IOException
	{
		String values = HandleBasicQueries.handleLists(
				"http://gridironexperts.com/fantasy-football-auction-prices",
				"div article div div div table tbody tr");
		String[] geLines = values.split("\n");
		for(int i = 0; i < geLines.length; i++)
		{
			if(!geLines[i].contains("RK"))
			{
				String[] parsedPlayer = geLines[i].split(" ");
				String name = "";
				for(int j = 1; j < parsedPlayer.length - 1; j++)
				{
					name += parsedPlayer[j] + " ";
				}
				name = name.substring(0, name.length()-1);
				String value = parsedPlayer[parsedPlayer.length-1];
				value = value.substring(1, value.length());
				int worth = Integer.parseInt(value);
				ParseRankings.finalStretch(holder, name, worth, "", "");
			}
		}
	}
}
