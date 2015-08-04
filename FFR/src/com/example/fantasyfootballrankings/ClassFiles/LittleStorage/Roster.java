package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;

/**
 * Stores the roster numbers
 * 
 * @author Jeff
 * 
 */
public class Roster {
	public int teams;
	public int qbs;
	public int rbs;
	public int wrs;
	public int tes;
	public int def;
	public int k;
	public Flex flex;

	/**
	 * empty constructor for the initial set up
	 */
	public Roster() {
		teams = 0;
		qbs = 0;
		rbs = 0;
		wrs = 0;
		tes = 0;
		flex = null;
		k = 0;
		def = 0;
	}

	/**
	 * Determines if the player in question is in the league's roster settings
	 * 
	 * @param player
	 * @return
	 */
	public boolean isRostered(PlayerObject player) {
		if (player.info.position.equals("QB") && qbs == 0
				&& (flex == null || flex.op == 0)) {
			return false;
		}
		if (player.info.position.equals("RB")
				&& rbs == 0
				&& (flex == null || flex.rbwr == 0 || flex.rbwrte == 0 || flex.op == 0)) {
			return false;
		}
		if (player.info.position.equals("WR")
				&& wrs == 0
				&& (flex == null || flex.rbwr == 0 || flex.rbwrte == 0 || flex.op == 0)) {
			return false;
		}
		if (player.info.position.equals("TE") && tes == 0
				&& (flex == null || flex.op == 0 || flex.rbwrte == 0)) {
			return false;
		}
		if (player.info.position.equals("D/ST") && def == 0) {
			return false;
		}
		if (player.info.position.equals("K") && k == 0) {
			return false;
		}
		return true;
	}
}
