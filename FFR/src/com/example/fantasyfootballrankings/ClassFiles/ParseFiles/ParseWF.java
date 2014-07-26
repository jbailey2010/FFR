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
	 * NOTE: THE NAMES CORRECTIONS SHOULD BE ABSTRACTED AS IT'LL PROBABLY
	 * COME UP AGAIN LATER
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void wfRankingsHelper(Storage holder, String url) throws IOException
	{
		List<String> perPlayer = HandleBasicQueries.handleLists(url, "span");
		String[][] all = new String[perPlayer.size()][];
		for(int i = 0; i < perPlayer.size(); i++)
		{
			if(i+5 < perPlayer.size() && perPlayer.get(i+5).contains("total posts"))
			{
				break;
			}
			perPlayer.set(i, perPlayer.get(i).replace(". -- ", ", "));
			all[i] = perPlayer.get(i).split(", ");
			String playerName = all[i][0];
			if(!playerName.contains(String.valueOf(i+1)))
			{
				break;
			}
			playerName = ParseRankings.fixNames(playerName.split(String.valueOf(i + 1) + "\\. ")[1]);
			String pos="";
			int val = 0;
			if(!perPlayer.get(i).contains("DEF"))
			{
				pos = all[i][1];
				val = Integer.parseInt(all[i][3].substring(1, all[i][3].length()));
			}
			else
			{
				playerName += " D/ST";
				pos = "D/ST";
				val = Integer.parseInt(all[i][3].substring(1, all[i][3].length()));
			}
			ParseRankings.finalStretch(holder, playerName, val, "", pos);
		}
	}
} 
