package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;

/**
 * A library to handle the parsing of
 * stats to the players (by position)
 * @author Jeff
 *
 */
public class ParseStats 
{
	/**
	 * Parses the qb passing and rushing stats
	 * from football outsiders
	 */
	public static Map<String, String> parseQBStats() throws IOException
	{
		String text = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/qb2012", "tr");
		String[] rows = text.split("\n");
		Map<String, String>qbPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.length; i++)
		{
			String[] player = rows[i].split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			//Name
			name = player[0].replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if(name.split(" ").length == 3)
			{
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if(player[0].equals("Player") || (!qbPlayers.containsKey(name + "/" + team)
					&& player.length < 17))
			{
				continue;
			}
			if(qbPlayers.containsKey(name + "/" + team))
			{
				String dvoa = player[6];
				String yards = player[10];
				String effectiveYards = player[11];
				String tds = player[12];
				String normal = qbPlayers.get(name + "/" + team);
				normal += "Rushing Yards: " + yards + "\n";
				normal += "Adjusted Rushing Yards: " + effectiveYards + "\n";
				normal += "Rushing Touchdowns: " + tds + "\n";
				normal += "Rushing Defense-Adjusted  Value Over Average: " + dvoa + "\n";
				qbPlayers.put(name + "/" + team, normal);
				continue;
			}
			else
			{
				data.append("Pass Attempts: " + player[11] + "\n");
				data.append("Yards: " + player[12].replace(",", "") + "\n");
				data.append("Adjusted Yards: " + player[13].replace(",", "") + "\n");
				data.append("Touchdowns: " + player[14] + "\n");
				data.append("Completion Percentage: " + player[18] + "\n");
				data.append("Interceptions: " + player[17] + "\n");
				data.append("Defense-Adjusted Yards Over Average (rank): " + player[2].replace(",", "") + 
						" (" + player[3] + ")\n");
				data.append("Defense-Adjusted Value Over Average (rank): " + player[6] + " (" + player[7] + ")\n");
				qbPlayers.put(name + "/" + team, data.toString());
			}
		}
		return qbPlayers;
	}
	
	/**
	 * Parses the rushing and receiving data for running backs
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseRBStats() throws IOException
	{
		String text = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/rb2012", "tr");
		String[] rows = text.split("\n");
		Map<String, String>rbPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.length; i++)
		{
			String[] player = rows[i].split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if(player[0].equals("Player"))
			{
				continue;
			}
			name = player[0].replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if(name.split(" ").length == 3)
			{
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if(rbPlayers.containsKey(name + "/" + team))
			{
				if(player.length > 12)
				{
					String dvoa = player[6];
					String catches = player[9];
					String yards = player[10];
					String effectiveYards = player[11];
					String tds = player[12];
					String catchRate = player[13];
					String normal = rbPlayers.get(name + "/" + team);
					normal += "Targets: " + catches + "\n";
					normal += "Catch Rate: " + catchRate + "\n";
					normal += "Receiving Yards: " + yards + "\n";
					normal += "Adjusted Receiving Yards: " + effectiveYards + "\n";
					normal += "Receiving Touchdowns: " + tds + "\n";
					normal += "Receiving Defense-Adjusted Value Over Average: " + dvoa + "\n";
					rbPlayers.put(name + "/" + team, normal);
					continue;
				}
				else
				{
					String dvoa = player[4];
					String catches = player[6];
					String yards = player[7];
					String effectiveYards = player[8];
					String tds = player[9];
					String catchRate = player[10];
					String normal = rbPlayers.get(name + "/" + team);
					normal += "Targets: " + catches + "\n";
					normal += "Catch Rate: " + catchRate + "\n";
					normal += "Receiving Yards: " + yards + "\n";
					normal += "Adjusted Receiving Yards: " + effectiveYards + "\n";
					normal += "Receiving Touchdowns: " + tds + "\n";
					normal += "Receiving Defense-Adjusted Value Over Average: " + dvoa + "\n";
					rbPlayers.put(name + "/" + team, normal);
					continue;
				}
			}
			else
			{
				if(player.length > 15)
				{
					data.append("Carries: " + player[9] + "\n");
					data.append("Yards: " + player[10].replace(",", "") + "\n");
					data.append("Adjusted Yards: " + player[11].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[12] + "\n");
					data.append("Fumbles: " + player[13] + "\n");
					data.append("Success Rate: " + player[14] + "\n");
					data.append("Defense-Adjusted Yards Over Average (rank): " + player[2].replace(",", "") + 
							" (" + player[3] + ")\n");
					data.append("Defense-Adjusted Value Over Average (rank): " + player[6] + " (" + player[7] + ")\n");
					rbPlayers.put(name + "/" + team, data.toString());
				}
				else
				{
					data.append("Carries: " + player[6] + "\n");
					data.append("Yards: " + player[7] + "\n");
					data.append("Adjusted Yards: " + player[8].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[9] + "\n");
					data.append("Fumbles: " + player[10] + "\n");
					data.append("Defense-Adjusted Yards Over Average: " + player[2].replace(",", "") + "\n");
					data.append("Defense-Adjusted Value Over Average: " + player[4] + "\n");
					rbPlayers.put(name + "/" + team, data.toString());
				}
			}
		}
		return rbPlayers;
	}
	
	/**
	 * Handles parsing of the wr stats
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseWRStats() throws IOException
	{
		String text = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/wr2012", "tr");
		String[] rows = text.split("\n");
		Map<String, String>wrPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.length; i++)
		{
			String[] player = rows[i].split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if(player[0].equals("Player"))
			{
				continue;
			}
			name = player[0].replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if(name.split(" ").length == 3)
			{
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if(!wrPlayers.containsKey(name + "/" + team) && player[6].contains("%") &&
					player[8].contains("%") && player.length < 15)
			{
				continue;
			}
			if(wrPlayers.containsKey(name + "/" + team))
			{
				String dvoa = player[6];
				String rushes = player[10];
				String yards = player[11];
				String tds = player[12];
				String normal = wrPlayers.get(name + "/" + team);
				normal += "Rushes: " + rushes + "\n";
				normal += "Rushing Yards: " + yards + "\n";
				normal += "Rushing Touchdowns: " + tds + "\n";
				normal += "Rushing Defense-Adjusted Value Over Average: " + dvoa + "\n";
				wrPlayers.put(name + "/" + team, normal);
				continue;
			}
			else
			{
				if(player.length > 15)
				{
					data.append("Targets: " + player[9] + "\n");
					data.append("Yards: " + player[10] + "\n");
					data.append("Adjusted Yards: " + player[11].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[12] + "\n");
					data.append("Catch Rate: " + player[13] + "\n");
					data.append("Fumbles: " + player[14] + "\n");
					data.append("Defensive Pass Interference Calls/Yards: " + player[15] + "\n");
					data.append("Defense-Adjusted Yards Over Average (rank): " + player[2].replace(",", "") + 
							" (" + player[3] + ")\n");
					data.append("Defense-Adjusted Value Over Average (rank): " + player[6] + " (" + player[7] + ")\n");
					wrPlayers.put(name + "/" + team, data.toString());
				}
				else
				{
					data.append("Targets: " + player[6] + "\n");
					data.append("Yards: " + player[7] + "\n");
					data.append("Adjusted Yards: " + player[8].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[9] + "\n");
					data.append("Catch Rate: " + player[10] + "\n");
					data.append("Fumbles: " + player[11] + "\n");
					data.append("Defensive Pass Interference Calls/Yards: " + player[12] + "\n");
					data.append("Defense-Adjusted Yards Over Average: " + player[2].replace(",", "") + "\n");
					data.append("Defense-Adjusted Value Over Average: " + player[4] + "\n");
					wrPlayers.put(name + "/" + team, data.toString());
				}
			}
		}
		return wrPlayers;
	}
	
	/**
	 * Handles parsing of te stats
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseTEStats() throws IOException
	{
		String text = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/te2012", "tr");
		String[] rows = text.split("\n");
		Map<String, String>tePlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.length; i++)
		{
			String[] player = rows[i].split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if(player[0].equals("Player"))
			{
				continue;
			}
			if(!tePlayers.containsKey(player[0].replace(".", " ")) && player[6].contains("%") &&
					player[8].contains("%") && player.length < 15)
			{
				continue;
			}
			name = player[0].replace(".", " ");
			team = player[1];
			if(name.split(" ").length == 3)
			{
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if(tePlayers.containsKey(name + "/" + team))
			{
				String dvoa = player[6];
				String rushes = player[10];
				String yards = player[11];
				String tds = player[12];
				String normal = tePlayers.get(name + "/" + team);
				normal += "Rushing Defense-Adjusted Value Over Average: " + dvoa + "\n";
				normal += "Rushes: " + rushes + "\n";
				normal += "Rushing Yards: " + yards + "\n";
				normal += "Rushing Touchdowns: " + tds + "\n";
				tePlayers.put(name + "/" + team, normal);
				continue;
			}
			else
			{
				if(player.length > 15)
				{ 
					data.append("Targets: " + player[9] + "\n");
					data.append("Yards: " + player[10] + "\n");
					data.append("Adjusted Yards: " + player[11].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[12] + "\n");
					data.append("Catch Rate: " + player[13] + "\n");
					data.append("Fumbles: " + player[14] + "\n");
					data.append("Defensive Pass Interference Calls/Yards: " + player[15] + "\n");
					data.append("Defense-Adjusted Yards Over Average (rank): " + player[2].replace(",", "") + 
							" (" + player[3] + ")\n");
					data.append("Defense-Adjusted Value Over Average (rank): " + player[6] + " (" + player[7] + ")\n");
					tePlayers.put(name + "/" + team, data.toString());
				}
				else
				{
					data.append("Targets: " + player[6] + "\n");
					data.append("Yards: " + player[7] + "\n");
					data.append("Adjusted Yards: " + player[8].replace(",", "") + "\n");
					data.append("Touchdowns: " + player[9] + "\n");
					data.append("Catch Rate: " + player[10] + "\n");
					data.append("Fumbles: " + player[11] + "\n");
					data.append("Defensive Pass Interference Calls/Yards: " + player[12] + "\n");
					data.append("Defense-Adjusted Yards Over Average: " + player[2].replace(",", "") + "\n");
					data.append("Defense-Adjusted Value Over Average: " + player[4] + "\n");
					tePlayers.put(name + "/" + team, data.toString());
				}
			}
		}
		return tePlayers;
	}
}
