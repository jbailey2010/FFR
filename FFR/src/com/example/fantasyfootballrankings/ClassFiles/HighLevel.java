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
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraft;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseInjuries;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseStats;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.content.Context;
import android.os.StrictMode;
import android.provider.ContactsContract.Data;
import android.widget.Toast;

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
		String html = HandleBasicQueries.handleLists("http://www.kffl.com/static/nfl/features/freeagents/fa.php?option=All", "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
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
			String status = td[i+2];
			if(!name.equals("Player") && !status.contains("Signed") && !status.contains("signed"))
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
		String[] allArr = ManageInput.tokenize(data, '\n', 1);
		String[][] team = new String[allArr.length][];
        HashMap<String, Integer>sos = new HashMap<String, Integer>();
		for(int i = 0; i  < allArr.length; i++)
		{  
			team[i] = ManageInput.tokenize(allArr[i], ' ', 1);
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
		HashMap<String, String> injuries = ParseInjuries.parseRotoInjuries();
		HashMap<String, String> byes = ParseFFTB.parseByeWeeks();
		holder.bye = byes;
		for(PlayerObject player : holder.players)
		{
			if(!player.info.position.equals("K") && !player.info.position.equals("D/ST"))
			{
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
		try{
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
		}catch(ArrayIndexOutOfBoundsException e1)
		{
			
		}catch(NumberFormatException e2)
		{
			
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
		kProj("http://www.fantasypros.com/nfl/projections/k.php", points, "K");
		defProjWeekly(points, "D/ST");
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
		
		String[] td = ManageInput.tokenize(html, '\n', 1);
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
		String[] td = ManageInput.tokenize(html, '\n', 1);
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
		String[] td = ManageInput.tokenize(html, '\n', 1);
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
		String[] td = ManageInput.tokenize(html, '\n', 1);
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
	 * Gets the kicker projections
	 */
	public static void kProj(String url, HashMap<String, Double> points, String pos) throws IOException
	{
		DecimalFormat df = new DecimalFormat("#.##");
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		ParseRankings.handleHashes();

		for(int i = 0; i < td.length; i++)
		{
			if(ManageInput.isDouble(td[i+1]))
			{
				min=i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=5)
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
			proj = Double.parseDouble(td[i+4]);
			points.put(name + "/" + team + "/" + pos, proj);
		}		
	}
	
	/**
	 * Handles the weekly defensive projections
	 * @param points
	 * @param pos
	 * @throws IOException
	 */
	public static void defProjWeekly(HashMap<String, Double> points, String pos) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/weeklycheatsheets.cfm?player_pos=DEF", "table#proj td");
		if(html.split("\n").length < 59 && !html.contains("Week") && !html.contains("will be up"))
		{
			defProjAnnual(points, pos);
		}
		else
		{
			String[] td = ManageInput.tokenize(html, '\n', 1);
			for(int i = 0; i < td.length; i+=5)
			{
				String teamName = ParseRankings.fixDefenses(td[i+1]);
				String team = ParseRankings.fixTeams(td[i+2]);
				double proj = Double.valueOf(td[i+4]);
				points.put(teamName + "/" + team + "/" + pos, proj);
			}
		}
	}
	
	/**
	 * Handles the defensive projection parsing on an annual basis
	 * @param points
	 * @param pos
	 * @throws IOException
	 */
	public static void defProjAnnual(HashMap<String, Double> points, String pos) throws IOException {
		String html = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/cheatsheets.cfm?player_pos=DEF", "table#proj td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 0; i < td.length; i+=5)
		{
			String teamName = ParseRankings.fixDefenses(td[i+1]);
			String team = ParseRankings.fixTeams(td[i+2]);
			double proj = Double.valueOf(td[i+4]);
			points.put(teamName + "/" + team + "/" + pos, proj);
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
		double dLimit = 0.0;
		double kLimit = 0.0;
		int x = roster.teams;
		kLimit = 1.25 * x;
		dLimit = 1.25 * x;
		if(roster.qbs == 1)
		{
			qbLimit = (1.25 * x) + 1.33333;
		}
		else
		{
			qbLimit = (6 * x - 30);
		}
		if(roster.tes == 1)
		{
			teLimit = (1.75 * x) - 3.3333333;
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
				wrLimit = (2.75 * x) - 1.66666667;
			}
			else
			{
				wrLimit = (4.5 * x) - 5;
			}
		}
		else
		{
			Scoring scoring = ReadFromFile.readScoring(cont);
			if(scoring.catches == 1)
			{
				//Legit
				if(roster.rbs == 2 && roster.wrs == 2)
				{
					rbLimit = 3.75 * x - 10.666667;
					wrLimit = 4.25 * x - 2.33333;
				}
				if(roster.rbs == 1 && roster.wrs == 3)
				{
					rbLimit = 3 * x - 3.3333;
					wrLimit = 4.75 * x - 6.3333;
				}
				if(roster.rbs == 2 && roster.wrs == 3)
				{
					rbLimit = 4.5 * x - 5.33333;
					wrLimit = 5.75 * x - 14;
				}
				//Guesstimated
				if(roster.rbs == 1 && roster.wrs == 1)
				{
					rbLimit = 2 * x - 3.3333;
					wrLimit = 2 * x - 1;
				}
				if(roster.rbs == 1 && roster.wrs == 2)
				{
					rbLimit = 2.5 * x;
					wrLimit = 4.25 * x - 5;
				}
				if(roster.rbs == 2 && roster.wrs == 1)
				{
					rbLimit = 3.5 * x - 10;
					wrLimit = 2.25 * x - 1;
				}
				if(roster.rbs == 3 && roster.wrs == 1)
				{
					wrLimit = 2.5 * x + 1;
					rbLimit = 4.7 * x - 5;
				}
				if(roster.rbs == 3 && roster.wrs == 2)
				{
					rbLimit = 4.75 * x - 4.33333;
					wrLimit = 4.25 * x;
				}
				if(roster.rbs == 3 && roster.wrs == 3)
				{
					rbLimit = 4.75 * x - 1;
					wrLimit = 5.75 * x - 12;
				}
			}
			else
			{
				//Legit
				if(roster.rbs == 2 && roster.wrs == 2)
				{
					rbLimit = 2.75 * x + 6;
					wrLimit = 4.25 * x - 7.3333;
				}
				if(roster.rbs == 1 && roster.wrs == 3)
				{
					rbLimit = 2.5 * x + 3.3333;
					wrLimit = 5.25 * x - 13;
				}
				if(roster.rbs == 2 && roster.wrs == 3)
				{
					rbLimit = 4.5 * x - 5.3333;
					wrLimit = 5.75 * x - 14;
				}
				//Guesstimated
				if(roster.rbs == 1 && roster.wrs == 1)
				{
					rbLimit = 2 * x - 2;
					wrLimit = 2 * x - 1.66667;
				}
				if(roster.rbs == 1 && roster.wrs == 2)
				{
					rbLimit = 2.5 * x + 1;
					wrLimit = 4.25 * x - 6;
				}
				if(roster.rbs == 2 && roster.wrs == 1)
				{
					rbLimit = 3.5 * x - 9;
					wrLimit = 2.25 * x - 1.666667;
				}
				if(roster.rbs == 3 && roster.wrs == 1)
				{
					wrLimit = 2.5 * x + 1.5;
					rbLimit = 4.7 * x - 3.6667;
				}
				if(roster.rbs == 3 && roster.wrs == 2)
				{
					rbLimit = 4.75 * x - 3.666667;
					wrLimit = 4.25 * x - 1;
				}
				if(roster.rbs == 3 && roster.wrs == 3)
				{
					rbLimit = 4.75 * x;
					wrLimit = 5.75 * x - 13;
				}
			}
		}
		double qbCounter = 0.0;
		double rbCounter = 0.0;
		double wrCounter = 0.0;
		double teCounter = 0.0;
		double dCounter = 0.0;
		double kCounter = 0.0;
		double qbTotal = 0.0;
		double rbTotal = 0.0;
		double wrTotal = 0.0;
		double teTotal = 0.0;
		double dTotal = 0.0;
		double kTotal = 0.0;
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
		PriorityQueue<PlayerObject>k = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
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
		PriorityQueue<PlayerObject>def = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
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
			else if(player.info.position.equals("D/ST"))
			{
				def.add(player);
			}
			else if(player.info.position.equals("K"))
			{
				k.add(player);
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
		for(dCounter = 0; dCounter < dLimit; dCounter++)
		{
			dTotal += def.poll().values.points;
		}
		for(kCounter = 0; kCounter < kLimit; kCounter++)
		{
			kTotal += k.poll().values.points;
		}
		qbTotal /= qbCounter;
		rbTotal /= rbCounter;
		wrTotal /= wrCounter;
		teTotal /= teCounter;
		dTotal /= dCounter;
		kTotal /= kCounter;
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB") || player.info.position.equals("RB") || 
					player.info.position.equals("WR") || player.info.position.equals("TE") || player.info.position.equals("K")
					|| player.info.position.equals("D/ST")&& 
					player.values.points != 0.0)
			{
				if(player.info.position.equals("QB"))
				{
					player.values.paa = player.values.points - qbTotal;
				}
				else if(player.info.position.equals("RB"))
				{ 
					player.values.paa = player.values.points - rbTotal;
				}
				else if(player.info.position.equals("WR"))
				{
					player.values.paa = player.values.points - wrTotal;
				}
				else if(player.info.position.equals("TE"))
				{
					player.values.paa = player.values.points - teTotal;
				}
				else if(player.info.position.equals("D/ST"))
				{
					player.values.paa = player.values.points - dTotal;
				}
				else if(player.info.position.equals("K"))
				{
					player.values.paa = player.values.points - kTotal;
				}
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
		HashMap<String, String> adp = new HashMap<String, String>();
		if(!isValidRankings("http://www.fantasypros.com/nfl/rankings/ppr-rb.php?week=1"))
		{
			String url = "http://www.fantasypros.com/nfl/rankings/consensus-cheatsheets.php";
			if(ReadFromFile.readScoring(cont).catches == 1)
			{
				url = "http://www.fantasypros.com/nfl/rankings/ppr-cheatsheets.php";
			}
			parseECRWorker(url, holder, ecr, risk, adp, 9);
		}
		else
		{
			holder.isRegularSeason = true;
			StringBuilder urlBase = new StringBuilder(100);
			urlBase.append("http://www.fantasypros.com/nfl/rankings/");
			String url = urlBase.toString();
			if(ReadFromFile.readScoring(cont).catches > 0)
			{
				urlBase.append("ppr-");
			}
			String urlRec = urlBase.toString();
			parseECRWeekly(url + "qb.php", holder, ecr, risk, adp);
			parseECRWeekly(urlRec + "rb.php", holder, ecr, risk, adp);
			parseECRWeekly(urlRec + "wr.php", holder, ecr, risk, adp);
			parseECRWeekly(urlRec + "te.php", holder, ecr, risk, adp);
			parseECRWeekly(url + "dst.php", holder, ecr, risk, adp);
			parseECRWeekly(url + "k.php", holder, ecr, risk, adp);
		}
		for(PlayerObject player : holder.players)
		{
			if(ecr.containsKey(player.info.name) && !(player.info.name.equals("Alex Smith") && player.info.team.equals("Cincinnati Bengals")))
			{
				player.values.ecr = ecr.get(player.info.name);
				player.risk = risk.get(player.info.name);
				if(holder.isRegularSeason)
				{
					if(adp.containsKey(player.info.team))
					{
						player.info.adp = adp.get(player.info.team);
					}
				}
				else
				{
					if(adp.containsKey(player.info.name))
					{
						player.info.adp = (adp.get(player.info.name));
					}
				}
			}
		} 
	}
	
	/**
	 * Does a mock request to see if the regular season rankings are up
	 * @return
	 * @throws IOException
	 */
	public static boolean isValidRankings(String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{ 
			if(td[i+1].contains("QB") || td[i+1].contains("RB") || td[i+1].contains("WR") || td[i+1].contains("TE"))
			{
				min = i;
				break;
			}
		}
		if(min != 0 && td.length - min > 15)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Similar to the ecr parser, but handles the minor differences in the weekly set
	 */
	public static void parseECRWeekly(String url, Storage holder, HashMap<String, Double> ecr, 
			HashMap<String, Double> risk, HashMap<String, String> adp) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{ 
			if((td[i+1].contains("vs") || td[i+1].contains("at")) && td[i+1].contains("(") && td[i+1].contains(")") && ManageInput.isInteger(td[i]))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=6)
		{
			String name = ParseRankings.fixNames(ParseRankings.fixDefenses(td[i+1].split(" \\(")[0].split(", ")[0]));
			double ecrVal = Double.parseDouble(td[i]);
			double riskVal = Double.parseDouble(td[i+5]);
			String team = ParseRankings.fixTeams(td[i+1].split(" \\(")[1].split("\\)")[0]);
			if(!adp.containsKey(team) && !team.contains("FA") && !team.contains(" vs. ") && !team.contains(" at ") && !ManageInput.isInteger(team))
			{
				String wholeSet = td[i+1];
				String opp = "";
				if(wholeSet.contains("vs"))
				{
					opp = ParseRankings.fixTeams(wholeSet.split("vs. ")[1]);
				}
				else
				{
					opp = ParseRankings.fixTeams(wholeSet.split("at ")[1]);
				}
				adp.put(team, opp);
			}
			ecr.put(name, ecrVal);
			risk.put(name, riskVal);
		}
	}
	
	/**
	 * Gets the ECR Data for players
	 */
	public static void parseECRWorker(String url, Storage holder, HashMap<String, Double> ecr, 
			HashMap<String, Double> risk, HashMap<String, String> adp, int loopIter) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{ 
			if(td[i+1].contains("QB") || td[i+1].contains("RB") || td[i+1].contains("WR") || td[i+1].contains("TE"))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=loopIter)
		{
			String name = ParseRankings.fixNames(ParseRankings.fixDefenses(td[i].split(" \\(")[0].split(", ")[0]));
			double ecrVal = Double.parseDouble(td[i+4]);
			double riskVal = Double.parseDouble(td[i+5]);
			try{
				double adpVal = Double.parseDouble(td[i+6]);
				adp.put(name, td[i+6]);
			} catch(NumberFormatException e)
			{
				
			} catch(ArrayIndexOutOfBoundsException e1)
			{
				
			}
			ecr.put(name, ecrVal);
			risk.put(name, riskVal);
		}
	}

	/**
	 * Calls the worker thread with the appropriate URL then sets it to the players' storage
	 */
	public static void getROSRankingsWrapper(Storage holder, Context cont) throws IOException 
	{
		HashMap<String, Integer> rankings = new HashMap<String, Integer>();
		parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-qb.php","QB", rankings);
		Scoring s = ReadFromFile.readScoring(cont);
		if(s.catches == 0)
		{
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-rb.php","RB", rankings);
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-wr.php","WR", rankings);
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-te.php","TE", rankings);
		}
		else
		{
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-ppr-rb.php","RB", rankings);
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-ppr-wr.php","WR", rankings);
			parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-ppr-te.php","TE", rankings);
		}
		parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-dst.php","D/ST", rankings);
		parseROSRankingsWorker("http://www.fantasypros.com/nfl/rankings/ros-k.php","K", rankings);
		for(PlayerObject player : holder.players)
		{
			if(rankings.containsKey(player.info.name + "," + player.info.position))
			{
				player.values.rosRank = rankings.get(player.info.name + "," + player.info.position);
			}
		}
	}
	
	/**
	 * Does the per page parsing, getting the ranking and the name and putting them in the hash
	 */
	public static void parseROSRankingsWorker(String url, String pos, HashMap<String, Integer> rankings) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{
			if(ManageInput.isInteger(td[i]))
			{
				min = i;
				break;
			}
		}
		for(int i = min; i < td.length; i+=6)
		{
			int ranking = Integer.parseInt(td[i]);
			String name = "";
			name = ParseRankings.fixNames(td[i+1].split(" \\(")[0]);
			if(pos.equals("D/ST"))
			{
				name = ParseRankings.fixDefenses(ParseRankings.fixTeams(name));
			}
			rankings.put(name+","+pos, ranking);
		}
	}
}
