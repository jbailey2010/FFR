package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseBrokenTackles;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraft;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseInjuries;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseStats;

import FileIO.ReadFromFile;
import android.content.Context;
import android.os.StrictMode;

/**
 * Handles operations done on all of the players
 * These are to be done all at once, once all of the rankings are fetched
 * @author Jeff
 *
 */

public class HighLevel 
{
	
	/**
	 * This parses an adp site to fetch the adp of all players given a match
	 * it also, in the interest of two birds, one stone, fetches the bye week
	 * @param holder the storage unit holding all of the players to check in
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void setADP(Storage holder) throws MalformedURLException, IOException, XPatherException
	{
		String adpURL = "http://fantasyfootballcalculator.com/adp_csv_ppr.php?teams=12";
    	String adpText = HandleBasicQueries.handleLists(adpURL, "pre");
    	String[] adpList = adpText.split("\n");
    	String[][] adpArray = new String[adpList.length][];
    	HashMap<String, String> adp = new HashMap<String, String>();
    	for(int i = 0; i < adpList.length; i++)
    	{
    		adpArray[i] = adpList[i].split(",");
    	}
    	for(int i = 0; i < adpArray.length; i++)
    	{ 
    		if(adpArray[i].length > 2)
    		{
	    		String name = adpArray[i][2];
	    		if(name.contains("Defense"))
	    		{
	    			name = ParseRankings.fixDefenses(name.split(" ")[0]);
	    		}
	    		adp.put(name, adpArray[i][1]);
    		}
    	}
    	for(PlayerObject player : holder.players)
    	{
    		if(adp.containsKey(player.info.name))
    		{
    			player.info.adp = adp.get(player.info.name);
    			double a = Double.parseDouble(adp.get(player.info.name));
    			double log = Math.log(a);
    			log = log * -12.5;
    			log = log - 0.06*a;
    			log = log + 73.0;
    			if(log < 0.0)
    			{
    				log = 0.0;
    			}
    			else if(log < 1.0)
    			{
    				log = 1.0;
    			}
    			player.vals.add(log);
    			Values.handleNewValue(player.values, log);
    		}
    	}
	}
	
	/**
	 * Couldn't be simpler. It just adds all the parsed
	 * players to a list of players to be stored.
	 * Note, this shouldn't be written to file later, as who cares?
	 * It's volatile and shouldn't be treated as if it isn't.
	 * @param holder
	 */
	public static void getParsedPlayers(Storage holder)
	{
		holder.parsedPlayers.clear();
		for(PlayerObject e: holder.players)
		{
			holder.parsedPlayers.add(e.info.name);
		}
	}
	
	/**
	 * Sets a contract status for players on the fftoolbox list
	 * @param holder
	 * @throws IOException
	 */
	public static void setContractStatus(Storage holder) throws IOException
	{
		HashMap<String, String> cs = new HashMap<String, String>();
		String html = HandleBasicQueries.handleLists("http://www.kffl.com/static/nfl/features/freeagents/fa.php?option=All&y=2014", "td");
		String[] td = html.split("\n");
		for(int i = 20; i < td.length; i+=5)
		{
			String pos = td[i];
			if(pos.equals("FB"))
			{
				pos = "RB";
			}
			else if(pos.equals("PK"))
			{
				pos = "K";
			}
			String name = td[i+1];
			if(!name.equals("Player"))
			{
				cs.put(pos + "/" + name, "In a contract year");
			}
		}
		for(PlayerObject player : holder.players)
		{
			if(cs.containsKey(player.info.position + "/" + player.info.name))
			{
				player.info.contractStatus = cs.get(player.info.position + "/" + player.info.name);
			}
		}
	}
	
	/**
	 * A function that gets the strength of schedule for each team
	 * and specific positions per.
	 * @param holder
	 * @throws IOException
	 */ 
	public static void getSOS(Storage holder) throws IOException
	{
		String data = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/strength_of_schedule.cfm", "tr.c");
		data = data.replaceAll("st", "").replaceAll("nd", "").replaceAll("rd", "").replaceAll("th", "");
		String[] allArr = data.split("\n");
		String[][] team = new String[allArr.length][];
        HashMap<String, Integer>sos = new HashMap<String, Integer>();
		for(int i = 0; i  < allArr.length; i++)
		{  
			team[i] = allArr[i].split(" ");
			String keyBase = ParseRankings.fixTeams(team[i][0]) + ",";
			sos.put(keyBase + "QB", Integer.parseInt(team[i][1]));
			sos.put(keyBase + "RB", Integer.parseInt(team[i][2]));
			sos.put(keyBase + "WR", Integer.parseInt(team[i][3]));
			sos.put(keyBase + "TE", Integer.parseInt(team[i][4]));
			sos.put(keyBase + "K", Integer.parseInt(team[i][5]));
			sos.put(keyBase + "D/ST", Integer.parseInt(team[i][6]));
		}
		for(PlayerObject player : holder.players)
		{
			if(sos.containsKey(player.info.team + "," + player.info.position))
			{
				player.info.sos = sos.get(player.info.team + "," + player.info.position);
			}
		}
	}
	
	/**
	 * A high level function that sets the permanent data in the
	 * player objects
	 * @param holder
	 * @param cont
	 */
	public static void setPermanentData(Storage holder, Context cont)
	{
		Map<String, String> ranks = ReadFromFile.readOLineRanks(cont);
		Map<String, String> ratios = ReadFromFile.readPassRun(cont);
		Map<String, String> menInBox = ReadFromFile.readMenInBox(cont);
		Set<String> names = menInBox.keySet();
		for(PlayerObject elem : holder.players)
		{
			elem.info.oLineStatus = ranks.get(elem.info.team);
			elem.info.passRunRatio = ratios.get(elem.info.team);
			if(elem.info.position.equals("RB") && names.contains(elem.info.name))
			{
				elem.info.additionalStat = menInBox.get(elem.info.name);
			}
		}
	}
	
	/**
	 * Sets the team data for players
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void setTeamInfo(Storage holder, Context cont) throws IOException
	{
		//Fetch the draft data
		HashMap<String, String> drafts = ParseDraft.parseTeamDraft();
		HashMap<String, String> gpas = ParseDraft.parseTeamDraftGPA();

		//Parse free agency data
		HashMap<String, List<String>> fa = ParseFreeAgents.parseFA();
		for(PlayerObject player : holder.players)
		{
			//Set draft data
			player.draftClass = gpas.get(player.info.team) + drafts.get(player.info.team); 
			if(fa.containsKey(player.info.team))
			{
				player.fa = fa.get(player.info.team);
			}
		}
	}
	
	/**
	 * Parse player specific data that aren't stats
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void parseSpecificData(Storage holder, Context cont) throws IOException
	{
		Map<String, String> bt = ParseBrokenTackles.parseBrokenTackles();
		HashMap<String, String> injuries = ParseInjuries.parseRotoInjuries();
		HashMap<String, String> byes = ParseFFTB.parseByeWeeks();
		for(PlayerObject player : holder.players)
		{
			player.info.bye = byes.get(player.info.team);
			if(!player.info.position.equals("K") && !player.info.position.equals("D/ST"))
			{
				if(bt.containsKey(player.info.name))
				{
					player.stats += "Broken Tackles: " + bt.get(player.info.name) + "\n";
				}
				if(injuries.containsKey(player.info.name))
				{
					player.injuryStatus = injuries.get(player.info.name);
				}
			}
		}
	}
	
	/**
	 * Sets the stats of a player
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void setStats(Storage holder, Context cont) throws IOException
	{
		//Fetch the stats
		Map<String, String> qbs = ParseStats.parseQBStats();
		Set<String> qbKeys = qbs.keySet();
		Map<String, String> rbs = ParseStats.parseRBStats();
		Set<String> rbKeys = rbs.keySet();
		Map<String, String> wrs = ParseStats.parseWRStats();
		Set<String> wrKeys = wrs.keySet();
		Map<String, String> tes = ParseStats.parseTEStats();
		Set<String> teKeys = tes.keySet();
		for(PlayerObject player : holder.players)
		{ 
			if(!player.info.position.equals("K") && !player.info.position.equals("D/ST"))
			{
				//else if testname in keyset
				String[] name = player.info.name.split(" ");
				String testName = name[0].charAt(0) + " " + name[1];
				
				if(player.info.position.equals("QB") && qbs.containsKey(testName + "/" + player.info.team))
				{
					player.stats = qbs.get(testName + "/" + player.info.team);
				}
				else if(player.info.position.equals("QB"))
				{
					for(String key : qbKeys)
					{
						if(key.contains(testName))
						{
							player.stats = qbs.get(key);
							break;
						}
					}
				}
				else if(player.info.position.equals("RB")&& rbs.containsKey(testName + "/" + player.info.team))
				{
					player.stats = rbs.get(testName + "/" + player.info.team);
				}
				else if(player.info.position.equals("RB"))
				{
					for(String key : rbKeys)
					{
						if(key.contains(testName))
						{
							player.stats = rbs.get(key);
							break;
						}
					}
				}
				else if(player.info.position.equals("WR") && wrs.containsKey(testName + "/" + player.info.team))
				{
					player.stats = wrs.get(testName + "/" + player.info.team);
				}
				else if(player.info.position.equals("WR"))
				{
					for(String key : wrKeys)
					{
						if(key.contains(testName))
						{
							player.stats = wrs.get(key);
							break;
						}
					}
				}
				else if(player.info.position.equals("TE") && tes.containsKey(testName + "/" + player.info.team))
				{
					player.stats = tes.get(testName + "/" + player.info.team);
				}
				else if(player.info.position.equals("TE"))
				{
					for(String key : teKeys)
					{
						if(key.contains(testName))
						{
							player.stats = tes.get(key);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Sets the relative risk of a player
	 * @param holder
	 */
	public static void setRisk(Storage holder)
	{
		double qbRisk = 0.0;
		int qbCount = 0;
		double rbRisk = 0.0;
		int rbCount = 0;
		double wrRisk = 0.0;
		int wrCount = 0;
		double teRisk = 0.0;
		int teCount = 0;
		double dRisk = 0.0;
		int dCount = 0;
		double kRisk = 0.0;
		int kCount = 0;
		double allRisk = 0.0;
		int allCount = 0;
		for(PlayerObject player : holder.players)
		{
			double mean = player.values.worth;
            double risk = 0;
            for(double a : player.vals)
            {
                risk += (mean-a)*(mean-a);
            }
            risk = risk / player.values.count;
            risk = Math.sqrt(risk);
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            risk = Double.valueOf(twoDForm.format(risk));
            player.risk = risk;
		}
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB") && player.values.worth >= 1.0)
			{
				qbRisk += player.risk;
				qbCount++;
			}
			else if(player.info.position.equals("RB") && player.values.worth >= 3.0)
			{
				rbRisk += player.risk;
				rbCount++;
			}
			else if(player.info.position.equals("WR") && player.values.worth >= 2.5)
			{
				wrRisk += player.risk;
				wrCount++;
			}
			else if(player.info.position.equals("TE") && player.values.worth >= 1.0)
			{
				teRisk += player.risk;
				teCount++;
			}
			else if(player.info.position.equals("D/ST"))
			{
				dRisk += player.risk;
				dCount++;
			}
			else if(player.info.position.equals("K"))
			{
				kRisk += player.risk;
				kCount++;
			}
			if((player.info.position.equals("QB") && player.values.worth >= 1.0) ||
					(player.info.position.equals("RB") && player.values.worth >= 3.0) ||
					(player.info.position.equals("WR") && player.values.worth >= 2.5) ||
					(player.info.position.equals("TE") && player.values.worth >= 1.0) ||
					(player.info.position.equals("K") || player.info.position.equals("D/ST")))
			{
				allRisk += player.risk;
				allCount++;
			}
		}
		qbRisk /= qbCount;
		DecimalFormat twoDForm = new DecimalFormat("#.##");
        qbRisk = Double.valueOf(twoDForm.format(qbRisk));
		rbRisk /= rbCount;
        rbRisk = Double.valueOf(twoDForm.format(rbRisk));
		wrRisk /= wrCount;
		wrRisk = Double.valueOf(twoDForm.format(wrRisk));
		teRisk /= teCount;
		teRisk = Double.valueOf(twoDForm.format(teRisk));
		dRisk /= dCount;
		dRisk = Double.valueOf(twoDForm.format(dRisk));
		kRisk /= kCount;
		kRisk = Double.valueOf(twoDForm.format(kRisk));
		allRisk /= allCount;
		allRisk = Double.valueOf(twoDForm.format(allRisk));
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - qbRisk));
			}
			if(player.info.position.equals("RB"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - rbRisk));
			}
			if(player.info.position.equals("WR"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - wrRisk));
			}
			if(player.info.position.equals("TE"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - teRisk));
			}
			if(player.info.position.equals("K"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - kRisk));
			}
			if(player.info.position.equals("D/ST"))
			{
				player.riskPos = Double.valueOf(twoDForm.format(player.risk - dRisk));
			}
			player.riskAll = Double.valueOf(twoDForm.format(player.risk - allRisk));
		}
	}
}
