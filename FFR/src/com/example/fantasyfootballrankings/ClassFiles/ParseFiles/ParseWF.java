package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
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
		String text = HandleBasicQueries.handleLists(url, "ol li");
		String[] perPlayer = text.split("\n");
		String[][] all = new String[perPlayer.length][];
		for(int i = 0; i < perPlayer.length; i++)
		{
			perPlayer[i] = perPlayer[i].replace(". -- ", ", ");
			all[i] = perPlayer[i].split(", ");
			String playerName = all[i][0];
			playerName = ParseRankings.fixNames(playerName);
			String team = all[i][2];
			String pos = all[i][1];
			int val = Integer.parseInt(all[i][3].substring(1, all[i][3].length()));
			ParseRankings.finalStretch(holder, playerName, val, team, pos);
		}
	}
} 
