package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;

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
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 2; i < td.length; i+=4)
		{
			if(td[i].contains("AdChoices"))
			{
				break;
			} 
			String name = td[i].split(" \\(")[0];
			if(td[i].contains("DEF"))
			{
				if(td[i].contains("NYG"))
				{
					name = "New York Giants";
				}
				else if(td[i].contains("NYJ"))
				{
					name = "New York Jets";
				}
				name = ParseRankings.fixDefenses(name);

			}
			String rank = td[i+1].split("\\$")[1];
			String aavStr = td[i+2].split("\\$")[1];
			double aav = 0.0;
			int worth = Integer.parseInt(rank);
			if(!aavStr.equals("-") && !aavStr.equals("0.0"))
			{
				aav = Double.parseDouble(aavStr);
			}
			String validated = ParseRankings.fixNames(name);
			String newName = Storage.nameExists(holder, validated);
			PlayerObject newPlayer = new PlayerObject(newName, "", "", worth);
			PlayerObject match =  Storage.pqExists(holder, newName);
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
