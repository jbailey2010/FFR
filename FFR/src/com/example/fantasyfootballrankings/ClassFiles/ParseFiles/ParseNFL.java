package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * Parses the NFL stuff
 * 
 * @author Jeff
 * 
 */
public class ParseNFL {

	/**
	 * Calls the top 200 on the worker
	 * 
	 * @param holder
	 * @throws IOException
	 */
	public static void parseNFLAAVWrapper(Storage holder) throws IOException {
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=0&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=26&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=51&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=76&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=101&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=126&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=151&sort=draftAveragePosition");
		parseNFLAAVWorker(
				holder,
				"http://fantasy.nfl.com/draftcenter/breakdown?leagueId=&offset=176&sort=draftAveragePosition");
	}

	/**
	 * Does the actual parsing of the NFL AAV
	 */
	public static void parseNFLAAVWorker(Storage holder, String url)
			throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		for (int i = 0; i < td.size(); i += 4) {
			String nameSet[] = td.get(i).split(" ");
			String name = "";
			int filter = 0;
			for (int j = 0; j < nameSet.length; j++) {
				if (nameSet[j].equals("DEF")) {
					filter = j;
					break;
				}
				if (nameSet[j].equals("-")) {
					filter = j - 1;
					break;
				}
				if (nameSet[j].equals("View")) {
					filter = j - 1;
					break;
				}
				if (nameSet[j].length() == j) {
					filter = j;
					break;
				}
				if (nameSet[j].equals(Constants.QB) || nameSet[j].equals(Constants.RB)
						|| nameSet[j].equals(Constants.WR) || nameSet[j].equals(Constants.TE)
						|| nameSet[j].equals(Constants.K)) {
					filter = j;
					break;
				}
			}
			for (int j = 0; j < filter; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixDefenses(ParseRankings.fixNames(name
					.substring(0, name.length() - 1)));
			String worth = td.get(i + 3);
			int val = Integer.parseInt(worth) * 2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
