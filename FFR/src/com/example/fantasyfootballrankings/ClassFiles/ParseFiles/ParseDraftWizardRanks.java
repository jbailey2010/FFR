package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

public class ParseDraftWizardRanks {
	public static void parseRanksWrapper(Storage holder, Scoring s, Roster r)
			throws IOException {
		String type = "STD";
		if (s.catches > 0) {
			type = "PPR";
		}
		int quantity = 4;
		String url = "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem="
				+ type + "&showAuction=Y";
		url += "&teams=" + r.teams;
		url += "&QB=" + r.qbs;
		url += "&WR=" + r.wrs;
		url += "&RB=" + r.rbs;
		url += "&TE=" + r.tes;
		url += "&DST=" + r.def;
		url += "&K=" + r.k;
		if (r.flex != null) {
			url += "&WR/RB=" + r.flex.rbwr;
			url += "&WR/RB/TE=" + r.flex.rbwrte;
			url += "&QB/WR/RB/TE=" + r.flex.op;
		}
		parseRanksWorker(holder, url, quantity);
	}

	public static void parseRanksWorker(Storage holder, String url, int quantity)
			throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url,
				"table#OverallTable td");
		int startingIndex = 0;
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i).contains(" - ") && td.get(i).split(" ").length > 3) {
				startingIndex = i;
				break;
			}
		}
		for (int i = startingIndex; i < td.size(); i += 5) {
			int aucVal = Integer.parseInt(td.get(i + 2).substring(1,
					td.get(i + 2).length()));
			String playerName = ParseRankings.fixNames(ParseRankings
					.fixDefenses(td.get(i).split(" \\(")[0]));
			String teamPos = td.get(i).split(" \\(")[1];
			String team = ParseRankings.fixTeams(teamPos.split(" - ")[0]);
			String pos = teamPos.split(" - ")[1];
			pos = pos.substring(0, pos.length() - 1);
			if (pos.equals("DST")) {
				pos = Constants.DST;
			}
			for (int j = 0; j < quantity; j++) {
				ParseRankings.finalStretch(holder, playerName, aucVal, team,
						pos);
			}
		}
	}

}
