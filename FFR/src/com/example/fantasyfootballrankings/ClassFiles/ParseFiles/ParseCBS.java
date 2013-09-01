package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A library that handles the parsing
 * of the cbs rankings
 * @author Jeff
 *
 */
public class ParseCBS 
{
	/**Calls the actual parser with both rows styling
	 * @param holder the storage to check/write to
	 * @throws IOException
	 */
	public static void cbsRankings(Storage holder) throws IOException
	{
		String url = "http://fantasynews.cbssports.com/fantasyfootball/rankings/yearly";
		String html = HandleBasicQueries.handleListsNoUA(url, "table.multiColumn td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		int min = 0;
		for(int i = 0; i < td.length; i++)
		{
			if(td[i].equals("Quarterbacks"))
			{
				min = i+1;
				break;
			}
		}
		for(int i = min; i < td.length; i+=2)
		{
			if(td[i].equals("Running Backs") || td[i].equals("Wide Receivers") || td[i].equals("Tight Ends") || td[i].equals("Kickers") 
					|| td[i].equals("Defensive Special Teams"))
			{
				i++; 
			} 
			if(td[i].split(" ").length > 8 || td[i+1].split(" ").length > 8)
			{
				i += 3;
			}
			String nameSet = td[i+1];
			String name = "";
			String[] valSet = td[i+1].split(" ");
			int limit = 0;
			if(!td[i+1].contains("$"))
			{
				if(nameSet.contains("("))
				{
					limit = nameSet.split(" \\(")[0].split(" ").length - 1;
				}
				else if(nameSet.split(" ")[nameSet.split(" ").length - 1].length() > 1)
				{
					limit = nameSet.split(" ").length - 1; 
				}
				else
				{
					limit = nameSet.split(" ").length - 2;
				}
			} 
			else
			{
				for(int j = 0; j < valSet.length; j++)
				{
					if(valSet[j].contains("$"))
					{ 
						limit = j - 1;
					}
				}
			} 
			for(int j = 0; j < limit; j++)
			{
				name += valSet[j] + " ";
			}
			name = name.substring(0, name.length()-1);
			String nameCopy = ParseRankings.fixTeams(name);
			if(name.split(" ").length == 1 && ! nameCopy.equals(name))
			{
				name += " D/ST";
			} 
			name = ParseRankings.fixNames(name);
			int val = 0;
			if(td[i+1].contains("$"))
			{
				int valIndex = 0;
				for(int j = 0; j < td[i+1].length(); j++)
				{
					if(td[i+1].split(" ")[j].contains("$"))
					{
						valIndex = j;
						break;
					}
				}
				String valStr = td[i+1].split(" ")[valIndex];
				val = Integer.parseInt(valStr.substring(1, valStr.length())) * 2;
			}
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
