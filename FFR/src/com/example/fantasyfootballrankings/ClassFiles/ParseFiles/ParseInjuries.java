package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * Parses injury data
 * @author Jeff
 *
 */
public class ParseInjuries 
{
	/**
	 * Parses the injury data from rotoworld's page
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseRotoInjuries() throws IOException
	{
		HashMap<String, String> injuries = new HashMap<String, String>();
		List<String> perRow = HandleBasicQueries.handleLists("http://www.rotoworld.com/teams/injuries/nfl/all/", "td");
		for(int i = 7; i < perRow.size(); i++)
		{
			String pos = perRow.get(i+2);
			String name = ParseRankings.fixNames(perRow.get(i));
			String status = perRow.get(i+=3);
			String injuryType = perRow.get(i+=2);
			if(injuryType.equals("-"))
			{
				injuryType = "Suspended";
			}
			String returnDate = perRow.get(++i);
			String output = "Injury Status: " + status + "\n" + 
							"Type of Injury: " + injuryType + "\n" + 
							"Expected Return: " + returnDate + "\n";
			injuries.put(name + "/" + pos, output);
		}
		return injuries;
	}
}
