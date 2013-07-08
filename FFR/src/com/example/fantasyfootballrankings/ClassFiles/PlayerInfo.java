package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.id;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.style;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener.OnDismissCallback;
import com.example.fantasyfootballrankings.Pages.Rankings;

/**
 * Handles the player info function
 * @author Jeff
 *
 */
public class PlayerInfo 
{

	/**
	 * Abstracted out of the menu handler as this could get ugly
	 * once the stuff is added to the dropdown
	 * @throws IOException
	 */ public static void searchCalled(final Context oCont) throws IOException
	{
		Rankings.matchedPlayers = new ArrayList<String>(15);
		Rankings.newCont = oCont;
		ReadFromFile.fetchNames(Rankings.holder, Rankings.newCont);
		final Dialog dialog = new Dialog(Rankings.newCont, R.style.RoundCornersFull);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
	
		dialog.setContentView(R.layout.search_players);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    
		Rankings.voice = (Button) dialog.findViewById(R.id.speakButton);
	    Rankings.textView = (AutoCompleteTextView)(dialog).findViewById(R.id.player_input);
	    Rankings.voice.setOnClickListener(new OnClickListener() {
	
		    @Override
		    public void onClick(final View v) {
		            ((Rankings)Rankings.newCont).speakButtonClicked(v);
		            
		    }
		});
	    if(Rankings.matchedPlayers.size() == 0)
	    {
	    	ManageInput.setupAutoCompleteSearch(Rankings.holder, Rankings.holder.parsedPlayers, Rankings.textView, Rankings.newCont);
	    }
	    Button searchDismiss = (Button)dialog.findViewById(R.id.search_cancel);
		searchDismiss.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button searchSubmit = (Button)dialog.findViewById(R.id.search_submit);
		searchSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				if(Rankings.holder.parsedPlayers.contains(Rankings.textView.getText().toString()))
				{
					dialog.dismiss();
					PlayerInfo.outputResults(Rankings.textView.getText().toString(), false, (Rankings)Rankings.newCont, Rankings.holder, false, true);
				}
			}
		});
		dialog.show();		
	}
	
	/**
	 * outputs the results to the search dialog
	 * @param namePlayer
	 */
	public static void outputResults(final String namePlayer, boolean flag, 
			final Activity act, final Storage holder, final boolean watchFlag, boolean draftable)
	{
		final Dialog dialog = new Dialog(act);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		dialog.setContentView(R.layout.search_output);
		Button addWatch = (Button)dialog.findViewById(R.id.add_watch);
		//If the add to list boolean exists
		if(!watchFlag)
		{
	    	addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//Check if the player is in the watchList
					int i = -1;
					for(String name : Rankings.watchList)
					{
						if(name.equals(namePlayer))
						{
							i++;
							break;
						}
					}
					//if not, add him on the click of the button
					if(i == -1)
					{
						Rankings.watchList.add(namePlayer);
						WriteToFile.writeWatchList(Rankings.context, Rankings.watchList);
						Toast.makeText(Rankings.context, namePlayer + " added to watch list", Toast.LENGTH_SHORT).show();
					}
					else//if so, ignore the click
					{
						Toast.makeText(Rankings.context, namePlayer + " already in watch list", Toast.LENGTH_SHORT).show();
					}
				}
	    	});
		}
		//Otherwise, the call is from the watch list, so it gives the option to remove it
		else
		{
			addWatch.setText("Remove From Watch List");
			addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(Rankings.context, namePlayer + " removed from watch list", Toast.LENGTH_SHORT).show();
					Rankings.watchList.remove(namePlayer);
					WriteToFile.writeWatchList((Context)act, Rankings.watchList);
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, Rankings.watchList);
				}
			});
		}
		//Create the output, make sure it's valid
		List<String>output = new ArrayList<String>(12);
		final TextView name = (TextView)dialog.findViewById(R.id.name);
		if(namePlayer.equals(""))
		{
			return;
		}
		//Set up the header, and make a mock object with the set name
		name.setText(namePlayer);
		PlayerObject searchedPlayer = new PlayerObject("","","",0);
		for(PlayerObject player : holder.players)
		{
			if(player.info.name.equals(namePlayer))
			{
				searchedPlayer = player;
				break;
			}
		}
		final PlayerObject copy = searchedPlayer;
		if(draftable)
		{
			name.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					int index = 0;
	                for(int i = 0; i < holder.players.size(); i++)
	                {
	                	
	               	 	if(Rankings.data.get(i).get("main").contains(namePlayer))
	               	 	{
	               	 		index = i;
	               	 		break;
	               	 	}
	                } 
	               	DecimalFormat df = new DecimalFormat("#.##");
	               	Rankings.data.remove(index);
	               	Rankings.adapter.notifyDataSetChanged();
	               	Map<String, String> datum = new HashMap<String, String>();
	               	datum.put("main", df.format(copy.values.worth) + ":  " + copy.info.name);
	               	datum.put("sub", copy.info.position + " - " + copy.info.team);
					Rankings.handleDrafted(datum, 
							holder, (Activity)Rankings.context, dialog, index);
					return true;
				}
			});
		}
		//If it's called from trending or watch list, ignore back
		if(flag)
		{
			Button backButton = (Button)dialog.findViewById(R.id.search_back);
			backButton.setVisibility(Button.GONE);
			View backView = (View)dialog.findViewById(R.id.back_view);
			backView.setVisibility(View.GONE);
		}
		//Set the data in the list
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		setSearchContent(searchedPlayer, data, holder);
		//Show the dialog, then set the list
		dialog.show();
		ListView results = (ListView)dialog.findViewById(R.id.listview_search);
		results.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String input = ((TwoLineListItem)arg1).getText1().getText().toString();
				//Show tweets about the player
				if(input.contains("See tweets about"))
				{
					playerTweetSearchInit(namePlayer, act);
				}
				//Bring up the interweb to show highlights of the player
				else if(input.contains("See highlights of"))
				{
					String[] nameArr = namePlayer.split(" ");
					String url = "http://www.youtube.com/results?search_query=";
					for(String name : nameArr)
					{
						url += name + "+";
					}
					url += "highlights";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					act.startActivity(i);
				}
			}
		});
		//ManageInput.handleArray(output, results, act);
		final SimpleAdapter adapter = new SimpleAdapter(act, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    results.setAdapter(adapter);
		Button back = (Button)dialog.findViewById(R.id.search_back);
		//If it isn't gone, set that it goes back
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					dialog.dismiss();
					searchCalled(Rankings.newCont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); 
		//Setting up close
		Button close = (Button)dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener()
		{ 
			public void onClick(View v) {
				//If it is called from trending or rankings, dismiss it
				if(!watchFlag)
				{
					dialog.dismiss();
				}
				else //otherwise it was from watch list, so call back from there
				{
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, Rankings.watchList);
				}
			}
		}); 
	}

	/**
	 * Sets the output of the search
	 * @param searchedPlayer
	 * @param data
	 * @param holder 
	 */
	public static void setSearchContent(PlayerObject searchedPlayer, List<Map<String, String>> data, Storage holder)
	{
	   	DecimalFormat df = new DecimalFormat("#.##");
		String low = String.valueOf(searchedPlayer.values.low);
		if(searchedPlayer.values.low == 100)
		{
			low = String.valueOf(searchedPlayer.values.high);
		}
		//See if they're drafted by me
		if(Draft.draftedMe(searchedPlayer.info.name, Rankings.holder.draft))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED BY YOU");
			data.add(datum);
		}
		//See if they're drafted by someone else
		else if(Draft.isDrafted(searchedPlayer.info.name, Rankings.holder.draft))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED");
			data.add(datum);
		}
		//Is he in the watch list?
		if(Rankings.watchList.contains(searchedPlayer.info.name))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "He is in your watch list");
			datum.put("sub", "");
			data.add(datum);
		}
		//Worth
		Map<String, String> datumWorth = new HashMap<String, String>(2);
		datumWorth.put("main", "$" + df.format(searchedPlayer.values.worth));
		datumWorth.put("sub", "High: " + searchedPlayer.values.high + "\nLow: " + searchedPlayer.values.low + 
				"\nShowed up in " + searchedPlayer.values.count + " rankings");
		data.add(datumWorth);
		//Team, position, and bye
		if(searchedPlayer.info.position.length() > 1 && searchedPlayer.info.position.length() > 1)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.info.position + " - " + searchedPlayer.info.team);
			String sub = "";
			if(searchedPlayer.info.bye != null && !searchedPlayer.info.bye.contains("null") &&  
					!searchedPlayer.info.bye.equals("Not set"))
			{
				sub = "Bye: Week " + searchedPlayer.info.bye;
			}
			if(!searchedPlayer.info.age.equals("0") && !searchedPlayer.info.position.equals("D/ST") && 
					!searchedPlayer.info.age.equals("") && searchedPlayer.info.age.length() >= 2)
			{
				sub += "\n" + "Age: " + searchedPlayer.info.age;
			}
			datum.put("sub", sub);
			data.add(datum);
		} 
		//Positional SOS
		if(searchedPlayer.info.sos > 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "Positional SOS: " + searchedPlayer.info.sos);
			datum.put("sub", "1 is Easiest, 32 Hardest");
			data.add(datum);
		}
		//Projections
		if(searchedPlayer.values.points != 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.values.points + " projected points");
			datum.put("sub", "Ranked " + rankProjPos(searchedPlayer, holder)  + " positionally");
			data.add(datum);
		}
		//PAA and PAAPD
		if(searchedPlayer.values.paa != 0.0 && searchedPlayer.values.points != 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", df.format(searchedPlayer.values.paa) + " PAA");
			datum.put("sub", "Ranked " + rankPAAPos(searchedPlayer, holder) + " positionally, " + rankPAAAll(searchedPlayer, holder) +
						 " overall");
			data.add(datum);
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", df.format(searchedPlayer.values.paapd) + " PAA per dollar");
			datum2.put("sub", "Ranked " + rankPAAPDPos(searchedPlayer, holder) + " positionally, " + rankPAAPDAll(searchedPlayer, holder) + " overall");
			data.add(datum2);
		} 

		//Rec oTD stuff
		if(searchedPlayer.values.oTD != 0.0 && searchedPlayer.values.tADEZ != 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String result = String.valueOf(searchedPlayer.values.tdDiff);
			if(!result.contains("-"))
			{
				result = "+" + result; 
			}
			datum.put("main", "Receiving oTD: " + searchedPlayer.values.oTD + " \n(" + result + " relative to last year's numbers)");
			datum.put("sub", "Targeted an average of " + searchedPlayer.values.tADEZ + " yards from the endzone");
			data.add(datum);
		}
		//Rush oTD stuff
		if(searchedPlayer.values.rADEZ != 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String result = String.valueOf(searchedPlayer.values.rtdDiff);
			if(!result.contains("-"))
			{
				result = "+" + result;
			}
			datum.put("main", "Rushing oTD: " + searchedPlayer.values.roTD + "\n(" + result + " relative to last year's numbers)");
			datum.put("sub", "Carried an average of " + searchedPlayer.values.rADEZ + " yards from the endzone");
			data.add(datum);
		}
		//Risk
		if(searchedPlayer.risk > 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(searchedPlayer.values.count > 8 && searchedPlayer.values.worth > 5 && !searchedPlayer.info.position.equals("K") && 
					!searchedPlayer.info.position.equals("D/ST"))
			{
				datum.put("main", searchedPlayer.risk + " Risk");
				datum.put("sub", searchedPlayer.riskPos + " relative to his position (" + rankRiskPos(searchedPlayer, holder) + ")\n" + 
						searchedPlayer.riskAll + " relative to everyone (" + rankRiskAll(searchedPlayer, holder) + ")");
				data.add(datum);
			}
			else
			{
				datum.put("main", searchedPlayer.risk + " Risk");
				datum.put("sub", searchedPlayer.riskPos + " relative to his position\n" + 
						searchedPlayer.riskAll + " relative to everyone");
				data.add(datum);
			}
		}
		//Contract status
		if(!searchedPlayer.info.position.equals("D/ST") && 
				!searchedPlayer.info.contractStatus.contains("Under Contract"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.info.contractStatus);
			datum.put("sub", "");
		} 
		//Injury status and stats
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST")
				&& !searchedPlayer.stats.equals(" ") && searchedPlayer.stats.length() > 5)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.stats);
			if(searchedPlayer.info.additionalStat != null && !searchedPlayer.info.additionalStat.equals("")
					&& searchedPlayer.info.additionalStat.length() > 2)
	    	{
	    		datum.put("sub", searchedPlayer.info.additionalStat);
	    	}
			data.add(datum);
			if(!searchedPlayer.injuryStatus.contains("Healthy"))
			{
				Map<String, String> datum2 = new HashMap<String, String>(2);
				datum2.put("main", searchedPlayer.injuryStatus);
				datum2.put("sub", "");
				data.add(datum2);
			}
		}
		//Draft class
		if(searchedPlayer.draftClass != null && !searchedPlayer.draftClass.contains("null") &&
				!searchedPlayer.info.position.equals("K") && searchedPlayer.draftClass.length() > 4)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String draft = searchedPlayer.draftClass.substring(searchedPlayer.draftClass.indexOf('\n'), searchedPlayer.draftClass.length());
			String gpa = searchedPlayer.draftClass.split("\n")[0];
			datum.put("main", draft);
			datum.put("sub", gpa);
			data.add(datum);
		}
		//Free agency classes
		if(searchedPlayer.fa.size() > 1)
		{
	    	if(searchedPlayer.fa.get(0).contains("\n"))
	    	{
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("main", searchedPlayer.fa.get(0));
	    		datum.put("sub", "");
	    		data.add(datum);
	    	}
	    	if(searchedPlayer.fa.get(1).contains("\n"))
	    	{
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("main",  searchedPlayer.fa.get(1));
	    		datum.put("sub", "");
	    		data.add(datum);
	    	}
		}
		//Rank ecr
		if(searchedPlayer.values.ecr != -1)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(rankECRPos(searchedPlayer, holder) != -1)
			{
				datum.put("main", "Average Expert Ranking: " + searchedPlayer.values.ecr);
				datum.put("sub", "Ranked " + rankECRPos(searchedPlayer, holder) + " positionally");
				data.add(datum);
			}
			else
			{
				datum.put("main", "Average Expert Rankings: " + searchedPlayer.values.ecr);
				datum.put("sub", "");
			}
		}
		//ADP Rankings
		if(!searchedPlayer.info.adp.equals("Not set"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(rankADPPos(searchedPlayer, holder) != -1)
			{
				datum.put("main", "Average Draft Position: " + searchedPlayer.info.adp);
				datum.put("sub", "Ranked " + rankADPPos(searchedPlayer, holder) + " positionally");
				data.add(datum);
			}
			else
			{
				datum.put("main", "Average Draft Position: " + searchedPlayer.info.adp);
				datum.put("sub", "");
				data.add(datum);
			}
		}
		if(!searchedPlayer.info.trend.equals("0.0"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "Weekly Value Trend: " + searchedPlayer.info.trend);
			datum.put("sub", "Per ESPN's AAV data");
			data.add(datum);
		}
		
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
		{ 
			if(searchedPlayer.info.passRunRatio != null && searchedPlayer.info.passRunRatio.length() > 2 && 
					searchedPlayer.info.oLineStatus != null && searchedPlayer.info.oLineStatus.length() > 3 && 
					searchedPlayer.info.oLineAdv != null && searchedPlayer.info.oLineAdv.length() > 3)
			{
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("main", searchedPlayer.info.oLineStatus + "\n" + searchedPlayer.info.oLineAdv + "\n");
				datum.put("sub", "\n" + searchedPlayer.info.passRunRatio);
				data.add(datum);
			}
		}
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "See tweets about this player");
			datum.put("sub", "");
			data.add(datum);
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", "See highlights of this player");
			datum2.put("sub", "");
			data.add(datum2);
		}
	}
	
	/**
	 * Ranks risk relative to position
	 */
	public static int rankRiskPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(player.info.position.equals("QB"))
			{
				if(iter.riskPos < player.riskPos && iter.values.count > 9 && iter.values.worth > 3 && player.info.position.equals(iter.info.position))
				{
					rank++;
				}
			}
			else if(player.info.position.equals("RB"))
			{
				if(iter.riskPos < player.riskPos && iter.values.count > 9 && iter.values.worth > 5 && player.info.position.equals(iter.info.position))
				{
					rank++;
				}
			}
			else if(player.info.position.equals("WR"))
			{
				if(iter.riskPos < player.riskPos && iter.values.count > 9 && iter.values.worth > 4 && player.info.position.equals(iter.info.position))
				{
					rank++;
				}
			}
			else if( player.info.position.equals("TE") && iter.riskPos < player.riskPos 
					&& iter.values.count > 9 && iter.values.worth > 1 && 
					iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks risk relative to all
	 */
	public static int rankRiskAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.riskAll < player.riskAll && iter.values.count > 9 && iter.values.worth > 3 && !iter.info.position.equals("K") && 
					!iter.info.position.equals("D/ST"))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks ECR positionally
	 */
	public static int rankECRPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		if(player.values.ecr == -1)
		{
			return -1;
		}
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.ecr == -1)
			{
				continue;
			}
			if(iter.info.position.equals(player.info.position)&&iter.values.ecr < player.values.ecr)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the adp positionally
	 */
	public static int rankADPPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		if(player.info.adp.equals("Not set"))
		{
			return -1;
		}
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.adp.equals("Not set"))
			{
				continue;
			}
			if(iter.info.position.equals(player.info.position) && Double.parseDouble(iter.info.adp)
					< Double.parseDouble(player.info.adp))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the projections positionally
	 */
	public static int rankProjPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.points != 0.0 && iter.values.points > player.values.points && 
					iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paa among positional players
	 */
	public static int rankPAAPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paa != 0.0 && iter.info.position.equals(player.info.position) && 
					iter.values.paa > player.values.paa)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paa among all players
	 */
	public static int rankPAAAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paa != 0.0 && iter.values.paa > player.values.paa)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paapd among positional players
	 */
	public static int rankPAAPDPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paa != 0.0 && iter.info.position.equals(player.info.position) && 
					iter.values.paapd > player.values.paapd)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paapd among all players
	 */
	public static int rankPAAPDAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paapd != 0.0 && iter.values.paapd > player.values.paapd)
			{
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Calls the asynchronous search of tweets about a player
	 */
	public static void playerTweetSearchInit(String name, Activity act)
	{
		ParseNews.startTwitterSearchAsync(act, name, "Twitter Search: " + name, false, name);
	}

	/**
	 * Makes a popup that shows the tweet so it's copyable from a user
	 */
	public static void tweetPopUp(View arg1, Activity cont)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.tweet_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button cancel = (Button)dialog.findViewById(R.id.tweet_popup_close);
	    cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    TextView showTweet = (TextView)dialog.findViewById(R.id.tweet_field);
	    String tweet = ((TwoLineListItem)arg1).getText1().getText().toString();
		tweet += "\n\n" + ((TwoLineListItem)arg1).getText2().getText().toString();
	    showTweet.setText(tweet);
	}

	/**
	 * Displays the output of the tweets about the player
	 * @param result
	 * @param act
	 */
	public static void playerTweetSearch(List<NewsObjects> result, final Activity act, String name)
	{
		final Dialog dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.player_tweet_search); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    TextView header = (TextView)dialog.findViewById(R.id.name);
	    header.setText("Twitter Search: " + name);
	    ListView tweetResults = (ListView)dialog.findViewById(R.id.listview_search);
	    Button close = (Button)dialog.findViewById(R.id.search_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    List<String> news = new ArrayList<String>(10000);
	    final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	    for(NewsObjects newsObj : result)
	    {
	    	Map<String, String> datum = new HashMap<String, String>();
	    	datum.put("news", newsObj.news + "\n\n" + newsObj.impact + "\n");
	    	datum.put("date", newsObj.date);
	    	data.add(datum);
	    }
	    final SimpleAdapter adapter = new SimpleAdapter(act, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"news", "date"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    tweetResults.setAdapter(adapter);
	    tweetResults.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Rankings.listview.setSelection(arg2);
				tweetPopUp(arg1, act);
			}
	    });
	    SwipeDismissListViewTouchListener touchListener =
	            new SwipeDismissListViewTouchListener(
	                    tweetResults,
	                    new SwipeDismissListViewTouchListener.OnDismissCallback() {
	                        @Override
	                        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
	                            for (int position : reverseSortedPositions) {
	                                data.remove(position);
	                            }
	                            adapter.notifyDataSetChanged();
	                            Toast.makeText(Rankings.context, "Temporarily hiding this news piece", Toast.LENGTH_SHORT).show();
	                            if(adapter.isEmpty())
	                            {
	                            	dialog.dismiss();
	                            	Toast.makeText(Rankings.context, "You've hidden all of the tweets", Toast.LENGTH_SHORT).show();
	                            }
	                        }
	                    });
	    tweetResults.setOnTouchListener(touchListener);
	    tweetResults.setOnScrollListener(touchListener.makeScrollListener());
	}
}
