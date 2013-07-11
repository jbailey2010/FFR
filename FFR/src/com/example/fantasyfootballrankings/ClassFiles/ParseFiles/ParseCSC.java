package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

public class ParseCSC 
{
	public static void parseCSCWrapper(Storage holder) throws IOException
	{
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=QB");
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=RB");
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=WR");
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=TE");
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=PK");
		parseCSCWorker(holder, "http://www.cheatsheetcreator.com/csc/browser.php?pos=Def");
	}
	
	public static void parseCSCWorker(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = html.split("\n");
		for(int i = 0; i < td.length; i+=8)
		{
			String[] nameSet = td[i+2].split(", ");
			String name = "";
			for(int j = nameSet.length-1; j >= 0; j--)
			{
				String nameObj = nameSet[j].trim();
				name += " " + nameObj.replaceAll("[^\\x20-\\x7e]","");
			}
			name = ParseRankings.fixDefenses(ParseRankings.fixNames(name.substring(1, name.length())));
			String inter = td[i+5];
			if(inter.equals("-"))
			{
				inter = "0";
			}
			int val = Integer.parseInt(inter);
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
