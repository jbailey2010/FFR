package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
/**
 * A library to parse a page loaded by javascript
 * @author Jeff
 *
 */
public class ParsePFF {
	/**
	 * Checks all of the positions' data
	 */
	public static void parsePFFWrapper(Storage holder) throws IOException
	{
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/QB/?public=true", "QB", holder);
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/RB/?public=true", "RB", holder);
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/WR/?public=true", "WR", holder);
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/TE/?public=true", "TE", holder);
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/DST/?public=true", "D/ST", holder);
		parsePFFWorker("http://www.profootballfocus.com/toolkit/data/1/ROS/K/?public=true", "K", holder);
	}
	
	/**
	 * Builds the parameter data from the json then parses it awesomely
	 */
	public static void parsePFFWorker(String urlStr, String position, Storage holder) throws IOException
	{
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    StringBuilder htmlBuilder = new StringBuilder(10000);
	    try {
	        url = new URL(urlStr);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	        	htmlBuilder.append(line);
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	    String html = htmlBuilder.toString().replaceAll("\"", "").replaceAll("\\]", "").replaceAll("\\}", "");
	    String[] players = html.split("\\[");
	    for(int i = 2; i < players.length; i++)
	    {
	    	String[] data = players[i].split(",");
	    	String name = data[1];
	    	String team = ParseRankings.fixTeams(data[2]);
	    	if(name.contains("Def"))
	    	{
	    		team = name.split(" ")[0];
	    		name = name.replace("Def", "D/ST");
	    	}
	    	int val = Integer.parseInt(data[data.length - 1]);
	    	ParseRankings.finalStretch(holder, name, val, team, position);
	    }
	}
}
