package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;

/**
 * Parses free agency data
 * @author Jeff
 *
 */
public class ParseFreeAgents 
{
	/**
	 * Parses free agency data to each team
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, List<String>> parseFA() throws IOException
	{
		HashMap<String, List<String>> faList = new HashMap<String, List<String>>();
		String url = "http://www.cbssports.com/nfl/transactions/free-agents";
		Document doc = Jsoup.connect(url).timeout(0).get();
		String html1 = HandleBasicQueries.handleListsMulti(doc, url, "tr.row1 td");
		String html2 = HandleBasicQueries.handleListsMulti(doc, url, "tr.row2 td");
		String[] perRow1 = ManageInput.tokenize(html1, '\n', 1);
		String[] perRow2 = ManageInput.tokenize(html2, '\n', 1);
		for(int i = 7; i < perRow1.length; i+=3)
		{
			String name = perRow1[i];
			String position = perRow1[++i];
			String oldTeam = ParseRankings.fixTeams(perRow1[++i]);
			String newTeam = ParseRankings.fixTeams(perRow1[++i]);
			if(!oldTeam.equals(newTeam))
			{
				if(!faList.containsKey(oldTeam))
				{
					List<String> list = new ArrayList<String>();
					list.add("Signed: ");
					list.add("Departing: " + name + "\n");
					faList.put(oldTeam, list);
				}
				else
				{
					List<String> list = faList.get(oldTeam);
					String outgoing = list.get(1);
					outgoing += name + "\n";
					list.remove(1);
					list.add(1, outgoing);
					faList.put(oldTeam, list);
				}
				if(!faList.containsKey(newTeam))
				{
					List<String> list = new ArrayList<String>();
					list.add("Signed: " + name + "\n");
					list.add("Departing: ");
					faList.put(newTeam, list);
				}
				else
				{
					List<String> list = faList.get(newTeam);
					String incoming = list.get(0);
					incoming += name + "\n";
					list.remove(0);
					list.add(0, incoming);
					faList.put(newTeam, list);
				}
			}	
		}
		for(int i = 1; i < perRow2.length; i+=3)
		{
			String name = perRow2[i];
			String position = perRow2[++i];
			String oldTeam = ParseRankings.fixTeams(perRow2[++i]);
			String newTeam = ParseRankings.fixTeams(perRow2[++i]);
			if(!oldTeam.equals(newTeam))
			{
				if(!faList.containsKey(oldTeam))
				{
					List<String> list = new ArrayList<String>();
					list.add("Signed: ");
					list.add("Departing: " + name + "\n");
					faList.put(oldTeam, list);
				}
				else
				{
					List<String> list = faList.get(oldTeam);
					String outgoing = list.get(1);
					outgoing += name + "\n";
					list.remove(1);
					list.add(1, outgoing);
					faList.remove(oldTeam);
					faList.put(oldTeam, list);
				}
				if(!faList.containsKey(newTeam))
				{
					List<String> list = new ArrayList<String>();
					list.add("Signed: " + name + "\n");
					list.add("Departing: ");
					faList.put(newTeam, list);
				}
				else
				{
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