package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

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
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=QB&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=RB&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=WR&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=TE&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=PK&teams=12&budget=200");
		parseFFTBPage(holder, "http://www.fftoolbox.com/football/2014/auction-values.cfm?pos=Def&teams=12&budget=200");
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
		String[] brokenUp = ManageInput.tokenize(text, '\n', 1);
		for(int i = 1; i < brokenUp.length; i+=2)
		{ 
			String name = brokenUp[i].replace("Defense", "D/ST");
			String team = brokenUp[++i];
			String pos = brokenUp[++i];
			if(pos.equals("Def"))
			{
				pos = "D/ST";
			}
			String age = brokenUp[i+=2];
			String val = brokenUp[i+=2];
			if(team.split(" ").length <= 2)
			{
				val = val.substring(1, val.length());
				String validated = ParseRankings.fixNames(name);
				String newName = Storage.nameExists(holder, validated);
				PlayerObject newPlayer = new PlayerObject("", "", "", 0);
				try{
					newPlayer = new PlayerObject(newName, team, pos, Integer.parseInt(val));
				} catch(NumberFormatException e)
				{
					break;
				}
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
	
	/**
	 * Parses the bye weeks into a hashmap
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseByeWeeks() throws IOException
	{
		HashMap<String, String> byes = new HashMap<String, String>();
		String html = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/byeweeks.cfm", "td");
		String[] brokenUp = ManageInput.tokenize(html, '\n', 1);
		for(int i = 0; i < brokenUp.length; i+=2)
		{
			String week = brokenUp[i];
			String[] teamSet = brokenUp[i+1].split(", ");
			for(String team : teamSet)
			{
				String newTeam = ParseRankings.fixTeams(team);
				byes.put(newTeam, week);
			}
		}
		return byes;
	}
	
	/**
	 * Calls the worker to handle all of the various pages
	 */
	public static void parseSOSInSeason(Storage holder) throws IOException
	{
		HashMap<String, Integer>sos = new HashMap<String, Integer>();
		parseSOSWorker("http://www.fftoolbox.com/football/2013/points-allowed.cfm?pos=QB", sos, "QB");
		parseSOSWorker("http://www.fftoolbox.com/football/2013/points-allowed.cfm?pos=RB", sos, "RB");
		parseSOSWorker("http://www.fftoolbox.com/football/2013/points-allowed.cfm?pos=WR", sos, "WR");
		parseSOSWorker("http://www.fftoolbox.com/football/2013/points-allowed.cfm?pos=TE", sos, "TE");
		parseSOSWorker("http://www.fftoolbox.com/football/2013/points-allowed.cfm?pos=K", sos, "K");
		holder.sos = sos;
	}
	
	/**
	 * Does the per page parsing of the positional SOS data
	 */
	public static void parseSOSWorker(String url, HashMap<String, Integer> sos, String pos) throws IOException
	{
		String tr = HandleBasicQueries.handleLists(url, "tr");
		String[] trSet = tr.split("\n");
		for(int i = 0; i < trSet.length; i++)
		{
			String[]trWords = trSet[i].split(" ");
			if(ManageInput.isInteger(trWords[0]))
			{
				StringBuilder team = new StringBuilder(100);
				for(int j = 1; j < trWords.length; j++)
				{
					if(!trWords[j].equals("vs"))
					{
						team.append(trWords[j] + " ");
					}
					else
					{
						break;
					}
				}
				String teamStr = team.toString();
				teamStr = ParseRankings.fixTeams(teamStr.substring(0, teamStr.length() - 1));
				sos.put(teamStr + "," + pos, Integer.valueOf(trWords[0]));
			}
		}
	}
}
