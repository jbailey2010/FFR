package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * A library to handle the parsing of stats to the players (by position)
 * 
 * @author Jeff
 * 
 */
public class ParseStats {
	/**
	 * Parses the qb passing and rushing stats from football outsiders
	 */
	public static Map<String, String> parseQBStats() throws IOException {
		List<String> rows = HandleBasicQueries.handleLists(
				"http://www.footballoutsiders.com/stats/qb", "tr");
		Map<String, String> qbPlayers = new HashMap<String, String>();
		for (int i = 0; i < rows.size(); i++) {
			String[] player = rows.get(i).split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			// Name
			name = ParseRankings.fixNames(player[0]).replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if (name.split(" ").length == 3) {
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if (player[0].equals("Player")
					|| (!qbPlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team) && player.length < 17)) {
				continue;
			}
			if (qbPlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team)) {
				String yards = player[player.length - 4];
				String effectiveYards = player[player.length - 3];
				String tds = player[player.length - 2];
				String normal = qbPlayers.get(name.toLowerCase() + Constants.HASH_DELIMITER + team);
				normal += "\nRushing Yards: " + yards + Constants.LINE_BREAK;
				normal += "Adjusted Rushing Yards: " + effectiveYards + Constants.LINE_BREAK;
				normal += "Rushing Touchdowns: " + tds;
				qbPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, normal);
				continue;
			} else {
				data.append("Pass Attempts: " + player[player.length - 9]
						+ Constants.LINE_BREAK);
				data.append("Yards: "
						+ player[player.length - 8].replace(",", "")
						+ Constants.LINE_BREAK);
				data.append("Adjusted Yards: "
						+ player[player.length - 7].replace(",", "")
						+ Constants.LINE_BREAK);
				data.append("Touchdowns: " + player[player.length - 6] + Constants.LINE_BREAK);
				data.append("Completion Percentage: "
						+ player[player.length - 2] + Constants.LINE_BREAK);
				data.append("Interceptions: " + player[player.length - 3]
						+ Constants.LINE_BREAK);
				data.append("Fumbles Lost: " + player[player.length - 4]);
				qbPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, data.toString());
			}
		}
		return qbPlayers;
	}

	/**
	 * Parses the rushing and receiving data for running backs
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseRBStats() throws IOException {
		List<String> rows = HandleBasicQueries.handleLists(
				"http://www.footballoutsiders.com/stats/rb", "tr");
		Map<String, String> rbPlayers = new HashMap<String, String>();
		for (int i = 0; i < rows.size(); i++) {
			String[] player = rows.get(i).split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if (player[0].equals("Player")) {
				continue;
			}
			name = ParseRankings.fixNames(player[0]).replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if (name.split(" ").length == 3) {
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if (rbPlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team)) {
				String catches = player[player.length - 6];
				String yards = player[player.length - 5];
				String effectiveYards = player[player.length - 4];
				String tds = player[player.length - 3];
				String catchRate = player[player.length - 2];
				StringBuilder normal = new StringBuilder(rbPlayers.get(name
						.toLowerCase() + Constants.HASH_DELIMITER + team));
				normal.append("\nTargets: " + catches + Constants.LINE_BREAK);
				normal.append("Catch Rate: " + catchRate + Constants.LINE_BREAK);
				normal.append("Receiving Yards: " + yards + Constants.LINE_BREAK);
				normal.append("Adjusted Receiving Yards: " + effectiveYards
						+ Constants.LINE_BREAK);
				normal.append("Receiving Touchdowns: " + tds);
				rbPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team,
						normal.toString());
				continue;
			} else {
				int incr = 1;
				if (player[player.length - 2].contains("%")) {
					incr = -1;
				}
				data.append("Carries: " + player[player.length - 6 + incr]
						+ Constants.LINE_BREAK);
				data.append("Yards: "
						+ player[player.length - 5 + incr].replace(",", "")
						+ Constants.LINE_BREAK);
				data.append("Adjusted Yards: "
						+ player[player.length - 4 + incr].replace(",", "")
						+ Constants.LINE_BREAK);
				data.append("Touchdowns: " + player[player.length - 3 + incr]
						+ Constants.LINE_BREAK);
				data.append("Fumbles: " + player[player.length - 2 + incr]);
				rbPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, data.toString());
			}
		}
		return rbPlayers;
	}

	/**
	 * Handles parsing of the wr stats
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseWRStats() throws IOException {
		List<String> rows = HandleBasicQueries.handleLists(
				"http://www.footballoutsiders.com/stats/wr", "tr");
		Map<String, String> wrPlayers = new HashMap<String, String>();
		for (int i = 0; i < rows.size(); i++) {
			String[] player = rows.get(i).split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if (player[0].equals("Player")) {
				continue;
			}
			name = ParseRankings.fixNames(player[0]).replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if (name.split(" ").length == 3) {
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if (!wrPlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team)
					&& player[6].contains("%") && player[8].contains("%")
					&& player.length < 15) {
				continue;
			}
			if (wrPlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team)) {
				String rushes = player[player.length - 4];
				String yards = player[player.length - 3];
				String tds = player[player.length - 2];
				String normal = wrPlayers.get(name.toLowerCase() + Constants.HASH_DELIMITER + team);
				normal += "\nRushes: " + rushes + Constants.LINE_BREAK;
				normal += "Rushing Yards: " + yards + Constants.LINE_BREAK;
				normal += "Rushing Touchdowns: " + tds;
				wrPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, normal);
				continue;
			} else {
				data.append("Targets: " + player[player.length - 7] + Constants.LINE_BREAK);
				data.append("Yards: " + player[player.length - 6] + Constants.LINE_BREAK);
				data.append("Adjusted Yards: "
						+ player[player.length - 5].replace(",", "")
						+ Constants.LINE_BREAK);
				data.append("Touchdowns: " + player[player.length - 4] + Constants.LINE_BREAK);
				data.append("Catch Rate: " + player[player.length - 3] + Constants.LINE_BREAK);
				data.append("Fumbles: " + player[player.length - 2]);
				wrPlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, data.toString());
			}
		}
		return wrPlayers;
	}

	/**
	 * Handles parsing of te stats
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseTEStats() throws IOException {
		List<String> rows = HandleBasicQueries.handleLists(
				"http://www.footballoutsiders.com/stats/te", "tr");
		Map<String, String> tePlayers = new HashMap<String, String>();
		for (int i = 0; i < rows.size(); i++) {
			String[] player = rows.get(i).split(" ");
			String name = "";
			String team = "";
			StringBuilder data = new StringBuilder(500);
			if (player[0].equals("Player")) {
				continue;
			}
			name = ParseRankings.fixNames(player[0]).replace(".", " ");
			team = ParseRankings.fixTeams(player[1]);
			if (name.split(" ").length == 3) {
				name = name.split(" ")[0] + " " + name.split(" ")[2];
			}
			if (!tePlayers.containsKey(name.toLowerCase() + Constants.HASH_DELIMITER + team)
					&& player[6].contains("%") && player[8].contains("%")
					&& player.length < 15) {
				continue;
			}
			data.append("Targets: " + player[player.length - 7] + Constants.LINE_BREAK);
			data.append("Yards: " + player[player.length - 6] + Constants.LINE_BREAK);
			data.append("Adjusted Yards: "
					+ player[player.length - 5].replace(",", "")
					+ Constants.LINE_BREAK);
			data.append("Touchdowns: " + player[player.length - 4] + Constants.LINE_BREAK);
			data.append("Catch Rate: " + player[player.length - 3] + Constants.LINE_BREAK);
			data.append("Fumbles: " + player[player.length - 2]);
			tePlayers.put(name.toLowerCase() + Constants.HASH_DELIMITER + team, data.toString());
		}
		return tePlayers;
	}
}
