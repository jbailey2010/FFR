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
 * 
 * @author user
 * 
 */
public class ParseMath {
	public static HashMap<String, Double> zMap;

	public static void initZMap(Storage holder) {
		zMap = initPAA1Map(holder);
	}

	/**
	 * Averages the two PAA calcs together to give one for playerinfo popup's
	 * sake
	 */
	public static double avgPAAMap(Storage holder, Roster r, PlayerObject player) {
		int rosterSize = getRosterSize(r);
		double discretOne = discretPAA(r, rosterSize);
		return paa1Calc(zMap, player, discretOne);

	}

	/**
	 * Calculates PAA for the first method for everyone
	 */
	public static void convertPAA(Storage holder, Roster r) {
		double discretCash = discretPAA(r, getRosterSize(r));
		initZMap(holder);
		for (PlayerObject player : holder.players) {
			if (zMap.containsKey(player.info.position)) {
				double possVal = paa1Calc(zMap, player, discretCash);
				ParseRankings.finalStretch(holder, player.info.name,
						(int) possVal, player.info.team, player.info.position);
			}
		}
	}

	/**
	 * Converts ECR to auction values for everyone
	 */
	public static void convertECR(Storage holder) {
		for (PlayerObject player : holder.players) {
			if (player.values.ecr > 0.0) {
				double conv = convertRanking(player.values.ecr);
				ParseRankings.finalStretch(holder, player.info.name,
						(int) conv, player.info.team, player.info.position);
			}
		}
	}

	/**
	 * Converts ADP to auction values for everyone
	 */
	public static void convertADP(Storage holder) {
		for (PlayerObject player : holder.players) {
			if (ManageInput.isDouble(player.info.adp)
					|| ManageInput.isInteger(player.info.adp)) {
				double conv = convertRanking(Double
						.parseDouble(player.info.adp));
				ParseRankings.finalStretch(holder, player.info.name,
						(int) conv, player.info.team, player.info.position);
			}
		}
	}

	/**
	 * The actual formula for adp and ecr conversions
	 */
	public static double convertRanking(double ranking) {
		double possVal = 78.6341 - 15.893 * Math.log(ranking);
		if (possVal < 0.0) {
			possVal = 0.0;
		}
		return possVal;
	}

	/**
	 * Builds the hashmap to make the first paa calculation easier
	 */
	public static HashMap<String, Double> initPAA1Map(Storage holder) {
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
	public static double paa1Calc(HashMap<String, Double> map,
			PlayerObject player, double discretCash) {
		double coeff = player.values.paa / map.get(player.info.position);
		double possVal = discretCash * coeff + 1.0;
		if (player.info.position.equals("RB")) {
			possVal *= 1.1;
		}
		if (player.info.position.equals("QB")) {
			possVal *= 1.05;
		}
		if (player.info.position.equals("D/ST")) {
			possVal /= 10;
		}
		if (player.info.position.equals("K")) {
			possVal /= 20;
		}
		possVal *= 1.1;
		if (possVal < 0.0) {
			possVal = 0.0;
		}
		return possVal;
	}

	/**
	 * Gets the discretionary cash for the first method
	 */
	public static double discretPAA(Roster r, int rosterSize) {
		return (200 - rosterSize) / rosterSize;
	}

	/**
	 * Gets the roster size
	 */
	public static int getRosterSize(Roster r) {
		if (r.flex != null) {
			return r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k + r.flex.op
					+ r.flex.rbwr + r.flex.rbwrte;
		}
		return r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k;
	}

	/**
	 * Gets the average paa of would be startable people
	 */
	public static double avgPAAPos(Storage holder, String position) {
		double paaTotal = 0.0;
		double paaCount = 0.0;
		for (PlayerObject player : holder.players) {
			if (player.values.paa > 0.0
					&& player.info.position.equals(position)) {
				paaTotal += player.values.paa;
				paaCount++;
			}
		}
		return paaTotal / paaCount;
	}

}
