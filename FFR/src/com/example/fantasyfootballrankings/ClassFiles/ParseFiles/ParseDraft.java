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
				"http://www.sbnation.com/nfl/2014/5/10/5704890/nfl-draft-results-recap-jadeveon-clowney-johnny-manziel-michael-sam", "td");
		HashMap<String, String> picks = new HashMap<String, String>();
		String[] perPick = ManageInput.tokenize(html, '\n', 1);
		for(int i = 5; i < perPick.length; i+=5)
		{
			if(!ManageInput.isInteger(perPick[i])){
				continue;
			}
			String team = ParseRankings.fixTeams(perPick[i+1]);
			String name = ParseRankings.fixNames(perPick[i+2]);
			String position = perPick[i+3];
			String overall = perPick[i];
			int j = Integer.parseInt(overall);
			String round = "";
			if(j <= 32){
				round = "1";
			}
			else if(j <= 64){
				round = "2";
			}
			else if(j <= 100){
				round = "3";
			}
			else if(j <= 140){
				round = "4";
			}
			else if(j <= 176){
				round = "5";
			}
			else if(j <= 215){
				round = "6";
			}
			else if(j <= 256){
				round = "7";
			}
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
