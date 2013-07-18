package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Parses realtimesports data
 * @author Jeff
 *
 */
public class ParseRTS 
{
	
	/**
	 * Calls the various parsers
	 */
	public static void parseRTSWrapper(Storage holder) throws IOException
	{
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=0", false);
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=1", false);
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=2", false);
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=3", false);
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=4", false);
		parseRTSWorker(holder, "http://www.rtsports.com/football-draft-guide/draft-guide-position.php?POS=5", true);
	}
	
	/**
	 * Does the actual parsing
	 */
	public static void parseRTSWorker(Storage holder, String url, boolean defFlag) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "tr.RankHeader td span");
		String[] td = html.split("\n");
		for(int i = 0; i < td.length; i++)
		{
			int val = 0;
			boolean flag = false;
			if(i+1 >= td.length)
			{
				break; 
			}
			if(td[i+1].contains("$"))
			{
				val = Integer.parseInt(td[i+1].substring(1, td[i+1].length()-1));
				flag = true;
			}
			String name = td[i].substring(0, td[i].length());
			name = ParseRankings.fixNames(name);
			name = name.substring(1, name.length());
			if(flag)
			{
				i++;
			}
			if(defFlag)
			{
				name = ParseRankings.fixDefenses(ParseRankings.fixTeams(name));
			}
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
