package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Handles parsing of the myfantasy league
 * aggregate data (no defenses included)
 * @author Jeff
 *
 */
public class ParseMyFantasyLeague 
{
	/**
	 * The actual function that handles parsing the aggregate
	 * auction value
	 * @param holder
	 * @throws IOException
	 */
	public static void parseMFLAggregate(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists(
				"http://football.myfantasyleague.com/2013/aav?COUNT=300&POS=QB%2BRB%2BWR%2BTE%2BPK&CUTOFF=5&FRANCHISES=-1&IS_PPR=-1&IS_KEEPER=-1&TIME=", "tr");
		String[] rows = html.split("\n");
		for(int i = 0; i < rows.length; i++)
		{
			String[] playerCheck = rows[i].split(" ");
			if(playerCheck[0].contains("."))
			{
				String[] firstSplit = rows[i].split(", ");
				String[] firstHalf = firstSplit[0].split(" ");
				StringBuilder lastNameBuilder = new StringBuilder(100);
				for(int j = 1; j < firstHalf.length; j++)
				{
					lastNameBuilder.append(firstHalf[j]);
				}
				String[] secondHalf = firstSplit[1].split(" ");
				String firstName = secondHalf[0];
				String lastName = lastNameBuilder.toString();
				int value = Integer.parseInt(secondHalf[3].replace("$", ""));
				System.out.println(firstName + " " + lastName + ": " + value);
				ParseRankings.finalStretch(holder, firstName + " " + lastName, value, "", "");
			}
		}
	}
}
