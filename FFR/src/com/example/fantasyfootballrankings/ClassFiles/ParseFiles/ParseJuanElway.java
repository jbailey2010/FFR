package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

/**
 * Parse fantasy football auction functions
 * @author Jeff
 *
 */
public class ParseJuanElway 
{
	/**
	 * Calls most of the positions (ignoring d/st, stupid rankings)
	 * @param holder
	 * @throws IOException
	 */
	public static void parseJuanElwayVals(Storage holder) throws IOException
	{
		String html = HandleBasicQueries.handleLists("http://www.juanelway.com/auction-values/", "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 6; i < td.length; i+=3)
		{
			if(td[i].length() > 20)
			{
				break;
			}
			int val = 0; 
			try{
				val = Integer.parseInt(td[i+2].replace("$", ""));
			} catch(NumberFormatException e)
			{
				
			}
			String name = ParseRankings.fixNames(td[i+1]);
			if(!name.equals("Running Back") && !name.equals("Wide Receiver") && !name.equals("Tight End"))
			{
				ParseRankings.finalStretch(holder, name, val, "", "");
			}
		}
	}
}
