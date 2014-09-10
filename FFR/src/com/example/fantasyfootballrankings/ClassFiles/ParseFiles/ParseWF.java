package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;



import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * A basic library to handle the parsing of the walter football
 * rankings
 * @author Jeff
 *
 */ 
public class ParseWF 
{
	/**
	 * There's two rankings, one standard, one ppr, so I'm using both
	 * @param holder
	 * @throws IOException
	 */
	public static void wfRankings(Storage holder, Scoring s, Roster r) throws IOException
	{
		if(r.qbs > 1 || (r.flex != null && r.flex.op == 1)){
			wfRankingsHelper(holder, "http://walterfootball.com/fantasy2014top250qb2.php");
		}
		else if(s.catches > 0){
			wfRankingsHelper(holder, "http://walterfootball.com/fantasy2014top250ppr.php");
		}
		else{
			wfRankingsHelper(holder, "http://walterfootball.com/fantasy2014top250.php");
		}
	}
	
	/**
	 * This is the relevant code for the walterfootball parsing.
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void wfRankingsHelper(Storage holder, String url) throws IOException, ArrayIndexOutOfBoundsException
	{
		List<String> perPlayer = HandleBasicQueries.handleLists(url, "ol.fantasy-board div li");
		String[][] all = new String[perPlayer.size()][];
		for(int i = 0; i < perPlayer.size(); i++)
		{
			all[i] = perPlayer.get(i).split(", ");
			String playerName = all[i][0];
			playerName = ParseRankings.fixNames(playerName);
			String pos="";
			int val = 0;
			if(!perPlayer.get(i).contains("DEF"))
			{
				pos = all[i][1];
			}
			else
			{
				playerName += " D/ST";
				pos = "D/ST";
			}
			val = Integer.parseInt(perPlayer.get(i).split("\\$")[1].split(" ")[0]);
			ParseRankings.finalStretch(holder, playerName, val, "", pos);
		}
	}
} 
