package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

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
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		for(int i = 0; i < td.size(); i++)
		{
			if(td.get(i).contains(" RB ") ||  td.get(i).contains(" QB ") || td.get(i).contains(" WR ") || td.get(i).contains(" TE ") ||
					td.get(i).contains(" K ") || td.get(i).contains(" DST "))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.size(); i+=6)
		{
			String name = td.get(i).split(", ")[0];
			if(td.get(i).contains("DST"))
			{
				String[] fullName = name.split(" ");
				name = fullName[fullName.length-1] + " D/ST";
			}
			int val1 = Integer.parseInt(td.get(i+1).split("\\$")[1]);
			int ecr = -1;
			try{
				ecr = Integer.parseInt(td.get(i+4));
			}
			catch (NumberFormatException e) {
				  ecr = -1;
			} 
			int adp = -1;
			try{
				adp = Integer.parseInt(td.get(i+5));
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
				if(adp != -1 && !holder.isRegularSeason)
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
				if(adp != -1 && !holder.isRegularSeason)
				{
					newPlayer.info.adp = String.valueOf(adp);
				}
				holder.players.add(newPlayer);
				holder.parsedPlayers.add(newPlayer.info.name);
			}	
		}
	}
}
