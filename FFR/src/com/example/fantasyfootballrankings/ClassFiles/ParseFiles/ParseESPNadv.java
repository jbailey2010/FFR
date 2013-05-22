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
		ArrayList<String>intermediate = new ArrayList<String>(500);
		for(int i = 13; i < brokenValues.length; i+=8)
		{
			intermediate.add(brokenValues[i+1]);
			intermediate.add(brokenValues[i+5]);
		}
		for(int i = 0; i < intermediate.size(); i+=3)
		{
			String toBeFixed = intermediate.get(i);
			if(toBeFixed.contains(","))
			{
				intermediate.set(i, toBeFixed.substring(0, toBeFixed.lastIndexOf(',')));
				String name = ParseRankings.fixNames(intermediate.get(i));
				String value = intermediate.get(i+1);
				String difference = intermediate.get(i+2);
				Double worth = Double.parseDouble(value);
				int playerVal = worth.intValue();
				if(playerVal == 0)
				{
					playerVal = 1;
				}
				String newName = Storage.nameExists(holder, name);
				PlayerObject newPlayer = new PlayerObject(newName, "", "", playerVal);
				PlayerObject match =  Storage.pqExists(holder, newName);
				if(match != null)
				{
					match.info.trend = difference;
				}
				else
				{
					newPlayer.info.trend = difference;
				}
				ParseRankings.handlePlayer(holder, newPlayer, match);
			}
		}
	}
}
