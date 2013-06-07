package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;

public class ParseFantasyPros 
{
	public static void parseFantasyProsAgg(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.fantasypros.com/nfl/auction-values/overall.php", "td");
		String[] td = html.split("\n");
		for(int i = 8; i < td.length; i+=6)
		{
			String name = td[i].split(", ")[0];
			if(td[i].contains("DST"))
			{
				String[] fullName = name.split(" ");
				name = fullName[fullName.length-1] + " D/ST";
			}
			int val1 = Integer.parseInt(td[i+1].split("\\$")[1]);
			int val2 = Integer.parseInt(td[i+2].split("\\$")[1]);
			int val3 = Integer.parseInt(td[i+3].split("\\$")[1]);
			String validated = ParseRankings.fixNames(name);
			String newName = Storage.nameExists(holder, validated);
			PlayerObject newPlayer = new PlayerObject(newName, "", "", val1);
			PlayerObject match =  Storage.pqExists(holder, newName);
			if(match != null)
			{
				holder.players.remove(match);
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				Values.handleNewValue(match.values, newPlayer.values.worth);
				Values.handleNewValue(match.values, val2);
				Values.handleNewValue(match.values, val3);
				holder.players.add(match);
			}
			else
			{
				Values.isExtreme(newPlayer.values, newPlayer.values.worth);
				Values.handleNewValue(newPlayer.values, val2);
				Values.isExtreme(newPlayer.values, val2);
				Values.handleNewValue(newPlayer.values, val3);
				Values.isExtreme(newPlayer.values, val3);
				holder.players.add(newPlayer);
			}	
		}
	}
}
