package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Handles parsing of espn's top
 * 300 rankings in the draft kit
 * @author Jeff
 *
 */
public class ParseESPN 
{
	/**
	 * Handles the parsing of espn's top 300 rankings
	 * @param holder
	 * @throws IOException
	 */
	public static void parseESPN300(Storage holder) throws IOException
	{
		String text = HandleBasicQueries.handleLists("http://sports.espn.go.com/fantasy/football/ffl/story?page=NFLDK2K13ranksTop300", "td");
		String[] brokenUp=text.split("\n");
		for(int i = 1; i < brokenUp.length; i+=5)
		{
			String namePos = brokenUp[i];
			String[] nameSplit = namePos.split(", ");
			String team;
			String name;
			String pos = "";
			if(nameSplit.length > 1)
			{
				team = nameSplit[1];
				name = nameSplit[0];
			}
			else
			{
				team = "";
				name = namePos;
			}
			String valDS = brokenUp[i+3];
			if(valDS.contains("-"))
			{
				valDS = "$1";
			}
			int val = Integer.parseInt(valDS.substring(1, valDS.length()));
			ParseRankings.finalStretch(holder, name, val, team, pos);
		}
	}
}
