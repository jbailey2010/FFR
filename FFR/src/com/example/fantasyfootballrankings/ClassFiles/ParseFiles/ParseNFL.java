package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Parses the NFL stuff
 * @author Jeff
 *
 */
public class ParseNFL 
{
	/**
	 * Calls the top 200 on the worker
	 * @param holder
	 * @throws IOException
	 */
	public static void parseNFLAAVWrapper(Storage holder) throws IOException
	{
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=0&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=26&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=51&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=76&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=101&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=126&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=151&position=all&sort=draftAveragePosition");
		parseNFLAAVWorker(holder, "http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=176&position=all&sort=draftAveragePosition");
	}
	
	/**
	 * Does the actual parsing of the NFL AAV
	 */
	public static void parseNFLAAVWorker(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		for(int i = 0; i < td.length; i+=4)
		{
			String nameSet[] = td[i].split(" ");
			String name = "";
			int filter = 0;
			for(int j = 0; j < nameSet.length; j++)
			{
				if(nameSet[j].equals("DEF"))
				{
					filter = j;
					break;
				}
				if(nameSet[j].equals("-"))
				{
					filter = j - 1;
					break;
				}
				if(nameSet[j].equals("View"))
				{
					filter = j - 1;
					break;
				}
				if(nameSet[j].length() == j)
				{
					filter = j;
					break;
				}
				if(nameSet[j].equals("QB") || nameSet[j].equals("RB") || nameSet[j].equals("WR") || nameSet[j].equals("TE") || nameSet[j].equals("K"))
				{
					filter = j;
					break;
				}
			}
			for(int j = 0; j < filter; j++)
			{
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixDefenses(ParseRankings.fixNames(name.substring(0, name.length()-1)));
			int val = Integer.parseInt(td[i+3]);
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
