package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseBrokenTackles;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraft;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseInjuries;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePermanentData;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseStats;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.content.Context;
import android.os.StrictMode;
import android.provider.ContactsContract.Data;

/**
 * Handles operations done on all of the players
 * These are to be done all at once, once all of the rankings are fetched
 * @author Jeff
 *
 */

public class HighLevel 
{
	
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
		holder.sos = sos;
	}
	
	/**
	 * A high level function that sets the permanent data in the
	 * player objects
	 * @param holder
	 * @param cont
	 * @throws IOException 
	 */
	public static void setPermanentData(Storage holder, Context cont) throws IOException
	{
		Map<String, String> menInBox = ParsePermanentData.parseMenInBox(holder, cont);
		Map<String, String> oLineRanks = ParsePermanentData.parseOLineRanksWrapper();
		Set<String> names = menInBox.keySet();
		holder.oLineRanks = oLineRanks;
		for(PlayerObject elem : holder.players)
		{
			//elem.info.oLineStatus = oLineRanks.get(elem.info.team);
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
		Set<String>teams = drafts.keySet();
		for(String team : teams)
		{
			holder.draftClasses.put(team, gpas.get(team) + drafts.get(team));
		}
		//Parse free agency data
		holder.fa = ParseFreeAgents.parseFA();
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
		holder.bye = byes;
		for(PlayerObject player : holder.players)
		{
			if(!player.info.position.equals("K") && !player.info.position.equals("D/ST"))
			{
				if(bt.containsKey(player.info.name))
				{
					player.stats += "Broken Tackles: " + bt.get(player.info.name) + "\n";
				}
				if(injuries.containsKey(player.info.name + "/" + player.info.position))
				{
					player.injuryStatus = injuries.get(player.info.name + "/" + player.info.position);
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
				testName = testName.toLowerCase();
				if(player.info.position.equals("QB"))
				{
					if(qbs.containsKey(testName + "/" + player.info.team))
					{
						player.stats = qbs.get(testName + "/" + player.info.team);
					}
					else if(player.info.team.length() < 2)
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
					else
					{
						int found = 0;
						String statHolder = "";
						for(String key : qbKeys)
						{
							if(key.contains(testName))
							{
								found++;
								statHolder = qbs.get(key);
							}
						}
						if(found == 1)
						{
							player.stats = statHolder;
						}
					}
				}
				else if(player.info.position.equals("RB"))
				{
					if(rbs.containsKey(testName + "/" + player.info.team))
					{
						player.stats = rbs.get(testName + "/" + player.info.team);
					}
					else if(player.info.team.length() < 2)
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
					else
					{
						int found = 0;
						String statHolder = "";
						for(String key : rbKeys)
						{
							if(key.contains(testName))
							{
								found++;
								statHolder = rbs.get(key);
							}
						}
						if(found == 1)
						{
							player.stats = statHolder;
						}
					}
				}
				else if(player.info.position.equals("WR"))
				{
					if(wrs.containsKey(testName + "/" + player.info.team))
					{
						player.stats = wrs.get(testName + "/" + player.info.team);
					}
					else if(player.info.team.length() < 2)
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
					else
					{
						int found = 0;
						String statHolder = "";
						for(String key : wrKeys)
						{
							if(key.contains(testName))
							{
								found++;
								statHolder = wrs.get(key);
							}
						}
						if(found == 1)
						{
							player.stats = statHolder;
						}
					}
				}
				else if(player.info.position.equals("TE"))
				{
					if(tes.containsKey(testName + "/" + player.info.team))
					{
						player.stats = tes.get(testName + "/" + player.info.team);
					}
					else if(player.info.team.length() < 2)
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
					else
					{
						int found = 0;
						String statHolder = "";
						for(String key : teKeys)
						{
							if(key.contains(testName))
							{
								found++;
								statHolder = tes.get(key);
							}
						}
						if(found == 1)
						{
							player.stats = statHolder;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Sets the relative risk of a player
	 * @param holder
	 * @throws IOException 
	 */
	public static void setRisk(Storage holder, Context cont) throws IOException
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
		parseECRWrapper(holder, cont);
		for(PlayerObject player : holder.players)
		{
			if(player.values.count > 2.0 && player.values.ecr != -1.0)
			{
				if(player.info.position.equals("QB"))
				{
					qbRisk += player.risk;
					qbCount++;
				}
				else if(player.info.position.equals("RB"))
				{
					rbRisk += player.risk;
					rbCount++;
				}
				else if(player.info.position.equals("WR"))
				{
					wrRisk += player.risk;
					wrCount++;
				}
				else if(player.info.position.equals("TE"))
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
		}
	}
	
	/**
	 * Calls the specific parsers and sets the projections
	 */
	public static void projPointsWrapper(Storage holder, Context cont) throws IOException
	{ 
		HashMap<String, Double> points = new HashMap<String, Double>();
		Scoring scoring = ReadFromFile.readScoring(cont);
		qbProj("http://www.fantasypros.com/nfl/projections/qb.php", points, scoring, "QB");
		rbProj("http://www.fantasypros.com/nfl/projections/rb.php", points, scoring, "RB");
		wrProj("http://www.fantasypros.com/nfl/projections/wr.php", points, scoring, "WR");
		teProj("http://www.fantasypros.com/nfl/projections/te.php", points, scoring, "TE");
		for(PlayerObject player : holder.players)
		{
			if(points.containsKey(player.info.name + "/" + player.info.team + "/" + player.info.position))
			{
				player.values.points = points.get(player.info.name + "/" + player.info.team + "/" + player.info.position);
			}
		}
	}
	
	/**
	 * Gets the qb projections
	 */
	public static void qbProj(String url, HashMap<String, Double> points, Scoring scoring, String pos) throws IOException
	{
        DecimalFormat df = new DecimalFormat("#.##");
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		int min = 0;
		ParseRankings.handleHashes();

		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains("MISC"))
			{
				min=i+1;
				break;
			}
		}
		for(int i = min; i < td.length; i+=11)
		{
			double proj = 0;
			String name = ParseRankings.fixNames(td[i].split(" \\(")[0]);
			String team = "None";
			if(td[i].contains("("))
			{
				String inter = td[i].split(" \\(")[1];
				inter = inter.split(",")[0].split("\\)")[0];
				team = ParseRankings.fixTeams(inter);
			}
			double yards = Double.parseDouble(td[i+3].replace(",", ""));
			double tdRush = Double.parseDouble(td[i+4]);
			double ints = Double.parseDouble(td[i+5]);
			double rushYards = Double.parseDouble(td[i+7]);
			double rushTD = Double.parseDouble(td[i+8]);
			double fumbles = Double.parseDouble(td[i+9]);
			proj += (yards/(scoring.passYards));
			proj -= ints * scoring.interception;
			proj += tdRush * scoring.passTD;
			proj += (rushYards / (scoring.rushYards));
			proj += rushTD * scoring.rushTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}
	
	/**
	 * Gets the running back projections
	 */
	public static void rbProj(String url, HashMap<String, Double> points, Scoring scoring, String pos) throws IOException
	{
        DecimalFormat df = new DecimalFormat("#.##");
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		int min = 0;
		ParseRankings.handleHashes();
		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains("MISC"))
			{
				min=i+1;
				break;
			}
		}
		for(int i = min; i < td.length; i+=9)
		{
			double proj = 0;
			String name = ParseRankings.fixNames(td[i].split(" \\(")[0]);
			String team = "None";
			if(td[i].contains("("))
			{
				String inter = td[i].split(" \\(")[1];
				inter = inter.split(",")[0].split("\\)")[0];
				team = ParseRankings.fixTeams(inter);
			}
			double rushYards = Double.parseDouble(td[i+2].replace(",",""));
			double rushTD = Double.parseDouble(td[i+3]);
			double catches = Double.parseDouble(td[i+4]);
			double recYards = Double.parseDouble(td[i+5].replace(",",""));
			double recTD = Double.parseDouble(td[i+6]);
			double fumbles = Double.parseDouble(td[i+7]);
			proj += (rushYards/(scoring.rushYards));
			proj += rushTD *scoring.rushTD;
			proj += catches*scoring.catches;
			proj += (recYards/(scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}
	
	/**
	 * Gets the wide receiver projections
	 */
	public static void wrProj(String url, HashMap<String, Double> points, Scoring scoring, String pos) throws IOException
	{
		DecimalFormat df = new DecimalFormat("#.##");
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		int min = 0;
		ParseRankings.handleHashes();
		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains("MISC"))
			{
				min=i+1;
				break;
			}
		}
		for(int i = min; i < td.length; i+=9)
		{
			double proj = 0;
			String name = ParseRankings.fixNames(td[i].split(" \\(")[0]);
			String team = "None";
			if(td[i].contains("("))
			{
				String inter = td[i].split(" \\(")[1];
				inter = inter.split(",")[0].split("\\)")[0];
				team = ParseRankings.fixTeams(inter);
			}
			double rushYards = Double.parseDouble(td[i+2].replace(",",""));
			double rushTD = Double.parseDouble(td[i+3]);
			double catches = Double.parseDouble(td[i+4]);
			double recYards = Double.parseDouble(td[i+5].replace(",",""));
			double recTD = Double.parseDouble(td[i+6]);
			double fumbles = Double.parseDouble(td[i+7]);
			proj += (rushYards/(scoring.rushYards));
			proj += rushTD *scoring.rushTD;
			proj += catches*scoring.catches;
			proj += (recYards/(scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}
	
	/**
	 * Gets the tight end projections
	 */
	public static void teProj(String url, HashMap<String, Double> points, Scoring scoring, String pos) throws IOException
	{
		DecimalFormat df = new DecimalFormat("#.##");
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		int min = 0;
		ParseRankings.handleHashes();

		for(int i = 0; i < td.length; i++)
		{
			if(td[i].contains("MISC"))
			{
				min=i+1;
				break;
			}
		}
		for(int i = min; i < td.length; i+=6)
		{		
			String team = "None";
			double proj = 0;
			String name = ParseRankings.fixNames(td[i].split(" \\(")[0]);
			if(td[i].contains("("))
			{
				String inter = td[i].split(" \\(")[1];
				inter = inter.split(",")[0].split("\\)")[0];
				team = ParseRankings.fixTeams(inter);
			}
			double catches = Double.parseDouble(td[i+1].replace(",",""));
			double recTD = Double.parseDouble(td[i+3]);
			double recYards = Double.parseDouble(td[i+2].replace(",",""));
			double fumbles = Double.parseDouble(td[i+4]);
			proj += catches*scoring.catches;
			proj += (recYards/(scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}		
	}
	
	/**
	 * Calculates the points above average per player per position
	 * @param holder
	 * @param cont
	 */
	public static void getPAA(Storage holder, Context cont)
	{
		Roster roster = ReadFromFile.readRoster(cont);
		double qbLimit = 0.0;
		double rbLimit = 0.0;
		double wrLimit = 0.0;
		double teLimit = 0.0;
		int x = roster.teams;
		if(roster.qbs == 1)
		{
			qbLimit = (1.25 * x) + 1.33333;
		}
		else
		{
			qbLimit = (2.5 * x + 2);
		}
		if(roster.tes == 1)
		{
			teLimit = (1.75 * x) - 2;
		}
		else
		{
			teLimit = (7.5 * x) - 41.66667;
		}
		if(roster.flex == 0)
		{
			if(roster.rbs == 1)
			{
				rbLimit = (1.5 * x) - 2;
			}
			else if(roster.rbs == 2)
			{
				rbLimit = (3.25 * x) - 5.33333;
			}
			else
			{
				rbLimit = (6 * x) - 16.33333;
			}
			if(roster.wrs == 1)
			{
				wrLimit = (1.25 * x) + 0.33333;
			}
			else if(roster.wrs == 2)
			{
				wrLimit = (2.75 * x);
			}
			else
			{
				wrLimit = (4.5 * x) - 4;
			}
		}
		else
		{
			if(roster.rbs == 2 && roster.wrs == 2)
			{
				rbLimit = (3.25*x) - 2.33333;
				wrLimit = (4.25*x) - 6;
			}
			else if(roster.rbs == 1 && roster.wrs == 3)
			{
				rbLimit = 2.75 * x;
				wrLimit = (5*x) - 9.666667;
			}
			else if(roster.rbs == 2 && roster.wrs == 3)
			{
				rbLimit = (4.75*x) - 10.88888889;
				wrLimit = (5.5*x) - 9.66666667;
			}
			else if(roster.rbs == 3 && roster.wrs == 3)
			{
				rbLimit = (5.9*x) - 11;
				wrLimit = (5.7*x) - 9.333333;
			}
			else if(roster.rbs == 1 && roster.wrs == 1)
			{
				rbLimit = (2.9 * x) - 2;
				wrLimit = (2 * x) + 1.33333;
			}
			else if(roster.rbs == 3 && roster.wrs == 2)
			{
				rbLimit = (6.25 * x) - 10.33333;
				wrLimit = (4.25 * x) - 4.333333;
			}
			else if(roster.rbs == 3 && roster.wrs == 1)
			{
				wrLimit = (2.25 * x) + 2.33333;
				rbLimit = (6 * x) - 10.33333;
			}
			else if(roster.rbs == 1 && roster.wrs == 2)
			{
				rbLimit = (3 * x) - 2.333333;
				wrLimit = (3 * x) - 1.5;
			}
			else if(roster.rbs == 2 && roster.wrs == 1)
			{
				wrLimit = (2 * x) + 4.33333;
				rbLimit = (4.2 * x) - 4.33333;
			}
		}
		double qbCounter = 0.0;
		double rbCounter = 0.0;
		double wrCounter = 0.0;
		double teCounter = 0.0;
		double qbTotal = 0.0;
		double rbTotal = 0.0;
		double wrTotal = 0.0;
		double teTotal = 0.0;
		PriorityQueue<PlayerObject>qb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.worth > b.values.worth)
			    {
			        return -1;
			    }
			    if (a.values.worth < b.values.worth)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		PriorityQueue<PlayerObject>rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB"))
			{
				qb.add(player);
			}
			else if(player.info.position.equals("RB"))
			{
				rb.add(player);
			}
			else if(player.info.position.equals("WR"))
			{
				wr.add(player);
			}
			else if(player.info.position.equals("TE"))
			{
				te.add(player);
			}
		}
		for(qbCounter = 0; qbCounter < qbLimit; qbCounter++)
		{
			qbTotal += qb.poll().values.points;
		}
		for(rbCounter = 0; rbCounter < rbLimit; rbCounter++)
		{
			rbTotal += rb.poll().values.points;
		}
		for(wrCounter = 0; wrCounter < wrLimit; wrCounter++)
		{
			wrTotal += wr.poll().values.points;
		}
		for(teCounter = 0; teCounter < teLimit; teCounter++)
		{
			teTotal += te.poll().values.points;
		}
		qbTotal /= qbCounter;
		rbTotal /= rbCounter;
		wrTotal /= wrCounter;
		teTotal /= teCounter;
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB") || player.info.position.equals("RB") || 
					player.info.position.equals("WR") || player.info.position.equals("TE") && 
					player.values.points != 0.0)
			{
				if(player.info.position.equals("QB"))
				{
					player.values.paa = player.values.points - qbTotal;
					player.values.paapd = player.values.paa / player.values.worth;
				}
				else if(player.info.position.equals("RB"))
				{
					player.values.paa = player.values.points - rbTotal;
					player.values.paapd = player.values.paa / player.values.worth;
				}
				else if(player.info.position.equals("WR"))
				{
					player.values.paa = player.values.points - wrTotal;
					player.values.paapd = player.values.paa / player.values.worth;
				}
				else if(player.info.position.equals("TE"))
				{
					player.values.paa = player.values.points - teTotal;
					player.values.paapd = player.values.paa / player.values.worth;
				}
			}
		}
	}
	
	/**
	 * Gets the adjusted touchdown numbers
	 */
	public static void parseRedZoneStats(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleListsNoUA("http://www.profootballfocus.com/blog/2013/06/28/introduction-to-otd/", "table#wp-table-reloaded-id-11-no-1 td");
		String[] td = html.split("\n");
		HashMap<String, List<Double>> redZoneStats = new HashMap<String, List<Double>>();
		for(int i = 0; i < td.length; i+=6)
		{
			List<Double> data = new ArrayList<Double>(3);
			String name = ParseRankings.fixNames(td[i]);
			double tADEZ = Double.parseDouble(td[i+2]);
			double oTD = Double.parseDouble(td[i+4]);
			double diff = Double.parseDouble(td[i+5]);
			data.add(tADEZ);
			data.add(oTD);
			data.add(diff);
			redZoneStats.put(name, data);
		}
		for(PlayerObject player : holder.players)
		{
			if(redZoneStats.containsKey(player.info.name))
			{
				List<Double> data = redZoneStats.get(player.info.name);
				player.values.tdDiff = data.get(2);
				player.values.oTD = data.get(1);
				player.values.tADEZ = data.get(0);
			}
		}
		html = HandleBasicQueries.handleListsNoUA("https://www.profootballfocus.com/blog/2013/07/01/adios-redzone-carries-hello-running-back-otd/", "table#wp-table-reloaded-id-14-no-1 td");
		td = html.split("\n");
		redZoneStats = new HashMap<String, List<Double>>();
		redZoneStats.clear();
		for(int i = 0; i < td.length; i+=6)
		{
			List<Double> data = new ArrayList<Double>(3);
			String name = ParseRankings.fixNames(td[i]);
			double rADEZ = Double.parseDouble(td[i+2]);
			double roTD = Double.parseDouble(td[i+4]);
			double rtdDiff = Double.parseDouble(td[i+5]);
			data.add(rADEZ); 
			data.add(roTD);
			data.add(rtdDiff);
			redZoneStats.put(name, data);
		}
		for(PlayerObject player : holder.players)
		{ 
			if(redZoneStats.containsKey(player.info.name))
			{
				List<Double> data = redZoneStats.get(player.info.name);
				player.values.rtdDiff = data.get(2);
				player.values.roTD = data.get(1);
				player.values.rADEZ = data.get(0);
			}
		}
		html = HandleBasicQueries.handleListsNoUA("https://www.profootballfocus.com/blog/2013/07/16/determining-playmaking-otd/", "table#wp-table-reloaded-id-337-no-1 td");
		td = html.split("\n");
		redZoneStats = new HashMap<String, List<Double>>();
		redZoneStats.clear();
		for(int i = 0; i < td.length; i+=6)
		{
			List<Double> data = new ArrayList<Double>(3);
			String name = ParseRankings.fixNames(td[i]);
			double cADEZ = Double.parseDouble(td[i+2]);
			double coTD = Double.parseDouble(td[i+4]);
			double ctdDiff = Double.parseDouble(td[i+5]);
			data.add(cADEZ); 
			data.add(coTD);
			data.add(ctdDiff);
			redZoneStats.put(name, data);
		}
		for(PlayerObject player : holder.players)
		{
			if(redZoneStats.containsKey(player.info.name))
			{
				List<Double> data = redZoneStats.get(player.info.name);
				player.values.ctdDiff = data.get(2);
				player.values.coTD = data.get(1);
				player.values.cADEZ = data.get(0);
			}
		}
	}
	
	/**
	 * Calls the parser and gets the functionsn
	 * @param cont 
	 */
	public static void parseECRWrapper(Storage holder, Context cont) throws IOException
	{
		HashMap<String, Double> ecr = new HashMap<String, Double>();
		HashMap<String, Double> risk = new HashMap<String, Double>();
		HashMap<String, Double> adp = new HashMap<String, Double>();
		String url = "http://www.fantasypros.com/nfl/rankings/consensus-cheatsheets.php";
		if(ReadFromFile.readScoring(cont).catches == 1)
		{
			url = "http://www.fantasypros.com/nfl/rankings/ppr-cheatsheets.php";
		}
		parseECRWorker(url, holder, ecr, risk, adp);
		for(PlayerObject player : holder.players)
		{
			if(ecr.containsKey(player.info.name) && !(player.info.name.equals("Alex Smith") && player.info.team.equals("Cincinnati Bengals")))
			{
				player.values.ecr = ecr.get(player.info.name);
				player.risk = risk.get(player.info.name);
				if(adp.containsKey(player.info.name))
				{
					player.info.adp = String.valueOf(adp.get(player.info.name));
				}
			}
		} 
	}
	 
	/**
	 * Gets the ECR Data for players
	 */
	public static void parseECRWorker(String url, Storage holder, HashMap<String, Double> ecr, 
			HashMap<String, Double> risk, HashMap<String, Double> adp) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{ 
			if(td[i+1].contains("QB") || td[i+1].contains("RB") || td[i+1].contains("WR") || td[i+1].contains("TE"))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=9)
		{
			String name = ParseRankings.fixNames(ParseRankings.fixDefenses(td[i].split(" \\(")[0].split(", ")[0]));
			double ecrVal = Double.parseDouble(td[i+4]);
			double riskVal = Double.parseDouble(td[i+5]);
			try{
				double adpVal = Double.parseDouble(td[i+6]);
				adp.put(name, adpVal);
			} catch(NumberFormatException e)
			{
				
			}
			ecr.put(name, ecrVal);
			risk.put(name, riskVal);
		}
	}
	
	/**
	 * Sets the leverage of players
	 * @param holder
	 * @param cont
	 */
	public static void setLeverage(Storage holder, Context cont)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		double qbMaxWorth = 0.0;
		double rbMaxWorth = 0.0;
		double wrMaxWorth = 0.0;
		double teMaxWorth = 0.0;
		double qbMaxProj = 0.0;
		double rbMaxProj = 0.0;
		double wrMaxProj = 0.0;
		double teMaxProj = 0.0;
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB") && player.values.worth > qbMaxWorth)
			{
				qbMaxWorth = player.values.worth;
				qbMaxProj = player.values.points;
			}
			if(player.info.position.equals("RB") && player.values.worth > rbMaxWorth)
			{
				rbMaxWorth = player.values.worth;
				rbMaxProj = player.values.points;
			}
			if(player.info.position.equals("WR") && player.values.worth > wrMaxWorth)
			{
				wrMaxWorth = player.values.worth;
				wrMaxProj = player.values.points;
			}
			if(player.info.position.equals("TE") && player.values.worth > teMaxWorth)
			{
				teMaxWorth = player.values.worth;
				teMaxProj = player.values.points;
			}
		}
		//Now that the max worth/projections are set, time to get the relative data and leverage
		for(PlayerObject player : holder.players)
		{
			if((player.info.position.equals("QB") || player.info.position.equals("RB") || player.info.position.equals("WR") || 
					player.info.position.equals("TE")) && player.values.worth > 0.0 && player.values.points > 0.0)
			{
				if(player.info.position.equals("QB"))
				{
					double relWorth = player.values.worth / qbMaxWorth;
					double relPoints = player.values.points / qbMaxProj;
					double leverage = relPoints / relWorth;
					player.values.relPoints = Double.valueOf(df.format(relPoints));
					player.values.relPrice = Double.valueOf(df.format(relWorth));
					player.values.leverage = Double.valueOf(df.format(leverage));
				}
				if(player.info.position.equals("RB"))
				{ 
					double relWorth = player.values.worth / rbMaxWorth;
					double relPoints = player.values.points / rbMaxProj;
					double leverage = relPoints / relWorth;
					player.values.relPoints = Double.valueOf(df.format(relPoints));
					player.values.relPrice = Double.valueOf(df.format(relWorth));
					player.values.leverage = Double.valueOf(df.format(leverage));
				} 
				if(player.info.position.equals("WR"))
				{
					double relWorth = player.values.worth / wrMaxWorth;
					double relPoints = player.values.points / wrMaxProj;
					double leverage = relPoints / relWorth;
					player.values.relPoints = Double.valueOf(df.format(relPoints));
					player.values.relPrice = Double.valueOf(df.format(relWorth));
					player.values.leverage = Double.valueOf(df.format(leverage));
				}
				if(player.info.position.equals("TE"))
				{
					double relWorth = player.values.worth / teMaxWorth;
					double relPoints = player.values.points / teMaxProj;
					double leverage = relPoints / relWorth;
					player.values.relPoints = Double.valueOf(df.format(relPoints));
					player.values.relPrice = Double.valueOf(df.format(relWorth));
					player.values.leverage = Double.valueOf(df.format(leverage));
				}
			}
		}
		WriteToFile.writeLeverage(cont, holder);
	}
}
