package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.util.HashMap;

import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

public class ParseMath {
	public static void convertPAA(Storage holder, Roster r)
	{
		int rosterSize = r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k + r.flex.op + r.flex.rbwr + r.flex.rbwrte;
		int discretCash = (200 - rosterSize)/rosterSize;
		HashMap<String, Double> zMap = new HashMap<String, Double>();
		zMap.put("QB", avgPAAPos(holder, "QB"));
		zMap.put("RB", avgPAAPos(holder, "RB"));
		zMap.put("WR", avgPAAPos(holder, "WR"));
		zMap.put("TE", avgPAAPos(holder, "TE"));
		zMap.put("D/ST", avgPAAPos(holder, "D/ST"));
		zMap.put("K", avgPAAPos(holder, "K"));
		for(PlayerObject player : holder.players){
			if(zMap.containsKey(player.info.position)){
				double coeff = player.values.paa / zMap.get(player.info.position);
				double possVal = discretCash * coeff + 1.0;
				if(player.info.position.equals("RB")){
					possVal *= 1.1;
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
				ParseRankings.finalStretch(holder, player.info.name, (int) possVal, player.info.team, player.info.position);
			}
		}
	}
	
	public static void convertPAA2(Storage holder, Roster r){
		int rosterSize = r.qbs + r.rbs + r.wrs + r.tes + r.def + r.k + r.flex.op + r.flex.rbwr + r.flex.rbwrte;
		double freeMoney = (200.0 - rosterSize) * r.teams;
		HashMap<String, Double> zMap = new HashMap<String, Double>();
		zMap.put("QB", totalPAAPos(holder, "QB")/freeMoney);
		zMap.put("RB", totalPAAPos(holder, "RB")/freeMoney);
		zMap.put("WR", totalPAAPos(holder, "WR")/freeMoney);
		zMap.put("TE", totalPAAPos(holder, "TE")/freeMoney);
		zMap.put("D/ST", totalPAAPos(holder, "D/ST")/freeMoney);
		zMap.put("K", totalPAAPos(holder, "K")/freeMoney);
		for(PlayerObject player : holder.players){
			if(zMap.containsKey(player.info.position)){
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
				ParseRankings.finalStretch(holder, player.info.name, (int) possVal, player.info.team, player.info.position);
			}
		}
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
