package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

public class ParseDraftWizardRanks {
	public static void parseRanksWrapper(Storage holder) throws IOException{
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=STD&showAuction=Y&teams=12&WR=2");
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=PPR&showAuction=Y&teams=12&WR=2");
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=PPR&showAuction=Y&teams=12&QB=2&WR=2");
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=PPR&showAuction=Y&teams=12&WR=2&WR/RB=1");
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=PPR&showAuction=Y&teams=12&WR=2&QB/WR/RB/TE=1");
		parseRanksWorker(holder, "http://draftwizard.fantasypros.com/editor/createFromProjections.jsp?sport=nfl&scoringSystem=PPR&showAuction=Y&teams=12&WR=2&QB/WR/RB/TE=1&WR/RB=1");
	}
	
	public static void parseRanksWorker(Storage holder, String url) throws IOException{
		List<String> td = HandleBasicQueries.handleLists(url, "table#OverallTable td");
		for(int i = 0; i < td.size(); i+=3){
			int aucVal = Integer.parseInt(td.get(i+2).substring(1, td.get(i+2).length()));
			String playerName = ParseRankings.fixNames(ParseRankings.fixDefenses(td.get(i).split(" \\(")[0]));
			String teamPos = td.get(i).split(" \\(")[1];
			String team = ParseRankings.fixTeams(teamPos.split(" - ")[0]);
			String pos = teamPos.split(" - ")[1];
			pos = pos.substring(0, pos.length() - 1);
			if(pos.equals("DST")){
				pos = "D/ST";
			}
			ParseRankings.finalStretch(holder, playerName, aucVal, team, pos);
		}
	}

}
