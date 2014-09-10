package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
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
	public static void parseESPN300(Storage holder, Scoring s) throws IOException
	{
		if(s.catches > 0){
			parseESPN300Worker(HandleBasicQueries.handleLists("http://espn.go.com/fantasy/football/story/_/page/FFLranks14PPR/2014-fantasy-football-rankings-preseason-top-ppr-rankings-point-per-reception", "td"), holder);
		}
		else {
			parseESPN300Worker(HandleBasicQueries.handleLists("http://espn.go.com/fantasy/football/story/_/page/FFLranks14top300/2014-fantasy-football-rankings-preseason-top-300", "td"), holder);
		}
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
			String name;
			if(nameSplit.length > 1)
			{
				name = nameSplit[0];
			}
			else
			{
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
			System.out.println(name + ": " + valDS);
			int val = Integer.parseInt(valDS.substring(1, valDS.length()));
			name = ParseRankings.fixDefenses(ParseRankings.fixNames(name));
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
