package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ffr.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseFP;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Handles the player comparing
 * 
 * @author Jeff
 * 
 */
public class ComparatorHandling {
	static TextView result;
	public static String name1;
	public static String name2;

	/**
	 * Does the initial setting up of the dialog itself
	 */
	public static void handleComparingInit(Storage holder, Context cont) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comparator_view);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		// For when this is called back by the comparing part:
		final AutoCompleteTextView player1Input = (AutoCompleteTextView) dialog
				.findViewById(R.id.player1_input);
		final AutoCompleteTextView player2Input = (AutoCompleteTextView) dialog
				.findViewById(R.id.player2_input);
		player1Input.setText("");
		player2Input.setText("");
		// Get rid of dialog
		Button close = (Button) dialog.findViewById(R.id.compare_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
		});
		// Clear kills all thus far inputted data
		Button clear = (Button) dialog.findViewById(R.id.clear_compare);
		clear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final AutoCompleteTextView player1Input = (AutoCompleteTextView) dialog
						.findViewById(R.id.player1_input);
				final AutoCompleteTextView player2Input = (AutoCompleteTextView) dialog
						.findViewById(R.id.player2_input);
				player1Input.setText("");
				player2Input.setText("");
				player1Input.setFocusableInTouchMode(true);
				player1Input.requestFocus();
			}
		});
		setAdapter(holder, cont, dialog);
	}

	/**
	 * Sets adapters and related listeners
	 */
	public static void setAdapter(final Storage holder, final Context cont,
			final Dialog dialog) {
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (PlayerObject player : holder.players) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", player.info.name);
			if (!player.info.name.contains("D/ST")
					&& player.info.position.length() >= 1
					&& player.info.team.length() > 2) {
				datum.put("sub", player.info.position + " - "
						+ player.info.team);
			} else {
				datum.put("sub", "");
			}
			data.add(datum);
		}
		List<Map<String, String>> dataSorted = ManageInput.sortData(data);
		final SimpleAdapter mAdapter = new SimpleAdapter(cont, dataSorted,
				android.R.layout.simple_list_item_2, new String[] { "main",
						"sub" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		final AutoCompleteTextView player1 = (AutoCompleteTextView) dialog
				.findViewById(R.id.player1_input);
		final AutoCompleteTextView player2 = (AutoCompleteTextView) dialog
				.findViewById(R.id.player2_input);
		player1.setAdapter(mAdapter);
		player1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem) arg1).getText1().getText()
						.toString();
				player1.setText(text
						+ ", "
						+ ((TwoLineListItem) arg1).getText2().getText()
								.toString());
				if (player2.getText().toString().length() > 3
						&& holder.parsedPlayers.contains(player2.getText()
								.toString().split(", ")[0])) {
					startBackEnd(dialog, cont, holder);
				}
 else if (player2.getText().toString().length() > 0) {
					Toast.makeText(
							cont,
							"Please enter two valid player names, using the dropdown to help",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		player2.setAdapter(mAdapter);
		player2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem) arg1).getText1().getText()
						.toString();
				player2.setText(text
						+ ", "
						+ ((TwoLineListItem) arg1).getText2().getText()
								.toString());
				if (player1.getText().toString().length() > 3
						&& holder.parsedPlayers.contains(player1.getText()
								.toString().split(", ")[0])) {
					startBackEnd(dialog, cont, holder);
				}
 else if (player1.getText().toString().length() > 0) {
					Toast.makeText(
							cont,
							"Please enter two valid player names, using the dropdown to help",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Starts the back-end work of the comparator
	 */
	public static void startBackEnd(Dialog dialog, Context cont, Storage holder) {
		AutoCompleteTextView player1 = (AutoCompleteTextView) dialog
				.findViewById(R.id.player1_input);
		AutoCompleteTextView player2 = (AutoCompleteTextView) dialog
				.findViewById(R.id.player2_input);
		String team1 = "";
		String nameData1 = player1.getText().toString();
		String name1 = nameData1.split(", ")[0];
		String pos1 = "";
		if (nameData1.split(", ").length > 1) {
			pos1 = nameData1.split(", ")[1].split(" - ")[0];
		}
		if (nameData1.split(", ").length > 1
				&& nameData1.split(", ")[1].split(" - ").length > 1) {
			team1 = nameData1.split(", ")[1].split(" - ")[1];
		}
		String team2 = "";
		String nameData2 = player2.getText().toString();
		String name2 = nameData2.split(", ")[0];
		String pos2 = "";
		if (nameData2.split(", ").length > 1) {
			pos2 = nameData2.split(", ")[1].split(" - ")[0];
		}
		if (nameData2.split(", ").length > 1
				&& nameData2.split(", ")[1].split(" - ").length > 1) {
			team2 = nameData2.split(", ")[1].split(" - ")[1];
		}
		if (name1.equals(name2)) {
			Toast.makeText(cont, "Please enter 2 different players",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// First player fetching and other lists
		PlayerObject firstPlayer = getPlayer(name1, team1, pos1, holder);
		List<PlayerObject> firstTeam = getPlayerTeam(holder, firstPlayer);
		List<PlayerObject> firstPos = getPlayerPosition(holder, firstPlayer);
		// Fetch second player and other lists
		PlayerObject secondPlayer = getPlayer(name2, team2, pos2, holder);
		List<PlayerObject> secTeam = getPlayerTeam(holder, secondPlayer);
		List<PlayerObject> secPos = getPlayerPosition(holder, secondPlayer);
		// Get the stats
		dialog.dismiss();
		handleStats(dialog, cont, holder, firstPlayer, secondPlayer, firstTeam,
				secTeam, firstPos, secPos);
	}

	/**
	 * Handles the various stats of the players to be compared
	 */
	public static void handleStats(Dialog dialog, Context cont, Storage holder,
			PlayerObject player1, PlayerObject player2,
			List<PlayerObject> firstTeam, List<PlayerObject> secTeam,
			List<PlayerObject> firstPos, List<PlayerObject> secPos) {
		StringBuilder p1 = new StringBuilder(10000);
		StringBuilder p2 = new StringBuilder(10000);
		if (!player1.info.position.equals(player2.info.position)) {
			int rank1 = posRank(player1, firstPos);
			int rank2 = posRank(player2, secPos);
			if (rank1 != rank2) {
				if (rank1 < rank2) {
					if (rank2 - rank1 > 10) {
						p1.append("-Positionally much higher ranked" + "\n");
					} else {
						p1.append("-Positionally higher ranked\n");
					}
				} else {
					if (rank1 - rank2 > 10) {
						p2.append("-Positionally much higher ranked" + "\n");
					} else {
						p2.append("-Positionally higher ranked\n");
					}
				}
			}
		}
		double worth1 = player1.values.secWorth;
		double worth2 = player2.values.secWorth;
		double aucFactor = ReadFromFile.readAucFactor(cont);
		if (worth1 > worth2) {
			if (worth1 - worth2 > 10.0 / aucFactor) {
				p2.append("-Costs much less\n");
			} else {
				p2.append("-Costs less" + "\n");
			}
		} else {
			if (worth2 - worth1 > 10.0 / aucFactor) {
				p1.append("-Costs much less\n");
			} else {
				p1.append("-Costs less" + "\n");
			}
		}
		double paa1 = player1.values.paa;
		double paa2 = player2.values.paa;
		if (paa1 != 0.0 && paa2 != 0.0) {
			if (paa1 > paa2) {
				if (paa1 - paa2 > 15.0) {
					p1.append("-Much higher PAA\n");
				} else {
					p1.append("-Higher PAA\n");
				}
			}
			if (paa2 > paa1) {
				if (paa2 - paa1 > 15.0) {
					p2.append("-Much higher PAA\n");
				} else {
					p2.append("-Higher PAA\n");
				}
			}
		}
		try {
			int age1 = Integer.parseInt(player1.info.age);
			int age2 = Integer.parseInt(player2.info.age);
			if (age1 != age2) {
				if (age1 > age2) {
					if (age1 - age2 > 5) {
						p2.append("-Much younger\n");
					} else {
						p2.append("-Younger" + "\n");
					}
				} else {
					if (age2 - age1 > 5) {
						p1.append("-Much younger\n");
					} else {
						p1.append("-Younger" + "\n");
					}
				}
			}
		} catch (NumberFormatException e) {
			// Nothing, just don't use the data
		}
		int depth1 = teamDepth(player1, firstTeam);
		int depth2 = teamDepth(player2, secTeam);
		if (depth1 != depth2) {
			if (depth1 < depth2) {
				p1.append("-Higher on his team's depth chart" + "\n");
			} else {
				p2.append("-Higher on his team's depth chart" + "\n");
			}
		}
		if (!(player1.info.position.equals("D/ST") || player2.info.position
				.equals("D/ST"))) {
			double cast1 = teamWorth(firstTeam);
			double cast2 = teamWorth(secTeam);
			if (cast1 > cast2) {
				if (cast1 - cast2 > 20.0) {
					p1.append("-Much better supporting cast\n");
				} else {
					p1.append("-Better supporting cast\n");
				}
			} else {
				if (cast2 - cast1 > 20.0) {
					p2.append("-Much better supporting cast\n");
				} else {
					p2.append("-Better supporting cast\n");
				}
			}
		}
		String cy1 = player1.info.contractStatus;
		String cy2 = player2.info.contractStatus;
		if (!cy1.contains("Under")) {
			p1.append("-In a contract year\n");
		}
		if (!cy2.contains("Under")) {
			p2.append("-In a contract year\n");
		}
		double risk1 = player1.risk;
		double risk2 = player2.risk;
		if (risk1 != 0 && risk2 != 0) {
			if (risk1 > risk2) {
				if (risk1 - risk2 > 3.0) {
					p2.append("-Much lower risk\n");
				} else {
					p2.append("-Lower risk\n");
				}
			}
			if (risk2 > risk1) {
				if (risk2 - risk1 > 3.0) {
					p1.append("-Much lower risk\n");
				} else {
					p1.append("-Lower risk\n");
				}
			}
		}
		double quantity1 = player1.values.count;
		double quantity2 = player2.values.count;
		if (quantity1 > quantity2) {
			if (quantity1 - quantity2 > 3) {
				p1.append("-Shows up in a lot more rankings\n");
			} else {
				p1.append("-Shows up in more rankings\n");
			}
		} else if (quantity2 > quantity1) {
			if (quantity2 - quantity1 > 3) {
				p2.append("-Shows up in a lot more rankings\n");
			} else {
				p2.append("-Shows up in more rankings\n");
			}
		}
		int draft1 = draftRank(player1, holder);
		int draft2 = draftRank(player2, holder);
		if (draft1 < draft2) {
			if (draft2 - draft1 > 5) {
				p1.append("-Much better graded draft\n");
			} else {
				p1.append("-Better graded draft\n");
			}
		} else if (draft2 < draft1) {
			if (draft1 - draft2 > 5) {
				p2.append("-Much better graded draft\n");
			} else {
				p2.append("-Better graded draft\n");
			}
		}
		if (!holder.isRegularSeason) {
			double adp1 = adp(player1);
			double adp2 = adp(player2);
			if (adp1 < adp2) {
				if (adp2 - adp1 > 15.0) {
					p1.append("-Much higher ADP\n");
				} else {
					p1.append("-Higher ADP\n");
				}
			} else {
				if (adp1 - adp2 > 15.0) {
					p2.append("-Much higher ADP\n");
				} else {
					p2.append("-Higher ADP\n");
				}
			}
		}
		double ecr1 = player1.values.ecr;
		double ecr2 = player2.values.ecr;
		if (ecr1 < ecr2) {
			if (ecr2 - ecr1 > 10) {
				p1.append("-Much higher ECR\n");
			} else {
				p1.append("-Higher ECR\n");
			}
		}
		if (ecr2 < ecr1) {
			if (ecr1 - ecr2 > 10) {
				p2.append("-Much higher ECR\n");
			} else {
				p2.append("-Higher ECR\n");
			}
		}
		int sos1 = 0;
		int sos2 = 0;
		try {
			if (!holder.isRegularSeason) {
				sos1 = holder.sos.get(player1.info.team + ","
						+ player1.info.position);// player1.info.sos;
				sos2 = holder.sos.get(player2.info.team + ","
						+ player2.info.position);// player2.info.sos;
			} else {
				sos1 = holder.sos.get(player1.info.adp + ","
						+ player1.info.position);// player1.info.sos;
				sos2 = holder.sos.get(player2.info.adp + ","
						+ player2.info.position);// player2.info.sos;
			}
			if (sos1 > sos2) {
				if (sos1 - sos2 < 5) {
					p2.append("-Easier positional SOS\n");
				} else {
					p2.append("-Much easier positional SOS\n");
				}
			} else if (sos2 > sos1) {
				if (sos2 - sos1 < 5) {
					p1.append("-Easier positional SOS\n");
				} else {
					p1.append("-Much easier positional SOS\n");
				}
			}
		} catch (NullPointerException e) {
		}
		boolean inj1 = injury(player1);
		boolean inj2 = injury(player2);
		if (inj1) {
			p1.append("-Injured\n");
		}
		if (inj2) {
			p2.append("-Injured\n");
		}
		double left1 = remTalent(holder, player1);
		double left2 = remTalent(holder, player2);
		if (!player1.info.position.equals(player2.info.position)) {
			if (left1 > left2) {
				if (left1 - left2 > 15.0) {
					p2.append("-Much less left just behind him at his position\n");
				} else {
					p2.append("-Less left just behind him at his position\n");
				}
			}
			if (left2 > left1) {
				if (left2 - left1 > 15.0) {
					p1.append("-Much less left just behind him at his position\n");
				} else {
					p1.append("-Less left just behind him at his position\n");
				}
			}
		}
		boolean sameBye1 = teamBye(holder, player1);
		boolean sameBye2 = teamBye(holder, player2);
		if (sameBye1) {
			p1.append("-Same bye as a player you've drafted of the same position\n");
		}
		if (sameBye2) {
			p2.append("-Same bye as a player you've drafted of the same position\n");
		}
		p1.append("\n");
		p2.append("\n");
		fixOutput(new Dialog(cont, R.style.RoundCornersFull), cont, holder,
				player1, player2, p1, p2);
	}

	/**
	 * Sets the output of the results
	 */
	public static void fixOutput(final Dialog dialog, final Context cont,
			final Storage holder, final PlayerObject player1,
			final PlayerObject player2, StringBuilder p1, StringBuilder p2) {
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comparator_output);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);

		TextView header1 = (TextView) dialog
				.findViewById(R.id.compare_header_1);
		header1.setText(player1.info.name);
		TextView header2 = (TextView) dialog
				.findViewById(R.id.compare_header_2);
		header2.setText(player2.info.name);
		final PlayerInfo obj = new PlayerInfo();
		obj.isImport = false;
		header1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				obj.outputResults(player1.info.name + ", "
						+ player1.info.position + " - " + player1.info.team,
						true, (Activity) cont, holder, false, true, false);
			}
		});
		header2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				obj.outputResults(player2.info.name + ", "
						+ player2.info.position + " - " + player2.info.team,
						true, (Activity) cont, holder, false, true, false);
			}
		});

		result = (TextView) dialog.findViewById(R.id.comparator_result);
		result.setVisibility(View.INVISIBLE);
		Button back = (Button) dialog.findViewById(R.id.compare_back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				handleComparingInit(holder, cont);
			}
		});
		TextView output1 = (TextView) dialog
				.findViewById(R.id.compare_output_1);
		TextView output2 = (TextView) dialog
				.findViewById(R.id.compare_output_2);
		output1.setText(p1.toString());
		output2.setText(p2.toString());
		Button close = (Button) dialog.findViewById(R.id.compare_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
		name1 = player1.info.name;
		name2 = player2.info.name;
		ParsingAsyncTask o = new ParsingAsyncTask();
		ParseFP task = o.new ParseFP(cont, player1.info.name,
				player2.info.name, player1.info.team, player2.info.team, false);
		task.execute(cont);
	}

	/**
	 * Sets the suggested draft pick header
	 * 
	 * @param results
	 */
	public void setResult(List<String> results) {
		result.setVisibility(View.VISIBLE);
		if (Integer.parseInt(results.get(0).substring(0,
				results.get(0).length() - 1)) > Integer.parseInt(results.get(1)
				.substring(0, results.get(1).length() - 1))) {
			result.setText(results.get(0) + " of experts prefer "
					+ results.get(2));
		} else {
			result.setText(results.get(1) + " of experts prefer "
					+ results.get(3));
		}
	}

	/**
	 * Gets the talent left behind a player at their position
	 */
	public static double remTalent(Storage holder, PlayerObject player) {
		double left = 0.0;
		int counter = 0;
		for (PlayerObject iter : holder.players) {
			if (!iter.info.name.equals(player.info.name)
					&& !Draft.isDrafted(iter.info.name, holder.draft)
					&& counter < 5
					&& iter.info.position.equals(player.info.position)
					&& iter.values.points != 0.0) {
				left += iter.values.points;
				counter++;
			}
		}
		return left;
	}

	/**
	 * Determines if a player is injured or not
	 */
	public static boolean injury(PlayerObject player) {
		if (!player.injuryStatus.contains("Healthy")) {
			return true;
		}
		return false;
	}

	/**
	 * Parses draft rank
	 */
	public static int draftRank(PlayerObject player, Storage holder) {
		if (player.info.team == null || "".equals(player.info.team)) {
			return 33;
		}
		String[] split = holder.draftClasses.get(player.info.team).split("\n");
		String average = split[0];
		String rank = average.split("\\(")[1].substring(0,
				average.split("\\(")[1].length() - 1);
		return Integer.parseInt(rank.replaceAll("T-", ""));
	}

	/**
	 * Parses the adp
	 */
	public static double adp(PlayerObject player) {
		if (player.info.adp.equals("Not set")) {
			return 500.0;
		}
		return Double.parseDouble(player.info.adp);
	}

	/**
	 * Sees if you've drafted a player with the same bye as the player
	 * considered
	 */
	public static boolean teamBye(Storage holder, PlayerObject player) {
		List<PlayerObject> draft = new ArrayList<PlayerObject>();
		if (player.info.position.equals("QB")) {
			draft = holder.draft.qb;
		} else if (player.info.position.equals("RB")) {
			draft = holder.draft.rb;
		} else if (player.info.position.equals("WR")) {
			draft = holder.draft.wr;
		} else if (player.info.position.equals("TE")) {
			draft = holder.draft.te;
		} else if (player.info.position.equals("D/ST")) {
			draft = holder.draft.def;
		} else {
			draft = holder.draft.k;
		}
		boolean sameBye = false;
		for (PlayerObject iter : draft) {
			if (holder.bye.get(iter.info.team).equals(
					holder.bye.get(player.info.team))) {
				return true;
			}
		}
		return sameBye;
	}

	/**
	 * Finds the sum of the worth of a team (supporting cast)
	 */
	public static double teamWorth(List<PlayerObject> teamList) {
		double sum = 0.0;
		for (PlayerObject player : teamList) {
			sum += player.values.worth;
		}
		return sum;
	}

	/**
	 * Finds if a player on the same team with the same position has a higher
	 * worth
	 */
	public static int teamDepth(PlayerObject player, List<PlayerObject> teamList) {
		int depth = 1;
		for (PlayerObject iter : teamList) {
			if (iter.info.position.equals(player.info.position)
					&& iter.values.worth > player.values.worth) {
				depth++;
			}
		}
		return depth;
	}

	/**
	 * Finds the positional rank of a player
	 */
	public static int posRank(PlayerObject player, List<PlayerObject> posList) {
		double worth = player.values.worth;
		int rank = 1;
		for (PlayerObject iter : posList) {
			if (iter.values.worth > worth) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Gets the player given a name
	 * 
	 * @param pos
	 * @param team
	 */
	public static PlayerObject getPlayer(String name, String team, String pos,
			Storage holder) {
		for (PlayerObject player : holder.players) {
			if (name.equals(player.info.name) && team.equals(player.info.team)
					&& pos.equals(player.info.position)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Gets all players of the same position of the player
	 */
	public static List<PlayerObject> getPlayerPosition(Storage holder,
			PlayerObject player) {
		List<PlayerObject> positionList = new ArrayList<PlayerObject>();
		for (PlayerObject iter : holder.players) {
			if (iter.info.position.equals(player.info.position)) {
				positionList.add(iter);
			}
		}
		return positionList;
	}

	/**
	 * Gets all the players on the same team as the player
	 */
	public static List<PlayerObject> getPlayerTeam(Storage holder,
			PlayerObject player) {
		List<PlayerObject> teamList = new ArrayList<PlayerObject>();
		for (PlayerObject iter : holder.players) {
			if (iter.info.team.equals(player.info.team)) {
				teamList.add(iter);
			}
		}
		return teamList;
	}
}
