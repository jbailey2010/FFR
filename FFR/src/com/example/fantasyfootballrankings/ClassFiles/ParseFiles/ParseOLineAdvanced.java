package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * Parses the advanced oline numbers
 * @author Jeff
 *
 */
public class ParseOLineAdvanced 
{
	/**
	 * Does the actual parsing work for the line data
	 * @param holder
	 * @throws IOException
	 */
	public static void parsePFOLineData(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.footballoutsiders.com/stats/ol", "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		HashMap<String, String> data = new HashMap<String, String>();
		for(int i = 18; i < td.length; i+=16)
		{
			if(td[i].equals("RUN BLOCKING"))
			{
				i+=2;
			}
			else if(td[i+1].equals("Team"))
			{
				i+=16;
			}
			else if(td[i+1].equals("NFL"))
			{
				break;
			}
			
			String team2 = ParseRankings.fixTeams(td[i+12]);
			String sack = td[i+15];
			data.put(team2 + "/" + "pass", sack + " adjusted team sack rate (" + td[i+13] + ")\n\n");
			
			String team1 = ParseRankings.fixTeams(td[i+1]);
			String adjYPC = td[i+2];
			String adjYPCRank = td[i];
			String power = td[i+4];
			String powerRank = td[i+5];
			String stuff = td[i+6];
			String stuffRank = td[i+7];
			String secLevel = td[i+8];
			String secLevelRank = td[i+9];
			String openField = td[i+10];
			String openFieldRank = td[i+11];
			StringBuilder runData = new StringBuilder(1000);
			runData.append(adjYPC + " adjusted team yards per carry (" + adjYPCRank + ")\n\n");
			runData.append(power + " success rate with < 3 yards per go (" + powerRank + ")\n\n");
			runData.append(stuff + " rate of being stuffed at the line (" + stuffRank + ")\n\n");
			runData.append(secLevel + " YPC earned between 5 and 10 yards past LOS (" + secLevelRank + ")\n\n");
			runData.append(openField + " YPC earned 10+ yards past LOS (" + openFieldRank + ")");
			data.put(team1 + "/" + "run", runData.toString());
		}
		List<String> teams = new ArrayList<String>();
		Set<String> keys = data.keySet();
		for(String key : keys)
		{
			String team = key.split("/")[0];
			if(!teams.contains(team))
			{
				teams.add(team);
			}
		}
		for(String team : teams)
		{
			holder.oLineAdv.put(team, data.get(team + "/pass")+ data.get(team + "/run"));
		}
	}
}
