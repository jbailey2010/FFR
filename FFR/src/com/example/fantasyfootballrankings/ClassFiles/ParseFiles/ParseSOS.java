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
		System.out.println(data);
	}
}
