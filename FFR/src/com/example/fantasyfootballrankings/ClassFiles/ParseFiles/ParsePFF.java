package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;


import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A library to handle the parsing of the pro football
 * focus rankings
 * @author Jeff
 *
 */
public class ParsePFF 
{
	/**
	 * Handles parsing pro football focus' rankings
	 * @param holder DOESNT WORK HEADER IS NULL
	 * @throws IOException
	 */
	public static void parsePFFRankingsWrapper(Storage holder) throws IOException
	{
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=QB&cat=Season+2013", holder);
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=RB&cat=Season+2013", holder);
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=WR&cat=Season+2013", holder);
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=TE&cat=Season+2013", holder);
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=DST&cat=Season+2013", holder);
		parsePFFRankings("https://www.profootballfocus.com/data/ffranking/inc/server.php?proj=K&cat=Season+2013", holder);
	} 
	
	/**
	 * Handles the individual parsing of positions
	 * @param url
	 * @param Holder
	 * @throws IOException
	 */
	public static void parsePFFRankings(String url, Storage Holder) throws IOException
	{
		Document doc = Jsoup.connect(url).get();

	}
}
