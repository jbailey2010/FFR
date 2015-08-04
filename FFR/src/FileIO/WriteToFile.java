package FileIO;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.auth.AccessToken;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Flex;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.Rankings;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.WriteDraft;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * A library of all of the functions that will write to file
 * 
 * @author Jeff
 * 
 */
public class WriteToFile {
	private final static StorageAsyncTask asyncObject = new StorageAsyncTask();

	/**
	 * This stores the player names to the SD card, it can only be called by
	 * fetchPlayerNames to avoid unnecessary calls
	 * 
	 * @param holder
	 *            holds the array to be stored
	 * @param cont
	 *            used to be allowed to write to file in android
	 */
	public static void storePlayerNames(HashSet<String> names, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putStringSet("Player Names", names);
		editor.apply();
	}

	/**
	 * Handles writing rankings to file asynchronously
	 * 
	 * @param holder
	 * @param cont
	 */
	public static void storeRankingsAsync(Storage holder, Context cont) {
		WriteDraft draftTask = asyncObject.new WriteDraft((Activity) cont);
		draftTask.execute(holder, cont);
	}

	/**
	 * Just writes the filter size to file for later usage
	 * 
	 * @param cont
	 * @param size
	 */
	public static void writeFilterSize(Context cont, int size, String flag) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		if (flag.equals("Rankings")) {
			editor.putInt("Filter Quantity Size Rankings", size);
		} else {
			editor.putInt("Filter Quantity Size", size);
		}
		editor.apply();
	}

	/**
	 * Writes the watch list to file
	 */
	public static void writeWatchList(Context cont, List<String> watch) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		StringBuilder newsSet = new StringBuilder(10000);
		for (String name : watch) {
			if (name.length() > 3 && name.charAt(0) != ' ') {
				newsSet.append(name + "----");
			}
		}
		editor.putString("Watch List", newsSet.toString()).apply();
	}

	/**
	 * Writes the draft to file (supplementary to writing rankings to file)
	 */
	public static void writeDraft(Draft draft, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		String draftList = "";
		draftList += writeDraftHelper(draft.qb) + "@";
		draftList += writeDraftHelper(draft.rb) + "@";
		draftList += writeDraftHelper(draft.wr) + "@";
		draftList += writeDraftHelper(draft.te) + "@";
		draftList += writeDraftHelper(draft.def) + "@";
		draftList += writeDraftHelper(draft.k) + "@";
		StringBuilder inter = new StringBuilder(1000);
		for (String name : draft.ignore) {
			inter.append(name + "~");
		}
		if (inter.length() < 3) {
			inter.append(" ");
		}
		draftList += inter.toString() + "@";
		draftList += draft.remainingSalary + "@" + draft.value;
		editor.putString("Draft Information", draftList).apply();
	}

	/**
	 * Writes scoring to file
	 * 
	 * @param key
	 */
	public static void writeScoring(String key, Context cont, Scoring scoring) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putInt("Pass Yards" + key, scoring.passYards);
		editor.putInt("Pass Touchdowns" + key, scoring.passTD);
		editor.putInt("Rush Yards" + key, scoring.rushYards);
		editor.putInt("Rush Touchdowns" + key, scoring.rushTD);
		editor.putInt("Receiving Yards" + key, scoring.recYards);
		editor.putInt("Receiving Touchdowns" + key, scoring.recTD);
		editor.putInt("Catches" + key, scoring.catches);
		editor.putInt("Interceptions" + key, scoring.interception);
		editor.putInt("Fumbles" + key, scoring.fumble);
		editor.putBoolean("Is Scoring Set?", true);
		editor.apply();
	}

	/**
	 * Writes roster to file
	 */
	public static void writeRoster(String key, Context cont, Roster roster) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putInt("Number of teams" + key, roster.teams);
		if (roster.flex == null) {
			roster.flex = new Flex();
		}
		editor.putInt("Starting QBs" + key, roster.qbs);
		editor.putInt("Starting RBs" + key, roster.rbs);
		editor.putInt("Starting WRs" + key, roster.wrs);
		editor.putInt("Starting TEs" + key, roster.tes);
		editor.putInt("Starting RB/WRs" + key, roster.flex.rbwr);
		editor.putInt("Starting RB/WR/TEs" + key, roster.flex.rbwrte);
		editor.putInt("Starting OPs" + key, roster.flex.op);
		editor.putInt("Starting Ks" + key, roster.k);
		editor.putInt("Starting Defs" + key, roster.def);
		editor.putBoolean("Is roster set?", true);
		editor.apply();
	}

	/**
	 * Writes that the app had been opened
	 * 
	 * @param cont
	 */
	public static void writeFirstOpen(Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putBoolean("First Open", false).apply();
	}

	/**
	 * Writes to file if it's an auciton
	 * 
	 * @param aucFactor
	 *            , Storage holder
	 */
	public static void writeIsAuction(Boolean isAuction, Context cont,
			double aucFactor, Storage holder) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putBoolean("Is Auction", isAuction).apply();
		editor.putFloat("Auction Factor", (float) aucFactor).apply();
	}

	/**
	 * A helper for writing rankings to file
	 */
	private static String writeDraftHelper(List<PlayerObject> list) {
		StringBuilder retSet = new StringBuilder(5000);
		int counter = -1;
		for (PlayerObject player : list) {
			retSet.append(player.info.name + "~");
			counter++;
		}
		if (counter == -1) {
			retSet.append(" ");
		}
		return retSet.toString();
	}

	/**
	 * Writes the use ID to file
	 */
	public static void storeID(long l, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putLong("Use ID", l);
		editor.apply();
	}

	/**
	 * Writes the token data to file to be read later
	 */
	public static void storeToken(AccessToken token, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putString("Token", token.getToken());
		editor.putString("Token Secret", token.getTokenSecret());
		editor.apply();
	}

	/**
	 * Writes the o line rankings to file
	 * 
	 * @param holder
	 * @param cont
	 */
	public static void writeTeamData(Storage holder, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		StringBuilder oLineRanks = new StringBuilder(10000);

		// Oline advanced
		Set<String> keysAdv = holder.oLineAdv.keySet();
		for (String key : keysAdv) {
			oLineRanks.append(key + "##" + holder.oLineAdv.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");

		// Draft classes
		Set<String> keysDraft = holder.draftClasses.keySet();
		for (String key : keysDraft) {
			oLineRanks
					.append(key + "##" + holder.draftClasses.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");

		// Sos
		Set<String> keysSos = holder.sos.keySet();
		if (keysSos == null || keysSos.size() == 0) {
			oLineRanks.append(" ## %%%");
		} else {
			for (String key : keysSos) {
				oLineRanks.append(key + "##" + holder.sos.get(key) + "%%%");
			}
		}
		oLineRanks.append("@#@#");

		// Bye
		Set<String> keysBye = holder.bye.keySet();
		for (String key : keysBye) {
			oLineRanks.append(key + "##" + holder.bye.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");

		// FA
		Set<String> keysFA = holder.fa.keySet();
		for (String key : keysFA) {
			List<String> fa = holder.fa.get(key);
			if (fa.size() == 0) {
				oLineRanks
						.append("Signed Free Agents: &&Departing Free Agents: ");
			} else {
				oLineRanks.append(key + "##" + fa.get(0) + "&&" + fa.get(1)
						+ "%%%");
			}
		}
		oLineRanks.append("@#@#");

		// Notes
		Set<String> keysNote = holder.notes.keySet();
		for (String key : keysNote) {
			String note = holder.notes.get(key);
			oLineRanks.append(key + "##" + note + "%%%");
		}

		editor.putString("Team By Team Data", oLineRanks.toString());
		editor.apply();
	}

	/**
	 * Stores a draft's data
	 * 
	 * @param holder
	 * @param cont
	 * @param teamName
	 * @param teamCount
	 * @param noteStr
	 */
	public static void writeDraftData(Storage holder, Context cont,
			String teamName, int teamCount, String noteStr) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		int nextDraft = ReadFromFile.readCurrDraft(cont) + 1;
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder secondaryData = new StringBuilder(1000);
		secondaryData.append("PAA: " + df.format(Draft.paaTotal(holder.draft)));
		if (ReadFromFile.readIsAuction(cont)) {
			secondaryData.append("\nValue: " + df.format(holder.draft.value));
		}
		String qbs = Rankings.handleDraftParsing(holder.draft.qb);
		String rbs = Rankings.handleDraftParsing(holder.draft.rb);
		String wrs = Rankings.handleDraftParsing(holder.draft.wr);
		String tes = Rankings.handleDraftParsing(holder.draft.te);
		String ds = Rankings.handleDraftParsing(holder.draft.def);
		String ks = Rankings.handleDraftParsing(holder.draft.k);
		StringBuilder team = new StringBuilder(10000);
		if (noteStr.length() > 1) {
			noteStr = "Comment: " + noteStr;
		}
		team.append(teamName + ": " + teamCount + " team league\n" + noteStr
				+ "\n");
		team.append("Quarterbacks: " + qbs + "\n");
		team.append("Running Backs: " + rbs + "\n");
		team.append("Wide Receivers: " + wrs + "\n");
		team.append("Tight Ends: " + tes + "\n");
		team.append("D/ST: " + ds + "\n");
		team.append("Kickers: " + ks + "\n");
		editor.putString("Primary " + ReadFromFile.readCurrDraft(cont),
				team.toString());
		editor.putString("Secondary " + ReadFromFile.readCurrDraft(cont),
				secondaryData.toString());
		editor.putInt("Current Draft", nextDraft).apply();
	}

	public static void writeFirstIsRegularSeason(Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putBoolean("Is regular season new " + Home.yearKey, false)
				.apply();
	}
}
