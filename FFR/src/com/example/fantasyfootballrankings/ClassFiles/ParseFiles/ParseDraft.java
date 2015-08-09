package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * A class to handle all the parsing of draft-related data
 * 
 * @author Jeff
 * 
 */
public class ParseDraft {
	/**
	 * Parses the drafts themselves.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseTeamDraft() throws IOException {
		List<String> perPick = HandleBasicQueries
				.handleLists(
						"http://www.sbnation.com/nfl/2015/4/30/8525229/2015-nfl-draft-results-pick-by-pick",
						"td");
		HashMap<String, String> picks = new HashMap<String, String>();
		for (int i = 4; i < perPick.size(); i += 4) {
			String pickStr = perPick.get(i);
			if (pickStr.contains(". (")) {
				pickStr = pickStr.split("\\)")[0].split("\\(")[1];
			}
			if (!ManageInput.isInteger(pickStr)) {
				continue;
			}
			String team = ParseRankings.fixTeams(perPick.get(i + 1).split(
					" \\(")[0]);
			String name = ParseRankings.fixNames(perPick.get(i + 2));
			String position = perPick.get(i + 3);
			int j = Integer.parseInt(pickStr);
			String round = "";
			if (j <= 32) {
				round = "1";
			} else if (j <= 64) {
				round = "2";
			} else if (j <= 100) {
				round = "3";
			} else if (j <= 140) {
				round = "4";
			} else if (j <= 176) {
				round = "5";
			} else if (j <= 215) {
				round = "6";
			} else if (j <= 256) {
				round = "7";
			}
			String pick = round + " (" + pickStr + "): " + name + ", "
					+ position + "\n";
			if (picks.containsKey(team)) {
				picks.put(team, picks.get(team) + pick);
			} else {
				picks.put(team, pick);
			}
		}
		return picks;
	}

	/**
	 * Parses the gpa and rank in gpa to each team
	 * 
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseTeamDraftGPA()
			throws IOException {
		String url = "http://www.footballoutsiders.com/stat-analysis/2015/2015-nfl-draft-report-card-report";
		HashMap<String, String> gpa = new HashMap<String, String>();
		List<String> brokenUp = HandleBasicQueries.handleLists(url, "td");
		for (int i = 0; i < brokenUp.size(); i += 7) {
			if (!brokenUp.get(i).contains("2014")) {
				String team = ParseRankings.fixTeams(brokenUp.get(i));
				String grade = brokenUp.get(i + 3);
				String rank = brokenUp.get(i + 4);
				if (team.split(" ").length == 1) {
					break;
				}
				gpa.put(team, "Average Draft Grade: " + grade + " (" + rank
						+ ")\n");
			}
		}
		return gpa;
	}
}
