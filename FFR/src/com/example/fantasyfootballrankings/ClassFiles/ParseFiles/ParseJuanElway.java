package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Parse fantasy football auction functions
 * @author Jeff
 *
 */
public class ParseJuanElway 
{
	/**
	 * Calls most of the positions (ignoring d/st, stupid rankings)
	 * @param holder
	 * @throws IOException
	 */
	public static void parseJuanElwayVals(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.juanelway.com/auction-values/", "td");
		String[] td= html.split("\n");
		for(int i = 6; i < td.length; i+=3)
		{
			if(td[i].equals("RK") || td[i].equals("K"))
			{
				continue;  
			}
			if(td[i].length() > 20)
			{
				break;
			}
			int val = Integer.parseInt(td[i+2].substring(1, td[i+2].length()));
			String name = ParseRankings.fixNames(td[i+1]); 
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
