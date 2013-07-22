package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import FileIO.ReadFromFile;
import android.content.Context;
import android.os.StrictMode;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Handle the parsing of the permanent data
 * (pff data from last year)
 * @author Jeff
 *
 */
public class ParsePermanentData 
{
	/**
	 * Parses the percentage of time there were 8+ men
	 * in the box for a running back
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static Map<String, String> parseMenInBox(Storage holder, Context cont) throws IOException
	{
		String url = "https://www.profootballfocus.com/blog/2013/05/08/facing-eight-in-the-box/";
		String text = HandleBasicQueries.handleListsNoUA(url, "td");
		String[] brokenData = text.split("\n");
		Map<String, String> players = new HashMap<String, String>();
		String name = "";
		String value = "";
		for(int i = 0; i < brokenData.length; i++)
		{
			if((i-1)%6 == 0)
			{
				name = brokenData[i];
				ReadFromFile.fetchNames(holder, cont);
				String validated = ParseRankings.fixNames(name);
				name = Storage.nameExists(holder, validated);
			}
			else if((i-5)%6 == 0)
			{
				value = "Amount of time 8+ men were in the box: " + brokenData[i];
				players.put(name, value);
				name = "";
				value = "";
			}
		}
		Set<String> keys = players.keySet();
		String[] keyArr = keys.toArray(new String[32]);
		for(int i = 0; i < keyArr.length; i++)
		{
			String val = players.get(keyArr[i]);
			String percentage = val.split(": ")[1].replace("%", "");
	    	DecimalFormat df = new DecimalFormat("#.##");
			double difference = (Double.parseDouble(percentage) - Double.parseDouble("23.25"));
			String diff = "";
			if(difference < 0)
			{
				diff = df.format(difference);
			}
			else
			{
				diff = "+" + df.format(difference);
			}
			String finalOutput = val + ", " + diff + "% relative to the league average.";
			players.put(keyArr[i], finalOutput);
		}
		return players;
	}
	
	/**
	 * A wrapper for the four pages of offensive line data
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseOLineRanksWrapper() throws IOException
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		String url1 = "https://www.profootballfocus.com/blog/2013/01/28/ranking-the-2012-offensive-lines/1/";
		String url2 = "https://www.profootballfocus.com/blog/2013/01/28/ranking-the-2012-offensive-lines/2/";
		String url3 = "https://www.profootballfocus.com/blog/2013/01/28/ranking-the-2012-offensive-lines/3/";
		String url4 = "https://www.profootballfocus.com/blog/2013/01/28/ranking-the-2012-offensive-lines/4/";
		Map<String, String> teams = new HashMap<String, String>();
		parseOLineRanks(url1, teams);
		parseOLineRanks(url2, teams);
		parseOLineRanks(url3, teams);
		parseOLineRanks(url4, teams);
		return teams;
	}
	
	/**
	 * Handles the per-page parsing of the offensive line data
	 * @param url
	 * @param teams
	 * @throws IOException
	 */
	public static void parseOLineRanks(String url, Map<String, String> teams) throws IOException
	{ 
		Document doc = Jsoup.connect(url).timeout(0).get();
		String textSub = HandleBasicQueries.handleListsMulti(doc, url, "p strong");
		String[] brokenData = textSub.split("\n");
		String name = "";
		String value = "";
		for(int i = 1; i < brokenData.length; i++)
		{
			String[] testWhole = brokenData[i].split(" ");
			String test = testWhole[0];
			if(test.contains(".") && !test.contains("+") && !test.contains("-"))
			{
				value = "Overall Line Ranking: " + test.replace(".", "") + "\n" + "\n";
				if(testWhole[3].contains("("))
				{
					name = ParseRankings.fixTeams(testWhole[1] + " " + testWhole[2]);
				}
				else
				{
					name = ParseRankings.fixTeams(testWhole[1] + " " + testWhole[2] + " " + testWhole[3]);
				}
			}
			if(brokenData[i].contains("PB"))
			{
				String[] data = brokenData[i].split(",");
				String[] pb = data[0].split(" – ");
				String[] rb = data[1].split(" – ");
				value += "Pass Blocking Ranking: " + pb[1].substring(0, pb[1].length() - 2) + "\n\n";
				value += "Run Blocking Ranking: " + rb[1].substring(0, rb[1].length() - 2) + "\n";
				teams.put(name, value);
				value = "";
				name = "";
			}
		}
	}
}
