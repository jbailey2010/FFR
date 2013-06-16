package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;

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
			String name = ParseRankings.fixNames(brokenValues[i+1].split(", ")[0]);
			name = Storage.nameExists(holder, name);
			String val = brokenValues[i+5];
			String trend = brokenValues[i+6];
			int worth = (int)Double.parseDouble(val);
			PlayerObject newPlayer = new PlayerObject(name, "", "", worth);
			PlayerObject match =  Storage.pqExists(holder, name);
			double a = Double.parseDouble(brokenValues[i+3]);
			double log = Math.log(a);
			log = log * -12.5;
			log = log - 0.06*a;
			log = log + 73.0;
			if(log < 0.0)
			{
				log = 0.0;
			}
			else if(log < 1.0)
			{
				log = 1.0;
			}
			if(match != null)
			{
				match.info.trend = trend;
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				Values.handleNewValue(match.values, newPlayer.values.worth);
				Values.handleNewValue(match.values, log);
				match.vals.add(log);
				match.vals.add(newPlayer.values.worth);
				match.info.team = ParseRankings.fixTeams(match.info.team);
			}
			else
			{
				newPlayer.info.trend = trend;
				Values.isExtreme(newPlayer.values, newPlayer.values.worth);
				Values.handleNewValue(newPlayer.values, log);
				newPlayer.info.team = ParseRankings.fixTeams(newPlayer.info.team);
				newPlayer.vals.add(log);
				newPlayer.vals.add(newPlayer.values.worth);
				holder.players.add(newPlayer);
			}
		}
	}
}
