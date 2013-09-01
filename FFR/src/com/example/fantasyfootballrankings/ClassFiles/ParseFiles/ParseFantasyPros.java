package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;

/**
 * Gets an aggregate set of auction values
 * @author Jeff
 *
 */
public class ParseFantasyPros 
{
	/**
	 * Gets the auction set, ecr set, and adp set
	 * @param holder
	 * @throws IOException
	 */
	
	public static void parseFantasyProsAgg(Storage holder) throws IOException
	{
		parseFantasyProsWorker(holder, "http://www.fantasypros.com/nfl/auction-values/overall.php?teams=12", 4);
		parseFantasyProsWorker(holder, "http://www.fantasypros.com/nfl/auction-values/overall.php?teams=10", 2);
	}
	
	/**
	 * Does the fantasy pros parsing
	 */
	public static void parseFantasyProsWorker(Storage holder, String url, int count) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains(" RB ") ||  td[i].contains(" QB ") || td[i].contains(" WR ") || td[i].contains(" TE ") ||
					td[i].contains(" K ") || td[i].contains(" DST "))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=6)
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
			int ecr = -1;
			try{
				ecr = Integer.parseInt(td[i+4]);
			}
			catch (NumberFormatException e) {
				  ecr = -1;
			} 
			int adp = -1;
			try{
				adp = Integer.parseInt(td[i+5]);
			}
			catch (NumberFormatException e) {
				  adp = -1;
			} 
			String validated = ParseRankings.fixNames(name);
			String newName = Storage.nameExists(holder, validated);
			PlayerObject newPlayer = new PlayerObject(newName, "", "", val1);
			PlayerObject match =  Storage.pqExists(holder, newName);
			if(match != null)
			{    
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				for(int j = 0; j < count; j++)
				{
					Values.handleNewValue(match.values, newPlayer.values.worth);
				}
				if(ecr != -1)
				{
					match.values.ecr = (double) ecr;
				}
				if(adp != -1)
				{
					match.info.adp = String.valueOf(adp);
				}
			}
			else
			{
				for(int j = 0; j < count - 1; j++)
				{
					Values.handleNewValue(newPlayer.values, newPlayer.values.worth);
				}
				if(ecr != -1)
				{
					newPlayer.values.ecr = (double) ecr;
				}
				if(adp != -1)
				{
					newPlayer.info.adp = String.valueOf(adp);
				}
				holder.players.add(newPlayer);
				holder.parsedPlayers.add(newPlayer.info.name);
			}	
		}
	}
}
