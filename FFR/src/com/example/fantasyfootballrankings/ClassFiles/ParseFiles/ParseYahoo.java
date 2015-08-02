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

public class ParseYahoo 
{
	public static void parseYahooWrapper(Storage holder) throws IOException
	{
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=QB&sort=DA_PC");
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=RB&sort=DA_PC");
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=WR&sort=DA_PC");
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=TE&sort=DA_PC");
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=K&sort=DA_PC");
		parseYahoo(holder, "http://football.fantasysports.yahoo.com/f1/draftanalysis?tab=AD&pos=DEF&sort=DA_PC");
	}
	
	public static void parseYahoo(Storage holder, String url) throws IOException
	{
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int startingIndex = 0;
		for(int i = 0; i < td.size(); i++) {
			if(td.get(i).contains("Note") || td.get(i).contains("Notes")){
				startingIndex = i;
				break;
			}
		}
		for(int i = startingIndex; i < td.size(); i+=4)
		{
			if(td.get(i).contains("AdChoices"))
			{
				break;
			} 
			String fName = "";
			String name = "";
			if(td.get(i).split(" \\(")[0].contains("Note"))
			{
				String splitter = "Note ";
				if(td.get(i).split(" \\(")[0].contains("Notes"))
				{
					splitter = "Notes ";
				}
				fName = td.get(i).split(" \\(")[0].split(splitter)[1].split(" - ")[0];
				String[] nameSet = fName.split(" ");
				for(int j = 0; j < nameSet.length - 1; j++)
				{
					name += nameSet[j] + " ";
				}
				name = name.substring(0, name.length() - 1);
			}
			else
			{
				name = td.get(i).split(" \\(")[0];
			}
			if(td.get(i).contains("DEF"))
			{
				if(td.get(i).contains("NYG"))
				{
					name = "New York Giants";
				}
				else if(td.get(i).contains("NYJ"))
				{
					name = "New York Jets";
				}
				name = ParseRankings.fixDefenses(name);

			}
			String rank = td.get(i+1).split("\\$")[1];
			String aavStr = td.get(i+2).split("\\$")[1];
			double aav = 0.0;
			int worth = Integer.parseInt(rank);
			if(!aavStr.equals("-") && !aavStr.equals("0.0"))
			{
				aav = Double.parseDouble(aavStr);
			}
			String validated = ParseRankings.fixNames(name);
			PlayerObject newPlayer = new PlayerObject(validated, "", "", worth);
			PlayerObject match =  Storage.pqExists(holder, validated);
			if(match != null)
			{
				BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
				Values.handleNewValue(match.values, newPlayer.values.worth);
				Values.handleNewValue(match.values, aav);
			}
			else
			{
				Values.handleNewValue(newPlayer.values, aav);
				holder.players.add(newPlayer);
				holder.parsedPlayers.add(newPlayer.info.name);
			}	
		}
	}
}
