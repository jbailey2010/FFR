package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

import java.io.IOException;
import java.util.List;

/**
 * Created by jeffbail on 8/13/2016.
 */
public class ParseRazzball {

    public static void getRazzballRankings(Storage holder, Roster r, Scoring s)  throws IOException {

        List<String> td = HandleBasicQueries.handleLists(
                "http://football.razzball.com/projections-dollars",
                "table.tablesorter tbody tr td");

        int min = 0;
        for(int i = 0; i < td.size(); i++) {
            if (td.get(i).split(" ").length == 2) {
                min = i;
                break;
            }
        }

        System.out.println(min + ", " + td.size());
        for (int i = min; i < td.size(); i+=10) {
            String name = ParseRankings.fixDefenses(ParseRankings.fixNames(td.get(i)));
            String pos = td.get(i+1);
            if ("DST".equals(pos)) {
                pos = Constants.DST;
            }
            String team = ParseRankings.fixTeams(td.get(i+2));
            int valueOffset = 3;
            if (s.catches > 0) {
                valueOffset += 4;
            }
            if (r.teams > 10) {
                valueOffset++;
            }
            int value = Integer.parseInt(td.get(i + valueOffset));

            ParseRankings.finalStretch(holder, name, value, team, pos);
        }
    }


}
