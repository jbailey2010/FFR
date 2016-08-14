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

public class ParseMFL {

    public static void getMFLAAVs(Storage holder) throws IOException {
        List<String> td = HandleBasicQueries.handleLists(
                "http://www03.myfantasyleague.com/2016/aav?COUNT=500&POS=QB%2BRB%2BWR%2BTE&CUTOFF=5&FRANCHISES=-1&IS_PPR=-1&IS_KEEPER=-1&TIME=",
                "table.report tbody tr td");

        int min = 0;
        for (int i = 0; i < td.size(); i++) {
            if (td.get(i).contains(", ")) {
                min = i;
                break;
            }
        }

        for (int i = min; i < td.size(); i += 4) {
            if (i >= td.size() - 3) {
                break;
            }
            int val = Integer.parseInt(td.get(i+1).substring(1));
            String nameTeamPos = td.get(i).replaceAll("\\*", "");
            String pos = nameTeamPos.substring(nameTeamPos.lastIndexOf(" ")).replaceAll("\\s","");
            String nameTeam = nameTeamPos.substring(0, nameTeamPos.lastIndexOf(" "));
            String team = nameTeam.substring(nameTeam.lastIndexOf(" "));
            String nameBackwards = nameTeam.substring(0, nameTeam.lastIndexOf(" "));
            String[] nameSet = nameBackwards.split(", ");
            String name = nameSet[1] + " " + nameSet[0];
            team = ParseRankings.fixTeams(team.replaceAll("\\s",""));
            name = ParseRankings.fixNames(name);
            ParseRankings.finalStretch(holder, name, val, team, pos);
        }
    }
}
