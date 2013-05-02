package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.net.MalformedURLException;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Handles parsing from fantasy football toolbox's rankings
 * @author Jeff
 *
 */
public class ParseFFTB 
{
	/**
	 * Parses each of the top four pages of the fftoolbox rankings
	 * @param holder
	 * @throws XPatherException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void parseFFTBRankingsWrapper(Storage holder) throws MalformedURLException, IOException, XPatherException
	{
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=QB&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=RB&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=WR&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=TE&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=PK&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2013/auction-values.cfm?pos=Def&teams=12&budget=200");
	}
	
	/**
	 * Does the individual, per page work
	 * @param holder the storage unit to write to
	 * @param url the url to be parsed
	 * @throws XPatherException 
	 * @throws IOException 
	 * @throws MalformedURLException  
	 */
	public static void parseFFTBPage(Storage holder, String url) throws MalformedURLException, IOException, XPatherException
	{
		String text = HandleBasicQueries.handleLists(url, "td");
		String[] brokenUp = text.split("\n");
		for(int i = 1; i < brokenUp.length; i+=2)
		{ 
			String name = brokenUp[i];
			String team = brokenUp[++i];
			String pos = brokenUp[++i];
			String age = brokenUp[i+=2];
			String val = brokenUp[i+=2];
			if(team.split(" ").length <= 2)
			{
				if(name.contains("Defense"))
				{
					name = name.replaceAll("Defense", "D/ST");
				}
				val = val.substring(1, val.length());
				String validated = ParseRankings.fixNames(name);
				String newName = Storage.nameExists(holder, validated);
				PlayerObject newPlayer = new PlayerObject(name, team, pos, Integer.parseInt(val));
				PlayerObject match =  Storage.pqExists(holder, newName);
				if(match != null)
				{
					match.info.age = age;
				}
				newPlayer.info.age = age;
				ParseRankings.handlePlayer(holder, newPlayer, match);
			}
		}
	}
}
