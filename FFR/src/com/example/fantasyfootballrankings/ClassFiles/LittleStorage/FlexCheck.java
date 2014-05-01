package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;

/**
 * A small class to handle the flex math in the restructured optimal lineup
 * @author user
 *
 */
public class FlexCheck {
	private List<PlayerObject> players;
	private HashMap<String, String> namePosToFlex;
	
	/**
	 * Sets up the list of players and hash of player to flex type given the list of mirrored indices of each
	 */
	public FlexCheck(List<PlayerObject> playersInput, List<String> posInput){
		players = new ArrayList<PlayerObject>();
		namePosToFlex = new HashMap<String, String>();
		for(int i = 0; i < playersInput.size(); i++){
			PlayerObject player = playersInput.get(i);
			players.add(player);
			namePosToFlex.put(player.info.name + player.info.position, posInput.get(i));
		}
	}
	
	/**
	 * Given a player, gets the flex they were added for and returns it
	 */
	public String getFlex(PlayerObject player){
		return namePosToFlex.get(player.info.name + player.info.position);
	}
	
	/**
	 * Iterates on the list of players to get the total set of points this flex set can get
	 */
	public double getTotal(){
		double total = 0.0;
		for(PlayerObject player : players){
			total += player.values.points;
		}
		return total;
	}
}
