package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * Handles parsing of espn's top
 * 300 rankings in the draft kit
 * @author Jeff
 *
 */
public class ParseESPN 
{
	/**
	 * Handles the parsing of espn's top 300 rankings
	 * @param holder
	 * @throws IOException
	 */
	public static void parseESPN300(Storage holder) throws IOException
	{
		parseESPN300Worker(HandleBasicQueries.handleLists("http://espn.go.com/fantasy/football/story/_/page/2013preseasonFFLranks300PPR/top-300-ppr-point-per-reception", "td"), holder);
		parseESPN300Worker(HandleBasicQueries.handleLists("http://espn.go.com/fantasy/football/story/_/page/2013preseasonFFLranks250/top-300-position", "td"), holder);

	}
	
	/**
	 * Does the actual espn parsing work
	 */
	public static void parseESPN300Worker(List<String> brokenUp, Storage holder)
	{
		for(int i = 1; i < brokenUp.size(); i+=5)
		{
			String namePos = brokenUp.get(i);
			String[] nameSplit = namePos.split(", ");
			String team;
			String name;
			String pos = "";
			if(nameSplit.length > 1)
			{
				team = nameSplit[1];
				name = nameSplit[0];
			}
			else
			{
				team = "";
				name = namePos;
			}
			if(name.contains("D/ST"))
			{
				name = name.replaceAll("D/ST", "").trim() + " D/ST";
			}
			String valDS = brokenUp.get(i+3);
			if(valDS.contains("-"))
			{
				valDS = "$1";
			}
			int val = Integer.parseInt(valDS.substring(1, valDS.length()));
			String posRank = brokenUp.get(i+2);
			pos = posRank.replaceAll("\\d*$", "");
			ParseRankings.finalStretch(holder, name, val, team, pos);
		}
	}
}
