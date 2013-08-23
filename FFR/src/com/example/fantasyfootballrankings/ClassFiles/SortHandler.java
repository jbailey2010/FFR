package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import jeff.isawesome.fantasyfootballrankings.R;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.Pages.Rankings;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

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
	
	/**
	 * Sets up the new dialog to get all the relevant info from the user
	 * @param cont
	 * @param hold
	 */
	public static void initialPopUp(final Context cont, Storage hold)
	{
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
	    topics.add("ECR");
	    topics.add("ADP");
	    topics.add("Under Drafted");
	    topics.add("Auction Values");
	    topics.add("Leverage");
	    topics.add("Projections");
	    topics.add("PAA");
	    topics.add("PAA per dollar");
	    topics.add("DYOA");
	    topics.add("DVOA");
	    topics.add("Success Rate");
	    topics.add("Yard Adjustment");
	    topics.add("Broken Tackles");
	    topics.add("Completion to Int Ratio");
	    topics.add("Risk relative to position");
	    topics.add("Risk");
	    topics.add("Positional SOS");
	    topics.add("Weekly Trend (ESPN)");
	    //Add the positional options
	    positions.add("All Positions");
	    positions.add("QB");
	    positions.add("RB");
	    positions.add("WR");
	    positions.add("TE");
	    positions.add("D/ST");
	    positions.add("K");
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
						if((subject.equals("Projections") || subject.equals("PAA") ||
								(subject.equals("PAA per dollar")) || subject.equals("Target Rec TD Difference") || subject.equals("Leverage"))
								&& (position.equals("K") || position.equals("D/ST")) || 
								((subject.equals("Target Rec oTD") || subject.equals("Rush oTD") || subject.equals("Rush TD Difference") || 
										subject.equals("Average target location") || subject.equals("Average carry location") || subject.equals("Average catch location") || 
										subject.equals("Catch Rec TD Difference") || subject.equals("Catch Rec oTD") || subject.equals("DYOA") || subject.equals("DVOA")
										|| subject.equals("Avg catch relative to target"))
										&&(position.equals("QB") || position.equals("D/ST") || position.equals("K"))) 
										|| (subject.equals("Success Rate") && 
												!(position.equals("RB") || position.equals("All Positions"))) || (subject.equals("Yard Adjustment") && 
														(position.equals("D/ST") || position.equals("K"))) || 
										((position.equals("RB") || position.equals("WR") || position.equals("TE") || position.equals("K") || position.equals("D/ST"))
												&& subject.equals("Completion to Int Ratio")) || ((subject.equals("Broken Tackles")) && 
														(position.equals("TE") || position.equals("D/ST") || position.equals("K"))))
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
						if((subject.equals("Projections") || subject.equals("PAA") ||subject.equals("Leverage") || 
								(subject.equals("PAA per dollar")) || subject.equals("Target Rec TD Difference"))
								&& (position.equals("K") || position.equals("D/ST")) || 
								((subject.equals("Target Rec oTD") || subject.equals("Rush oTD") || subject.equals("Rush TD Difference") || 
										subject.equals("Average target location") || subject.equals("Average carry location") || subject.equals("Average catch location") || 
										subject.equals("Catch Rec TD Difference") || subject.equals("Catch Rec oTD") || subject.equals("DYOA") || subject.equals("DVOA")
										|| subject.equals("Avg catch relative to target"))
										&&(position.equals("QB") || position.equals("D/ST") || position.equals("K"))) || (subject.equals("Success Rate") && 
												!(position.equals("RB") || position.equals("All Positions"))) || (subject.equals("Yard Adjustment") && 
														(position.equals("D/ST") || position.equals("K"))) || 
														((position.equals("RB") || position.equals("WR") || position.equals("TE") || position.equals("K") || position.equals("D/ST"))
																&& subject.equals("Completion to Int Ratio")) || ((subject.equals("Broken Tackles")) && 
																		(position.equals("TE") || position.equals("D/ST") || position.equals("K"))))
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
				initialPopUp(context, holder);
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
						if(player.values.worth > maxProj)
						{
							maxProj = player.values.worth;
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
		else
		{
			posList.add(position);
		}
		for(PlayerObject player : holder.players)
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
							String oLine = holder.oLineRanks.get(player.info.team);//player.info.oLineStatus;
							
							int runRank = -1;
							int passRank = -1;
							if(oLine != null && !oLine.equals("") && oLine.contains("\n"))
							{
								runRank = Integer.parseInt(oLine.split(": ")[2].split("\n")[0]);
								passRank = Integer.parseInt(oLine.split(": ")[3].split("\n")[0]);
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
		else
		{
			posList.add(position);
		}
		for(PlayerObject player : holder.players)
		{
			if(posList.contains(player.info.position))
			{
				players.add(player);
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
		else if(subject.equals("PAA per dollar"))
		{
			paapd(cont);
		}
		else if(subject.equals("DVOA"))
		{
			dvoa(cont);
		}
		else if(subject.equals("DYOA"))
		{
			dyoa(cont);
		}
		else if(subject.equals("Success Rate"))
		{
			success(cont);
		}
		else if(subject.equals("Yard Adjustment"))
		{
			yardAdj(cont);
		}
		else if(subject.equals("Risk relative to position"))
		{
			riskPos(cont);
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
		else if(subject.equals("Weekly Trend (ESPN)"))
		{
			weeklyTrend(cont);
		}
		else if(subject.equals("Completion to Int Ratio"))
		{
			compInt(cont);
		}
		else if(subject.equals("Broken Tackles"))
		{
			brokenTackles(cont);
		}
		else if(subject.equals("Leverage"))
		{
			leverage(cont);
		}
	}


	private static void brokenTackles(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						int aDiff = Integer.parseInt(a.stats.split("Broken Tackles: ")[1].split(", ")[0]);
						int aDiff2 = Integer.parseInt(b.stats.split("Broken Tackles: ")[1].split(", ")[0]);
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && 
							player.stats.contains("Broken Tackles") )
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && 
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && 
							player.stats.contains("Yards") && player.stats.contains("Adjusted Yards") && 
							!player.stats.split("Adjusted Yards: ")[1].split("\n")[0].contains("%"))
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}

	
	public static void success(Context cont) {
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						String aS = a.stats.split("Success Rate: ")[1].split("\n")[0];
						String bS = b.stats.split("Success Rate: ")[1].split("\n")[0];
						int sr1 = Integer.parseInt(aS.substring(0, aS.length()-1));
						int sr2 = Integer.parseInt(bS.substring(0, aS.length()-1));
						if(sr1 > sr2)
						{
							return -1;
						}
						if(sr1 < sr2)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj 
							&& player.stats.contains("Success Rate"))
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.risk != -1 && 
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
						aDiff = Integer.parseInt(a.info.adp) - a.values.ecr;
						bDiff = Integer.parseInt(b.info.adp) - b.values.ecr; 
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.ecr != -1.0 && 
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	public static void leverage(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.leverage > b.values.leverage)
						{
							return -1;
						}
						if(a.values.leverage < b.values.leverage)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && 
							player.values.leverage != 0.0 && player.values.relPoints != 0.0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	public static void dvoa(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						String close = a.stats.split("\\(rank\\)")[2].split("\n")[0];
						String close2 = b.stats.split("\\(rank\\)")[2].split("\n")[0];
						int r1 = Integer.parseInt(close.split(" ")[2].replaceAll("\\(", "").replaceAll("\\)", ""));
						int r2 = Integer.parseInt(close2.split(" ")[2].replaceAll("\\(", "").replaceAll("\\)", ""));
						if(r1 > r2)
						{
							return 1;
						}
						if(r1 < r2)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.stats.contains("(rank)"))
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	public static void dyoa(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						String close = a.stats.split("\\(rank\\)")[1].split("\n")[0];
						String close2 = b.stats.split("\\(rank\\)")[1].split("\n")[0];
						int r1 = Integer.parseInt(close.split(" ")[2].replaceAll("\\(", "").replaceAll("\\)", ""));
						int r2 = Integer.parseInt(close2.split(" ")[2].replaceAll("\\(", "").replaceAll("\\)", ""));
						if(r1 > r2)
						{
							return 1;
						}
						if(r1 < r2)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.stats.contains("(rank)"))
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
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.points != 0.0)
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
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.points != 0.0)
					{
						sorted.add(player);
					}
				}
				wrappingUp(sorted, cont);
	}
	
	/**
	 * Sorts by paapd
	 */
	public static void paapd(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.paapd > b.values.paapd)
						{
							return -1;
						}
						if(a.values.paapd < b.values.paapd)
						{
							return 1;
						}
						return 0;
					}
				});
				for(PlayerObject player : players)
				{
					if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.points != 0.0)
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
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && 
					holder.sos.get(player.info.team + "," + player.info.position) != null && 
					holder.sos.get(player.info.team + "," + player.info.position) > 0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void riskPos(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.riskPos > b.riskPos)
				{
					return 1;
				}
				if(a.riskPos < b.riskPos)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.riskPos != 1.0)
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
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.ecr != -1)
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
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && !player.info.adp.equals("Not set"))
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted, cont);
	}
	
	/**
	 * Sets up the priority queue based on the Weekly Trend (ESPN) in values
	 */
	public static void weeklyTrend(Context cont)
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(Double.parseDouble(a.info.trend) > Double.parseDouble(b.info.trend))
				{
					return -1;
				}
				if(Double.parseDouble(a.info.trend) < Double.parseDouble(b.info.trend))
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && !player.info.trend.equals("0.0"))
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
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.search_output); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show(); 
	    RelativeLayout base = (RelativeLayout)dialog.findViewById(R.id.info_sub_header);
		base.setVisibility(View.GONE);
	    Button watch = (Button)dialog.findViewById(R.id.add_watch);
	    watch.setText("Hide Drafted");
	    TextView header = (TextView)dialog.findViewById(R.id.name);
	    header.setText(subject);
	    final BounceListView results = (BounceListView)dialog.findViewById(R.id.listview_search);
	    header.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				results.smoothScrollToPosition(0);
			}
	    	
	    });
	    Button back = (Button)dialog.findViewById(R.id.search_back);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				initialPopUp(context, holder);
			}
	    });
	    Button close = (Button)dialog.findViewById(R.id.search_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    results.setAdapter(null);
	    List<String> rankings = new ArrayList<String>(400);
	    int counter = 0;
	    data = new ArrayList<Map<String, String>>();
	    if(sorted.size() == 0)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("main", "No results were found with this combination of requests");
	    	datum.put("sub", "Try broadening your search a bit");
	    	data.add(datum);
	    }
	    int count = 0;
	    while(!sorted.isEmpty())
	    {
	    	PlayerObject elem = sorted.poll();
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
	    	if(isHidden && !output.equals(""))
	    	{
	    		continue;
	    	}  
	    	if(subject.equals("Projections"))
			{
				datum.put("main", output + elem.values.points + ": " + elem.info.name);
				datum.put("sub", "ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth));
			}
	    	else if(subject.equals("Auction Values"))
	    	{
	    		datum.put("main", output + df.format(elem.values.worth)+ ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + df.format(elem.values.paa) + " PAA");
	    	}
	    	else if(subject.equals("Under Drafted"))
	    	{
	    		double diff = Integer.parseInt(elem.info.adp) - elem.values.ecr;
	    		datum.put("main", output + df.format(diff)+ ": " + elem.info.name);
	    		datum.put("sub", "$" +df.format(elem.values.worth)+ ", Projection: " + elem.values.points + "\n" + "ADP: " + elem.info.adp + ", " + "ECR: " + elem.values.ecr);
	    	}
	    	else if(subject.equals("PAA"))
	    	{
	    		datum.put("main", output + df.format(elem.values.paa)+ ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("PAA per dollar"))
	    	{ 
	    		datum.put("main", output + df.format(elem.values.paapd)+ ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("DYOA"))
	    	{
	    		String close1 = elem.stats.split("\\(rank\\):")[1].split("\n")[0];
				String r1 = (close1.split("\\(")[0].trim());
				datum.put("main", output + r1 + ": " + elem.info.name);
				datum.put("sub", "ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth) + ", " + elem.values.points + " projected points");
	    	}
	    	else if(subject.equals("DVOA"))
	    	{
	    		String close1 = elem.stats.split("\\(rank\\):")[2].split("\n")[0];
				String r1 = close1.split("\\(")[0].trim();
				datum.put("main", output + r1 + ": " + elem.info.name);
				datum.put("sub", "ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth) + ", " + elem.values.points + " projected points");
	    	}
	    	else if(subject.equals("Risk"))
	    	{
	    		datum.put("main", output + df.format(elem.risk)+ ": " + elem.info.name);
	    		datum.put("sub", elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("Risk relative to position"))
			{
	    		datum.put("main",output + elem.riskPos + ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Risk"))
			{
				datum.put("main", output + elem.risk + ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Positional SOS"))
			{
				if(elem.values.points != 0.0)
				{
					datum.put("main",output + holder.sos.get(elem.info.team + "," + elem.info.position) + ": " + elem.info.name);
		    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth) + ", " + 
							elem.values.points);
				}
				else
				{
					datum.put("main",output + holder.sos.get(elem.info.team + "," + elem.info.position) + ": " + elem.info.name);
		    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
				}
			}
			else if(subject.equals("ECR"))
			{
				datum.put("main", output + elem.values.ecr + ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("ADP"))
			{
				datum.put("main", output + elem.info.adp + ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Weekly Trend (ESPN)"))
			{
				datum.put("main", output + elem.info.trend + ": " + elem.info.name);
	    		datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Success Rate"))
			{
				String aS = elem.stats.split("Success Rate: ")[1].split("\n")[0];
				int sr1 = Integer.parseInt(aS.substring(0, aS.length()-1));
				datum.put("main", output + sr1 + ": " + elem.info.name);
				datum.put("sub", "ECR: " + elem.values.ecr + ", ADP: " + elem.info.adp + ", $" + df.format(elem.values.worth));
			}
			else if(subject.equals("Yard Adjustment"))
			{
				String yardsStr = elem.stats.split("Yards: ")[1].split("\n")[0];
				int yards = Integer.parseInt(yardsStr.replaceAll(",", ""));
				String adjStr = elem.stats.split("Adjusted Yards: ")[1].split("\n")[0];
				int adjYards = Integer.parseInt(adjStr.replaceAll(",", ""));
				int aDiff = adjYards - yards;
				datum.put("main", output + aDiff + ": " + elem.info.name);
				datum.put("sub", "Actual: " + yards + ", Adjusted: " + adjYards + ", ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth));
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
				datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Broken Tackles"))
			{
				int aDiff = Integer.parseInt(elem.stats.split("Broken Tackles: ")[1].split(", ")[0]);
				datum.put("main", output + aDiff + ": " + elem.info.name);
				datum.put("sub", "ECR: " + elem.values.ecr + ", " + "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Leverage"))
			{
				datum.put("main", output + df.format(elem.values.leverage) + ": " + elem.info.name);
				datum.put("sub", df.format(elem.values.relPrice) + " relative price, " + df.format(elem.values.relPoints) + " relative points\n" + 
						"ECR: " + elem.values.ecr + ", $" + df.format(elem.values.worth));
			}
	    	data.add(datum);
		} 
	    adapter = new SimpleAdapter(context, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    results.setAdapter(adapter);
	    //adapter = ManageInput.handleArray(rankings, results, (Activity) context);
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
				String tv1 = ((TwoLineListItem)arg1).getText1().getText().toString();
				String selected = tv1.split(": ")[1];
				PlayerInfo.outputResults(selected, true, (Rankings)context, holder, false, false);
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
	}
}
