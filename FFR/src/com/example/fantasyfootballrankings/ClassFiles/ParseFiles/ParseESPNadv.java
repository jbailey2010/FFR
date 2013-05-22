package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A simple library to help parse espn's adv value from
 * their live drafts
 * @author Jeff
 *
 */
public class ParseESPNadv 
{
	/**
	 * Does all of the parsing of the espn aggregate drafting data
	 * @param holder
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void parseESPNAggregate(Storage holder) throws IOException, XPatherException
	{
		String values = HandleBasicQueries.handleLists("http://games.espn.go.com/ffl/livedraftresults",
				"div div div div table tbody tr td");
		String[] brokenValues = values.split("\n");
		for(int i = 13; i < brokenValues.length; i+=8)
		{ 
			String name = Storage.nameExists(holder, brokenValues[i+1].split(", ")[0]);
			String val = brokenValues[i+5];
			String trend = brokenValues[i+6];
			int worth = (int)Double.parseDouble(val);
			PlayerObject newPlayer = new PlayerObject(name, "", "", worth);
			PlayerObject match =  Storage.pqExists(holder, name);
			if(match != null)
			{
				match.info.trend = trend;
			}
			else
			{
				newPlayer.info.trend = trend;
			}
			ParseRankings.handlePlayer(holder, newPlayer, match);
		}
	}
}
