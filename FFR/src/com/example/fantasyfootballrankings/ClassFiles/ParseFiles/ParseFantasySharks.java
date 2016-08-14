package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.Pages.Home;

/**
 * Gets an aggregate set of auction values
 * 
 * @author Jeff
 * 
 */
public class ParseFantasySharks {

	public static void getFantasySharksAuctionValues(Storage holder) throws IOException{
        List<String> td = HandleBasicQueries.handleLists(
                "http://www.fantasysharks.com/apps/Projections/PrintableCheatSheetSalCapped.php",
                "table tbody tr td");

        int min = 0;
        for (int i = 0; i < td.size(); i++) {
            if (td.get(i).split(",").length == 2) {
                min = i;
                break;
            }
        }
        for (int i = min; i < td.size(); i+=3) {
            int sample = td.get(i).split(",").length;
            int defenseCheck = td.get(i).split(" ").length;
            while ((sample != 2 && defenseCheck != 1) && i < td.size() - 3) {
                sample = td.get(++i).split(",").length;
                defenseCheck = td.get(i).split(" ").length;
            }
            if (i >= td.size() - 3) {
                break;
            }

            String name = "";
            String team = "";
            if (sample != 2 && defenseCheck == 1) {
                team = ParseRankings.fixTeams(td.get(i).replaceAll("\\s",""));
                name = ParseRankings.fixDefenses(team);
            } else {
                String[] nameSet = td.get(i).replaceAll("\\s","").split(",");
                name = ParseRankings.fixNames(nameSet[1] + " " + nameSet[0]);
                team = ParseRankings.fixTeams(td.get(i+1).replaceAll("\\s",""));
            }

            String auctionValue = td.get(i+2).replaceAll("\\s","");
            int value = Integer.parseInt(auctionValue.substring(1));
            ParseRankings.finalStretch(holder, name, value, team, "");
        }

    }
}
