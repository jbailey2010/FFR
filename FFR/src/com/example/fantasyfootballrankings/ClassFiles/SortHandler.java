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
	public static int minVal;
	public static int maxVal;
	public static int minProj;
	public static String position;
	public static String subject;
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
	public static void initialPopUp(Context cont, Storage hold)
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
	    topics.add("Projections");
	    topics.add("PAA");
	    topics.add("PAA per dollar");
	    topics.add("Target Rec oTD");
	    topics.add("Target Rec TD Difference");
	    topics.add("Average target location");
	    topics.add("Catch Rec oTD");
	    topics.add("Catch Rec TD Difference");
	    topics.add("Average catch location");
	    topics.add("Rush oTD");
	    topics.add("Rush TD Difference");
	    topics.add("Average carry location");
	    topics.add("DYOA");
	    topics.add("DVOA");
	    topics.add("Risk relative to position");
	    topics.add("Risk");
	    topics.add("Positional SOS");
	    topics.add("Weekly Trend (ESPN)");
	    topics.add("Highest Value");
	    topics.add("Lowest Value");
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
						if((subject.equals("Projections") || subject.equals("PAA") ||
								(subject.equals("PAA per dollar")) || subject.equals("Target Rec TD Difference"))
								&& (position.equals("K") || position.equals("D/ST")) || 
								((subject.equals("Target Rec oTD") || subject.equals("Rush oTD") || subject.equals("Rush TD Difference") || 
										subject.equals("Average target location") || subject.equals("Average carry location") || subject.equals("Average catch location") || 
										subject.equals("Catch Rec TD Difference") || subject.equals("Catch Rec oTD") || subject.equals("DYOA") || subject.equals("DVOA"))
										&&(position.equals("QB") || position.equals("D/ST") || position.equals("K"))))
						{
							Toast.makeText(context, "That subject is not available for that position", Toast.LENGTH_SHORT).show();
						}
						else
						{
							dialog.dismiss();
							handleSortingSec();
							//handleSortingSetUp();
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
	public static void handleSortingSec()
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
							run.isChecked(), pass.isChecked(), minimum);
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
			boolean run, boolean pass, int minimum) {
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
							String oLine = player.info.oLineStatus;
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
		handleSortingSetUp();
	}
	
	/**
	 * Gets only the players who are the right position
	 */
	public static void handleSortingSetUp()
	{
		if(subject.equals("Projections"))
		{
			projPoints();
		}
		else if(subject.equals("Auction Values"))
		{
			auctionVals();
		}
		else if(subject.equals("PAA"))
		{
			paa();
		}
		else if(subject.equals("Under Drafted"))
		{
			underDrafted();
		}
		else if(subject.equals("PAA per dollar"))
		{
			paapd();
		}
		else if(subject.equals("Target Rec oTD"))
		{
			oTD();
		}
		else if(subject.equals("Catch Rec oTD"))
		{
			coTD();
		}
		else if(subject.equals("Catch Rec TD Difference"))
		{
			ctdDiff();
		}
		else if(subject.equals("Average catch location"))
		{
			cADEZ();
		}
		else if(subject.equals("Target Rec TD Difference"))
		{
			tdDiff();
		}
		else if(subject.equals("Rush oTD"))
		{
			roTD();
		}
		else if(subject.equals("Rush TD Difference"))
		{
			rtdDiff();
		}
		else if(subject.equals("DVOA"))
		{
			dvoa();
		}
		else if(subject.equals("DYOA"))
		{
			dyoa();
		}
		else if(subject.equals("Risk relative to position"))
		{
			riskPos();
		}
		else if(subject.equals("Risk"))
		{
			risk();
		}
		else if(subject.equals("Positional SOS"))
		{
			pSOS();
		}
		else if(subject.equals("ECR"))
		{
			ecr();
		}
		else if(subject.equals("ADP"))
		{
			adp();
		}
		else if(subject.equals("Average target location"))
		{
			tadez();
		}
		else if(subject.equals("Average carry location"))
		{
			radez();
		}
		else if(subject.equals("Weekly Trend (ESPN)"))
		{
			weeklyTrend();
		}
		else if(subject.equals("Highest Value"))
		{
			highVal();
		}
		else if(subject.equals("Lowest Value"))
		{
			lowVal();
		}
	}
	
	public static void risk() {
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
				wrappingUp(sorted);
	}

	/**
	 * Calculates the most underdrafted players
	 */
	private static void underDrafted() {
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
				wrappingUp(sorted);
	}

	public static void auctionVals()
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
				wrappingUp(sorted);
	}
	
	public static void dvoa()
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
				wrappingUp(sorted);
	}
	
	public static void dyoa()
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
				wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for projected points
	 */
	public static void projPoints()
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
		wrappingUp(sorted);
	}
	
	/**
	 * Sorts by paa
	 */
	public static void paa()
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
				wrappingUp(sorted);
	}
	
	/**
	 * Sorts by paapd
	 */
	public static void paapd()
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
				wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void oTD()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.oTD > b.values.oTD)
				{
					return -1;
				}
				if(a.values.oTD < b.values.oTD)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.tADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	public static void coTD()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.coTD > b.values.coTD)
				{
					return -1;
				}
				if(a.values.coTD < b.values.coTD)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.cADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void roTD()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.roTD > b.values.roTD)
				{
					return -1;
				}
				if(a.values.roTD < b.values.roTD)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.rADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void tdDiff()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.tdDiff > b.values.tdDiff)
				{
					return -1;
				}
				if(a.values.tdDiff < b.values.tdDiff)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.tADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	public static void ctdDiff()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.ctdDiff > b.values.ctdDiff)
				{
					return 1;
				}
				if(a.values.ctdDiff < b.values.ctdDiff)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.cADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void rtdDiff()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.rtdDiff > b.values.rtdDiff)
				{
					return 1;
				}
				if(a.values.rtdDiff < b.values.rtdDiff)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.rADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void pSOS()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.info.sos > b.info.sos)
				{
					return 1;
				}
				if(a.info.sos < b.info.sos)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.info.sos != -1.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void radez()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.rADEZ > b.values.rADEZ)
				{
					return 1;
				}
				if(a.values.rADEZ < b.values.rADEZ)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.rADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	public static void cADEZ()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.cADEZ > b.values.cADEZ)
				{
					return 1;
				}
				if(a.values.cADEZ < b.values.cADEZ)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.cADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void tadez()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.tADEZ > b.values.tADEZ)
				{
					return 1;
				}
				if(a.values.tADEZ < b.values.tADEZ)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.tADEZ != 0.0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void riskPos()
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
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for the ecr
	 */
	public static void ecr()
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
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for adp
	 */
	public static void adp()
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
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue based on the Weekly Trend (ESPN) in values
	 */
	public static void weeklyTrend()
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
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue based on the high value
	 */
	public static void highVal()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.high > b.values.high)
				{
					return -1;
				}
				if(a.values.high < b.values.high)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.high != 0)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up a priority queue based on the low value
	 */
	public static void lowVal()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.low > b.values.low)
				{
					return -1;
				}
				if(a.values.low < b.values.low)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.points >= minProj && player.values.low != 100)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Puts the priority queue into the list, and sets up the dialog
	 * @param sorted
	 */
	public static void wrappingUp(PriorityQueue<PlayerObject> sorted)
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

	    while(!sorted.isEmpty())
	    {
	    	PlayerObject elem = sorted.poll();
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	String output = "";
	    	if(Draft.draftedMe(elem.info.name, holder.draft))
	    	{
	    		output = "DRAFTED (YOU) - ";
	    	}
	    	else if(Draft.isDrafted(elem.info.name, holder.draft))
	    	{
	    		output = "DRAFTED - ";
	    	}
	    	if(isHidden && !output.equals(""))
	    	{
	    		continue;
	    	}  
	    	if(subject.equals("Projections"))
			{
				datum.put("main", output + elem.values.points + ": " + elem.info.name);
				datum.put("sub", "$" + df.format(elem.values.worth));
			}
	    	else if(subject.equals("Auction Values"))
	    	{
	    		datum.put("main", output + df.format(elem.values.worth)+ ": " + elem.info.name);
	    		datum.put("sub", df.format(elem.values.paa) + " paa");
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
	    		datum.put("sub", "$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("PAA per dollar"))
	    	{ 
	    		datum.put("main", output + df.format(elem.values.paapd)+ ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("Target Rec oTD"))
	    	{
	    		datum.put("main", output + df.format(elem.values.oTD) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.tdDiff + " difference");
	    	}
	    	else if(subject.equals("Catch Rec oTD"))
	    	{
	    		datum.put("main", output + df.format(elem.values.coTD) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.ctdDiff + " difference");
	    	}
	    	else if(subject.equals("Rush oTD"))
	    	{
	    		datum.put("main", output + df.format(elem.values.roTD) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.rtdDiff + " difference");
	    	}
	    	else if(subject.equals("Average carry location"))
	    	{
	    		datum.put("main", output + df.format(elem.values.rADEZ) + ": " + elem.info.name);
	    		datum.put("sub",  elem.values.roTD + " expected rushing TDs, $" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("DYOA"))
	    	{
	    		String close1 = elem.stats.split("\\(rank\\):")[1].split("\n")[0];
				String r1 = (close1.split("\\(")[0].trim());
				datum.put("main", output + r1 + ": " + elem.info.name);
				datum.put("sub", "$" + df.format(elem.values.worth) + ", " + elem.values.points + " projected points");
	    	}
	    	else if(subject.equals("DVOA"))
	    	{
	    		String close1 = elem.stats.split("\\(rank\\):")[2].split("\n")[0];
				String r1 = close1.split("\\(")[0].trim();
				datum.put("main", output + r1 + ": " + elem.info.name);
				datum.put("sub", "$" + df.format(elem.values.worth) + ", " + elem.values.points + " projected points");
	    	}
	    	else if(subject.equals("Average target location"))
	    	{
	    		datum.put("main", output + df.format(elem.values.tADEZ) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.oTD + 
	    				" expected receiving TDs, " + elem.values.cADEZ + " average catch location, " + 
	    				"$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("Average catch location"))
	    	{
	    		datum.put("main", output + df.format(elem.values.cADEZ) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.coTD + 
	    				" expected receiving TDs, " + elem.values.tADEZ + " average target location, " + 
	    				"$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("Risk"))
	    	{
	    		datum.put("main", output + df.format(elem.risk)+ ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
	    	}
	    	else if(subject.equals("Target Rec TD Difference"))
	    	{
	    		datum.put("main", output + df.format(elem.values.tdDiff) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.oTD + " expected, had " + 
	    				df.format(elem.values.oTD - elem.values.tdDiff));
	    	}
	    	else if(subject.equals("Catch Rec TD Difference"))
	    	{
	    		datum.put("main", output + df.format(elem.values.ctdDiff) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.coTD + " expected, had " + 
	    				df.format(elem.values.coTD - elem.values.ctdDiff));
	    	}
	    	else if(subject.equals("Rush TD Difference"))
	    	{
	    		datum.put("main", output + df.format(elem.values.rtdDiff) + ": " + elem.info.name);
	    		datum.put("sub", elem.values.roTD + " expected, had " + 
	    				df.format(elem.values.roTD - elem.values.rtdDiff));
	    	}
	    	else if(subject.equals("Risk relative to position"))
			{
	    		datum.put("main",output + elem.riskPos + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Risk"))
			{
				datum.put("main", output + elem.risk + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Positional SOS"))
			{
				if(elem.values.points != 0.0)
				{
					datum.put("main",output + elem.info.sos + ": " + elem.info.name);
		    		datum.put("sub", "$" + df.format(elem.values.worth) + ", " + 
							elem.values.points);
				}
				else
				{
					datum.put("main",output + elem.info.sos + ": " + elem.info.name);
		    		datum.put("sub", "$" + df.format(elem.values.worth));
				}
			}
			else if(subject.equals("ECR"))
			{
				datum.put("main", output + elem.values.ecr + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("ADP"))
			{
				datum.put("main", output + elem.info.adp + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Weekly Trend (ESPN)"))
			{
				datum.put("main", output + elem.info.trend + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Highest Value"))
			{
				datum.put("main", output + df.format(elem.values.high) + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
			}
			else if(subject.equals("Lowest Value"))
			{
				datum.put("main", output + df.format(elem.values.low) + ": " + elem.info.name);
	    		datum.put("sub", "$" + df.format(elem.values.worth));
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
				handleSortingSetUp();
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
