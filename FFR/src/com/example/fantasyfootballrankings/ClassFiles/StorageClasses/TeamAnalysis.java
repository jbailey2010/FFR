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
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

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
		String[] qb = new String[0];
		String[] rb = new String[0];
		String[] wr = new String[0];
		String[] te = new String[0];
		String[] d = new String[0];
		String[] k = new String[0];
		if (teamStr.contains(Constants.ML_QB)) {
			qb = teamStr.split(Constants.ML_QB_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}
		if (teamStr.contains(Constants.ML_RB)) {
			rb = teamStr.split(Constants.ML_RB_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}
		if (teamStr.contains(Constants.ML_WR)) {
			wr = teamStr.split(Constants.ML_WR_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}
		if (teamStr.contains(Constants.ML_TE)) {
			te = teamStr.split(Constants.ML_TE_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}
		if (teamStr.contains(Constants.DST)) {
			d = teamStr.split(Constants.ML_DEF_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}
		if (teamStr.contains(Constants.ML_K)) {
			k = teamStr.split(Constants.ML_K_HEADER)[1]
					.split(Constants.LINE_BREAK)[0].split(", ");
		}

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
		qbProjTotal = getPosProj(players, Constants.QB);
		rbProjTotal = getPosProj(players, Constants.RB);
		wrProjTotal = getPosProj(players, Constants.WR);
		teProjTotal = getPosProj(players, Constants.TE);
		dProjTotal = getPosProj(players, Constants.DST);
		kProjTotal = getPosProj(players, Constants.K);
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
		posFix.put(Constants.ML_QB, Constants.QB);
		posFix.put(Constants.ML_RB, Constants.RB);
		posFix.put(Constants.ML_WR, Constants.WR);
		posFix.put(Constants.ML_TE, Constants.TE);
		posFix.put(Constants.ML_K, Constants.K);
		String[] posSet = team.team.split(Constants.LINE_BREAK);
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
		List<PlayerObject> qbBasic = startersList(r.qbs, qb, Constants.QB);
		List<PlayerObject> rbBasic = startersList(r.rbs, rb, Constants.RB);
		List<PlayerObject> wrBasic = startersList(r.wrs, wr, Constants.WR);
		List<PlayerObject> teBasic = startersList(r.tes, te, Constants.TE);
		List<PlayerObject> dfBasic = startersList(r.def, d, Constants.DST);
		List<PlayerObject> kBasic = startersList(r.k, k, Constants.K);
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
				qbNext = getNextBest(qbConsider, ignore, qb, Constants.QB);
			}
			if (rbConsider > 0) {
				rbNext = getNextBest(rbConsider, ignore, rb, Constants.RB);
			}
			if (wrConsider > 0) {
				wrNext = getNextBest(wrConsider, ignore, wr, Constants.WR);
			}
			if (teConsider > 0) {
				teNext = getNextBest(teConsider, ignore, te, Constants.TE);
			}
			List<PlayerObject> best = getFlexOptions(qbNext, rbNext, wrNext,
					teNext, qbConsider, rbConsider, wrConsider, teConsider);
			for (PlayerObject player : best) {
				if (player.info.position.equals(Constants.QB)) {
					qbBasic.add(player);
				}
				if (player.info.position.equals(Constants.RB)) {
					rbBasic.add(player);
				}
				if (player.info.position.equals(Constants.WR)) {
					wrBasic.add(player);
				}
				if (player.info.position.equals(Constants.TE)) {
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
			if (iter.info.position.equals(Constants.QB) && qbConsider > 0) {
				qbConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals(Constants.RB) && rbConsider > 0) {
				rbConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals(Constants.WR) && wrConsider > 0) {
				wrConsider--;
				validCt--;
				topScorers.add(iter);
			} else if (iter.info.position.equals(Constants.TE) && teConsider > 0) {
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

	public String stringifyTeam() {
		String newTeam = team;
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.QB)) {
			newTeam = newTeam.replace(Constants.ML_QB_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.RB)) {
			newTeam = newTeam.replace(Constants.ML_RB_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.WR)) {
			newTeam = newTeam.replace(Constants.ML_WR_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.TE)) {
			newTeam = newTeam.replace(Constants.ML_TE_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.DST)) {
			newTeam = newTeam.replace(Constants.ML_DEF_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		if (!ImportLeague.newImport.doesLeagueAllowPosition(Constants.K)) {
			newTeam = newTeam.replace(Constants.ML_K_HEADER
					+ Constants.ML_NONE_SELECTED + Constants.LINE_BREAK, "");
		}
		return newTeam;
	}

	/**
	 * Turns the whole lineup into a string, position by position
	 */
	public String stringifyLineup() {
		StringBuilder lineup = new StringBuilder(1000);
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.QB)) {
			lineup.append(stringifyPos(qbStarters, Constants.ML_QB_SINGULAR));
		}
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.RB)) {
			lineup.append(stringifyPos(rbStarters, Constants.ML_RB_SINGULAR));
		}
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.WR)) {
			lineup.append(stringifyPos(wrStarters, Constants.ML_WR_SINGULAR));
		}
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.TE)) {
			lineup.append(stringifyPos(teStarters, Constants.ML_TE_SINGULAR));
		}
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.DST)) {
			lineup.append(stringifyPos(dStarters, Constants.DST));
		}
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.K)) {
			lineup.append(stringifyPos(kStarters, Constants.ML_K_SINGULAR));
		}
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
		DecimalFormat df = new DecimalFormat(Constants.NUMBER_FORMAT);
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
			return starters.substring(0, starters.length() - 2)
					+ Constants.LINE_BREAK;
		}
	}
}
