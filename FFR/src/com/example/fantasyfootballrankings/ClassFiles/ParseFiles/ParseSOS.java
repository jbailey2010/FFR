package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * A class that will hold the functions necessary to parse
 * strength of schedule positionally from fftoolbox
 * @author Jeff
 *
 */
public class ParseSOS 
{
	
	public static void getSOS(Storage holder) throws IOException
	{
		String data = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/strength_of_schedule.cfm", "tr.c");
		data = data.replaceAll("st", "").replaceAll("nd", "").replaceAll("rd", "").replaceAll("th", "");
		String[] allArr = data.split("\n");
		String[][] team = new String[allArr.length][];
		for(int i = 0; i  < allArr.length; i++)
		{
			team[i] = allArr[i].split(" ");
			//[i][0] has team name, rest individual
		}
	}
}
