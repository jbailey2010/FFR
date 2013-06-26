package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Does the parsing of rotopost's rankings
 * @author Jeff
 *
 */
public class ParseRotoPost 
{
	/**
	 * Calls the parsing function to get the average of the two
	 */
	public static void parseRotoPostWrapper(Storage holder) throws IOException
	{
		parseRotoPostWorker(holder, "http://www.rotopost.com/xml/2013/rankings/top200_standard.xml");
		parseRotoPostWorker(holder, "http://www.rotopost.com/xml/2013/rankings/top200_ppr.xml");
	}
	
	/**
	 * Does the actual parsing work
	 */
	public static void parseRotoPostWorker(Storage holder, String url) throws IOException
	{
		Document doc = Jsoup.connect(url).timeout(0).get();
		String names = HandleBasicQueries.handleListsMulti(doc, url, "player");
		String vals = HandleBasicQueries.handleListsMulti(doc, url, "value");
		String[] nameSet = names.split("\n");
		String[] valSet = vals.split("\n");
		for(int i = 0; i < nameSet.length; i++)
		{
			String name = ParseRankings.fixDefenses(ParseRankings.fixNames(nameSet[i]));
			int val = Integer.parseInt(valSet[i]);
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
