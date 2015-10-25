package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraft;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseInjuries;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseStats;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.Pages.Home;

import FileIO.ReadFromFile;
import android.content.Context;

/**
 * Handles operations done on all of the players These are to be done all at
 * once, once all of the rankings are fetched
 * 
 * @author Jeff
 * 
 */

public class HighLevel {

	/**
	 * Sets a contract status for players on the fftoolbox list
	 * 
	 * @param holder
	 * @throws IOException
	 */
	public static void setContractStatus(Storage holder) throws IOException {
		HashMap<String, String> cs = new HashMap<String, String>();
		List<String> td = HandleBasicQueries
				.handleLists(
						"http://www.kffl.com/static/nfl/features/freeagents/fa.php?option=All&y=2015",
						"td");
		for (int i = 20; i < td.size(); i += 5) {
			String pos = td.get(i);
			if (pos.equals("FB")) {
				pos = "RB";
			} else if (pos.equals("PK")) {
				pos = "K";
			}
			String name = td.get(i + 1);
			String status = td.get(i + 2);
			if (!name.equals("Player") && !status.contains("Signed")
					&& !status.contains("signed")) {
				cs.put(pos + "/" + name, "In a contract year");
			}
		}
		for (PlayerObject player : holder.players) {
			if (cs.containsKey(player.info.position + "/" + player.info.name)) {
				player.info.contractStatus = cs.get(player.info.position + "/"
						+ player.info.name);
			}
		}
	}

	/**
	 * A function that gets the strength of schedule for each team and specific
	 * positions per.
	 * 
	 * @param holder
	 * @throws IOException
	 */
	public static void getSOS(Storage holder) throws IOException {
		List<String> allArr = HandleBasicQueries.handleLists(
				"http://www.fftoolbox.com/football/strength_of_schedule.cfm",
				"tr.c");
		String[][] team = new String[allArr.size()][];
		HashMap<String, Integer> sos = new HashMap<String, Integer>();
		for (int i = 0; i < allArr.size(); i++) {
			team[i] = ManageInput.tokenize(allArr.get(i), ' ', 1);
			String keyBase = ParseRankings.fixTeams(team[i][0]) + ",";
			sos.put(keyBase + "QB", Integer.parseInt(cleanRanking(team[i][1])));
			sos.put(keyBase + "RB", Integer.parseInt(cleanRanking(team[i][2])));
			sos.put(keyBase + "WR", Integer.parseInt(cleanRanking(team[i][3])));
			sos.put(keyBase + "TE", Integer.parseInt(cleanRanking(team[i][4])));
			sos.put(keyBase + "K", Integer.parseInt(cleanRanking(team[i][5])));
			sos.put(keyBase + "D/ST",
					Integer.parseInt(cleanRanking(team[i][6])));
		}
		holder.sos = sos;
	}

	public static String cleanRanking(String input) {
		return input.replaceAll("rd", "").replaceAll("st", "")
				.replaceAll("nd", "").replaceAll("th", "");
	}

	/**
	 * Sets the team data for players
	 * 
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void setTeamInfo(Storage holder, Context cont)
			throws IOException {
		// Fetch the draft data
		HashMap<String, String> drafts = ParseDraft.parseTeamDraft();
		HashMap<String, String> gpas = ParseDraft.parseTeamDraftGPA();
		Set<String> teams = drafts.keySet();
		for (String team : teams) {
			holder.draftClasses.put(team, gpas.get(team) + drafts.get(team));
		}
		// Parse free agency data
		holder.fa = ParseFreeAgents.parseFA();
	}

	/**
	 * Parse player specific data that aren't stats
	 * 
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void parseSpecificData(Storage holder, Context cont)
			throws IOException {
		HashMap<String, String> injuries = ParseInjuries.parseRotoInjuries();
		HashMap<String, String> byes = ParseFFTB.parseByeWeeks();
		holder.bye = byes;
		for (PlayerObject player : holder.players) {
			if (!player.info.position.equals("K")
					&& !player.info.position.equals("D/ST")) {
				if (injuries.containsKey(player.info.name + "/"
						+ player.info.position)) {
					player.injuryStatus = injuries.get(player.info.name + "/"
							+ player.info.position);
				}
			}
		}
	}

	/**
	 * Sets the stats of a player
	 * 
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void setStats(Storage holder, Context cont)
			throws IOException {
		try {
			// Fetch the stats
			Map<String, String> qbs = ParseStats.parseQBStats();
			Set<String> qbKeys = qbs.keySet();
			Map<String, String> rbs = ParseStats.parseRBStats();
			Set<String> rbKeys = rbs.keySet();
			Map<String, String> wrs = ParseStats.parseWRStats();
			Set<String> wrKeys = wrs.keySet();
			Map<String, String> tes = ParseStats.parseTEStats();
			Set<String> teKeys = tes.keySet();
			for (PlayerObject player : holder.players) {
				if (!player.info.position.equals("K")
						&& !player.info.position.equals("D/ST")) {
					// else if testname in keyset
					String[] name = player.info.name.split(" ");
					String testName = name[0].charAt(0) + " " + name[1];
					testName = testName.toLowerCase();
					if (player.info.position.equals("QB")) {
						if (qbs.containsKey(testName + "/" + player.info.team)) {
							player.stats = qbs.get(testName + "/"
									+ player.info.team);
						} else if (player.info.team.length() < 2) {
							for (String key : qbKeys) {
								if (key.contains(testName)) {
									player.stats = qbs.get(key);
									break;
								}
							}
						} else {
							int found = 0;
							String statHolder = "";
							for (String key : qbKeys) {
								if (key.contains(testName)) {
									found++;
									statHolder = qbs.get(key);
								}
							}
							if (found == 1) {
								player.stats = statHolder;
							}
						}
					} else if (player.info.position.equals("RB")) {
						if (rbs.containsKey(testName + "/" + player.info.team)) {
							player.stats = rbs.get(testName + "/"
									+ player.info.team);
						} else if (player.info.team.length() < 2) {
							for (String key : rbKeys) {
								if (key.contains(testName)) {
									player.stats = rbs.get(key);
									break;
								}
							}
						} else {
							int found = 0;
							String statHolder = "";
							for (String key : rbKeys) {
								if (key.contains(testName)) {
									found++;
									statHolder = rbs.get(key);
								}
							}
							if (found == 1) {
								player.stats = statHolder;
							}
						}
					} else if (player.info.position.equals("WR")) {
						if (wrs.containsKey(testName + "/" + player.info.team)) {
							player.stats = wrs.get(testName + "/"
									+ player.info.team);
						} else if (player.info.team.length() < 2) {
							for (String key : wrKeys) {
								if (key.contains(testName)) {
									player.stats = wrs.get(key);
									break;
								}
							}
						} else {
							int found = 0;
							String statHolder = "";
							for (String key : wrKeys) {
								if (key.contains(testName)) {
									found++;
									statHolder = wrs.get(key);
								}
							}
							if (found == 1) {
								player.stats = statHolder;
							}
						}
					} else if (player.info.position.equals("TE")) {
						if (tes.containsKey(testName + "/" + player.info.team)) {
							player.stats = tes.get(testName + "/"
									+ player.info.team);
						} else if (player.info.team.length() < 2) {
							for (String key : teKeys) {
								if (key.contains(testName)) {

									player.stats = tes.get(key);
									break;
								}
							}
						} else {
							int found = 0;
							String statHolder = "";
							for (String key : teKeys) {
								if (key.contains(testName)) {
									found++;
									statHolder = tes.get(key);
								}
							}
							if (found == 1) {
								player.stats = statHolder;
							}
						}
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e1) {

		} catch (NumberFormatException e2) {

		}
	}

	/**
	 * Calls the specific parsers and sets the projections
	 */
	public static void projPointsWrapper(Storage holder, Context cont)
			throws IOException {
		HashMap<String, Double> points = new HashMap<String, Double>();
		Scoring scoring = ReadFromFile.readScoring(cont);
		String suffix = "?year=" + Home.yearKey;
		qbProj("http://www.fantasypros.com/nfl/projections/qb.php" + suffix,
				points, scoring, "QB");
		rbProj("http://www.fantasypros.com/nfl/projections/rb.php" + suffix,
				points, scoring, "RB");
		wrProj("http://www.fantasypros.com/nfl/projections/wr.php" + suffix,
				points, scoring, "WR");
		teProj("http://www.fantasypros.com/nfl/projections/te.php" + suffix,
				points, scoring, "TE");
		kProj("http://www.fantasypros.com/nfl/projections/k.php" + suffix,
				points, "K");
		try {
			defProjWeekly(points, "D/ST");
		} catch (IOException e) {

		}
		for (PlayerObject player : holder.players) {
			if (points.containsKey(player.info.name + "/" + player.info.team
					+ "/" + player.info.position)) {
				player.values.points = points.get(player.info.name + "/"
						+ player.info.team + "/" + player.info.position);
			} else {
				player.values.points = 0;
			}
		}
	}

	/**
	 * Gets the qb projections
	 */
	public static void qbProj(String url, HashMap<String, Double> points,
			Scoring scoring, String pos) throws IOException {
		DecimalFormat df = new DecimalFormat("#.##");
		List<String> td = HandleBasicQueries.handleLists(url, "td");

		int min = 0;
		ParseRankings.handleHashes();

		for (int i = 0; i < td.size(); i++) {
			if (td.get(i).contains("MISC")) {
				min = i + 1;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 11) {
			double proj = 0;
			String name = "";
			String[] nameSet = td.get(i).split(" ");
			for (int j = 0; j < nameSet.length - 1; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixNames(name.substring(0, name.length() - 1));
			String team = ParseRankings.fixTeams(nameSet[nameSet.length - 1]);
			double yards = Double.parseDouble(td.get(i + 3).replace(",", ""));
			double tdRush = Double.parseDouble(td.get(i + 4));
			double ints = Double.parseDouble(td.get(i + 5));
			double rushYards = Double.parseDouble(td.get(i + 7));
			double rushTD = Double.parseDouble(td.get(i + 8));
			double fumbles = Double.parseDouble(td.get(i + 9));
			proj += (yards / (scoring.passYards));
			proj -= ints * scoring.interception;
			proj += tdRush * scoring.passTD;
			proj += (rushYards / (scoring.rushYards));
			proj += rushTD * scoring.rushTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}

	/**
	 * Gets the running back projections
	 */
	public static void rbProj(String url, HashMap<String, Double> points,
			Scoring scoring, String pos) throws IOException {
		DecimalFormat df = new DecimalFormat("#.##");
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		ParseRankings.handleHashes();
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i).contains("MISC")) {
				min = i + 1;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 9) {
			double proj = 0;
			String name = "";
			String[] nameSet = td.get(i).split(" ");
			for (int j = 0; j < nameSet.length - 1; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixNames(name.substring(0, name.length() - 1));
			String team = ParseRankings.fixTeams(nameSet[nameSet.length - 1]);
			double rushYards = Double.parseDouble(td.get(i + 2)
					.replace(",", ""));
			double rushTD = Double.parseDouble(td.get(i + 3));
			double catches = Double.parseDouble(td.get(i + 4));
			double recYards = Double
					.parseDouble(td.get(i + 5).replace(",", ""));
			double recTD = Double.parseDouble(td.get(i + 6));
			double fumbles = Double.parseDouble(td.get(i + 7));
			proj += (rushYards / (scoring.rushYards));
			proj += rushTD * scoring.rushTD;
			proj += catches * scoring.catches;
			proj += (recYards / (scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}

	/**
	 * Gets the wide receiver projections
	 */
	public static void wrProj(String url, HashMap<String, Double> points,
			Scoring scoring, String pos) throws IOException {
		DecimalFormat df = new DecimalFormat("#.##");
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		ParseRankings.handleHashes();
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i).contains("MISC")) {
				min = i + 1;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 9) {
			double proj = 0;
			String name = "";
			String[] nameSet = td.get(i).split(" ");
			for (int j = 0; j < nameSet.length - 1; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixNames(name.substring(0, name.length() - 1));
			String team = ParseRankings.fixTeams(nameSet[nameSet.length - 1]);
			double rushYards = Double.parseDouble(td.get(i + 2)
					.replace(",", ""));
			double rushTD = Double.parseDouble(td.get(i + 3));
			double catches = Double.parseDouble(td.get(i + 4));
			double recYards = Double
					.parseDouble(td.get(i + 5).replace(",", ""));
			double recTD = Double.parseDouble(td.get(i + 6));
			double fumbles = Double.parseDouble(td.get(i + 7));
			proj += (rushYards / (scoring.rushYards));
			proj += rushTD * scoring.rushTD;
			proj += catches * scoring.catches;
			proj += (recYards / (scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}

	/**
	 * Gets the tight end projections
	 */
	public static void teProj(String url, HashMap<String, Double> points,
			Scoring scoring, String pos) throws IOException {
		DecimalFormat df = new DecimalFormat("#.##");
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		ParseRankings.handleHashes();

		for (int i = 0; i < td.size(); i++) {
			if (td.get(i).contains("MISC")) {
				min = i + 1;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 6) {
			double proj = 0;
			String name = "";
			String[] nameSet = td.get(i).split(" ");
			for (int j = 0; j < nameSet.length - 1; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixNames(name.substring(0, name.length() - 1));
			String team = ParseRankings.fixTeams(nameSet[nameSet.length - 1]);
			double catches = Double.parseDouble(td.get(i + 1).replace(",", ""));
			double recTD = Double.parseDouble(td.get(i + 3));
			double recYards = Double
					.parseDouble(td.get(i + 2).replace(",", ""));
			double fumbles = Double.parseDouble(td.get(i + 4));
			proj += catches * scoring.catches;
			proj += (recYards / (scoring.recYards));
			proj += recTD * scoring.recTD;
			proj -= fumbles * scoring.fumble;
			proj = Double.parseDouble(df.format(proj));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}

	/**
	 * Gets the kicker projections
	 */
	public static void kProj(String url, HashMap<String, Double> points,
			String pos) throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		ParseRankings.handleHashes();

		for (int i = 0; i < td.size(); i++) {
			if (ManageInput.isDouble(td.get(i + 1))) {
				min = i;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 5) {
			double proj = 0;
			String name = "";
			String[] nameSet = td.get(i).split(" ");
			for (int j = 0; j < nameSet.length - 1; j++) {
				name += nameSet[j] + " ";
			}
			name = ParseRankings.fixNames(name.substring(0, name.length() - 1));
			String team = ParseRankings.fixTeams(nameSet[nameSet.length - 1]);
			proj = Double.parseDouble(td.get(i + 4));
			points.put(name + "/" + team + "/" + pos, proj);
		}
	}

	/**
	 * Handles the weekly defensive projections
	 * 
	 * @param points
	 * @param pos
	 * @throws IOException
	 */
	public static void defProjWeekly(HashMap<String, Double> points, String pos)
			throws IOException {
		List<String> td = HandleBasicQueries
				.handleLists(
				"http://www.fftoolbox.com/football/" + Home.yearKey
						+ "/weeklycheatsheets.cfm?player_pos=DEF",
						"table.grid td");
		boolean hasWeek = false;
		boolean hasWill = false;
		boolean notYetDone = false;
		for (String elem : td) {
			if (elem.contains("Week")) {
				hasWeek = true;
				break;
			}
		}
		Document doc = Jsoup
				.connect(
				"http://www.fftoolbox.com/football/" + Home.yearKey
						+ "/weeklycheatsheets.cfm?player_pos=DEF")
				.get();
		if (doc.html().contains("will be up")) {
			hasWill = true;
		} else if (doc.html().contains("for this position yet")) {
			notYetDone = true;
		}

		if (td.size() < 20 && !hasWeek && !hasWill && !notYetDone) {
			defProjAnnual(points, pos);
		} else {
			for (int i = 0; i < td.size(); i += 5) {
				String teamName = ParseRankings.fixDefenses(td.get(i + 1));
				String team = ParseRankings.fixTeams(td.get(i + 2));
				double proj = Double.valueOf(td.get(i + 4));
				points.put(teamName + "/" + team + "/" + pos, proj);
			}
		}
	}

	/**
	 * Handles the defensive projection parsing on an annual basis
	 * 
	 * @param points
	 * @param pos
	 * @throws IOException
	 */
	public static void defProjAnnual(HashMap<String, Double> points, String pos)
			throws IOException {
		List<String> td = HandleBasicQueries
				.handleLists(
				"http://www.fftoolbox.com/football/" + Home.yearKey
						+ "/cheatsheets.cfm?player_pos=DEF",
						"table.grid td");
		try {
			for (int i = 0; i < td.size(); i += 5) {
				String teamName = ParseRankings.fixDefenses(ParseRankings
						.fixTeams(td.get(i + 2)));
				String team = ParseRankings.fixTeams(td.get(i + 2));
				double proj = Double.valueOf(td.get(i + 4));
				points.put(teamName + "/" + team + "/" + pos, proj);
			}
		} catch (NumberFormatException e) {
			System.out.println("There was an error with defproj");
		}
	}

	/**
	 * Calls the parser and gets the functionsn
	 * 
	 * @param cont
	 */
	public static void parseECRWrapper(Storage holder, Context cont)
			throws IOException {
		HashMap<String, Double> ecr = new HashMap<String, Double>();
		HashMap<String, Double> risk = new HashMap<String, Double>();
		HashMap<String, String> adp = new HashMap<String, String>();
		if (!holder.isRegularSeason) {
			String url = "http://www.fantasypros.com/nfl/rankings/consensus-cheatsheets.php";
			int limit = 9;
			if (ReadFromFile.readScoring(cont).catches == 1) {
				url = "http://www.fantasypros.com/nfl/rankings/ppr-cheatsheets.php";
				limit = 9;
			}
			parseECRWorker(url, holder, ecr, risk, adp, limit);
		} else {
			StringBuilder urlBase = new StringBuilder(100);
			urlBase.append("http://www.fantasypros.com/nfl/rankings/");
			String url = urlBase.toString();
			if (ReadFromFile.readScoring(cont).catches > 0) {
				urlBase.append("ppr-");
			}
			String urlRec = urlBase.toString();
			parseECRWeekly(url + "qb.php", holder, ecr, risk, adp, "QB");
			parseECRWeekly(urlRec + "rb.php", holder, ecr, risk, adp, "RB");
			parseECRWeekly(urlRec + "wr.php", holder, ecr, risk, adp, "WR");
			parseECRWeekly(urlRec + "te.php", holder, ecr, risk, adp, "TE");
			parseECRWeekly(url + "dst.php", holder, ecr, risk, adp, "D/ST");
			parseECRWeekly(url + "k.php", holder, ecr, risk, adp, "K");
		}
		for (PlayerObject player : holder.players) {
			if (ecr.containsKey(player.info.name + player.info.position)) {
				player.values.ecr = ecr.get(player.info.name
						+ player.info.position);
				player.risk = risk.get(player.info.name + player.info.position);
			}
			if (holder.isRegularSeason && player.values.points > 0) {
				if (adp.containsKey(player.info.team)) {
					player.info.adp = adp.get(player.info.team);
				}
			} else if (holder.isRegularSeason && player.values.points == 0) {
				player.info.adp = "Bye Week";
				player.values.ecr = -1.0;
			} else {
				if (adp.containsKey(player.info.name + player.info.position)) {
					player.info.adp = (adp.get(player.info.name
							+ player.info.position));
				}
			}

		}
	}

	/**
	 * Similar to the ecr parser, but handles the minor differences in the
	 * weekly set
	 * 
	 * @param pos
	 */
	public static void parseECRWeekly(String url, Storage holder,
			HashMap<String, Double> ecr, HashMap<String, Double> risk,
			HashMap<String, String> adp, String pos) throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i + 1).split(" ").length > 2
					&& ManageInput.isInteger(td.get(i))) {
				min = i;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 7) {
			// Odd case where 50 players in the size changes
			if (ManageInput.isInteger(td.get(i + 1))) {
				i++;
			}
			String name = "";
			String team = "";
			if (pos.equals("D/ST")) {
				team = ParseRankings.fixTeams(td.get(i + 1).split(" \\(")[0]);
				name = ParseRankings.fixDefenses(team);
			} else {
				String[] nameArr = td.get(i + 1).split(", ")[0].split(" ");
				// Trim of the team from the row. If they're injured, an extra
				// letter is there, so check
				int trimLength = 1;
				if (nameArr[nameArr.length - 1].length() == 1) {
					trimLength = 2;
				}
				for (int namePieceIndex = 0; namePieceIndex < nameArr.length
						- trimLength; namePieceIndex++) {
					name += nameArr[namePieceIndex] + " ";
				}
				name = name.substring(0, name.length() - 1);
				name = ParseRankings.fixNames(name);
				team = ParseRankings.fixTeams(nameArr[nameArr.length - 1]);
			}

			double ecrVal = Double.parseDouble(td.get(i + 5));
			double riskVal = Double.parseDouble(td.get(i + 6));
			if (!adp.containsKey(team) && !team.contains("FA")) {
				String wholeSet = td.get(i + 2);
				String opp = "Bye Week";
				if (wholeSet.contains("vs")) {
					opp = ParseRankings.fixTeams(wholeSet.split("vs. ")[1]);
				} else {
					if (wholeSet.contains("at ")) {
						opp = ParseRankings.fixTeams(wholeSet.split("at ")[1]);
					}
				}
				adp.put(team, opp);
			}
			ecr.put(name + pos, ecrVal);
			risk.put(name + pos, riskVal);
		}
	}

	/**
	 * Gets the ECR Data for players
	 */
	public static void parseECRWorker(String url, Storage holder,
			HashMap<String, Double> ecr, HashMap<String, Double> risk,
			HashMap<String, String> adp, int loopIter) throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		String adpUrl = "http://www.fantasypros.com/nfl/adp/overall.php";
		int loopIterAdp = 10;
		if (url.contains("ppr")) {
			adpUrl = "http://www.fantasypros.com/nfl/adp/ppr-overall.php";
			loopIterAdp = 8;
		}
		parseADPWorker(holder, adp, adpUrl, loopIterAdp);
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i + 1).contains("QB") || td.get(i + 1).contains("RB")
					|| td.get(i + 1).contains("WR")
					|| td.get(i + 1).contains("TE")) {
				min = i;
				break;
			}
		}
		for (int i = min; i < td.size(); i += loopIter) {
			String name = ParseRankings.fixNames(ParseRankings.fixDefenses(td
					.get(i).split(" \\(")[0].split(", ")[0]));
			double ecrVal = Double.parseDouble(td.get(i + 4));
			double riskVal = Double.parseDouble(td.get(i + 5));
			String posInd = td.get(i + 1).replaceAll("(\\d+,\\d+)|\\d+", "")
					.replaceAll("DST", "D/ST");
			ecr.put(name + posInd, ecrVal);
			risk.put(name + posInd, riskVal);
		}
	}

	public static void parseADPWorker(Storage holder,
			HashMap<String, String> adp, String adpUrl, int loopIterAdp)
			throws IOException {
		List<String> td = HandleBasicQueries.handleLists(adpUrl, "td");
		int min = 0;
		try {
			for (int i = 0; i < td.size(); i++) {

				if (td.get(i + 1).contains("QB")
						|| td.get(i + 1).contains("RB")
						|| td.get(i + 1).contains("WR")
						|| td.get(i + 1).contains("TE")) {
					min = i;
					break;
				}
			}
			for (int i = min; i < td.size(); i += loopIterAdp) {
				String name = ParseRankings
						.fixNames(ParseRankings.fixDefenses(td.get(i).split(
								" \\(")[0].split(", ")[0]));
				if (i + 6 >= td.size()) {
					break;
				}
				String adpStr = td.get(i + 6);
				String posInd = td.get(i + 1)
						.replaceAll("(\\d+,\\d+)|\\d+", "")
						.replaceAll("DST", "D/ST");
				adp.put(name + posInd, adpStr);
			}
		} catch (ArrayIndexOutOfBoundsException notUp) {
			return;
		}
	}

	/**
	 * Calls the worker thread with the appropriate URL then sets it to the
	 * players' storage
	 */
	public static void getROSRankingsWrapper(Storage holder, Context cont)
			throws IOException {
		HashMap<String, Integer> rankings = new HashMap<String, Integer>();
		parseROSRankingsWorker(
				"http://www.fantasypros.com/nfl/rankings/ros-qb.php", "QB",
				rankings);
		Scoring s = ReadFromFile.readScoring(cont);
		if (s.catches == 0) {
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-rb.php", "RB",
					rankings);
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-wr.php", "WR",
					rankings);
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-te.php", "TE",
					rankings);
		} else {
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-ppr-rb.php",
					"RB", rankings);
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-ppr-wr.php",
					"WR", rankings);
			parseROSRankingsWorker(
					"http://www.fantasypros.com/nfl/rankings/ros-ppr-te.php",
					"TE", rankings);
		}
		parseROSRankingsWorker(
				"http://www.fantasypros.com/nfl/rankings/ros-dst.php", "D/ST",
				rankings);
		parseROSRankingsWorker(
				"http://www.fantasypros.com/nfl/rankings/ros-k.php", "K",
				rankings);
		for (PlayerObject player : holder.players) {
			if (rankings.containsKey(player.info.name + ","
					+ player.info.position)) {
				player.values.rosRank = rankings.get(player.info.name + ","
						+ player.info.position);
			}
 else {
				player.values.rosRank = 300;
			}
		}
	}

	/**
	 * Does the per page parsing, getting the ranking and the name and putting
	 * them in the hash
	 */
	public static void parseROSRankingsWorker(String url, String pos,
			HashMap<String, Integer> rankings) throws IOException {
		List<String> td = HandleBasicQueries.handleLists(url, "td");
		int min = 0;
		for (int i = 0; i < td.size(); i++) {
			if (td.get(i + 1).split(" ").length > 2
					&& ManageInput.isInteger(td.get(i))) {
				min = i;
				break;
			}
		}
		for (int i = min; i < td.size(); i += 7) {
			// Fix the odd case where the row size changes
			if (ManageInput.isInteger(td.get(i + 1))) {
				i++;
			}
			int ranking = Integer.parseInt(td.get(i));
			String name = "";
			String team = "";
			if (pos.equals("D/ST")) {
				team = ParseRankings.fixTeams(td.get(i + 1).split(" \\(")[0]);
				name = ParseRankings.fixDefenses(team);
			} else {
				String[] nameArr = td.get(i + 1).split(", ")[0].split(" ");
				// Trim of the team from the row. If they're injured, an extra
				// letter is there, so check
				int trimLength = 1;
				if (nameArr[nameArr.length - 1].length() == 1) {
					trimLength = 2;
				}
				for (int namePieceIndex = 0; namePieceIndex < nameArr.length
						- trimLength; namePieceIndex++) {
					name += nameArr[namePieceIndex] + " ";
				}
				name = name.substring(0, name.length() - 1);
				name = ParseRankings.fixNames(name);
				team = ParseRankings.fixTeams(nameArr[nameArr.length - 1]);
			}
			rankings.put(name + "," + pos, ranking);
		}
	}
}
