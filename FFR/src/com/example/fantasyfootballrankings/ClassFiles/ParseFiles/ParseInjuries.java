package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;

public class ParseInjuries 
{
	public static HashMap<String, String> parseRotoInjuries() throws IOException
	{
		HashMap<String, String> injuries = new HashMap<String, String>();
		String html = HandleBasicQueries.handleLists("http://www.rotoworld.com/teams/injuries/nfl/all/", "td");
		String[] perRow = html.split("\n");
		for(int i = 7; i < perRow.length; i++)
		{
			String name = ParseRankings.fixNames(perRow[i]);
			String status = perRow[i+=3];
			String injuryType = perRow[i+=2];
			if(injuryType.equals("-"))
			{
				injuryType = "Suspended";
			}
			String returnDate = perRow[++i];
			String output = "Injury Status: " + status + "\n" + 
							"Type of Injury: " + injuryType + "\n" + 
							"Expected Return: " + returnDate + "\n";
			injuries.put(name, output);
			System.out.println(output);
		}
		return injuries;
	}
}
