package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Parses a few combos of the 4 for 4 rankings
 * @author Jeff
 *
 */
public class Parse4for4 
{
	/**
	 * Calls the parser with the 4 urls
	 * @param holder
	 * @throws IOException
	 */
	public static void parse4for4Wrapper(Storage holder) throws IOException
	{
		parse4for4(holder, "http://www.4for4.com/fantasy-football/tools/auction_values?num_qb=1&num_rb=2.5&num_wr=2.5&num_te=1&num_teams=12&type=STANDARD");
		parse4for4(holder, "http://www.4for4.com/fantasy-football/tools/auction_values?num_qb=1&num_rb=2.5&num_wr=2.5&num_te=1&num_teams=12&type=PPR");
		parse4for4(holder, "http://www.4for4.com/fantasy-football/tools/auction_values?num_qb=2&num_rb=2.5&num_wr=2.5&num_te=1&num_teams=12&type=STANDARD");
		parse4for4(holder, "http://www.4for4.com/fantasy-football/tools/auction_values?num_qb=2&num_rb=2.5&num_wr=2.5&num_te=1&num_teams=12&type=PPR");
	}
	
	/**
	 * Actually parses the html, ignoring most of the extra data
	 */
	public static void parse4for4(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "table tbody tr td");
		String[] brokenUp = html.split("\n");
		int counter = 0;
		for(int i = 0; i < brokenUp.length; i+=8)
		{
			counter++;
			String name = brokenUp[i+1];
			String value = brokenUp[i+5].substring(1, brokenUp[i+5].length());
			ParseRankings.finalStretch(holder, name, Integer.parseInt(value), "", "", counter);
		}
	}
}
