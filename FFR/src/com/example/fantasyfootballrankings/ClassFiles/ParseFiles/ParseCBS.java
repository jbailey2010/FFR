package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * A library that handles the parsing of the cbs rankings
 * 
 * @author Jeff
 * 
 */
public class ParseCBS {
	/**
	 * Calls the actual parser with both rows styling
	 * 
	 * @param holder
	 *            the storage to check/write to
	 * @throws IOException
	 */
	public static void cbsRankings(Storage holder, Scoring s)
			throws IOException {
        String type = "standard/";
        if (s.catches > 0) {
            type = "ppr/";
        }
        String url = "http://www.cbssports.com/fantasy/football/rankings/";
        cbsWorker(holder, s, url + type + "QB/yearly/");
        cbsWorker(holder, s, url + type + "RB/yearly/");
        cbsWorker(holder, s, url + type + "WR/yearly/");
        cbsWorker(holder, s, url + type + "TE/yearly/");
        cbsWorker(holder, s, url + type + "DST/yearly/");
        cbsWorker(holder, s, url + type + "K/yearly/");

    }

    public static void cbsWorker(Storage holder, Scoring s, String url) throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url,
				"tbody.rankings-body tr.ranking-tbl-data-inner-tr td");
        int min = 0;
		for (int i = 0; i < td.size(); i++) {
            if (!ManageInput.isInteger(td.get(i))) {
                min = i;
                break;
            }
        }

        for (int i = min; i < td.size(); i+=2) {
            String[] playerData = td.get(i).split(" ");
            String value = playerData[playerData.length - 1];
            int aucValue = Integer.parseInt(value.replace("$", "")) * 2;
            String nameAndTeam = td.get(i).substring(0, td.get(i).lastIndexOf(" "));
            String name = nameAndTeam.substring(0, nameAndTeam.lastIndexOf(" "));
            if (name.split(" ").length == 1) {
                name += " D/ST";
            }
            String finalName = ParseRankings.fixNames(name);
            ParseRankings.finalStretch(holder, finalName, aucValue, "", "");
        }
	}
}
