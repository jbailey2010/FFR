package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.util.HashMap;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
/**
 * A library to build auction values mathematically
 * @author user
 *
 */
public class ParseMath {
	
	/**
	 * Averages the two PAA calcs together to give one for playerinfo popup's sake
	 */
	public static double avgPAAMap(Storage holder, Roster r, PlayerObject player){
		int rosterSize = getRosterSize(r);
		double discretOne = discretPAA(r, rosterSize);
		double discretTwo = discretPAA2(r, rosterSize);
		HashMap<String, Double> zMapOne = initPAA1Map(holder);
		HashMap<String, Double> zMapTwo = initPAA2Map(holder, discretTwo);
		double val1 = paa1Calc(zMapOne, player, discretOne);
		double val2 = paa2Calc(zMapTwo, player);
		return ((val1 + val2)/2.0);
		
	}
	
	/**
	 * Calculates PAA for the first method for everyone
	 */
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
	
	/**
	 * Calculates the PAA for the second method for everyone
	 */
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
	
	/**
	 * Converts ECR to auction values for everyone
	 */
	public static void convertECR(Storage holder){
		for(PlayerObject player : holder.players){
			if(player.values.ecr > 0.0){
				double conv = convertRanking(player.values.ecr);
				ParseRankings.finalStretch(holder, player.info.name, (int) conv, player.info.team, player.info.position);
			}
		}
	}
	
	/**
	 * Converts ADP to auction values for everyone
	 */
	public static void convertADP(Storage holder){
		for(PlayerObject player : holder.players){
			if(ManageInput.isDouble(player.info.adp) || ManageInput.isInteger(player.info.adp)){
				double conv = convertRanking(Double.parseDouble(player.info.adp));
				ParseRankings.finalStretch(holder, player.info.name, (int) conv, player.info.team, player.info.position);
			}
		}
	}
	
	/**
	 * The actual formula for adp and ecr conversions
	 */
	public static double convertRanking(double ranking){
		return 78.6341-15.893 * Math.log(ranking);
	}
	
	/**
	 * Builds the hashmap to make the first paa calculation easier
	 */
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
	
	/**
	 * Does the individual calculation for the first paa method
	 */
	public static double paa1Calc(HashMap<String, Double> map, PlayerObject player, double discretCash){
		double coeff = player.values.paa / map.get(player.info.position);
		double possVal = discretCash * coeff + 1.0;
		if(player.info.position.equals("RB")){
			possVal *= 1.08;
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
	
	/**
	 * Builds the hashmap to simplify calculations for the second paa method
	 */
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
	
	/**
	 * Does the individual paa calculations for the second method
	 */
	public static double paa2Calc(HashMap<String, Double> zMap, PlayerObject player){
		double possVal = (player.values.paa / zMap.get(player.info.position)) + 1;
		if(player.info.position.equals("TE")){
			possVal /= 3.9;
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
	
	/**
	 * Gets the discretionary cash for the first method
	 */
	public static double discretPAA(Roster r, int rosterSize){
		return (200 - rosterSize)/rosterSize;
	}
	
	/**
	 * Gets the discretionary cash for the second method
	 */
	public static double discretPAA2(Roster r, int rosterSize){
		return (200.0 - rosterSize) * r.teams;
	}
	
	/**
	 * Gets the roster size
	 */
	public static int getRosterSize(Roster r){
		return r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k + r.flex.op + r.flex.rbwr + r.flex.rbwrte;
	}
	
	
	/**
	 * Gets the average paa of would be startable people
	 */
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
	
	/**
	 * Gets the total paa of would be startable people
	 */
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
