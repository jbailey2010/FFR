package com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ffr.fantasyfootballrankings.R;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

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
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ESPNImport {
	public String url;
	public String username;
	public String password;
	public Document doc;
	public Context cont;
	public Storage holder;
	public ImportLeague dummy;
	public boolean isRefresh;

	public ESPNImport(Storage hold, ImportLeague obj, boolean b) {
		holder = hold;
		dummy = obj;
		isRefresh = b;
	}

	/**
	 * Gets the process started with a trial query
	 * 
	 * @param urlOrig
	 * @param c
	 * @throws IOException
	 */
	public void handleESPNParsing(String urlOrig, final Context c)
			throws IOException {
		cont = c;
		System.out.println(urlOrig);
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
		ESPNImport obj;
		ProgressDialog pda;

		public GetTestDoc(Activity activity, ESPNImport espnImport) {
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
		if (isSignIn(test)) {
			handleSignInNeed(test);
		} else if (!isRosters(test)) {
			final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
			popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
			popUp.setContentView(R.layout.tweet_popup);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(popUp.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			popUp.getWindow().setAttributes(lp);
			popUp.show();
			TextView textView = (TextView) popUp.findViewById(R.id.tweet_field);
			textView.setText("   The league ID you input was invalid.\n\n");
			Button close = (Button) popUp.findViewById(R.id.tweet_popup_close);
			close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popUp.dismiss();
					return;
				}
			});
		} else {
			System.out.println("In else");
			doc = test;
			handleParsing();
		}

	}

	/**
	 * Handles signing in if need be
	 * 
	 * @param test
	 */
	public void handleSignInNeed(Document test) {
		if (!isCredentialsSet(cont)) {
			setLogIn();
		} else {
			readUnPw(cont);
			GetLogInFirst task = this.new GetLogInFirst((Activity) cont, this);
			task.execute();
		}
	}

	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * 
	 * @author Jeff
	 * 
	 */
	public class GetLogInFirst extends AsyncTask<Object, String, Document> {
		Activity act;
		ESPNImport obj;
		ProgressDialog pda;

		public GetLogInFirst(Activity activity, ESPNImport espnImport) {
			pda = new ProgressDialog(activity);
			act = activity;
			obj = espnImport;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pda.setMessage("Please wait, making a first attempt with your credentials...");
			pda.show();
		}

		@Override
		protected void onPostExecute(Document result) {
			super.onPostExecute(result);
			pda.dismiss();
			obj.handleFirstLogIn(result);
		}

		@Override
		protected Document doInBackground(Object... data) {
			try {
				Document test = parseESPNPassWord(url, username, password);
				return test;
			} catch (IOException e) {
				return null;
			}

		}

	}

	/**
	 * Handles the logic of the first sign in attempt
	 * 
	 * @param test
	 */
	public void handleFirstLogIn(Document test) {
		Elements elements = test.select("td.playertablePlayerName");
		if (isSignIn(test) || elements.size() <= 10) {
			Toast.makeText(cont, "Log in failed with the stored information",
					Toast.LENGTH_SHORT).show();
			setLogIn();
		} else {
			doc = test;
			handleParsing();
		}
	}

	/**
	 * Handles the logging in
	 */
	public void setLogIn() {
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popUp.setContentView(R.layout.espn_unpw);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		popUp.getWindow().setAttributes(lp);
		popUp.show();
		Button close = (Button) popUp.findViewById(R.id.espn_unpw_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				return;
			}
		});
		final EditText unField = (EditText) popUp
				.findViewById(R.id.username_input);
		final EditText pwField = (EditText) popUp
				.findViewById(R.id.password_input);
		Button submit = (Button) popUp.findViewById(R.id.espn_unpw_submit);
		final ESPNImport obj = this;
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HandleLogIn task = obj.new HandleLogIn((Activity) cont, obj,
						popUp);
				String un = unField.getText().toString();
				String pw = pwField.getText().toString();
				if (un.length() > 0 && pw.length() > 0) {
					task.execute(un, pw);
					popUp.dismiss();
				} else {
					Toast.makeText(cont,
							"Please enter a username and a password",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * 
	 * @author Jeff
	 * 
	 */
	public class HandleLogIn extends AsyncTask<Object, String, Document> {
		Activity act;
		ESPNImport obj;
		Dialog dialog;
		ProgressDialog pda;

		public HandleLogIn(Activity activity, ESPNImport espnImport,
				Dialog popUp) {
			act = activity;
			obj = espnImport;
			dialog = popUp;
			pda = new ProgressDialog(act);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pda.setMessage("Please wait, trying to log in...");
			pda.show();
		}

		@Override
		protected void onPostExecute(Document result) {
			super.onPostExecute(result);
			pda.dismiss();
			if (result == null) {
				Toast.makeText(cont, "Invalid username/password",
						Toast.LENGTH_SHORT).show();
				obj.setLogIn();
			} else {
				obj.handleParsing();
			}
		}

		@Override
		protected Document doInBackground(Object... data) {
			String un = (String) data[0];
			String pw = (String) data[1];
			try {
				Document testSignIn = parseESPNPassWord(url, un, pw);
				if (testSignIn.html().contains("We're Sorry")) {
					return null;
				}
				if (isRosters(testSignIn)) {
					obj.doc = testSignIn;
					obj.username = un;
					obj.password = Base64.encodeToString(pw.getBytes(),
							Base64.DEFAULT);
					storeUnPw(cont);
					return obj.doc;
				}
			} catch (IOException e) {
				System.out.println(e.toString());
			}
			return null;
		}

	}

	public class HandleParsingAsync extends
			AsyncTask<Object, String, List<TeamAnalysis>> {
		Activity act;
		ProgressDialog pda;

		public HandleParsingAsync(Activity activity, ESPNImport espnImport) {
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
			getLeagueName(result);
		}

		@Override
		protected List<TeamAnalysis> doInBackground(Object... data) {
			Elements elements = doc.select("td.playertablePlayerName");
			Map<String, List<String>> players = new HashMap<String, List<String>>();
			ParseRankings.handleHashes();
			for (Element elem : elements) {
				if (!elem.html().contains("Compare Players")) {
					String playerName = ParseRankings.fixNames(elem.child(0)
							.text());
					String team = "";
					Elements parent = elem.parent().parent().parent()
							.children();
					for (Element children : parent) {
						if (children.children().size() > 0) {
							Element child = children.child(0);
							if (child.children().size() > 0) {
								team = child.child(0).text();
								// team = team.split("\\(")[0];
								String[] teamSet = team.split(" ");
								StringBuilder teamBuilder = new StringBuilder(
										40);
								for (String teamIter : teamSet) {
									teamBuilder.append(ManageInput
											.capitalize(teamIter) + " ");
								}
								String intermediate = teamBuilder.toString()
										.replaceAll("[^a-zA-Z0-9\\s]", "");
								team = (intermediate.substring(0,
										intermediate.length() - 1)
										+ "@@@" + child.child(0).html());
							}
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
				qb.append("Quarterbacks: ");
				rb.append("Running Backs: ");
				wr.append("Wide Receivers: ");
				te.append("Tight Ends: ");
				d.append("D/ST: ");
				k.append("Kickers: ");
				for (String member : onTeam) {
					for (PlayerObject player : holder.players) {
						if (player.info.name.equals(member)) {
							if (player.info.position.equals("QB")) {
								qbs.add(member);
							}
							if (player.info.position.equals("RB")) {
								rbs.add(member);
							}
							if (player.info.position.equals("WR")) {
								wrs.add(member);
							}
							if (player.info.position.equals("TE")) {
								tes.add(member);
							}
							if (player.info.position.equals("D/ST")) {
								def.add(member);
							}
							if (player.info.position.equals("K")) {
								ks.add(member);
							}
							break;
						}
					}

				}

				if (qbs.size() == 0) {
					qb.append("None Selected\n");
				} else {
					for (String name : qbs) {
						qb.append(name + ", ");
					}
				}
				if (rbs.size() == 0) {
					rb.append("None Selected\n");
				} else {
					for (String name : rbs) {
						rb.append(name + ", ");
					}
				}
				if (wrs.size() == 0) {
					wr.append("None Selected\n");
				} else {
					for (String name : wrs) {
						wr.append(name + ", ");
					}
				}
				if (tes.size() == 0) {
					te.append("None Selected\n");
				} else {
					for (String name : tes) {
						te.append(name + ", ");
					}
				}
				if (def.size() == 0) {
					d.append("None Selected\n");
				} else {
					for (String name : def) {
						d.append(name + ", ");
					}
				}
				if (ks.size() == 0) {
					k.append("None Selected\n");
				} else {
					for (String name : ks) {
						k.append(name + ", ");
					}
				}
				String qbStr = qb.toString();
				if (!qbStr.contains("None Selected")) {
					qbStr = qbStr.substring(0, qbStr.length() - 2) + "\n";
				}
				String rbStr = rb.toString();
				if (!rbStr.contains("None Selected")) {
					rbStr = rbStr.substring(0, rbStr.length() - 2) + "\n";
				}
				String wrStr = wr.toString();
				if (!wrStr.contains("None Selected")) {
					wrStr = wrStr.substring(0, wrStr.length() - 2) + "\n";
				}
				String teStr = te.toString();
				if (!teStr.contains("None Selected")) {
					teStr = teStr.substring(0, teStr.length() - 2) + "\n";
				}
				String dStr = d.toString();
				if (!dStr.contains("None Selected")) {
					dStr = dStr.substring(0, dStr.length() - 2) + "\n";
				}
				String kStr = k.toString();
				if (!kStr.contains("None Selected")) {
					kStr = kStr.substring(0, kStr.length() - 2) + "\n";
				}
				TeamAnalysis teamObj = new TeamAnalysis(team, qbStr + rbStr
						+ wrStr + teStr + dStr + kStr, holder, cont, r);
				teamSet.add(teamObj);
			}
			return teamSet;
		}

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
					writeToFile(namePossible, "ESPN", teamSet);
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
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
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

	/**
	 * Returns the html of the document when it for sure needs a password. Note:
	 * this does NOT validate that the url input is what it should be
	 */
	public Document parseESPNPassWord(String url, String username,
			String password) throws IOException {
		String base = "https://r.espn.go.com/espn/memberservices/pc/login";
		Connection.Response res = Jsoup
				.connect(base)
				.data("SUBMIT", "1", "failedLocation", "", "aff_code",
						"espn_fantgames", "appRedirect", url, "cookieDomain",
						".go.com", ".multipleDomains", "true", "username",
						username, "password", password, "submit", "Sign+In")
				.userAgent(
						"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.method(Method.POST).execute();
		Document doc2 = Jsoup
				.connect(url)
				.cookies(res.cookies())
				.userAgent(
						"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.get();
		return doc2;
	}

	/**
	 * Checks if the document is currently a sign in page
	 * 
	 * @param doc
	 * @return
	 */
	public boolean isSignIn(Document doc) {
		Elements elements = doc.select("title");
		for (Element elem : elements) {
			System.out.println("Iterating isSignin");

			if (elem.text().contains("Sign In")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the document is currently the league rosters page
	 * 
	 * @param doc
	 * @return
	 */
	public boolean isRosters(Document doc) {
		Elements elements = doc.select("title");
		for (Element elem : elements) {
			if (elem.text().contains("League Rosters")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns if the username/password are stored
	 */
	public boolean isCredentialsSet(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		return prefs.getBoolean("ESPN Credentials Stored", false);
	}

	/**
	 * Reads the stored username and password from file
	 * 
	 * @param cont
	 */
	public void readUnPw(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		username = prefs.getString("ESPN Username", "Not Set");
		password = prefs.getString("ESPN Password", "Not Set");
		password = new String(Base64.decode(password, Base64.DEFAULT));
	}

	/**
	 * Stores the username and password to avoid unnecessary hassle later
	 * 
	 * @param cont
	 */
	public void storeUnPw(Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0)
				.edit();
		editor.putString("ESPN Username", username);
		editor.putString("ESPN Password", password);
		editor.putBoolean("ESPN Credentials Stored", true);
		editor.apply();
	}

}
