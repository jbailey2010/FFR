package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
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
		
		String[] brokenValues = ManageInput.tokenize(values, '\n', 1);
		for(int i = 13; i < brokenValues.length; i+=8)
		{ 
			if(i+1 >= brokenValues.length)
			{
				break;
			}
			String name = ParseRankings.fixNames(brokenValues[i+1].split(", ")[0]).replace("*", "");
			name = Storage.nameExists(holder, name);
			String val = brokenValues[i+5];
			String trend = brokenValues[i+6];
			int worth = (int)Double.parseDouble(val);
			PlayerObject newPlayer = new PlayerObject(name, "", "", worth);
			PlayerObject match =  Storage.pqExists(holder, name);
			if(match != null)
			{
				match.info.trend = trend;
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				Values.handleNewValue(match.values, newPlayer.values.worth);
				match.info.team = ParseRankings.fixTeams(match.info.team);
			}
			else
			{
				newPlayer.info.trend = trend;
				newPlayer.info.team = ParseRankings.fixTeams(newPlayer.info.team);
				holder.players.add(newPlayer);
				holder.parsedPlayers.add(newPlayer.info.name);
			}
		}
	}
}
