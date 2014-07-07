package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;

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
	public static void cbsRankings(Storage holder, Scoring s) throws IOException
	{
		String type = "/standard";
		if(s.catches > 0){
			type = "/ppr";
		}
		String url = "http://fantasynews.cbssports.com/fantasyfootball/rankings/yearly" + type;
		List<String> td = HandleBasicQueries.handleListsNoUA(url, "table.multiColumn td");
		int min = 0;
		for(int i = 0; i < td.size(); i++)
		{
			if(td.get(i).equals("Quarterbacks"))
			{
				min = i+1;
				break;
			}
		}
		for(int i = min; i < td.size(); i+=2)
		{
			if(td.get(i).equals("Running Backs") || td.get(i).equals("Wide Receivers") || td.get(i).equals("Tight Ends") || td.get(i).equals("Kickers") 
					|| td.get(i).equals("Defensive Special Teams"))
			{
				i++; 
			} 
			if(td.get(i).split(" ").length > 8 || td.get(i+1).split(" ").length > 8)
			{
				i += 3;
			}
			String nameSet = td.get(i+1);
			String name = "";
			String[] valSet = td.get(i+1).split(" ");
			int limit = 0;
			int index = Integer.parseInt(td.get(i));
			if(!td.get(i+1).contains("$"))
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
			if(td.get(i+1).contains("$"))
			{
				int valIndex = 0;
				for(int j = 0; j < td.get(i+1).length(); j++)
				{
					if(td.get(i+1).split(" ")[j].contains("$"))
					{
						valIndex = j;
						break;
					}
				}
				String valStr = td.get(i+1).split(" ")[valIndex];
				val = Integer.parseInt(valStr.substring(1, valStr.length())) * 2;
			}
			if(!(val == 0 && index <= 15)){
				ParseRankings.finalStretch(holder, name, val, "", "");
			}
		}
	}
}
