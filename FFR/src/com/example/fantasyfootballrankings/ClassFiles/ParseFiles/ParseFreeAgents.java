package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

/**
 * Parses free agency data
 * 
 * @author Jeff
 * 
 */
public class ParseFreeAgents {
	/**
	 * Parses free agency data to each team
	 * 
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, List<String>> parseFA() throws IOException {
		HashMap<String, List<String>> faList = new HashMap<String, List<String>>();
		String url = "http://www.sbnation.com/nfl/2015/3/10/8150357/nfl-free-agent-signings-tracker-2015-rumors";
		Document doc = Jsoup.connect(url).timeout(0).get();
		List<String> perRow1 = HandleBasicQueries.handleListsMulti(doc, url,
				"tr td");
		int floor = 0;
		for (int i = 0; i < 20; i++) {
			if (perRow1.get(i).contains("Player")) {
				floor = i + 6; // 6 col table
				break;
			}
		}
		for (int i = floor; i < perRow1.size(); i += 6) {
			String name = ParseRankings.fixNames(perRow1.get(i));
			String oldTeam = ParseRankings.fixTeams(perRow1.get(i + 3)
					.replaceAll("\\*", ""));
			if (perRow1.get(i + 4).contains("--")) {
				continue;
			}
			String newTeam = ParseRankings.fixTeams(perRow1.get(i + 4)
					.replaceAll("\\*", ""));
			if (!oldTeam.equals(newTeam) && !(newTeam.split(" ").length < 2)) {
				if (!faList.containsKey(oldTeam)) {
					List<String> list = new ArrayList<String>();
					list.add("Signed: ");
					list.add("Departing: " + name + "\n");
					faList.put(oldTeam, list);
				} else {
					List<String> list = faList.get(oldTeam);
					String outgoing = list.get(1);
					outgoing += name + "\n";
					list.remove(1);
					list.add(1, outgoing);
					faList.put(oldTeam, list);
				}
				if (!faList.containsKey(newTeam)) {
					List<String> list = new ArrayList<String>();
					list.add("Signed: " + name + "\n");
					list.add("Departing: ");
					faList.put(newTeam, list);
				} else {
					List<String> list = faList.get(newTeam);
					String incoming = list.get(0);
					incoming += name + "\n";
					list.remove(0);
					list.add(0, incoming);
					faList.put(newTeam, list);
				}
			}
		}
		return faList;
	}
}