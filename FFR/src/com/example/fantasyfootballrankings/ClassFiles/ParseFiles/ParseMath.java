package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.util.HashMap;

import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

public class ParseMath {
	
	public static double avgPAAMap(Storage holder, Roster r, PlayerObject player){
		int rosterSize = getRosterSize(r);
		double discretOne = discretPAA(r, rosterSize);
		double discretTwo = discretPAA2(r, rosterSize);
		HashMap<String, Double> zMapOne = initPAA1Map(holder);
		HashMap<String, Double> zMapTwo = initPAA2Map(holder, discretTwo);
		double val1 = paa1Calc(zMapOne, player, discretOne);
		double val2 = paa2Calc(zMapTwo, player);
		System.out.println(val1 + ", " + val2 + " for " + player.info.name);
		return ((val1 + val2)/2.0);
		
	}
	
	public static void convertPAA(Storage holder, Roster r)
	{
		double discretCash = discretPAA(r, getRosterSize(r));
		HashMap<String, Double> zMap = initPAA1Map(holder);
		for(PlayerObject player : holder.players){
			if(zMap.containsKey(player.info.position)){
				double possVal = paa1Calc(zMap, player, discretCash);
				ParseRankings.finalStretch(holder, player.info.name, (int) possVal, player.info.team, player.info.position);
			}
		}
	}
	
	public static void convertPAA2(Storage holder, Roster r){
		double freeMoney = discretPAA2(r, getRosterSize(r));
		HashMap<String, Double> zMap = initPAA2Map(holder, freeMoney);
		for(PlayerObject player : holder.players){
			if(zMap.containsKey(player.info.position)){
				double possVal = paa2Calc(zMap, player);
				ParseRankings.finalStretch(holder, player.info.name, (int) possVal, player.info.team, player.info.position);
			}
		}
	}
	
	public static HashMap<String, Double> initPAA1Map(Storage holder){
		HashMap<String, Double> zMap = new HashMap<String, Double>();
		zMap.put("QB", avgPAAPos(holder, "QB"));
		zMap.put("RB", avgPAAPos(holder, "RB"));
		zMap.put("WR", avgPAAPos(holder, "WR"));
		zMap.put("TE", avgPAAPos(holder, "TE"));
		zMap.put("D/ST", avgPAAPos(holder, "D/ST"));
		zMap.put("K", avgPAAPos(holder, "K"));
		return zMap;
	}
	
	public static double paa1Calc(HashMap<String, Double> map, PlayerObject player, double discretCash){
		double coeff = player.values.paa / map.get(player.info.position);
		double possVal = discretCash * coeff + 1.0;
		if(player.info.position.equals("RB")){
			possVal *= 1.35;
		}
		if(player.info.position.equals("D/ST"))
		{
			possVal /= 10;
		}
		if(player.info.position.equals("K")){
			possVal /= 20;
		}
		if(possVal < 1.0)
		{
			possVal = 1.0;
		}
		return possVal;
	}
	
	public static HashMap<String, Double> initPAA2Map(Storage holder, double freeMoney){
		HashMap<String, Double> zMap = new HashMap<String, Double>();
		zMap.put("QB", totalPAAPos(holder, "QB")/freeMoney);
		zMap.put("RB", totalPAAPos(holder, "RB")/freeMoney);
		zMap.put("WR", totalPAAPos(holder, "WR")/freeMoney);
		zMap.put("TE", totalPAAPos(holder, "TE")/freeMoney);
		zMap.put("D/ST", totalPAAPos(holder, "D/ST")/freeMoney);
		zMap.put("K", totalPAAPos(holder, "K")/freeMoney);
		return zMap;
	}
	
	public static double paa2Calc(HashMap<String, Double> zMap, PlayerObject player){
		double possVal = (player.values.paa / zMap.get(player.info.position)) + 1;
		if(player.info.position.equals("TE")){
			possVal /= 4.0;
		}
		if(player.info.position.equals("D/ST")){
			possVal /= 30.0;
		}
		if(player.info.position.equals("K")){
			possVal /= 25.0;
		}
		if(possVal < 1.0){
			possVal = 1.0;
		}
		return possVal;
	}
	
	public static double discretPAA(Roster r, int rosterSize){
		return (200 - rosterSize)/rosterSize;
	}
	
	public static double discretPAA2(Roster r, int rosterSize){
		return (200.0 - rosterSize) * r.teams;
	}
	
	public static int getRosterSize(Roster r){
		return r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k + r.flex.op + r.flex.rbwr + r.flex.rbwrte;
	}
	
	
	public static double avgPAAPos(Storage holder, String position)
	{
		double paaTotal = 0.0;
		double paaCount = 0.0;
		for(PlayerObject player : holder.players){
			if(player.values.paa > 0.0 && player.info.position.equals(position)){
				paaTotal += player.values.paa;
				paaCount ++;
			}
		}
		return paaTotal / paaCount;
	}
	
	public static double totalPAAPos(Storage holder, String position){
		double paaTotal = 0.0;
		for(PlayerObject player : holder.players){
			if(player.values.paa > 0.0 && player.info.position.equals(position)){
				paaTotal += player.values.paa;
			}
		}
		return paaTotal * 4;
	}


}
