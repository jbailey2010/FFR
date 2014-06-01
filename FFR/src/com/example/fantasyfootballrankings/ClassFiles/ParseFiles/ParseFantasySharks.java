package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

public class ParseFantasySharks {
	public static void parseFantasySharksRankings(Storage holder) throws IOException{
		List<String> td = HandleBasicQueries.handleLists("http://www.fantasysharks.com/apps/Projections/PrintableCheatSheetSalCapped.php?league=1", "tr td table tr td table tr td");
		for(int i = 0; i < td.size(); i+=3){
			String elem = td.get(i);
			while(elem.equals("Quarterback") || elem.equals("Running Back") || elem.equals("Wide Receiver") || elem.equals("Tight End") 
					|| elem.equals("Place Kicker") || elem.equals("Team Defense")){
				i++;
				elem = td.get(i);
			}
			String[] nameSet = elem.trim().split(",");
			StringBuilder nameBuilder = new StringBuilder(100);
			for(int j = nameSet.length-1; j >= 0; j--){
				nameBuilder.append(nameSet[j] + " ");
			}
			String name = nameBuilder.toString();
			name = name.substring(0, name.length() - 1);
			name = ParseRankings.fixNames(ParseRankings.fixDefenses(name));
			String team = ParseRankings.fixTeams(td.get(i+1).trim());
			String aucStr = td.get(i+2).substring(td.get(i+2).indexOf("$") + 1, td.get(i+2).length()).trim();
			int aucVal = Integer.parseInt(aucStr);
			ParseRankings.finalStretch(holder, name, aucVal, team, "");
		}
	}

}
