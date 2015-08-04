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
		String url = "http://www.cbssports.com/nfl/transactions/free-agents";
		Document doc = Jsoup.connect(url).timeout(0).get();
		List<String> perRow1 = HandleBasicQueries.handleListsMulti(doc, url,
				"tr.row1 td");
		List<String> perRow2 = HandleBasicQueries.handleListsMulti(doc, url,
				"tr.row2 td");
		int floor = 0;
		for (int i = 0; i < 20; i++) {
			if (ManageInput.isInteger(perRow1.get(i))) {
				floor = i;
				break;
			}
		}
		for (int i = floor; i < perRow1.size(); i += 10) {
			String name = ParseRankings.fixNames(perRow1.get(i + 1));
			String oldTeam = ParseRankings.fixTeams(perRow1.get(i + 7));
			if (perRow1.get(i + 8).contains("TBD")) {
				continue;
			}
			String newTeam = ParseRankings.fixTeams(perRow1.get(i + 8));
			if (!oldTeam.equals(newTeam) && !newTeam.contains("TBD")) {
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
		for (int i = floor; i < perRow2.size(); i += 10) {
			String name = ParseRankings.fixNames(perRow2.get(i + 1));
			String oldTeam = ParseRankings.fixTeams(perRow2.get(i + 7));
			if (perRow2.get(i + 8).contains("TBD")) {
				continue;
			}
			String newTeam = ParseRankings.fixTeams(perRow2.get(i + 8));
			if (!oldTeam.equals(newTeam) && !newTeam.contains("TBD")) {
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
					faList.remove(oldTeam);
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
					faList.remove(newTeam);
					faList.put(newTeam, list);
				}
			}
		}
		return faList;
	}
}