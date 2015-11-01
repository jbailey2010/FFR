package com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources.ESPNImport.GetTestDoc;
import com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources.ESPNImport.HandleParsingAsync;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

public class YahooImport {
	public String url;
	public Document doc;
	public Context cont;
	public Storage holder;
	public ImportLeague dummy;
	public Boolean isRefresh;

	/**
	 * A dummy constructor to handle the setting of the various structures
	 * 
	 * @param hold
	 * @param obj
	 * @param b
	 */
	public YahooImport(Storage hold, ImportLeague obj, Context c, boolean b) {
		holder = hold;
		dummy = obj;
		cont = c;
		isRefresh = b;
	}

	/**
	 * Gets the process started with a trial query
	 * 
	 * @param urlOrig
	 * @param c
	 * @throws IOException
	 */
	public void handleYahooParsing(String urlOrig) throws IOException {
		url = urlOrig;
		GetTestDoc task1 = this.new GetTestDoc((Activity) cont, this);
		task1.execute(url);
	}

	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * 
	 * @author Jeff
	 * 
	 */
	public class GetTestDoc extends AsyncTask<Object, String, Document> {
		Activity act;
		YahooImport obj;
		ProgressDialog pda;

		public GetTestDoc(Activity activity, YahooImport espnImport) {
			act = activity;
			obj = espnImport;
			pda = new ProgressDialog(act);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pda.setMessage("Please wait, querying the league...");
			pda.show();
		}

		@Override
		protected void onPostExecute(Document result) {
			super.onPostExecute(result);
			pda.dismiss();
			obj.handleTest(result);
		}

		@Override
		protected Document doInBackground(Object... data) {
			String url = (String) data[0];

			try {
				return Jsoup.connect(url).timeout(0).get();
			} catch (IOException e) {
				return null;
			}
		}

	}

	/**
	 * Uses the new test document to see if someone needs to sign in at all
	 * 
	 * @param test
	 */
	public void handleTest(Document test) {
		if (!isRosters(test)) {
			final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
			popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
			popUp.setContentView(R.layout.tweet_popup);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(popUp.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			popUp.getWindow().setAttributes(lp);
			popUp.show();
			TextView textView = (TextView) popUp.findViewById(R.id.tweet_field);
			textView.setText("   Either the league ID you input was invalid or it is not a public league.\n\n");
			Button close = (Button) popUp.findViewById(R.id.tweet_popup_close);
			close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popUp.dismiss();
					return;
				}
			});
		} else {
			doc = test;
			handleParsing();
		}

	}

	/**
	 * Checks if the document is currently the league rosters page
	 * 
	 * @param doc
	 * @return
	 */
	public boolean isRosters(Document doc) {
		Elements elements = doc.select("td");
		if (elements.size() > 25) {
			return true;
		}
		return false;
	}

	/**
	 * Takes the document and converts it into the relevant data structures to
	 * keep track of everything
	 */
	public void handleParsing() {
		HandleParsingAsync task = this.new HandleParsingAsync((Activity) cont,
				this);
		task.execute();
	}

	public class HandleParsingAsync extends
			AsyncTask<Object, String, List<TeamAnalysis>> {
		Activity act;
		ProgressDialog pda;

		public HandleParsingAsync(Activity activity, YahooImport espnImport) {
			act = activity;
			pda = new ProgressDialog(act);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pda.setMessage("Please wait, parsing your league data...");
			pda.show();
		}

		@Override
		protected void onPostExecute(List<TeamAnalysis> result) {
			super.onPostExecute(result);
			pda.dismiss();
			if ((result.size() >= 6)) {
				getLeagueName(result);
			} else {
				Toast.makeText(act,
						"This feature is only available after you've drafted",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected List<TeamAnalysis> doInBackground(Object... data) {
			Elements elements = doc.select("a.name");
			Map<String, List<String>> players = new HashMap<String, List<String>>();
			ParseRankings.handleHashes();
			for (Element elem : elements) {
				String playerName = ParseRankings.fixDefenses(ParseRankings
						.fixNames(elem.text()));
				String metadata = elem.parent().child(1).text();
				if (playerName.equals("New York")) {
					if (metadata.contains("NYJ")) {
						playerName = "Jets D/ST";
					}
					if (metadata.contains("NYG")) {
						playerName = "Giants D/ST";
					}
				}
				String team = "";
				Elements parent = elem.parent().parent().parent().parent()
						.parent().parent().parent().children();
				for (Element children : parent) {
					if (children.children().size() > 0) {
						Element child = parent.get(0);
						team = child.text();
						String[] teamSet = team.split(" ");
						StringBuilder teamBuilder = new StringBuilder(40);
						for (String teamIter : teamSet) {
							teamBuilder.append(ManageInput.capitalize(teamIter)
									+ " ");
						}
						String intermediate = teamBuilder.toString()
								.replaceAll("[^a-zA-Z0-9\\s]", "");
						team = (intermediate.substring(0,
								intermediate.length() - 1) + "@@@" + child
								.child(0).html());
					}
				}
				if (players.containsKey(team)) {
					List<String> tempList = players.get(team);
					tempList.add(playerName);
					players.put(team, tempList);
				} else {
					List<String> newList = new ArrayList<String>();
					newList.add(playerName);
					players.put(team, newList);
				}
			}
			Set<String> teamNames = players.keySet();
			List<TeamAnalysis> teamSet = new ArrayList<TeamAnalysis>();
			Roster r = ReadFromFile.readRoster(cont, "");
			for (String team : teamNames) {
				List<String> onTeam = players.get(team);
				List<String> qbs = new ArrayList<String>();
				List<String> rbs = new ArrayList<String>();
				List<String> wrs = new ArrayList<String>();
				List<String> tes = new ArrayList<String>();
				List<String> def = new ArrayList<String>();
				List<String> ks = new ArrayList<String>();
				StringBuilder qb = new StringBuilder(1000);
				StringBuilder rb = new StringBuilder(1000);
				StringBuilder wr = new StringBuilder(1000);
				StringBuilder te = new StringBuilder(1000);
				StringBuilder d = new StringBuilder(1000);
				StringBuilder k = new StringBuilder(1000);
				team = team.split("@@@")[0];
				qb.append(Constants.ML_QB_HEADER);
				rb.append(Constants.ML_RB_HEADER);
				wr.append(Constants.ML_WR_HEADER);
				te.append(Constants.ML_TE_HEADER);
				d.append(Constants.ML_DEF_HEADER);
				k.append(Constants.ML_K_HEADER);
				for (String member : onTeam) {
					for (PlayerObject player : holder.players) {
						if (player.info.name.equals(member)) {
							if (player.info.position.equals(Constants.QB)) {
								qbs.add(member);
							}
							if (player.info.position.equals(Constants.RB)) {
								rbs.add(member);
							}
							if (player.info.position.equals(Constants.WR)) {
								wrs.add(member);
							}
							if (player.info.position.equals(Constants.TE)) {
								tes.add(member);
							}
							if (player.info.position.equals(Constants.DST)) {
								def.add(member);
							}
							if (player.info.position.equals(Constants.K)) {
								ks.add(member);
							}
							break;
						}
					}

				}

				if (qbs.size() == 0) {
					qb.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : qbs) {
						qb.append(name + ", ");
					}
				}
				if (rbs.size() == 0) {
					rb.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : rbs) {
						rb.append(name + ", ");
					}
				}
				if (wrs.size() == 0) {
					wr.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : wrs) {
						wr.append(name + ", ");
					}
				}
				if (tes.size() == 0) {
					te.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : tes) {
						te.append(name + ", ");
					}
				}
				if (def.size() == 0) {
					d.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : def) {
						d.append(name + ", ");
					}
				}
				if (ks.size() == 0) {
					k.append(Constants.ML_NONE_SELECTED + Constants.LINE_BREAK);
				} else {
					for (String name : ks) {
						k.append(name + ", ");
					}
				}
				String qbStr = qb.toString();
				if (!qbStr.contains(Constants.ML_NONE_SELECTED)) {
					qbStr = qbStr.substring(0, qbStr.length() - 2) + Constants.LINE_BREAK;
				}
				String rbStr = rb.toString();
				if (!rbStr.contains(Constants.ML_NONE_SELECTED)) {
					rbStr = rbStr.substring(0, rbStr.length() - 2) + Constants.LINE_BREAK;
				}
				String wrStr = wr.toString();
				if (!wrStr.contains(Constants.ML_NONE_SELECTED)) {
					wrStr = wrStr.substring(0, wrStr.length() - 2) + Constants.LINE_BREAK;
				}
				String teStr = te.toString();
				if (!teStr.contains(Constants.ML_NONE_SELECTED)) {
					teStr = teStr.substring(0, teStr.length() - 2) + Constants.LINE_BREAK;
				}
				String dStr = d.toString();
				if (!dStr.contains(Constants.ML_NONE_SELECTED)) {
					dStr = dStr.substring(0, dStr.length() - 2) + Constants.LINE_BREAK;
				}
				String kStr = k.toString();
				if (!kStr.contains(Constants.ML_NONE_SELECTED)) {
					kStr = kStr.substring(0, kStr.length() - 2) + Constants.LINE_BREAK;
				}
				TeamAnalysis teamObj = new TeamAnalysis(team, qbStr + rbStr
						+ wrStr + teStr + dStr + kStr, holder, cont, r);
				System.out.println("Adding teamObj: " + teamObj.team.length()
						+ ", " + team);
				teamSet.add(teamObj);
			}
			return teamSet;
		}

	}

	/**
	 * Gets the league name from the user
	 * 
	 * @param teamSet
	 */
	public void getLeagueName(final List<TeamAnalysis> teamSet) {
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popUp.setContentView(R.layout.import_get_league_name);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		popUp.getWindow().setAttributes(lp);
		popUp.show();
		popUp.setCancelable(false);
		final EditText input = (EditText) popUp
				.findViewById(R.id.league_name_input);
		final Button submit = (Button) popUp
				.findViewById(R.id.import_league_name_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String namePossible = input.getText().toString();
				if (namePossible.length() > 0) {
					writeToFile(namePossible, "Yahoo", teamSet);
					Intent intent = new Intent(cont, ImportLeague.class);
					cont.startActivity(intent);
					popUp.dismiss();
				} else {
					Toast.makeText(cont, "Please input a name",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Writes all of the new league data to file
	 */
	public void writeToFile(String namePossible, String string,
			List<TeamAnalysis> teamSet) {
		SharedPreferences.Editor editor = cont.getSharedPreferences(Constants.SP_KEY, 0)
				.edit();
		SharedPreferences prefs = cont.getSharedPreferences(Constants.SP_KEY, 0);
		int oldCount = prefs.getInt("Number of Leagues Imported", 0);
		editor.putInt("Number of Leagues Imported", oldCount + 1);
		ImportedTeam newImport = new ImportedTeam(teamSet, namePossible, string);
		String leagueKey = newImport.leagueHost + "@@@" + newImport.leagueName;
		StringBuilder wholeSet = new StringBuilder(100000);
		String oldKeys = prefs.getString("Imported League Keys", "");
		editor.putString("Imported League Keys", leagueKey + "~~~" + oldKeys);
		for (TeamAnalysis team : newImport.teams) {
			wholeSet.append(team.teamName + "~~" + team.team + "@@@");
		}
		editor.putString(leagueKey,
				url + "LEAGUEURLSPLIT" + wholeSet.toString());
		if (isRefresh) {
			newImport.roster = ImportLeague.newImport.roster;
			newImport.scoring = ImportLeague.newImport.scoring;
		} else {
			newImport.roster = ReadFromFile.readRoster(cont);
			newImport.scoring = ReadFromFile.readScoring(cont);
		}
		WriteToFile.writeRoster(newImport.leagueHost + newImport.leagueName,
				cont, newImport.roster);
		WriteToFile.writeScoring(newImport.leagueHost + newImport.leagueName,
				cont, newImport.scoring);
		editor.apply();
		dummy.handleLayoutInit();
	}
}
