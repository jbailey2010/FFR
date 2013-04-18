package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A library that handles the parsing
 * of the cbs rankings
 * @author Jeff
 *
 */
public class ParseCBS 
{
	/**
	 * NOTE CHECK FOR A PPR UPDATE WITH AUCTION VALUES
	 * THESE ARE NOT PPR, BUT NONE ELSE HAD AUCTION.
	 * Code can be copy/pasted to another fn to do the same
	 * NOTE: THIS DOES NOT VALIDATE PLAYER NAMES IN THE 
	 * @param holder the storage to check/write to
	 * @throws IOException
	 */
	public static void cbsRankings(Storage holder) throws IOException
	{
		String url = "http://fantasynews.cbssports.com/fantasyfootball/rankings/yearly";
		
		try {
			cbsHelper(holder, url, "row1");
			cbsHelper(holder, url, "row2");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * The same code was present in the cbs function twice,
	 * so it got abstracted to here. It's pretty obvious what does what
	 * @param holder the storage used
	 * @param url the url to be parsed
	 * @param params the parameters to be fetched
	 * @throws IOException
	 */
	public static void cbsHelper(Storage holder, String url, String params) throws IOException
	{
		String text = HandleBasicQueries.handleTables(url, params);
		String[] textArr = text.split("\n");
		String[][] words = new String[textArr.length][];
		for(int i = 0; i < words.length; i++)
		{
			words[i] = textArr[i].split(" ");
			int val = 0;
			int a = -1; 
			for(int e = 0; e < words[i].length; e++)
			{
				if(words[i][e].contains("$"))
				{
					a = e;
					break;
				}
			}
			int valueTeam = a - 1;
			if(a == -1)
			{
				valueTeam = words[i].length - 2;
				if(words[i][words[i].length - 1].length() > 1)
				{
					valueTeam = valueTeam + 1;
				}
			}
			String team = words[i][valueTeam];
			String playerName = "";
			for(int v = 1; v < valueTeam; v++)
			{
				playerName += words[i][v] + " ";
			}
			if(playerName.length() > 0)
			{
				playerName = playerName.substring(0, playerName.length() -1);
			}
			if(a != -1)
			{
				String value = words[i][a];
				value = value.replace("$", "");
				val = Integer.parseInt(value) * 2;
				if(val == 0)
				{
					val = 1;
				}
			}
			String pos = " ";
			String[] check = playerName.split(" ");
			if(check.length == 1)
			{
				pos = "D/ST";
				playerName += " D/ST";
			}
			ParseRankings.finalStretch(holder, playerName, val, team, pos);
		}
	}
}
