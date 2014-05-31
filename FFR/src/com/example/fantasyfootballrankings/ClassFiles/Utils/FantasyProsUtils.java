package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.util.HashMap;


public class FantasyProsUtils {
	
	public HashMap<String, String> teamMap = new HashMap<String, String>();
	
	public FantasyProsUtils(){
		initMap();
	}
	
	public String playerNameUrl(String playerName){
		String[] nameSet = playerName.toLowerCase().replaceAll("\\.", "").split(" ");
		StringBuilder nameBuilder = new StringBuilder(100);
		for(String name : nameSet){
			nameBuilder.append(name + "-");
		}
		String base = nameBuilder.toString();
		base = base.substring(0, base.length() - 1);
		return base;
	}
	
	
	public void initMap(){
		teamMap.put("Denver Broncos", "den");
		teamMap.put("Green Bay Packers", "gb");
		teamMap.put("New Orleans Saints", "no");
		teamMap.put("Detroit Lions", "det");
		teamMap.put("Indianapolis Colts", "ind");
		teamMap.put("Philadelphia Eagles", "phi");
		teamMap.put("Carolina Panthers", "car");
		teamMap.put("Atlanta Falcons", "atl");
		teamMap.put("Washington Redskins", "was");
		teamMap.put("New England Patriots", "ne");
		teamMap.put("San Francisco 49ers", "sf");
		teamMap.put("Dallas Cowboys", "dal");
		teamMap.put("Seattle Seahawks", "sea");
		teamMap.put("Chicago Bears", "chi");
		teamMap.put("San Diego Chargers", "sd");
		teamMap.put("Cincinnati Bengals", "cin");
		teamMap.put("Pittsburgh Steelers", "pit");
		teamMap.put("Miami Dolphins", "mia");
		teamMap.put("Kansas City Chiefs", "kc");
		teamMap.put("New York Giants", "nyg");
		teamMap.put("Arizona Cardinals", "ari");
		teamMap.put("Cleveland Browns", "cle");
		teamMap.put("Baltimore Ravens", "bal");
		teamMap.put("Tampa Bay Buccaneers", "tb");
		teamMap.put("St. Louis Rams", "stl");
		teamMap.put("Tennessee Titans", "ten");
		teamMap.put("Buffalo Bills", "buf");
		teamMap.put("New York Jets", "nyj");
		teamMap.put("Minnesota VIkings", "min");
		teamMap.put("Oakland Raiders", "oak");
		teamMap.put("Houston Texans", "hou");
		teamMap.put("Jacksonville Jaguars", "jac");
	}

}
