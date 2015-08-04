package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * A simple library to help parse espn's adv value from their live drafts
 * 
 * @author Jeff
 * 
 */
public class ParseESPNadv {
	/**
	 * Does all of the parsing of the espn aggregate drafting data
	 * 
	 * @param holder
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void parseESPNAggregate(Storage holder) throws IOException,
			XPatherException {
		List<String> brokenValues = HandleBasicQueries.handleLists(
				"http://games.espn.go.com/ffl/livedraftresults",
				"div div div div table tbody tr td");

		for (int i = 13; i < brokenValues.size(); i += 8) {
			if (i + 1 >= brokenValues.size()) {
				break;
			}
			String name = ParseRankings.fixNames(
					brokenValues.get(i + 1).split(", ")[0]).replace("*", "");
			String val = brokenValues.get(i + 5);
			int worth = (int) Double.parseDouble(val);
			ParseRankings.finalStretch(holder, name, worth, "", "");
		}
	}
}
