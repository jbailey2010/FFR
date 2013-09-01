package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;

/**
 * Parses SI's positional rankings
 * @author Jeff
 *
 */
public class ParseSI 
{
	/**
	 * Calls the positional parser functions
	 * @param holder
	 * @throws IOException
	 */
	public static void parseSIWrapper(Storage holder) throws IOException
	{
		parseSIQB(holder, "http://sportsillustrated.cnn.com/fantasy/news/20130723/fantasy-football-quarterback-rankings-projections-preview/");
		parseSIRB(holder, "http://sportsillustrated.cnn.com/fantasy/news/20130725/fantasy-football-running-back-rankings-projections-preview/");
		parseSIWR(holder, "http://sportsillustrated.cnn.com/fantasy/news/20130724/fantasy-football-wide-receiver-rankings-projections-preview/");
		parseSITE(holder, "http://sportsillustrated.cnn.com/fantasy/news/20130726/fantasy-football-tight-end-rankings-projections-preview/");
		parseSIK(holder, "http://sportsillustrated.cnn.com/fantasy/news/20130726/fantasy-football-kicker-rankings-projections-preview/");
	}
	
	

	/**
	 * Parses the qb data from si
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void parseSIQB(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 3; i < td.length; i+=16)
		{
			String name = ParseRankings.fixNames(td[i+1]);
			if(i+5 >= td.length)
			{
				break;
			}
			String aucVal = td[i+5];
			if(!ManageInput.isInteger(aucVal.substring(1, aucVal.length())))
			{
				continue;
			}
			int val = Integer.parseInt(aucVal.substring(1, aucVal.length())) * 2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
	
	/**
	 * Parses the rb data from SI
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void parseSIRB(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 3; i < td.length; i+=17)
		{
			String name = ParseRankings.fixNames(td[i+1]);
			if(i + 5 >= td.length)
			{
				break;
			}
			String aucVal = td[i+5];
			if(!ManageInput.isInteger(aucVal.substring(1, aucVal.length())))
			{
				continue;
			}
			int val = Integer.parseInt(aucVal.substring(1, aucVal.length())) *2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
	
	/**
	 * Parses the WR data from SI
	 * @param holder
	 * @param url
	 * @throws IOException 
	 */
	public static void parseSIWR(Storage holder, String url) throws IOException
	{
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 3; i < td.length; i+=15)
		{
			String name = ParseRankings.fixNames(td[i+1]);
			if(i+5 >= td.length)
			{
				break;
			}
			String aucVal = td[i+5];
			if(!ManageInput.isInteger(aucVal.substring(1, aucVal.length())))
			{
				continue;
			}
			int val = Integer.parseInt(aucVal.substring(1, aucVal.length())) * 2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
	
	/**
	 * Gets the TE data from SI
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void parseSITE(Storage holder, String url) throws IOException {
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 3; i < td.length; i+=15)
		{
			String name = ParseRankings.fixNames(td[i+1]);
			if(i+5 >= td.length)
			{
				break;
			}
			String aucVal = td[i+5];
			if(!ManageInput.isInteger(aucVal.substring(1, aucVal.length())))
			{
				continue;
			}
			int val = Integer.parseInt(aucVal.substring(1, aucVal.length())) * 2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
	
	/**
	 * Gets the K data from SI
	 * @param holder
	 * @param url
	 * @throws IOException
	 */
	public static void parseSIK(Storage holder, String url) throws IOException {
		String html = HandleBasicQueries.handleLists(url, "td");
		String[] td = ManageInput.tokenize(html, '\n', 1);
		for(int i = 3; i < td.length; i+=15)
		{
			String name = ParseRankings.fixNames(td[i+1]);
			if(i+5 >= td.length)
			{
				break;
			}
			String aucVal = td[i+5];
			if(!ManageInput.isInteger(aucVal.substring(1, aucVal.length())))
			{
				continue;
			}
			int val = Integer.parseInt(aucVal.substring(1, aucVal.length())) * 2;
			ParseRankings.finalStretch(holder, name, val, "", "");
		}
	}
}
