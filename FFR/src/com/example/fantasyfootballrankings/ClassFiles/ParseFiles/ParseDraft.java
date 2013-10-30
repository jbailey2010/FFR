package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import java.util.HashMap;
import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;

/**
 * A class to handle all the parsing of draft-related data
 * @author Jeff
 *
 */
public class ParseDraft 
{
	/**
	 * Parses the drafts themselves.
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseTeamDraft() throws IOException
	{
		String html = HandleBasicQueries.handleLists(
				"http://www.sbnation.com/nfl/2013/4/27/4276432/2013-nfl-draft-full-7-round-results", "td");
		HashMap<String, String> picks = new HashMap<String, String>();
		String[] perPick = ManageInput.tokenize(html, '\n', 1);
		for(int i = 0; i < perPick.length; i+=2)
		{
			String round = perPick[i];
			String overall = perPick[++i];
			String team = ParseRankings.fixTeams(perPick[++i].replaceAll("[^\\x20-\\x7e]",""));
			String name = perPick[++i];
			String position = perPick[++i];
			String pick = round + " (" + overall + "): " + name + ", " + position + "\n";
			if(picks.containsKey(team))
			{
				picks.put(team, picks.get(team) + pick);
			}
			else
			{
				picks.put(team, pick);
			}
		}
		return picks;
	}
	
	/**
	 * Parses the gpa and rank in gpa to each team
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, String> parseTeamDraftGPA() throws IOException
	{
		String url = "http://www.footballoutsiders.com/nfl-draft/2013/2013-nfl-draft-report-card-report";
		HashMap<String, String> gpa = new HashMap<String, String>();
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] brokenUp = ManageInput.tokenize(html, '\n', 1);
		for(int i = 1; i < brokenUp.length; i+=2)
		{ 
			if(!brokenUp[i].contains("2013"))
			{
				String before = brokenUp[i];
				String team = ParseRankings.fixTeams(before);
				String grade = brokenUp[i+=3];
				String rank = brokenUp[i+=2];
				if(team.equals("High Grade"))
				{
					break;
				}
				gpa.put(team, "Average Draft Grade: " + grade + " (" + rank + ")\n");
			}
		}
		return gpa;
	}
}
