package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Parses the football guys sets of rankings
 * @author Jeff
 *
 */
public class ParseFootballGuys 
{
	/**
	 * Calls the worker for the 4 relavent ranks
	 * @param holder
	 * @throws IOException
	 */
	public static void parseFGWrapper(Storage holder) throws IOException
	{
		parseFGWorker("http://footballguys.com/cs_p2e200.htm", holder);
		parseFGWorker("http://footballguys.com/cs_c2e200.htm", holder);
		parseFGWorker("http://footballguys.com/cs_h1e200.htm", holder);
		parseFGWorker("http://footballguys.com/cs_h2e200.htm", holder);
	}
	
	/**
	 * Does the actual parsing
	 * @param url
	 * @param holder
	 * @throws IOException
	 */
	public static void parseFGWorker(String url, Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		for(int i = 44; i < td.length; i+=5)
		{
			if(i < td.length - 1 && td[i+1].contains("Use Position List"))
			{
				i+=4;
				continue;
			}
			if(td[i].contains("ADP"))
			{
				i--;
				continue;
			}
			if((!ManageInput.isInteger(td[i]) && td[i].length() < 3))// || td[i].length() == 0)
			{
				i+=4;
				continue;
			}
			if(td[i].contains("Draft by Position Lists"))
			{
				continue;
			}
			if(td[i].contains("Maximize Draft Value by"))
			{
				i+=3;
				continue;
			}
			if(td[i].contains("Use Overall list to"))
			{
				i++;
				continue;
			}
			if(td[i].contains("Use 51-60 picks with"))
			{
				i+=3;
				continue;
			}
			if(td[i].contains("Use Positional Lists"))
			{
				continue;
			}
			if(td[i].contains("Draft a"))
			{
				i++;
				continue;
			}
			if(i < td.length - 1)
			{
				String name = ParseRankings.fixNames(ParseRankings.fixDefenses(td[i+1].split(" \\(")[0]));
				int val = Integer.parseInt(td[i+3]);
				ParseRankings.finalStretch(holder, name, val, "", "");
			}
		}
	}
}
