package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

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
	public static void wfRankings(Storage holder) throws IOException
	{
		wfRankingsHelper(holder, "http://walterfootball.com/fantasy2013top250ppr.php");
		wfRankingsHelper(holder, "http://walterfootball.com/fantasy2013top250.php");
		wfRankingsHelper(holder, "http://walterfootball.com/fantasy2013top250qb2.php");
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
		String text = HandleBasicQueries.handleLists(url, "span");
		String[] perPlayer = ManageInput.tokenize(text, '\n', 1);
		String[][] all = new String[perPlayer.length][];
		for(int i = 0; i < perPlayer.length; i++)
		{
			if(i+5 < perPlayer.length && perPlayer[i+5].contains("total posts"))
			{
				break;
			}
			perPlayer[i] = perPlayer[i].replace(". -- ", ", ");
			all[i] = perPlayer[i].split(", ");
			String playerName = all[i][0];
			if(!playerName.contains(String.valueOf(i+1)))
			{
				break;
			}
			playerName = ParseRankings.fixNames(playerName.split(String.valueOf(i + 1) + "\\. ")[1]);
			String team = "";
			String pos="";
			int val = 0;
			if(!perPlayer[i].contains("DEF"))
			{
				team = ParseRankings.fixTeams(all[i][2].split(". ")[0]);
				pos = all[i][1];
				val = Integer.parseInt(all[i][3].substring(1, all[i][3].length()));
			}
			else
			{
				team = ParseRankings.fixTeams(playerName);
				playerName += " D/ST";
				pos = "D/ST";
				val = Integer.parseInt(all[i][3].substring(1, all[i][3].length()));
			}
			ParseRankings.finalStretch(holder, playerName, val, team, pos);
		}
	}
} 
