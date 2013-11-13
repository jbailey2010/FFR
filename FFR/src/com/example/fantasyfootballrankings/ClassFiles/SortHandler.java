package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.ffr.fantasyfootballrankings.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

import FileIO.ReadFromFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Handles the player sorting work
 * @author Jeff
 *
 */
public class SortHandler 
{
	public static int minVal = 0;
	public static int maxVal = 100;
	public static int minProj;
	public static String position = "All Positions";
	public static String subject = "ECR";
	public static Storage holder;
	public static Context context;
	public static List<PlayerObject> players = new ArrayList<PlayerObject>();
	public static SimpleAdapter adapter;
	static boolean isHidden = false;
	public static List<Map<String, String>> data;
	static HashMap<PlayerObject, Integer> ignore = new HashMap<PlayerObject, Integer>();
	public static int listViewLookup;
	public static boolean isRankings;
	public static int status;
	public static ImportedTeam league;
	
	/**
	 * Sets up the new dialog to get all the relevant info from the user
	 * @param cont
	 * @param hold
	 */
	public static void initialPopUp(final Context cont, Storage hold, int listId, boolean flag, int playerStatusSwitch, ImportedTeam newImport)
	{
		league = newImport;
		status = playerStatusSwitch;
		isRankings = flag;
		listViewLookup = listId;
		players.clear();
		context = cont;
		holder = hold;
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sort_initial); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.sort_cancel);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    final Spinner sort = (Spinner)dialog.findViewById(R.id.sort_stat_spinner);
	    final Spinner pos = (Spinner)dialog.findViewById(R.id.sort_pos_spinner);
	    final EditText max = (EditText)dialog.findViewById(R.id.maxVal);
	    final EditText min = (EditText)dialog.findViewById(R.id.minVal);
	    List<String> topics = new ArrayList<String>();
	    List<String> positions = new ArrayList<String>();
	    //Add the topics which it can sort by
	    if(!holder.isRegularSeason)
	    {
		    topics.add("ECR");
		    topics.add("ADP");
		    topics.add("Under Drafted");
		    topics.add("Auction Values");
	    }
	    topics.add("Projections");
	    topics.add("PAA");
	    topics.add("Yard Adjustment");
	    topics.add("Completion to Int Ratio");
	    topics.add("Risk");
	    topics.add("Positional SOS");
	    if(holder.isRegularSeason)
	    {
	    	topics.add("Targets");
	    	topics.add("Weekly Positional Ranking");
	    	topics.add("Rest of Season Positional Ranking");
	    }
	    //Add the positional options
	    Roster r = ReadFromFile.readRoster(cont);
	    positions.add("All Positions");
	    if(r.qbs != 0)
	    {
	    	positions.add("QB");
	    }
	    if(r.rbs != 0)
	    {
	    	positions.add("RB");
	    }
	    if(r.wrs != 0)
	    {
	    	positions.add("WR");
	    }
	    if(r.rbs != 0 && r.wrs != 0)
	    {
	    	positions.add("RB/WR");
	    }
	    if(r.tes != 0)
	    {
	    	positions.add("TE");
	    }
	    if(r.rbs != 0 && r.wrs != 0 && r.tes != 0)
	    {
	    	positions.add("RB/WR/TE");
	    }
	    if(r.def != 0)
	    {
		    positions.add("D/ST");
	    }
	    if(r.k != 0)
	    {
	    	positions.add("K");
	    }
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, topics);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, positions);
		sort.setAdapter(spinnerArrayAdapter);
		pos.setAdapter(spinnerAdapter);
		sort.setSelection(topics.indexOf(subject));
		pos.setSelection(positions.indexOf(position));
		max.setText(Integer.toString(maxVal));
		min.setText(Integer.toString(minVal));
		Button adv = (Button)dialog.findViewById(R.id.sort_advanced);
		adv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String maxStr = max.getText().toString();
				String minStr = min.getText().toString();
				if(ManageInput.isInteger(maxStr) && ManageInput.isInteger(minStr))
				{
					minVal = Integer.parseInt(minStr);
					maxVal = Integer.parseInt(maxStr);
					if(maxVal > minVal)
					{
						position = (String)pos.getSelectedItem();
						subject = (String)sort.getSelectedItem();
						if((subject.equals("Success Rate") && !(position.equals("RB") || position.equals("All Positions"))) || 
								(subject.equals("Yard Adjustment") && (position.equals("D/ST") || position.equals("K"))) || 
										((position.equals("RB") || position.equals("RB/WR") || position.equals("RB/WR/TE") || position.equals("WR") || position.equals("TE") || position.equals("K") || position.equals("D/ST"))
										&& subject.equals("Completion to Int Ratio")) || ((subject.equals("Broken Tackles")) && 
										(position.equals("TE") || position.equals("D/ST") || position.equals("K"))) || 
										(subject.equals("Rest of Season Positional Ranking") && position.equals("D/ST")) ||
										subject.equals("Targets") && (position.equals("QB") || position.equals("D/ST") || position.equals("K")))
						{
							Toast.makeText(context, "That subject is not available for that position", Toast.LENGTH_SHORT).show();
						}
						else
						{
							dialog.dismiss();
							handleSortingSec(cont);
						}
					}
					else
					{
						Toast.makeText(context, "Please enter a number for max that's greater than the min", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, "Please enter integer values for the max/min", Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button submit = (Button) dialog.findViewById(R.id.sort_submit);
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String maxStr = max.getText().toString();
				String minStr = min.getText().toString();
				if(ManageInput.isInteger(maxStr) && ManageInput.isInteger(minStr))
				{
					minVal = Integer.parseInt(minStr);
					maxVal = Integer.parseInt(maxStr);
					if(maxVal > minVal)
					{
						position = (String)pos.getSelectedItem();
						subject = (String)sort.getSelectedItem();
						if((subject.equals("Success Rate") && !(position.equals("RB") || position.equals("All Positions"))) || 
								(subject.equals("Yard Adjustment") && (position.equals("D/ST") || position.equals("K"))) || 
										((position.equals("RB") || position.equals("WR") || position.equals("RB/WR") || position.equals("RB/WR/TE") || position.equals("TE") || position.equals("K") || position.equals("D/ST"))
										&& subject.equals("Completion to Int Ratio")) || ((subject.equals("Broken Tackles")) && 
										(position.equals("TE") || position.equals("D/ST") || position.equals("K"))) || 
										(subject.equals("Rest of Season Positional Ranking") && position.equals("D/ST")) ||
										subject.equals("Targets") && (position.equals("QB") || position.equals("D/ST") || position.equals("K")))
						{
							Toast.makeText(context, "That subject is not available for that position", Toast.LENGTH_SHORT).show();
						}
						else
						{
							dialog.dismiss();
							handleSortingBasic(cont);
						}
					}
					else
					{
						Toast.makeText(context, "Please enter a number for max that's greater than the min", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, "Please enter integer values for the max/min", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * Gets options to limit the output of the sorting
	 */
	public static void handleSortingSec(final Context cont)
	{
		final Dialog dialog = new Dialog(context, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sort_second_dialog); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    final EditText minProject = (EditText)dialog.findViewById(R.id.sort_second_min_projection);
	    Button close = (Button)dialog.findViewById(R.id.sort_second_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
	    });
	    Button back = (Button)dialog.findViewById(R.id.sort_second_back);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				initialPopUp(context, holder, listViewLookup, isRankings, status, league);
			}
	    });
	    final CheckBox age = (CheckBox)dialog.findViewById(R.id.sort_second_under_30);
	    final CheckBox wl = (CheckBox)dialog.findViewById(R.id.sort_second_watch);
	    final CheckBox cy = (CheckBox)dialog.findViewById(R.id.sort_second_contract_year);
	    final CheckBox healthy = (CheckBox)dialog.findViewById(R.id.sort_second_healthy);
	    final CheckBox run = (CheckBox)dialog.findViewById(R.id.sort_second_run);
	    final CheckBox pass = (CheckBox)dialog.findViewById(R.id.sort_second_pass);
	    final EditText minRanks = (EditText)dialog.findViewById(R.id.min_rankings);
	    Button submit = (Button)dialog.findViewById(R.id.sort_second_submit);
	    submit.setOnClickListener(new OnClickListener(){
			@Override 
			public void onClick(View v) {
				dialog.dismiss();
				String min = minRanks.getText().toString();
				String minProjection = minProject.getText().toString();
				if(ManageInput.isInteger(min) && ManageInput.isInteger(minProjection))
				{
					int minimum = Integer.parseInt(min);
					minProj = Integer.parseInt(minProjection);
					if(minimum < 0)
					{
						minimum = 1;
					}
					if(minProj < 0)
					{
						minProj = 0;
					}
					int max = 0;
					double maxProj = 0.0;
					for(PlayerObject player : holder.players)
					{
						if(player.values.count > max)
						{
							max = (int) player.values.count;
						}
						if(player.values.secWorth > maxProj)
						{
							maxProj = player.values.secWorth;
						}
					}
					if(minimum > max)
					{
						minimum = max;
					}
					if(minProj > maxProj)
					{
						minProj = (int) maxProj;
					}
					handleSecSortingOptions(age.isChecked(), wl.isChecked(), cy.isChecked(), healthy.isChecked(),
							run.isChecked(), pass.isChecked(), minimum, cont);
				}
				else
				{
					Toast.makeText(context, "Please enter a number", Toast.LENGTH_SHORT).show();
				}
			}
	    });
	}
	
	/**
	 * Handles the positions/booleans, using only the real data
	 */
	public static void handleSecSortingOptions(boolean young, boolean wl, boolean cy, boolean healthy,
			boolean run, boolean pass, int minimum, Context cont) {
		List<String> posList = new ArrayList<String>();
		if(position.equals("All Positions"))
		{
			posList.add("QB");
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("D/ST");
			posList.add("K");
		}
		else if(position.equals("RB/WR"))
		{
			posList.add("RB");
			posList.add("WR");
		}
		else if(position.equals("RB/WR/TE"))
		{
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
		}
		else
		{
			posList.add(position);
		}
		for(PlayerObject player : holder.players)
		{
			if(player.info.team.split(" ").length > 1)
			{
				if(!young || (!player.info.age.equals("0") && ManageInput.isInteger(player.info.age) && Integer.parseInt(player.info.age)<30 && young))
				{
					
					List<String> watchList = ReadFromFile.readWatchList(context);
					if(!wl || (wl && watchList.contains(player.info.name)))
					{
						if(!cy || (cy && !player.info.contractStatus.equals("Under Contract")))
						{
							if(!healthy || (healthy && player.injuryStatus.equals("Injury Status: Healthy")))
							{
								if((run || pass) && !holder.oLineAdv.containsKey(player.info.team))
								{
									continue;
								}
								String oLine = holder.oLineAdv.get(player.info.team).split("~~~~")[1];
								
								int runRank = -1;
								int passRank = -1;
								if(oLine != null && !oLine.equals("") && oLine.contains("\n"))
								{
									runRank = Integer.parseInt(oLine.split(": ")[2].split("\n")[0]);
									passRank = Integer.parseInt(oLine.split(": ")[1].split("\n")[0]);
								}
								if(!run || (run && runRank > 0 && runRank < 17))
								{
									if(!pass || (pass && passRank > 0 && passRank < 17))
									{
										if(posList.contains(player.info.position))
										{
											if(player.values.count >= minimum)
											{
												players.add(player);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		handleSortingSetUp(cont);
	}
	
	/**
	 * Does only the basic stuff
	 * @param cont
	 */
	public static void handleSortingBasic(Context cont)
	{
		List<String> posList = new ArrayList<String>();
		if(position.equals("All Positions"))
		{
			posList.add("QB");
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("D/ST");
			posList.add("K");
		}
		else if(position.equals("RB/WR"))
		{
			posList.add("RB");
			posList.add("WR");
		}
		else if(position.equals("RB/WR/TE"))
		{
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
		}
		else
		{
			posList.add(position);
		}
		for(PlayerObject player : holder.players)
		{
			if(posList.contains(player.info.position))
			{
				if(isRankings || status == 1)
				{
					players.add(player);
				}
				else
				{
					boolean isOnTeam = false;
					for(TeamAnalysis team : league.teams)
					{
						if(team.team.contains(player.info.name))
						{
							isOnTeam = true;
							if(status == 2)
							{
								players.add(player);
								break;
							}
						}
					}
					if(status == 3 && isOnTeam != true)
					{
						players.add(player);
					}
				}
			}
		}
		handleSortingSetUp(cont);
	}
	
	/**
	 * Gets only the players who are the right position
	 */
	public static void handleSortingSetUp(Context cont)
	{
		if(subject.equals("Projections"))
		{
			projPoints(cont);
		}
		else if(subject.equals("Auction Values"))
		{
			auctionVals(cont);
		}
		else if(subject.equals("PAA"))
		{
			paa(cont);
		}
		else if(subject.equals("Under Drafted"))
		{
			underDrafted(cont);
		}
		else if(subject.equals("Yard Adjustment"))
		{
			yardAdj(cont);
		}
		else if(subject.equals("Risk"))
		{
			risk(cont);
		}
		else if(subject.equals("Positional SOS"))
		{
			pSOS(cont);
		}
		else if(subject.equals("ECR"))
		{
			ecr(cont);
		}
		else if(subject.equals("ADP"))
		{
			adp(cont);
		}
		else if(subject.equals("Completion to Int Ratio"))
		{
			compInt(cont);
		}
		else if(subject.equals("Targets"))
		{
			targets(cont);
		}
		else if(subject.equals("Weekly Positional Ranking"))
		{
			weeklyRank(cont);
		}
		else if(subject.equals("Rest of Season Positional Ranking"))
		{
			rosRank(cont);
		}
	}
	
	public static void rosRank(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.rosRank > b.values.rosRank)
						{
							return 1;
						}
						if(a.values.rosRank < b.values.rosRank)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && 
							player.values.rosRank > 0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	/**
	 * Handles the sorting by weekly positional ranking
	 * @param cont
	 */
	public static void weeklyRank(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.ecr > b.values.ecr)
						{
							return 1;
						}
						if(a.values.ecr < b.values.ecr)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && 
							player.values.ecr > 0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	
	/**
	 * Handles the sorting by target count
	 * @param cont
	 */
	public static void targets(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						int aVal = Integer.valueOf(a.stats.split("Targets: ")[1].split("\n")[0]);
						int bVal = Integer.valueOf(b.stats.split("Targets: ")[1].split("\n")[0]);
						if(aVal > bVal)
						{
							return -1;
						}
						if(aVal < bVal)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj
							 && player.stats.contains("Targets"))
					{
						sorted.add(player);
					}
				}
				if(sorted.size() == 0)
				{
					Toast.makeText(cont, "Targets are not yet an available stat", Toast.LENGTH_SHORT).show();
					initialPopUp(cont, holder, listViewLookup, isRankings, status, league);
				}
				else
				{
					wrappingUp(sorted, cont);
				}
	}

	private static void compInt(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						String intsA = a.stats.split("Interceptions: ")[1].split("\n")[0];
						int intA = Integer.parseInt(intsA);
						String compA = a.stats.split("Completion Percentage: ")[1].split("\n")[0].replace("%", "");
						double compPercent = Double.parseDouble(compA)/100.0;
						double attempts = Double.parseDouble(a.stats.split("Pass Attempts: ")[1].split("\n")[0]);
						double completions = attempts * compPercent;
						double aDiff = (completions)/((double)intA);
						String intsB = b.stats.split("Interceptions: ")[1].split("\n")[0];
						int intB = Integer.parseInt(intsB);
						String compB = b.stats.split("Completion Percentage: ")[1].split("\n")[0].replace("%", "");
						double compPercentB = Double.parseDouble(compB)/100.0;
						double attemptsB = Double.parseDouble(b.stats.split("Pass Attempts: ")[1].split("\n")[0]);
						double completionsB = attemptsB * compPercentB;
						double aDiff2 = (completionsB)/((double)intB);
						if(aDiff > aDiff2)
						{
							return -1;
						}
						if(aDiff < aDiff2)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && 
							player.stats.contains("Completion Percentage") && player.stats.contains("Interceptions"))
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	public static void yardAdj(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						String yardsStr = a.stats.split("Yards: ")[1].split("\n")[0];
						int yards = Integer.parseInt(yardsStr.replaceAll(",", ""));
						String adjStr = a.stats.split("Adjusted Yards: ")[1].split("\n")[0];
						int adjYards = Integer.parseInt(adjStr.replaceAll(",", ""));
						int aDiff = adjYards - yards;
						String yardsStr2 = b.stats.split("Yards: ")[1].split("\n")[0];
						int yards2 = Integer.parseInt(yardsStr2.replaceAll(",", ""));
						int aDiff2 = -1000;
						String adjStr2 = b.stats.split("Adjusted Yards: ")[1].split("\n")[0];
						int adjYards2 = Integer.parseInt(adjStr2.replaceAll(",", ""));
						aDiff2 = adjYards2 - yards2;
						if(aDiff > aDiff2)
						{
							return -1;
						}
						if(aDiff < aDiff2)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && 
							player.stats.contains("Yards") && player.stats.contains("Adjusted Yards") && 
							!player.stats.split("Adjusted Yards: ")[1].split("\n")[0].contains("%"))
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	
	public static void risk(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.risk > b.risk)
						{
							return 1;
						}
						if(a.risk < b.risk)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && player.risk != -1 && 
							player.risk != -1.0 && player.risk != 0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	/**
	 * Calculates the most underdrafted players
	 */
	private static void underDrafted(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						double aDiff = 0;
						double bDiff = 0;
						aDiff = Double.parseDouble(a.info.adp) - a.values.ecr;
						bDiff = Double.parseDouble(b.info.adp) - b.values.ecr; 
						if(aDiff > bDiff)
						{
							return -1;
						}
						if(bDiff > aDiff)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && player.values.ecr != -1.0 && 
							!player.info.adp.equals("Not set"))
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	public static void auctionVals(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.worth > b.values.worth)
						{
							return -1;
						}
						if(a.values.worth < b.values.worth)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	
	/**
	 * Sets up the priority queue for projected points
	 */
	public static void projPoints(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.points > b.values.points)
				{
					return -1;
				}
				if(a.values.points < b.values.points)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && player.values.points != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	/**
	 * Sorts by paa
	 */
	public static void paa(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.paa > b.values.paa)
						{
							return -1;
						}
						if(a.values.paa < b.values.paa)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && player.values.points != 0.0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void pSOS(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(holder.sos.get(a.info.team + "," + a.info.position) > holder.sos.get(b.info.team + "," + b.info.position))
				{
					return 1;
				}
				if(holder.sos.get(a.info.team + "," + a.info.position) < holder.sos.get(b.info.team + "," + b.info.position))
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && 
					holder.sos.get(player.info.team + "," + player.info.position) != null && 
					holder.sos.get(player.info.team + "," + player.info.position) > 0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	/**
	 * Sets up the priority queue for the ecr
	 */
	public static void ecr(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.ecr > b.values.ecr)
				{
					return 1;
				}
				if(a.values.ecr < b.values.ecr)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && player.values.ecr != -1)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	/**
	 * Sets up the priority queue for adp
	 */
	public static void adp(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(Double.parseDouble(a.info.adp) > Double.parseDouble(b.info.adp))
				{
					return 1;
				}
				if(Double.parseDouble(a.info.adp) < Double.parseDouble(b.info.adp))
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.secWorth >= minVal && player.values.secWorth < maxVal && player.values.points >= minProj && !player.info.adp.equals("Not set"))
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	/**
	 * Puts the priority queue into the list, and sets up the dialog
	 * @param sorted
	 */
	public static void wrappingUp(PriorityQueue<PlayerObject> sorted, final Context cont)
	{		
		DecimalFormat df = new DecimalFormat("#.##");
		BounceListView results = null;
		if(isRankings)
		{
			final Dialog dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.search_output); 
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(dialog.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		    dialog.getWindow().setAttributes(lp);
		    dialog.show(); 
		    RelativeLayout base = (RelativeLayout)dialog.findViewById(R.id.info_sub_header);
			base.setVisibility(View.GONE);
			LinearLayout base2 = (LinearLayout)dialog.findViewById(R.id.category_base);
			Button back = (Button)base2.findViewById(R.id.category_ranking);
			back.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					initialPopUp(context, holder, listViewLookup, isRankings, status, league);
				}
		    });
			back.setText("Back");
			back.setTypeface(null, Typeface.NORMAL);
			back.setTextSize(13);
			Button watch = (Button)base2.findViewById(R.id.category_info);
			Button hidden = (Button)base2.findViewById(R.id.category_team);
			View backView = (View)dialog.findViewById(R.id.back_view);
			View addView = (View)dialog.findViewById(R.id.add_view);
			backView.setVisibility(View.GONE);
			addView.setVisibility(View.GONE);
			hidden.setText("Scroll to Top");
			hidden.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					BounceListView r = (BounceListView)dialog.findViewById(listViewLookup);
					r.smoothScrollToPosition(0);
				}
				
			});
			Button graph = (Button)base2.findViewById(R.id.category_other);
			graph.setText("Graph");
			graph.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					buildGraph(context, adapter, subject, position, "");
				}
			});
			//base2.setVisibility(View.GONE);
		    Button watch2 = (Button)dialog.findViewById(R.id.add_watch);
		    watch2.setVisibility(View.GONE);
		    watch.setText("Hide Drafted");
		    TextView header = (TextView)dialog.findViewById(R.id.name);
		    header.setText(subject);
		    results = (BounceListView)dialog.findViewById(listViewLookup);
		    Button back2 = (Button)dialog.findViewById(R.id.search_back);
		    back2.setVisibility(View.GONE);
		    Button close = (Button)dialog.findViewById(R.id.search_close);
		    close.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					return;
				}
		    });
		    results.setAdapter(null);
		    watch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					isHidden = !isHidden;
					dialog.dismiss();
					if(isHidden)
					{
						Toast.makeText(context, "Hiding the drafted players", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(context, "Showing the drafted players", Toast.LENGTH_SHORT).show();
					}
					handleSortingSetUp(cont);
				}
		    });
		}
		else
		{
			results = (BounceListView)((Activity)cont).findViewById(listViewLookup);
			results.setAdapter(null);
		}
	    data = new ArrayList<Map<String, String>>();
	    if(sorted.size() == 0)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("main", "No results were found with this combination of requests");
	    	datum.put("sub", "Try broadening your search a bit");
	    	data.add(datum);
	    }
	    int count = 0;
	    Roster r = ReadFromFile.readRoster(cont);
	    String baseECR = "ECR: ";
	    if(holder.isRegularSeason)
	    {
	    	baseECR = "Weekly ECR: ";
	    }
	    while(!sorted.isEmpty())
	    {
	    	PlayerObject elem = sorted.poll();
	    	if(r.isRostered(elem))
	    	{
		    	if(elem.values.ecr == -1)
		    	{
		    		elem.values.ecr = 300.0;
		    	}
		    	Map<String, String> datum = new HashMap<String, String>(2);
		    	String output = "";
		    	count++;
		    	output = String.valueOf(count) + ") ";
		    	if(Draft.draftedMe(elem.info.name, holder.draft))
		    	{
		    		output += "DRAFTED (YOU) - ";
		    	}
		    	else if(Draft.isDrafted(elem.info.name, holder.draft))
		    	{
		    		output += "DRAFTED - ";
		    	}
		    	if(isHidden && !output.equals(String.valueOf(count) + ") "))
		    	{
		    		continue;
		    	}  
		    	if(subject.equals("Projections"))
				{
					datum.put("main", output + elem.values.points + ": " + elem.info.name);
					datum.put("sub", baseECR + elem.values.ecr);
				}
		    	else if(subject.equals("Auction Values"))
		    	{
		    		if(elem.values.secWorth > 0)
		    		{
		    			datum.put("main", output + df.format(elem.values.secWorth) + ": " + elem.info.name);
		    		}
		    		else
		    		{
		    			datum.put("main", output + df.format(elem.values.worth)+ ": " + elem.info.name );
		    		}
		    		datum.put("sub", baseECR + elem.values.ecr + ", " + df.format(elem.values.paa) + " PAA");
		    	}
		    	else if(subject.equals("Under Drafted"))
		    	{
		    		double diff = Double.parseDouble(elem.info.adp) - elem.values.ecr;
		    		datum.put("main", output + df.format(diff)+ ": " + elem.info.name);
		    		datum.put("sub", "Projection: " + elem.values.points + "\n" + "ADP: " + elem.info.adp + ", " + baseECR + elem.values.ecr);
		    	}
		    	else if(subject.equals("PAA"))
		    	{
		    		datum.put("main", output + df.format(elem.values.paa)+ ": " + elem.info.name);
		    		datum.put("sub", baseECR + elem.values.ecr);
		    	}
		    	else if(subject.equals("Risk"))
		    	{
		    		datum.put("main", output + df.format(elem.risk)+ ": " + elem.info.name);
		    		datum.put("sub", baseECR + elem.values.ecr);
		    	}
				else if(subject.equals("Risk"))
				{
					datum.put("main", output + elem.risk + ": " + elem.info.name);
		    		datum.put("sub", baseECR + elem.values.ecr );
				}
				else if(subject.equals("Positional SOS"))
				{
					if(elem.values.points != 0.0)
					{
						datum.put("main",output + holder.sos.get(elem.info.team + "," + elem.info.position) + ": " + elem.info.name);
			    		datum.put("sub", baseECR + elem.values.ecr + ", " + 
								elem.values.points);
			    	}
					else
					{
						datum.put("main",output + holder.sos.get(elem.info.team + "," + elem.info.position) + ": " + elem.info.name);
			    		datum.put("sub", baseECR + elem.values.ecr);
					}
				}
				else if(subject.equals("ECR"))
				{
					datum.put("main", output + elem.values.ecr + ": " + elem.info.name);
		    		datum.put("sub", "");
				}
				else if(subject.equals("ADP"))
				{
					datum.put("main", output + elem.info.adp + ": " + elem.info.name);
		    		datum.put("sub", baseECR + elem.values.ecr);
				}
				else if(subject.equals("Yard Adjustment"))
				{
					String yardsStr = elem.stats.split("Yards: ")[1].split("\n")[0];
					int yards = Integer.parseInt(yardsStr.replaceAll(",", ""));
					String adjStr = elem.stats.split("Adjusted Yards: ")[1].split("\n")[0];
					int adjYards = Integer.parseInt(adjStr.replaceAll(",", ""));
					int aDiff = adjYards - yards;
					datum.put("main", output + aDiff + ": " + elem.info.name);
					datum.put("sub", "Actual: " + yards + ", Adjusted: " + adjYards + ", ECR: " + elem.values.ecr);
				}
				else if(subject.equals("Completion to Int Ratio"))
				{
					String intsA = elem.stats.split("Interceptions: ")[1].split("\n")[0];
					int intA = Integer.parseInt(intsA);
					String compA = elem.stats.split("Completion Percentage: ")[1].split("\n")[0].replace("%", "");
					double compPercent = Double.parseDouble(compA)/100.0;
					double attempts = Double.parseDouble(elem.stats.split("Pass Attempts: ")[1].split("\n")[0]);
					double completions = attempts * compPercent;
					double aDiff = (completions)/((double)intA);
					datum.put("main", output + df.format(aDiff) + ": " + elem.info.name);
					datum.put("sub", baseECR + elem.values.ecr);
				}
				else if(subject.equals("Targets"))
				{
					int targets = Integer.valueOf(elem.stats.split("Targets: ")[1].split("\n")[0]);
					datum.put("main", output + targets + ": "+  elem.info.name);
					datum.put("sub", elem.values.rosRank + " ROS Positional Ranking");
				}
				else if(subject.equals("Weekly Positional Ranking"))
				{
					datum.put("main", output + elem.values.ecr + ": " + elem.info.name);
					datum.put("sub", elem.values.points + " Projected Points This Week, " + elem.values.rosRank + " ROS Positional Ranking");
				}
				else if(subject.equals("Rest of Season Positional Ranking"))
				{
					datum.put("main", output + elem.values.rosRank + ": " + elem.info.name);
					datum.put("sub", elem.values.ecr + " Weekly Positional Ranking");
				}
		    	data.add(datum);
	    	}
		} 
	    adapter = new SimpleAdapter(context, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    results.setAdapter(adapter);
	    handleOnClicks(results);
	}
	
	/**
	 * Swipte to hide and click for more info
	 * @param results
	 */
	public static void handleOnClicks(final BounceListView results)
	{
		results.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				results.setSelection(arg2);
				String tv1 = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				String selected = tv1.split(": ")[1];
				PlayerInfo obj = new PlayerInfo();
				obj.outputResults(selected, true, (Activity)context, holder, false, false);
			}
    	 });
		SwipeDismissListViewTouchListener touchListener =
                 new SwipeDismissListViewTouchListener(
                         results,
                         new SwipeDismissListViewTouchListener.OnDismissCallback() {
                             @Override
                             public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                 for (int position : reverseSortedPositions) {
                                     data.remove(position);
                                 }
                                 adapter.notifyDataSetChanged();
                                 Toast.makeText(context, "Temporarily hiding this player", Toast.LENGTH_SHORT).show();
                             }
                         });
         results.setOnTouchListener(touchListener);
         results.setOnScrollListener(touchListener.makeScrollListener());
         if(!isRankings)
         {
        	 results.setOnTouchListener(new ListView.OnTouchListener() {
                 @Override
                 public boolean onTouch(View v, MotionEvent event) {
                 	int action = event.getAction();
                     switch (action) {
                     case MotionEvent.ACTION_DOWN:
                         // Disallow ScrollView to intercept touch events.
                         v.getParent().requestDisallowInterceptTouchEvent(true);
                         break;

                     case MotionEvent.ACTION_UP:
                         // Allow ScrollView to intercept touch events.
                         v.getParent().requestDisallowInterceptTouchEvent(false);
                         break;
                     }

                     // Handle ListView touch events.
                     v.onTouchEvent(event);
                     return true;
                 }
             });
         }
	}
	
	/**
	 * Shows the graph of the content in the output
	 * @param a 
	 */
	public static void buildGraph(Context cont, SimpleAdapter a, String subject, String position, String addit)
	{
		String team = position;
		String header = "Sorted by " + subject + addit;
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.team_info_popup);
		TextView sub = (TextView) popUp.findViewById(R.id.textView1);
		TextView head = (TextView)popUp.findViewById(R.id.team_info_popup_header);
		head.setText(header);
		sub.setText(team);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    Button close = (Button)popUp.findViewById(R.id.team_info_close);
	    close.setOnClickListener(new OnClickListener(){
		@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
	    });
		popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    GraphViewStyle gvs = new GraphViewStyle();
		gvs.setTextSize(12);
		GraphView graphView = new LineGraphView(cont, "");
		graphView.setGraphViewStyle(gvs);
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[data.size()];
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
		for(int i = 0; i < a.getCount(); i++)
		{
			Map<String, String> datum = data.get(i);
			if(datum.get("main").contains(")"))
			{
				dataSet[i] = new GraphViewData(i, Double.valueOf(datum.get("main").split(":")[0].split("\\)")[1]));
			}
			else
			{
				dataSet[i] = new GraphViewData(i, Double.valueOf(datum.get("main").split(":")[0]));
			}
		}
		GraphViewSeries es = new GraphViewSeries(subject, seriesStyle, dataSet);
		graphView.addSeries(es);
		((LineGraphView)graphView).setDrawBackground(true);
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);

	}
}
