package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

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
		List<String> rows = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/qb", "tr");
		Map<String, String>qbPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.size(); i++)
		{
			String[] player = rows.get(i).split(" ");
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
			if(player[0].equals("Player") || (!qbPlayers.containsKey(name.toLowerCase() + "/" + team)
					&& player.length < 17))
			{
				continue;
			}
			if(qbPlayers.containsKey(name.toLowerCase() + "/" + team))
			{
				String yards = player[player.length - 4];
				String effectiveYards = player[player.length - 3];
				String tds = player[player.length - 2];
				String normal = qbPlayers.get(name.toLowerCase() + "/" + team);
				normal += "\nRushing Yards: " + yards + "\n";
				normal += "Adjusted Rushing Yards: " + effectiveYards + "\n";
				normal += "Rushing Touchdowns: " + tds;
				qbPlayers.put(name.toLowerCase() + "/" + team, normal);
				continue;
			}
			else
			{
				data.append("Pass Attempts: " + player[player.length - 9] + "\n");
				data.append("Yards: " + player[player.length - 8].replace(",", "") + "\n");
				data.append("Adjusted Yards: " + player[player.length - 7].replace(",", "") + "\n");
				data.append("Touchdowns: " + player[player.length - 6] + "\n");
				data.append("Completion Percentage: " + player[player.length - 2] + "\n");
				data.append("Interceptions: " + player[player.length - 3] + "\n");
				data.append("Fumbles Lost: " + player[player.length - 4]);
				qbPlayers.put(name.toLowerCase() + "/" + team, data.toString());
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
		List<String>rows = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/rb", "tr");
		Map<String, String>rbPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.size(); i++)
		{
			String[] player = rows.get(i).split(" ");
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
			if(rbPlayers.containsKey(name.toLowerCase() + "/" + team))
			{
				String catches = player[player.length - 6];
				String yards = player[player.length - 5];
				String effectiveYards = player[player.length - 4];
				String tds = player[player.length - 3];
				String catchRate = player[player.length - 2];
				StringBuilder normal = new StringBuilder(rbPlayers.get(name.toLowerCase()+ "/" + team));
				normal.append("\nTargets: " + catches + "\n");
				normal.append("Catch Rate: " + catchRate + "\n");
				normal.append("Receiving Yards: " + yards + "\n");
				normal.append("Adjusted Receiving Yards: " + effectiveYards + "\n");
				normal.append("Receiving Touchdowns: " + tds);
				rbPlayers.put(name.toLowerCase() + "/" + team, normal.toString());
				continue;
			}
			else
			{
				int incr = 1;
				if(player[player.length - 2].contains("%"))
				{
					incr = -1;
				}
				data.append("Carries: " + player[player.length - 6 + incr] + "\n");
				data.append("Yards: " + player[player.length - 5 + incr].replace(",", "") + "\n");
				data.append("Adjusted Yards: " + player[player.length - 4 + incr].replace(",", "") + "\n");
				data.append("Touchdowns: " + player[player.length - 3 + incr] + "\n");
				data.append("Fumbles: " + player[player.length - 2 + incr]);
				rbPlayers.put(name.toLowerCase() + "/" + team, data.toString());
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
		List<String> rows = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/wr", "tr");
		Map<String, String>wrPlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.size(); i++)
		{
			String[] player = rows.get(i).split(" ");
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
			if(!wrPlayers.containsKey(name.toLowerCase() + "/" + team) && player[6].contains("%") &&
					player[8].contains("%") && player.length < 15)
			{
				continue;
			}
			if(wrPlayers.containsKey(name.toLowerCase() + "/" + team))
			{
				String rushes = player[player.length - 4];
				String yards = player[player.length - 3];
				String tds = player[player.length - 2]; 
				String normal = wrPlayers.get(name.toLowerCase() + "/" + team);
				normal += "\nRushes: " + rushes + "\n";
				normal += "Rushing Yards: " + yards + "\n";
				normal += "Rushing Touchdowns: " + tds;
				wrPlayers.put(name.toLowerCase() + "/" + team, normal);
				continue;
			}
			else
			{
				data.append("Targets: " + player[player.length - 7] + "\n");
				data.append("Yards: " + player[player.length - 6] + "\n");
				data.append("Adjusted Yards: " + player[player.length - 5].replace(",", "") + "\n");
				data.append("Touchdowns: " + player[player.length - 4] + "\n");
				data.append("Catch Rate: " + player[player.length - 3] + "\n");
				data.append("Fumbles: " + player[player.length - 2]);
				wrPlayers.put(name.toLowerCase() + "/" + team, data.toString());
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
		List<String> rows = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/te", "tr");
		Map<String, String>tePlayers = new HashMap<String, String>();
		for(int i = 0; i < rows.size(); i++)
		{
			String[] player = rows.get(i).split(" ");
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
			if(!tePlayers.containsKey(name.toLowerCase() + "/" + team) && player[6].contains("%") &&
					player[8].contains("%") && player.length < 15)
			{
				continue;
			}
			data.append("Targets: " + player[player.length - 7] + "\n");
			data.append("Yards: " + player[player.length - 6] + "\n");
			data.append("Adjusted Yards: " + player[player.length - 5].replace(",", "") + "\n");
			data.append("Touchdowns: " + player[player.length - 4] + "\n");
			data.append("Catch Rate: " + player[player.length - 3] + "\n");
			data.append("Fumbles: " + player[player.length - 2]);
			tePlayers.put(name.toLowerCase() + "/" + team, data.toString());
		}
		return tePlayers;
	}
}
