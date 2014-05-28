package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
/**
 * Parses the NFL stuff
 * @author Jeff
 *
 */
public class ParseNFL 
{
	
	public static void parseNFLRankingsWrapper(Storage holder, Context cont) throws IOException, ParseException{
		 URL url;
		    InputStream is = null;
		    BufferedReader br;
		    String line;
		    StringBuilder htmlBuilder = new StringBuilder(10000);
		    try {
		        url = new URL("http://www.nfl.com/fantasyfootball/rankings#tabset=pr-top");
		        is = url.openStream();  // throws an IOException
		        br = new BufferedReader(new InputStreamReader(is));

		        while ((line = br.readLine()) != null) {
		        	htmlBuilder.append(line);
		        }
		    } catch (MalformedURLException mue) {
		         mue.printStackTrace();
		    } catch (IOException ioe) {
		         ioe.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException ioe) {
		            // nothing to see here
		        }
		    }  
		String html = htmlBuilder.toString();
		String fir = html.split("var players101_200 = ")[1];
		String sec = html.split("_dataMerged\\[\\'top\\'\\] = ")[1];
		String firstSet = fir.split("\\[")[1].replaceAll("\\}\\,\\{", "} ,,, {").replaceAll("\\}\\]\\}", "} ,,, }");
		String playerSet = sec.split("\\[")[1].replaceAll("\\}\\,\\{", "} ,,, {").replaceAll("\\}\\]\\}", "} ,,, }");
		String[] firstArr = firstSet.split(" ,,, ");
		String[] playerArr = playerSet.split(" ,,, ");
		for(int i = 0; i < playerArr.length; i++){
			String player = playerArr[i];
			String secPla = firstArr[i];
			JSONObject json1 =(JSONObject)new JSONParser().parse(secPla);
			JSONObject json = (JSONObject)new JSONParser().parse(player);
			String firstName = ParseRankings.fixNames(ParseRankings.fixDefenses(json1.get("firstName") + " " + json1.get("lastName")));
			String name = ParseRankings.fixNames(ParseRankings.fixDefenses(json.get("firstName") + " " + json.get("lastName")));
			double val = 0.0;
			double val2 = 0.0;
			try{
				val = Double.parseDouble("" + json.get("auction"));
				val2 = Double.parseDouble("" + json1.get("auction"));
			}catch(Exception e){
				val = 0.0;
				val2 = 0.0;
			}
			ParseRankings.finalStretch(holder, name, (int) val, "", "");
			ParseRankings.finalStretch(holder, firstName, (int) val2, "", "");
		}
	}
	
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
		String[] td = ManageInput.tokenize(html, '\n', 1);//html.split("\n");
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
			String worth = td[i+3];
			if(i+3 == td.length - 1)
			{
				worth = worth.substring(0, worth.length() - 1);
			}
			int val = Integer.parseInt(worth);
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
