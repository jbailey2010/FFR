package AsyncTasks;

import java.io.IOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.htmlcleaner.XPatherException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ComparatorHandling;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraftWizardRanks;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPNadv;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFantasyPros;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMath;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNFL;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseOLineAdvanced;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePlayerNames;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseYahoo;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.FantasyProsUtils;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.Utils.MathUtils;
import com.example.fantasyfootballrankings.ClassFiles.Utils.TwitterWork;
import com.example.fantasyfootballrankings.MyLeagueSupport.LineupHelp;
import com.example.fantasyfootballrankings.Pages.Rankings;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * A library of all the asynctasks involving parsing
 * 
 * @author Jeff
 * 
 */
public class ParsingAsyncTask {
	/**
	 * This handles the running of the rankings in the background such that the
	 * user can't do anything until they're fetched
	 * 
	 * @author Jeff
	 * 
	 */
	private long start;
	private long all;

	public class ParseRanks extends AsyncTask<Object, String, Void> {
		private ProgressDialog pdia;
		private Activity act;
		private Storage hold;
		private int draftIter;

		public ParseRanks(Activity activity, Storage holder) {
			SharedPreferences prefs = activity.getSharedPreferences("FFR", 0);
			draftIter = prefs.getInt("Parse Count", 0);
			pdia = new ProgressDialog(activity);
			pdia.setCancelable(false);
			act = activity;
			hold = holder;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia.setMessage("Please wait, fetching the rankings...(0/30)");
			pdia.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pdia.dismiss();
			SharedPreferences.Editor editor = act
					.getSharedPreferences("FFR", 0).edit();
			if (draftIter >= 8) {
				draftIter = -1;
			}
			editor.putInt("Parse Count", ++draftIter).apply();
			if (hold.players.size() > 1) {
				((Rankings) act).intermediateHandleRankings(act);
			}
		}

		@Override
		protected Void doInBackground(Object... data) {
			Storage holder = (Storage) data[0];
			Context cont = (Context) data[1];
			Map<String, List<String>> fa = new HashMap<String, List<String>>();
			Map<String, String> draftClasses = new HashMap<String, String>();
			if (holder.isRegularSeason) {
				fa = holder.fa;
				draftClasses = holder.draftClasses;
			}
			Roster r = ReadFromFile.readRoster(cont);
			if (!holder.isRegularSeason || holder.players.size() < 100
					|| draftIter >= 8) {
				holder.players.clear();
				holder.parsedPlayers.clear();
				Scoring s = ReadFromFile.readScoring(cont);
				all = System.nanoTime();
				System.out.println("Before WF");
				try {
					ParseWF.wfRankings(holder, s, r);
				} catch (ArrayIndexOutOfBoundsException ee) {
					ee.printStackTrace();
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e15) {

				}
				publishProgress("Please wait, fetching the rankings...(1/30)");
				System.out.println("Before CBS");
				try {
					ParseCBS.cbsRankings(holder, s);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e14) {

				}
				System.out.println("Before ESPN ADV");
				publishProgress("Please wait, fetching the rankings...(3/30)");
				try {
					ParseESPNadv.parseESPNAggregate(holder);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e13) {
					// TODO Auto-generated catch block
				} catch (XPatherException e13) {
					// TODO Auto-generated catch block
					e13.printStackTrace();
				}
				publishProgress("Please wait, fetching the rankings...(4/30)");
				System.out.println("Before FFTB");
				try {
					ParseFFTB.parseFFTBRankingsWrapper(holder);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (MalformedURLException e12) {
					// TODO Auto-generated catch block
					e12.printStackTrace();
				} catch (IOException e12) {
				} catch (XPatherException e12) {
					// TODO Auto-generated catch block
					e12.printStackTrace();
				}
				publishProgress("Please wait, fetching the rankings...(6/30)");
				System.out.println("Before Yahoo");
				try {
					ParseYahoo.parseYahooWrapper(holder);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e9) {
				}
				publishProgress("Please wait, fetching the rankings...(8/30)");
				System.out.println("Before Fantasy Pros");
				try {
					ParseFantasyPros.parseFantasyProsAgg(holder);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e8) {
				}
				publishProgress("Please wait, fetching the rankings...(17/30)");
				System.out.println("Before NFL AAV");
				try {
					ParseNFL.parseNFLAAVWrapper(holder);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e3) {
				}
				System.out.println("Before Draft Wizard Rankings");
				try {
					ParseDraftWizardRanks.parseRanksWrapper(holder, s, r);
					publishProgress("Please wait, fetching the rankings...(23/30)");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			publishProgress("Please wait, getting projected points...");
			try {
				HighLevel.projPointsWrapper(holder, cont);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}
			if (holder.maxProj() < 70.0) {
				holder.isRegularSeason = true;
				System.out.println("Setting to true");
			} else {
				holder.isRegularSeason = false;
				System.out.println("Setting to false, " + holder.maxProj());
			}
			publishProgress("Please wait, normalizing projections...");
			MathUtils.getPAA(holder, cont);
			publishProgress("Please wait, calculating relative risk...");
			try {
				HighLevel.parseECRWrapper(holder, cont);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}
			if (!holder.isRegularSeason) {
				ParseMath.convertPAA(holder, r);
				ParseMath.convertPAA(holder, r);
				ParseMath.convertPAA(holder, r);
				publishProgress("Please wait, fetching the rankings...(26/30)");
				ParseMath.convertECR(holder);
				ParseMath.convertECR(holder);
				publishProgress("Please wait, fetching the rankings...(28/30)");
				ParseMath.convertADP(holder);
				ParseMath.convertADP(holder);
				publishProgress("Please wait, fetching the rankings...(30/30)");
			}

			publishProgress("Please wait, normalizing auction values...");
			double auctionFactor = ReadFromFile.readAucFactor(cont);
			for (PlayerObject player : holder.players) {
				Values.normVals(player.values);
				player.values.secWorth = player.values.worth / auctionFactor;
			}

			start = System.nanoTime();
			publishProgress("Please wait, fetching player stats...");
			try {
				HighLevel.setStats(holder, cont);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e) {
			}

			publishProgress("Please wait, fetching team data...");
			if (!holder.isRegularSeason
					|| (holder.isRegularSeason && (fa.size() < 5 || draftClasses
							.size() < 5))) {
				try {
					HighLevel.setTeamInfo(holder, cont);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
			} else {
				holder.fa = fa;
				holder.draftClasses = draftClasses;
			}

			publishProgress("Please wait, fetching positional SOS...");

			try {
				if (!holder.isRegularSeason) {
					HighLevel.getSOS(holder);
				} else {
					ParseFFTB.parseSOSInSeason(holder);
				}
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}

			publishProgress("Please wait, fetching player contract status...");
			try {
				HighLevel.setContractStatus(holder);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}

			publishProgress("Please wait, setting specific player info...");
			try {
				HighLevel.parseSpecificData(holder, cont);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}

			publishProgress("Please wait, getting advanced line stats...");
			try {
				ParseOLineAdvanced.parsePFOLineData(holder);
			} catch (HttpStatusException e2) {
				System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
			} catch (IOException e1) {
			}

			if (holder.isRegularSeason) {
				publishProgress("Please wait, getting rest of season rankings...");
				try {
					HighLevel.getROSRankingsWrapper(holder, cont);
				} catch (HttpStatusException e2) {
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
			}
			publishProgress("Please wait, getting quality start numbers...");
			try {
				HighLevel.parseQualityDists(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			pdia.setMessage((String) values[0]);
		}
	}

	/**
	 * Re-calls projections, and stores changes.
	 * 
	 * @author Jeff
	 * 
	 */
	public class ParseProjections extends AsyncTask<Object, String, Void> {
		private Activity act;
		private Storage hold;
		private ProgressDialog pdia;

		public ParseProjections(Activity activity, Storage holder) {
			pdia = new ProgressDialog(activity);
			pdia.setCancelable(false);
			act = activity;
			hold = holder;
		}

		@Override
		protected void onPreExecute() {
			pdia.setMessage("Please wait, updating and saving the projections...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			pdia.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Object... data) {
			Storage holder = (Storage) data[0];
			Context cont = (Context) data[1];
			WriteToFile.writeTeamData(holder, cont);
			try {
				HighLevel.projPointsWrapper(holder, cont);
				HighLevel.parseECRWrapper(holder, cont);
				MathUtils.getPAA(holder, cont);
				SharedPreferences.Editor editor = cont.getSharedPreferences(
						"FFR", 0).edit();
				// Rankings work
				Set<String> playerData = new HashSet<String>();
				for (PlayerObject player : holder.players) {
					StringBuilder players = new StringBuilder(10000);
					players.append(Double.toString(player.values.worth));
					players.append("&&");
					players.append(Double.toString(player.values.count));
					players.append("&&");
					players.append(player.info.name);
					players.append("&&");
					players.append(player.info.team);
					players.append("&&");
					players.append(player.info.position);
					players.append("&&");
					players.append(player.info.adp);
					players.append("&&");
					players.append(player.info.contractStatus);
					players.append("&&");
					players.append(player.info.age);
					players.append("&&");
					players.append(player.stats);
					players.append("&&");
					players.append(player.injuryStatus);
					players.append("&&");
					players.append(player.values.ecr);
					players.append("&&");
					players.append(player.risk);
					players.append("&&");
					players.append(player.values.points);
					players.append("&&");
					players.append(player.values.paa);
					players.append("&&");
					players.append(player.values.rosRank);
					players.append("&&");
					players.append(player.values.startDists.get("Bad") + ","
							+ player.values.startDists.get("Good") + ","
							+ player.values.startDists.get("Great"));
					playerData.add(players.toString());
				}
				editor.putStringSet("Player Values", playerData).apply();
			} catch (IOException e) {
				return null;
			}
			return null;
		}

	}

	/**
	 * This handles the running of the rankings in the background such that the
	 * user can't do anything until they're fetched
	 * 
	 * @author Jeff
	 * 
	 */
	public class ParseNames extends AsyncTask<Object, Void, Void> {
		private ProgressDialog pdia;
		private Activity act;
		private boolean isFirstFetch;

		public ParseNames(Activity activity, boolean iff) {
			pdia = new ProgressDialog(activity);
			pdia.setCancelable(false);
			act = activity;
			isFirstFetch = iff;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia.setMessage("Please wait, fetching the player names list...");
			pdia.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pdia.dismiss();
			if (isFirstFetch) {
				Intent intent = new Intent(act, Rankings.class);
				act.startActivity(intent);
			}
		}

		@Override
		protected Void doInBackground(Object... data) {
			Context cont = (Context) data[0];
			try {
				ParsePlayerNames.fetchPlayerNames(cont);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Handles the back-end parsing of the twitter feeds
	 * 
	 * @author Jeff
	 * 
	 */
	public class ParseTwitterSearch extends
			AsyncTask<Object, Void, List<NewsObjects>> {
		private ProgressDialog pdia;
		private Activity act;
		private String query;
		private TwitterWork tw;

		public ParseTwitterSearch(Context cont, String input, TwitterWork obj) {
			pdia = new ProgressDialog(cont);
			pdia.setCancelable(false);
			act = (Activity) cont;
			query = input;
			tw = obj;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia.setMessage("Please wait, searching the feeds...");
			pdia.show();
		}

		@Override
		protected void onPostExecute(List<NewsObjects> result) {
			super.onPostExecute(result);
			pdia.dismiss();
			PlayerInfo.playerTweetSearch(result, act, query);
		}

		@Override
		protected List<NewsObjects> doInBackground(Object... data) {
			String selection = (String) data[1];
			TwitterWork obj = (TwitterWork) data[3];
			List<NewsObjects> news = new ArrayList<NewsObjects>(100);
			news = TwitterWork.searchTweets(selection, obj.userTwitter);
			return news;
		}
	}

	/**
	 * Gets the ecr of each player being compared
	 * 
	 * @author Jeff
	 * 
	 */
	public class ParseFP extends AsyncTask<Object, Void, List<String>> {
		private ProgressDialog pdia;
		private Activity act;
		private String player1;
		private String player2;
		private boolean isStart;
		private String team1;
		private String team2;

		public ParseFP(Context cont, String p1, String p2, String t1,
				String t2, boolean flag) {
			pdia = new ProgressDialog(cont);
			pdia.setCancelable(false);
			act = (Activity) cont;
			player1 = p1;
			player2 = p2;
			team1 = t1;
			team2 = t2;
			isStart = flag;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia.setMessage("Please wait, trying to get the ECR starting numbers...");
			pdia.show();
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			pdia.dismiss();
			if (result != null && isStart) {
				LineupHelp.setECR(result);
			} else if (result != null) {
				ComparatorHandling obj = new ComparatorHandling();
				obj.setResult(result);
			}
		}

		@Override
		protected List<String> doInBackground(Object... data) {
			Context cont = (Context) data[0];
			Scoring s = ReadFromFile.readScoring(cont);
			List<String> ecrList = new ArrayList<String>();
			String baseURL = "";
			if (isStart) {
				baseURL = "http://www.fantasypros.com/nfl/start/";
			} else {
				baseURL = "http://www.fantasypros.com/nfl/draft/";
			}
			FantasyProsUtils obj = new FantasyProsUtils();
			baseURL += obj.playerNameUrl(player1, team1) + "-"
					+ obj.playerNameUrl(player2, team2) + ".php";
			String firstName = player1;
			String secondName = player2;
			if (s.catches == 1) {
				baseURL += "?scoring=PPR";
			}
			try {
				Document doc = Jsoup.connect(baseURL).get();
				System.out.println("Got doc");
				List<String> percentages = HandleBasicQueries.handleListsMulti(
						doc, baseURL, "div div.mpb-col span");
				for (String percent : percentages) {
					if (percent.contains("%")
							&& (percent.contains("50") || !ecrList
									.contains(percent))
							&& !(ecrList.size() >= 2)) {
						System.out.println("Adding " + percent + " to ecrList");
						ecrList.add(percent);
					}
				}
				if (percentages.size() < 2) {
					System.out.println("Returning null :(");
					return null;
				}
				Elements elems = doc.select("div.mpb-left");
				Element p = null;
				for (Element elem : elems) {
					System.out.println("Iterating with " + elem.text());
					if (isStart
							&& (elem.text().contains("Points / Game") || elem
									.text().contains("Ave Projection"))) {
						p = elem;
						ecrList.add(elem.parent().child(1).text());
						ecrList.add(elem.parent().child(2).text());
						break;
					} else if (!isStart && elem.text().contains("ECR")) {
						p = elem;
						ecrList.add(elem.parent().parent().parent().parent()
								.child(2).child(1).child(1).child(0).text());
						ecrList.add(elem.parent().parent().parent().parent()
								.child(2).child(2).child(1).child(0).text());
						break;
					}
				}
				System.out.println("Through average with a total size of "
						+ ecrList.size());
				if (p != null) {
					String name = "";
					if (!isStart) {
						Element megaParent = p.parent().parent().parent()
								.parent();// .child(2).child(1).child(1).child(0).text();
						name = (megaParent.child(2).child(1).child(1).child(0)
								.text());
					} else {
						Element megaParent = p.parent().parent().parent()
								.parent();
						name = megaParent.child(2).child(1).child(2).text();
					}
					if (((isStart && !(name.equals(firstName) || name
							.contains(firstName))) && !firstName
							.contains("D/ST"))
							|| (firstName.contains("D/ST") && !(name
									.equals(team1) || name.contains(team1) || name
										.contains(firstName.split(" D/ST")[0])))) {
						List<String> newEcr = new ArrayList<String>();
						newEcr.add(ecrList.get(1));
						newEcr.add(ecrList.get(0));
						newEcr.add(ecrList.get(3));
						newEcr.add(ecrList.get(2));
						return newEcr;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return ecrList;
		}
	}

	public class ParseADP extends AsyncTask<Object, Void, String> {
		private ProgressDialog pdia;
		private Activity act;
		private Storage h;
		private TextView view;

		public ParseADP(Activity activity, Storage holder, TextView tv) {
			pdia = new ProgressDialog(activity);
			pdia.setCancelable(false);
			act = activity;
			h = holder;
			view = tv;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia.setMessage("Please wait, doing fancy math...");
			pdia.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdia.dismiss();
			view.setText(result);
		}

		@Override
		protected String doInBackground(Object... data) {
			int pick = (Integer) data[0];
			String name = (String) data[1];
			Roster r = ReadFromFile.readRoster(act);
			Scoring s = ReadFromFile.readScoring(act);
			String type = "standard";
			if (s.catches > 0) {
				type = "ppr";
			}
			if (r.qbs > 1 || (r.flex != null && r.flex.op > 0)) {
				type = "2qb";
			}
			String url = "https://fantasyfootballcalculator.com/scenario-calculator?format="
					+ type + "&num_teams=" + r.teams + "&draft_pick=" + pick;
			ParseRankings.handleHashes();
			String first = checkUrl(url, name, pick);
			if (s.catches > 0 && first.contains("error")) {
				first = checkUrl(url.replace("ppr", "standard"), name, pick);
			}
			return first;
		}
	}

	private String checkUrl(String url, String name, int pick) {
		try {
			List<String> td = HandleBasicQueries.handleLists(url,
					"table.adp td");
			for (int i = 0; i < td.size(); i++) {
				String elem = td.get(i);
				if (ParseRankings.fixNames(elem).equals(name)) {
					return "Odds " + name + " is available at pick " + pick
							+ ": " + td.get(i + 4);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "An error occurred. Either the data is unavailable, or the internet may have dropped.";
	}

}
