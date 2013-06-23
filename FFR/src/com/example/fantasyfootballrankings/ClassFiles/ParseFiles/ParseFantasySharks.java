package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Handles parsing of the fantasy sharks aggregate data
 * @author Jeff
 *
 */
public class ParseFantasySharks {
	/**
	 * Does the actual parsing
	 * @param holder
	 * @throws IOException
	 */
	public static void parseFSAverage(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.fantasysharks.com/apps/Projections/SeasonProjections.php?pos=ALL", "td");
		String[] td = html.split("\n");
		for(int i = 37; i < td.length; i+=20)
		{
			if(td[i].contains("The above views are not"))
			{
				break;
			}
			if(td[i].equals("Tier Break"))
			{
				i++;
			}
			String nameFull = td[i+3];
			String lastName = nameFull.split(",")[0].trim().replaceAll("[^a-zA-Z]+","");
			String firstName = nameFull.split(",")[1].trim().replaceAll("[^a-zA-Z]+","");
			String name = ParseRankings.fixNames(firstName + " " + lastName);
			String val = td[i+19].replace("$", "");
			if(val.equals("") || val.equals(" "))
			{
				val = "0";
			}
			int worth = Integer.parseInt(val);
			ParseRankings.finalStretch(holder, name, worth, "", "");
		}
	}
}
