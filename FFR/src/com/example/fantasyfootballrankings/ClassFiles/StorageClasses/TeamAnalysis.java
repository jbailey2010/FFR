package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;

/**
 * Handles the analysis of a team, given the string input of it
 * 
 * @author Jeff
 * 
 */
public class TeamAnalysis {
	public String team;
	public List<PlayerObject> players = new ArrayList<PlayerObject>();
	public String teamName;
	Storage holder;
	Context cont;
	public double totalProj;
	public double totalPAA;
	public double qbProjTotal;
	public double rbProjTotal;
	public double wrProjTotal;
	public double teProjTotal;
	public double dProjTotal;
	public double kProjTotal;
	public List<PlayerObject> qbStarters;
	public List<PlayerObject> rbStarters;
	public List<PlayerObject> wrStarters;
	public List<PlayerObject> teStarters;
	public List<PlayerObject> dStarters;
	public List<PlayerObject> kStarters;
	public Roster r;

	/**
	 * Does all of the string parsing
	 * 
	 * @param teamStr
	 * @param hold
	 * @param c
	 */
	public TeamAnalysis(String name, String teamStr, Storage hold, Context c,
			Roster roster) {
		r = roster;
		teamName = name;
		team = teamStr;
		holder = hold;
		cont = c;

		// Break the input blob into positional arrays for easier management
		String[] qb = teamStr.split("Quarterbacks: ")[1].split("\n")[0]
				.split(", ");
		String[] rb = teamStr.split("Running Backs: ")[1].split("\n")[0]
				.split(", ");
		String[] wr = teamStr.split("Wide Receivers: ")[1].split("\n")[0]
				.split(", ");
		String[] te = teamStr.split("Tight Ends: ")[1].split("\n")[0]
				.split(", ");
		String[] d = teamStr.split("D/ST: ")[1].split("\n")[0].split(", ");
		String[] k = teamStr.split("Kickers: ")[1].split("\n")[0].split(", ");

		// Break out the starters and build the team list of players
		manageStarters(qb, rb, wr, te, d, k);
		populateTeamsList(this);

		// Update the projections for all by each position
		populateTotals(qb);
		populateTotals(rb);
		populateTotals(wr);
		populateTotals(te);
		populateTotals(d);
		populateTotals(k);

		// Set the individual positional projections
		qbProjTotal = getPosProj(players, "QB");
		rbProjTotal = getPosProj(players, "RB");
		wrProjTotal = getPosProj(players, "WR");
		teProjTotal = getPosProj(players, "TE");
		dProjTotal = getPosProj(players, "D/ST");
		kProjTotal = getPosProj(players, "K");
	}

	/**
	 * A dummy constructor to handle generic queries without needing storage of
	 * data
	 */
	public TeamAnalysis() {

	}

	/**
	 * Populates each team's player object list
	 */
	public void populateTeamsList(TeamAnalysis team) {
		Map<String, String> posFix = new HashMap<String, String>();
		posFix.put("Quarterbacks", "QB");
		posFix.put("Running Backs", "RB");
		posFix.put("Wide Receivers", "WR");
		posFix.put("Tight Ends", "TE");
		posFix.put("Kickers", "K");
		String[] posSet = team.team.split("\n");
		for (String pos : posSet) {
			if (!pos.contains("None ") && pos.contains(":")) {

				String position = posFix.get(pos.split(": ")[0]);
				String[] playerList = pos.split(": ")[1].split(", ");
				for (String name : playerList) {
					for (PlayerObject player : holder.players) {
						if (player.info.name.equals(name)
								&& player.info.position.equals(position)) {
							team.players.add(player);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the paa of all of the players at each position (given)
	 * 
	 * @param pos
	 * @return
	 */
	private void populateTotals(String[] pos) {
		for (int i = 0; i < pos.length; i++) {
			if (this.holder.parsedPlayers.contains(pos[i])) {
				for (PlayerObject player : this.holder.players) {
					if (player.info.name.equals(pos[i])) {
						if (player.values.paa > 0 || player.values.paa < 0) {
							totalProj += player.values.points;
							totalPAA += player.values.paa;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the starting lineup for the team, flex and standard
	 */
	public void manageStarters(String[] qb, String[] rb, String[] wr,
			String[] te, String[] d, String[] k) {
		HashSet<PlayerObject> ignore = new HashSet<PlayerObject>();
		List<PlayerObject> qbBasic = startersList(r.qbs, qb, "QB");
		List<PlayerObject> rbBasic = startersList(r.rbs, rb, "RB");
		List<PlayerObject> wrBasic = startersList(r.wrs, wr, "WR");
		List<PlayerObject> teBasic = startersList(r.tes, te, "TE");
		List<PlayerObject> dfBasic = startersList(r.def, d, "D/ST");
		List<PlayerObject> kBasic = startersList(r.k, k, "K");
		ignore.addAll(qbBasic);
		ignore.addAll(rbBasic);
		ignore.addAll(wrBasic);
		ignore.addAll(teBasic);
		ignore.addAll(dfBasic);
		ignore.addAll(kBasic);
		if (r.flex != null
				&& (r.flex.rbwr > 0 || r.flex.rbwrte > 0 || r.flex.op > 0)) {
			int qbConsider = 0;
			int rbConsider = 0;
			int wrConsider = 0;
			int teConsider = 0;
			if (r.flex.rbwr > 0) {
				rbConsider++;
				wrConsider++;
			}
			if (r.flex.rbwrte > 0) {
				rbConsider++;
				wrConsider++;
				teConsider++;
			}
			if (r.flex.op > 0) {
				qbConsider++;
				rbConsider++;
				wrConsider++;
				teConsider++;
			}
			List<PlayerObject> qbNext = new ArrayList<PlayerObject>();
			List<PlayerObject> rbNext = new ArrayList<PlayerObject>();
			List<PlayerObject> wrNext = new ArrayList<PlayerObject>();
			List<PlayerObject> teNext = new ArrayList<PlayerObject>();
			if (qbConsider > 0) {
				qbNext = getNextBest(qbConsider, ignore, qb, "QB");
			}
			if (rbConsider > 0) {
				rbNext = getNextBest(rbConsider, ignore, rb, "RB");
			}
			if (wrConsider > 0) {
				wrNext = getNextBest(wrConsider, ignore, wr, "WR");
			}
			if (teConsider > 0) {
				teNext = getNextBest(teConsider, ignore, te, "TE");
			}
			List<PlayerObject> best = getFlexOptions(qbNext, rbNext, wrNext,
					teNext, qbConsider, rbConsider, wrConsider, teConsider);
			for (PlayerObject player : best) {
				if (player.info.position.equals("QB")) {
					qbBasic.add(player);
				}
				if (player.info.position.equals("RB")) {
					rbBasic.add(player);
				}
				if (player.info.position.equals("WR")) {
					wrBasic.add(player);
				}
				if (player.info.position.equals("TE")) {
					teBasic.add(player);
				}
			}
		}
		qbStarters = qbBasic;
		rbStarters = rbBasic;
		wrStarters = wrBasic;
		teStarters = teBasic;
		dStarters = dfBasic;
		kStarters = kBasic;
	}

	/**
	 * Looks through the flex options to find the highest scorers
	 */
	public List<PlayerObject> getFlexOptions(List<PlayerObject> qbNext,
			List<PlayerObject> rbNext, List<PlayerObject> wrNext,
			List<PlayerObject> teNext, int qbConsider, int rbConsider,
			int wrConsider, int teConsider) {
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(
				300, new Comparator<PlayerObject>() {
					@Override
					public int compare(PlayerObject a, PlayerObject b) {
						if (a.values.points > b.values.points) {
							return -1;
						}
						if (a.values.points < b.values.points) {
							return 1;
						}
						return 0;
					}
				});
		inter.addAll(qbNext);
		inter.addAll(rbNext);
		inter.addAll(wrNext);
		inter.addAll(teNext);
		int validCt = r.flex.op + r.flex.rbwr + r.flex.rbwrte;
		List<PlayerObject> topScorers = new ArrayList<PlayerObject>();
		while (validCt > 0 && inter.size() > 0) {
			PlayerObject iter = inter.poll();
			if (iter.info.position.equals("QB") && qbConsider > 0) {
				qbConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals("RB") && rbConsider > 0) {
				rbConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals("WR") && wrConsider > 0) {
				wrConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals("TE") && teConsider > 0) {
				teConsider--;
				validCt--;
				topScorers.add(iter);
			}
		}
		return topScorers;
	}

	/**
	 * Gets the valid players who aren't yet in the starting roles to consider
	 * for flex roles
	 */
	public List<PlayerObject> getNextBest(int limit,
			HashSet<PlayerObject> ignore, String[] pos, String posStr) {
		PriorityQueue<PlayerObject> posSort = new PriorityQueue<PlayerObject>(
				300, new Comparator<PlayerObject>() {
					@Override
					public int compare(PlayerObject a, PlayerObject b) {
						if (a.values.points > b.values.points) {
							return -1;
						}
						if (a.values.points < b.values.points) {
							return 1;
						}
						return 0;
					}
				});
		int counter = 0;
		for (String playerName : pos) {
			for (PlayerObject player : holder.players) {
				if (player.info.name.equals(playerName)
						&& player.info.position.equals(posStr)) {
					posSort.add(player);
					break;
				}
			}
		}
		List<PlayerObject> posList = new ArrayList<PlayerObject>();
		while (!posSort.isEmpty()) {
			if ((counter) >= limit) {
				break;
			}
			PlayerObject nextBest = posSort.poll();
			if (!ignore.contains(nextBest)) {
				posList.add(nextBest);
				counter++;
			}
		}
		return posList;
	}

	/**
	 * Finds the basic starters for a given roster/team, NOT flexes
	 */
	public List<PlayerObject> startersList(int limit, String[] pos,
			String posStr) {
		PriorityQueue<PlayerObject> posSort = new PriorityQueue<PlayerObject>(
				300, new Comparator<PlayerObject>() {
					@Override
					public int compare(PlayerObject a, PlayerObject b) {
						if (a.values.points > b.values.points) {
							return -1;
						}
						if (a.values.points < b.values.points) {
							return 1;
						}
						return 0;
					}
				});
		int counter = 0;
		for (String playerName : pos) {
			for (PlayerObject player : holder.players) {
				if (player.info.name.equals(playerName)
						&& player.info.position.equals(posStr)) {
					posSort.add(player);
					break;
				}
			}
		}
		List<PlayerObject> posList = new ArrayList<PlayerObject>();
		while (!posSort.isEmpty()) {
			if ((counter) >= limit) {
				break;
			}
			PlayerObject nextBest = posSort.poll();
			posList.add(nextBest);
			counter++;
		}
		return posList;
	}

	/**
	 * Turns the whole lineup into a string, position by position
	 */
	public String stringifyLineup() {
		StringBuilder lineup = new StringBuilder(1000);
		lineup.append(stringifyPos(qbStarters, "Quarterback"));
		lineup.append(stringifyPos(rbStarters, "Running Back"));
		lineup.append(stringifyPos(wrStarters, "Wide Receiver"));
		lineup.append(stringifyPos(teStarters, "Tight End"));
		lineup.append(stringifyPos(dStarters, "D/ST"));
		lineup.append(stringifyPos(kStarters, "Kicker"));
		return lineup.toString();
	}

	/**
	 * Gets the total starters paa
	 */
	public double getStarterPAA() {
		return getPosPAA(qbStarters) + getPosPAA(rbStarters)
				+ getPosPAA(wrStarters) + getPosPAA(teStarters)
				+ getPosPAA(dStarters) + getPosPAA(kStarters);
	}

	/**
	 * Gets the paa of an individual position
	 */
	public double getPosPAA(List<PlayerObject> players) {
		DecimalFormat df = new DecimalFormat("#.##");
		double total = 0.0;
		for (PlayerObject player : players) {
			total += player.values.paa;
		}
		return Double.valueOf(df.format(total));
	}

	/**
	 * Gets the total starters projection of a team
	 */
	public double getStarterProj() {
		return getProjSum(qbStarters) + getProjSum(rbStarters)
				+ getProjSum(wrStarters) + getProjSum(teStarters)
				+ getProjSum(dStarters) + getProjSum(kStarters);
	}

	/**
	 * Gets the projection of an individual position
	 * 
	 * @param players
	 * @return
	 */
	public double getProjSum(List<PlayerObject> players) {
		double total = 0.0;
		for (PlayerObject player : players) {
			total += player.values.points;
		}
		return total;
	}

	public double getPosProj(List<PlayerObject> players, String position) {
		double total = 0.0;
		for (PlayerObject player : players) {
			if (player.info.position.equals(position)) {
				total += player.values.points;
			}
		}
		return total;
	}

	/**
	 * Puts the position's list of players in a string format
	 */
	public String stringifyPos(List<PlayerObject> players, String pos) {
		StringBuilder lineup = new StringBuilder(1000);
		if (players.size() == 0) {
			return pos + "s: N/A\n";
		} else {
			lineup.append(pos + "s: ");
			for (PlayerObject player : players) {
				lineup.append(player.info.name + ", ");
			}
			String starters = lineup.toString();
			return starters.substring(0, starters.length() - 2) + "\n";
		}
	}
}
