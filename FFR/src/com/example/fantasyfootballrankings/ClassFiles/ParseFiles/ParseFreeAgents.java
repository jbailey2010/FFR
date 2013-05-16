package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;

public class ParseFreeAgents 
{
	public static HashMap<String, List<String>> parseFA() throws IOException
	{
		System.out.println("in parse free agency");
		HashMap<String, List<String>> faList = new HashMap<String, List<String>>();
		String url = "http://www.spotrac.com/free-agents/nfl/";
		String html = HandleBasicQueries.handleLists(url, "tr");
		System.out.println(html);
		return faList;
	}
}
