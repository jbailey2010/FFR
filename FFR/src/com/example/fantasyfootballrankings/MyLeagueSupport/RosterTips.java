package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

/**
 * A static library to handle the management of rosters, giving suggested FA
 * targets and trade targets
 * 
 * @author Jeff
 * 
 */
public class RosterTips {
	public static List<PlayerObject> freeAgents;
	public static ImportedTeam newImport;
	public static View res;

	/**
	 * Handles the logistics of setting up roster tips
	 * 
	 * @param n
	 */
	public static void init(ImportedTeam n, View r) {
		newImport = n;
		res = r;
		setUpLists();
		initFrontEnd();
	}

	/**
	 * Populates the free agency list with free agents in the league
	 */
	public static void setUpLists() {
		freeAgents = new ArrayList<PlayerObject>();
		for (PlayerObject player : ImportLeague.holder.players) {
			if (player.info.team.split(" ").length > 1) {
				boolean isFound = false;
				for (TeamAnalysis iter : newImport.teams) {
					if (iter.team.contains(player.info.name)) {
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					freeAgents.add(player);
				}
			}
		}
	}

	public static void initFrontEnd() {
		final Spinner teamsSp = (Spinner) res
				.findViewById(R.id.team_tips_spinner);
		List<String> teamNames = new ArrayList<String>();
		for (TeamAnalysis team : newImport.teams) {
			teamNames.add(team.teamName);
		}
		ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(
				ImportLeague.cont,
				android.R.layout.simple_spinner_dropdown_item, teamNames);
		teamsSp.setAdapter(adapterPos);
		final Button fa = (Button) res.findViewById(R.id.fa_tips);
		final Button trade = (Button) res.findViewById(R.id.trade_tips);
		Spinner topics = (Spinner) res.findViewById(R.id.fa_topics);
		topics.setVisibility(View.GONE);
		fa.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fa.setBackgroundResource(R.drawable.selected_tab);
				trade.setBackgroundResource(R.drawable.not_selected_tab);
				String teamName = teamsSp.getSelectedItem().toString();
				TeamAnalysis iter = null;
				for (TeamAnalysis team : newImport.teams) {
					if (team.teamName.equals(teamName)) {
						iter = team;
						break;
					}
				}
				handleFA(iter, res);
			}
		});
		trade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ImportLeague.cont,
						"Still in development, should be available soon!",
						Toast.LENGTH_SHORT).show();
			}
		});
		teamsSp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				TextView output = (TextView) res.findViewById(R.id.fa_content);
				TextView tradeOutput = (TextView) res
						.findViewById(R.id.trade_content);
				String teamName = teamsSp.getSelectedItem().toString();
				TeamAnalysis iter = null;
				for (TeamAnalysis team : newImport.teams) {
					if (team.teamName.equals(teamName)) {
						iter = team;
						break;
					}
				}
				if (output.isShown()) {
					fa.setBackgroundResource(R.drawable.selected_tab);
					trade.setBackgroundResource(R.drawable.not_selected_tab);
					handleFA(iter, res);
				} else if (tradeOutput.isShown()) {
					fa.setBackgroundResource(R.drawable.not_selected_tab);
					trade.setBackgroundResource(R.drawable.selected_tab);
					Toast.makeText(ImportLeague.cont,
							"Still in development, stay tuned!",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	/**
	 * Gets all of the relevant maps (each position), given a team
	 * 
	 * @param team
	 */
	public static void handleFA(final TeamAnalysis team, View res) {
		final TextView output = (TextView) res.findViewById(R.id.fa_content);
		final ScrollView base = (ScrollView) res.findViewById(R.id.fa_scroll);
		final TextView tradeOutput = (TextView) res
				.findViewById(R.id.trade_content);
		Spinner topic = (Spinner) res.findViewById(R.id.fa_topics);
		topic.setVisibility(View.VISIBLE);
		List<String> teamNames = new ArrayList<String>();
		teamNames.add("Rest of Season Free Agents");
		teamNames.add("Streaming Free Agents");
		ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(
				ImportLeague.cont,
				android.R.layout.simple_spinner_dropdown_item, teamNames);
		topic.setAdapter(adapterPos);
		topic.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				StringBuilder outputS = new StringBuilder(10000);
				tradeOutput.setVisibility(View.GONE);
				base.setVisibility(View.VISIBLE);
				output.setVisibility(View.VISIBLE);
				String faTypeKey = arg2 == 0 ? "ROS" : "weekly";
				parseFAData(faMoves(team, Constants.QB, arg2), outputS, Constants.QB,
						faTypeKey, arg2);
				parseFAData(faMoves(team, Constants.RB, arg2), outputS, Constants.RB,
						faTypeKey, arg2);
				parseFAData(faMoves(team, Constants.WR, arg2), outputS, Constants.WR,
						faTypeKey, arg2);
				parseFAData(faMoves(team, Constants.TE, arg2), outputS, Constants.TE,
						faTypeKey, arg2);
				parseFAData(faMoves(team, Constants.DST, arg2), outputS, Constants.DST,
						faTypeKey, arg2);
				parseFAData(faMoves(team, Constants.K, arg2), outputS, Constants.K, faTypeKey,
						arg2);
				output.setText(outputS.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	/**
	 * Handles the iterating through the map/pq and stringbuffering
	 * 
	 * @param leadStr
	 * @param faType
	 */
	public static void parseFAData(
			Map<PlayerObject, PriorityQueue<PlayerObject>> improvementMappings,
			StringBuilder outputS, String pos, String typeKey, int faType) {
		if (ImportLeague.newImport.doesLeagueAllowPosition(pos)) {
			String leadStr = " has a " + typeKey + " ranking of ";
			outputS.append(pos + "s\n\n");
			if (improvementMappings != null && improvementMappings.size() > 0) {
				for (PlayerObject old : improvementMappings.keySet()) {
					StringBuilder outputStr = new StringBuilder(100);
					if (old.info.name.equals("None")) {
						outputStr
								.append("No players of this position are owned. ");
					} else if (faType == 0) {
						outputStr.append(old.info.name + leadStr
								+ old.values.rosRank + ", but ");
					} else {
						outputStr.append(old.info.name + leadStr
								+ old.values.ecr + ", but ");
					}
					PriorityQueue<PlayerObject> better = improvementMappings
							.get(old);
					int counter = 12;
					boolean flag = false;
					if (better.size() == 2) {
						flag = true;
					}
					while (!better.isEmpty() && counter > 0) {
						counter--;
						PlayerObject iter = better.poll();
						if (faType == 0) {
							if (!flag) {
								outputStr.append(iter.info.name + " ("
										+ iter.values.rosRank + "), ");
							} else {
								outputStr.append(iter.info.name + " ("
										+ iter.values.rosRank + ") ");
							}
						}
						if (faType == 1) {
							if (!flag) {
								outputStr.append(iter.info.name + " ("
										+ iter.values.ecr + "), ");
							} else {
								outputStr.append(iter.info.name + " ("
										+ iter.values.ecr + ") ");
							}
						}
						if (better.size() == 1 || counter == 1) {
							outputStr.append("and ");
						}
					}
					String inter = outputStr.toString();
					inter = inter.substring(0, inter.length() - 2);
					if (counter <= 9) {
						outputS.append(inter + " are all available\n\n");
					} else if (counter <= 10) {
						outputS.append(inter + ") are available\n\n");
					} else if (counter <= 11) {
						outputS.append(inter + " is available\n\n");
					}
				}
				outputS.append("\n\n");
			} else {
				outputS.append("No obvious improvements available in free agency\n\n\n");
			}
		}
	}

	/**
	 * Given a team and a position, it runs through all free agents and
	 * generates a list of maybe better players, in order of ROS rank
	 * 
	 * @param flag
	 */
	public static Map<PlayerObject, PriorityQueue<PlayerObject>> faMoves(
			TeamAnalysis team, String pos, int flag) {
		List<PlayerObject> iter = new ArrayList<PlayerObject>();
		if (flag == 0) {
			iter = team.players;
		}
		if (flag == 1) {
			String[] middle = team.stringifyLineup().toString().split(Constants.LINE_BREAK);
			for (String posIter : middle) {
				String[] players = posIter.split(": ")[1].split(", ");
				for (String name : players) {
					if (ImportLeague.holder.parsedPlayers.contains(name)) {
						for (PlayerObject iterPlayers : ImportLeague.holder.players) {
							if (iterPlayers.info.name.equals(name)
									&& iterPlayers.info.position.equals(pos)) {
								iter.add(iterPlayers);
								break;
							}
						}
					}
				}
			}
		}
		Map<PlayerObject, PriorityQueue<PlayerObject>> improvements = new HashMap<PlayerObject, PriorityQueue<PlayerObject>>();
		// If no players are owned of this position and the position is allowed
		// (to be output it must be),
		// a dummy player with crappy rankings is used to get all FAs.
		if (iter.size() == 0) {
			PlayerObject noneOwned = new PlayerObject();
			noneOwned.info.name = "None";
			noneOwned.values.rosRank = 300;
			noneOwned.values.ecr = 300.0;
			noneOwned.info.position = pos;
			iter.add(noneOwned);
		}
		for (PlayerObject player : iter) {
			if (player.info.position.equals(pos)) {
				int rosRank = 0;
				if (flag == 0) {
					rosRank = player.values.rosRank;
					if (rosRank <= 0) {
						rosRank = 100;
					}
				}
				if (flag == 1) {
					rosRank = player.values.ecr.intValue();
					if (rosRank <= 0) {
						continue;
					}
				}
				PriorityQueue<PlayerObject> sorted = null;
				if (flag == 0) {
					sorted = new PriorityQueue<PlayerObject>(100,
							new Comparator<PlayerObject>() {
								@Override
								public int compare(PlayerObject a,
										PlayerObject b) {
									if (a.values.rosRank > b.values.rosRank) {
										return 1;
									}
									if (a.values.rosRank < b.values.rosRank) {
										return -1;
									}
									return 0;
								}
							});
				} else if (flag == 1) {
					sorted = new PriorityQueue<PlayerObject>(100,
							new Comparator<PlayerObject>() {
								@Override
								public int compare(PlayerObject a,
										PlayerObject b) {
									if (a.values.ecr > b.values.ecr) {
										return 1;
									}
									if (a.values.ecr < b.values.ecr) {
										return -1;
									}
									return 0;
								}
							});
				}
				for (PlayerObject fa : freeAgents) {
					if (flag == 0) {
						if (fa.info.position.equals(pos)
								&& fa.values.rosRank > 0) {
							if (fa.values.rosRank < rosRank) {
								sorted.add(fa);
							}
						}
					}
					if (flag == 1) {
						if (fa.info.position.equals(pos) && fa.values.ecr > 0) {
							if (fa.values.ecr < rosRank) {
								sorted.add(fa);
							}
						}
					}
				}
				if (sorted.size() > 0) {
					improvements.put(player, sorted);
				}
			}
		}
		if (improvements.size() == 0) {
			return null;
		}
		return improvements;
	}
}
