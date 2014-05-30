package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

public class ParseFantasySharks {
	public static void parseFantasySharksRankings(Storage holder) throws IOException{
		String html = HandleBasicQueries.handleLists("http://www.fantasysharks.com/apps/Projections/PrintableCheatSheetSalCapped.php?league=1", "tr td table tr td table tr td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 0; i < td.length; i+=3){
			String elem = td[i];
			while(elem.equals("Quarterback") || elem.equals("Running Back") || elem.equals("Wide Receiver") || elem.equals("Tight End") 
					|| elem.equals("Place Kicker") || elem.equals("Team Defense")){
				i++;
				elem = td[i];
			}
			String[] nameSet = elem.trim().split(",");
			StringBuilder nameBuilder = new StringBuilder(100);
			for(int j = nameSet.length-1; j >= 0; j--){
				nameBuilder.append(nameSet[j] + " ");
			}
			String name = nameBuilder.toString();
			name = name.substring(0, name.length() - 1);
			name = ParseRankings.fixNames(ParseRankings.fixDefenses(name));
			String team = ParseRankings.fixTeams(td[i+1].trim());
			String aucStr = td[i+2].substring(td[i+2].indexOf("$") + 1, td[i+2].length()).trim();
			int aucVal = Integer.parseInt(aucStr);
			ParseRankings.finalStretch(holder, name, aucVal, team, "");
		}
	}

}
