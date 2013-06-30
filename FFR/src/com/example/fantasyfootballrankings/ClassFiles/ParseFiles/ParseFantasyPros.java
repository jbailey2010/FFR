package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
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
		String html = HandleBasicQueries.handleLists("http://www.fantasypros.com/nfl/auction-values/overall.php", "td");
		String[] td = html.split("\n");
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
			double a = ecr;
			double b = adp;
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
			double logb = Math.log(b);
			logb = logb * -23.5;
			logb = logb - 0.06*b;
			logb = logb + 73.0;
			if(logb < 0.0)
			{
				logb = 0.0;
			}
			else if(logb < 1.0)
			{
				logb = 1.0;
			}
			if(match != null)
			{    
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				Values.handleNewValue(match.values, newPlayer.values.worth);
				Values.handleNewValue(match.values, val2);
				Values.handleNewValue(match.values, val3);
				match.vals.add(newPlayer.values.worth);
				match.vals.add((double) val2);
				match.vals.add((double) val3);
				if(a != -1)
				{
					Values.handleNewValue(match.values, log);
					match.vals.add(log);
					match.values.ecr = a;
				}
				if(b != -1)
				{
					Values.handleNewValue(match.values, logb);
					match.info.adp = String.valueOf(b);
					match.vals.add(logb);
				}
			}
			else
			{
				Values.isExtreme(newPlayer.values, newPlayer.values.worth);
				Values.handleNewValue(newPlayer.values, val2);
				Values.handleNewValue(newPlayer.values, val3);
				newPlayer.vals.add((double) val2);
				newPlayer.vals.add((double) val3);
				newPlayer.vals.add(newPlayer.values.worth);
				if(a != -1)
				{
					Values.handleNewValue(newPlayer.values, log);
					newPlayer.vals.add(log);
					newPlayer.values.ecr = a;
				}
				if(b != -1)
				{
					Values.handleNewValue(newPlayer.values, logb);
					newPlayer.info.adp = String.valueOf(b);
					newPlayer.vals.add(logb);
				}
				holder.players.add(newPlayer);
			}	
		}
	}
}
