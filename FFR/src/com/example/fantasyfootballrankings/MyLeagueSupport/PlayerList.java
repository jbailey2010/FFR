package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.io.IOException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import FileIO.ReadFromFile;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.SortHandler;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.NDSpinner;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;
/**
 * A static library to handle the player list work in importleague
 * @author Jeff
 *
 */
public class PlayerList {
	private static Context cont;
	private static ImportedTeam newImport;
	public static List<Map<String, String>>data;
	public static SimpleAdapter adapter;
	/**
	 * Handles the initial set up of the list
	 * @param res
	 */
	public static void setPlayerInfoList(View res, Context c, ImportedTeam n)
	{
		cont = c;
		newImport = n;
		data = new ArrayList<Map<String, String>>();
		adapter = new SimpleAdapter(cont, data, 
		    		R.layout.web_listview_item, 
		    		new String[] {"main", "sub"}, 
		    		new int[] {R.id.text1, 
		    			R.id.text2});
		 final ListView list = (ListView)res.findViewById(R.id.imported_teams_players);
		 list.setAdapter(adapter);
		 final Spinner pos = (Spinner)res.findViewById(R.id.player_pos_spinner);
		 final Spinner status = (Spinner)res.findViewById(R.id.player_status_spinner);
		 final NDSpinner sortSp = (NDSpinner)res.findViewById(R.id.player_sort_spinner);
		 pos.clearFocus();
		 status.clearFocus();
		 list.clearFocus();
		 sortSp.clearFocus();
		 List<String> positions = new ArrayList<String>();
		 positions.add("All Positions");
		 positions.add("QB");
		 positions.add("RB");
		 positions.add("WR");
		 positions.add("RB/WR");
		 positions.add("TE");
		 positions.add("RB/WR/TE");
		 positions.add("QB/RB/WR/TE");
		 positions.add("D/ST");
		 positions.add("K");
		 List<String> playerStatus = new ArrayList<String>();
		 playerStatus.add("All Players");
		 playerStatus.add("Free Agents");
		 playerStatus.add("On Team");
		 List<String> sortFactors = new ArrayList<String>();
		 sortFactors.add("PAA");
		 sortFactors.add("Projection");
		 sortFactors.add("Weekly Ranking");
		 sortFactors.add("ROS Ranking");
		 sortFactors.add("Custom");
		 ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, positions);
		 pos.setAdapter(adapterPos);
		 ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, playerStatus);
		 status.setAdapter(statusAdapter);
		 ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, sortFactors);
		 sortSp.setAdapter(sAdapter);
		 final OnItemSelectedListener l = new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String pos = ((TextView)arg1).getText().toString();
					populatePlayerList(list, pos, status.getSelectedItem().toString(), sortSp.getSelectedItem().toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			 };
		 pos.post(new Runnable() {
			    public void run() {
			    	pos.setOnItemSelectedListener(l);
			    }
			});
		 final OnItemSelectedListener l2 = new OnItemSelectedListener(){
 
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String status = ((TextView)arg1).getText().toString();
					populatePlayerList(list, pos.getSelectedItem().toString(), status, sortSp.getSelectedItem().toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			 };
		 status.post(new Runnable() {
			    public void run() {
			    	status.setOnItemSelectedListener(l2);
			}});
		 final OnItemSelectedListener l3 = new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String sort = ((TextView)arg1).getText().toString();
				populatePlayerList(list, pos.getSelectedItem().toString(), status.getSelectedItem().toString(), sort);
				if(sort.equals("Custom"))
				{
					pos.setClickable(false);
					Toast.makeText(cont, "Note position now can only be set through the pop up, not through the drop down here until you choose"
							+ " a different sorting factor", Toast.LENGTH_LONG).show();
				}
				else
				{
					pos.setClickable(true);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		 };
		 sortSp.post(new Runnable() {
			    public void run() {
			    	sortSp.setOnItemSelectedListener(l3);
			}});
		 populatePlayerList(list, "All Positions", "All Players", "PAA");
		 Button search = (Button)res.findViewById(R.id.player_list_search);
		 Button graph = (Button)res.findViewById(R.id.player_list_graph);
		 search.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					PlayerInfo obj = new PlayerInfo();
					obj.holder = ImportLeague.holder;
					try {
						obj.searchCalled(cont, true, newImport);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		 });
		 graph.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SortHandler.buildGraph(cont, adapter, (sortSp.getSelectedItem()).toString(), (pos.getSelectedItem()).toString(), " with plotted Projection");
			}
		 });
	}
	
	/**
	 * Sets the adapter to handle changed input data
	 * @param data
	 * @param list
	 */
	public static void setPlayerAdapter(List<Map<String, String>> data, ListView list)
	{
		adapter = new SimpleAdapter(cont, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString().split(":  ")[1];
				String posTeam = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text2)).getText().toString().split("\n")[1];
				PlayerInfo obj = new PlayerInfo();
				obj.isImport = true;
				obj.newImport = newImport;
				obj.outputResults(name + ", " + posTeam, true, (Activity) cont, ImportLeague.holder, false, false);
			}
		});
	}
	
	/**
	 * Handles the logic of populating the player list 
	 * to fill the selected team/status...etc.
	 */
	public static void populatePlayerList(ListView list, String pos, String status, String sortFactor)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		PriorityQueue<PlayerObject> players = null;
		List<String> posList = new ArrayList<String>();
		if(pos.equals("All Positions"))
		{
			posList.add("QB");
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("D/ST");
			posList.add("K");
		}
		else if(pos.equals("RB/WR"))
		{
			posList.add("RB");
			posList.add("WR");
		}
		else if(pos.equals("RB/WR/TE"))
		{
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
		}
		else if(pos.equals("QB/RB/WR/TE"))
		{
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("QB");
		}
		else
		{
			posList.add(pos);
		}
		if(sortFactor.equals("Custom"))
		{
			int flag = 1;
			if(status.equals("All Players"))
			{
				flag = 1;
			}
			if(status.equals("Free Agents"))
			{
				flag = 3;
			}
			if(status.equals("On Team"))
			{
				flag = 2;
			}
			SortHandler.initialPopUp(cont, ImportLeague.holder, R.id.imported_teams_players, false, flag, newImport);
		}
		else
		{
			if(sortFactor.equals("PAA"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
			    		{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.points == 0 && b.values.points > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.points > 0 && b.values.points == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.points == 0 && b.values.points == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.paa > b.values.paa || (b.values.points == 0 && a.values.points > 0))
			    			    {
			    			        return -1;
			    			    }
			    			    if (a.values.paa < b.values.paa || (a.values.points == 0 && b.values.points > 0))
			    			    {
			    			    	return 1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("Projection"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.points <= 0 && b.values.points > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.points > 0 && b.values.points == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.points == 0 && b.values.points == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.points > b.values.points || (b.values.points == 0 && a.values.points > 0))
			    			    {
			    			        return -1;
			    			    }
			    			    if (a.values.points < b.values.points || (a.values.points == 0 && b.values.points > 0))
			    			    {
			    			    	return 1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("Weekly Ranking"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.ecr > 0 || b.values.ecr > 0)
			    				{
				    				if(a.values.ecr <= 0 && b.values.ecr > 0)
				    				{
				    					return 1;
				    				}
				    				if(a.values.ecr > 0 && b.values.ecr <= 0)
				    				{
				    					return -1;
				    				}
				    				if((a.values.ecr > b.values.ecr))
				    				{
				    					return 1;
				    				}
				    				if((a.values.ecr < b.values.ecr)) 
				    				{
				    					return -1;
				    				}
			    				}
			    				return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("ROS Ranking"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.rosRank <= 0 && b.values.rosRank > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.rosRank > 0 && b.values.rosRank == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.rosRank == 0 && b.values.rosRank == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.rosRank > b.values.rosRank || (b.values.rosRank == 0 && a.values.rosRank > 0))
			    			    {
			    			        return 1;
			    			    }
			    			    if (a.values.rosRank < b.values.rosRank || (a.values.rosRank == 0 && b.values.rosRank > 0))
			    			    {
			    			    	return -1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			 for(PlayerObject player : ImportLeague.holder.players)
			 {
				 if(posList.contains(player.info.position) && 
						 !(player.info.team.length() == 0 || player.info.team.length() == 1 || player.info.position.length() == 0) )
				 {
					 players.add(player);
				 }
			 }
			 data.clear();
			 while(!players.isEmpty())
			 {
				 Map<String, String> datum = new HashMap<String, String>();
				 PlayerObject iter = players.poll();
				 datum.put("main", df.format(iter.values.points) + ":  " + iter.info.name);
				 StringBuilder subInfo = new StringBuilder(100);
				 for(TeamAnalysis team : newImport.teams)
				 {
					 if(team.team.contains(iter.info.name))
					 {
						 subInfo.append(team.teamName + "\n");
						 break;
					 }
				 }
				 if(!subInfo.toString().contains("\n"))
				 {
					 subInfo.append("Free Agent \n");
				 }
				 if(status.equals("Free Agents") && !subInfo.toString().contains("Free Agent"))
				 {
					 continue;
				 }
				 if(status.equals("On Team") && subInfo.toString().contains("Free Agent"))
				 {
					 continue;
				 }
				 subInfo.append(iter.info.position + " - " + iter.info.team + "\n");
				 if(ImportLeague.holder.isRegularSeason && iter.values.points > 0.0)
				 {
					 if(iter.values.rosRank > 0)
					 {
						 subInfo.append("ROS Positional Rank: " + iter.values.rosRank + "\n");
					 }
					 if(iter.values.ecr.intValue() != -1)
					 {
						 subInfo.append("Weekly Positional Rank: " + iter.values.ecr.intValue() + "\n");
					 }
				 }
				 else
				 {
					 subInfo.append("Preseason Expert Rank: " + iter.values.ecr.intValue() + "\n");
				 }
				 if(iter.values.points > 0.0)
				 {
					 if(!iter.info.adp.contains("Not set"))
					 {
						 if(ImportLeague.holder.isRegularSeason)
						 {
							 subInfo.append("Opponent: " + iter.info.adp);
						 }
						 if(!iter.info.adp.equals("Bye Week") && 
								 ((!ImportLeague.holder.isRegularSeason && ImportLeague.holder.sos.keySet().contains(iter.info.team + "," + iter.info.position)) ||
								(ImportLeague.holder.isRegularSeason && ImportLeague.holder.sos.keySet().contains(iter.info.adp + "," + iter.info.position))))
						 {
							 if(!ImportLeague.holder.isRegularSeason)
							 {
								 subInfo.append(" (SOS: " + ImportLeague.holder.sos.get(iter.info.team + "," + iter.info.position) + ")");
							 }
							 if(ImportLeague.holder.isRegularSeason)
							 {
								 subInfo.append(" (SOS: " + ImportLeague.holder.sos.get(iter.info.adp + "," + iter.info.position) + ")");
							 }
						 }
					 }
					 else
					 {
						 subInfo.append("Bye Week");
					 }
				 }
				 datum.put("sub", subInfo.toString());
				 data.add(datum);
			 }
			 setPlayerAdapter(data, list);
		}
	}
}
