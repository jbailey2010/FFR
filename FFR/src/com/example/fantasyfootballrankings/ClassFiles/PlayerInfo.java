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
import jeff.isawesome.fantasyfootballrankings.R;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
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
	    	ManageInput.setupAutoCompleteSearch(Rankings.holder, Rankings.holder.players, Rankings.textView, Rankings.newCont);
	    }
	    Button searchDismiss = (Button)dialog.findViewById(R.id.search_cancel);
		searchDismiss.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button searchSubmit = (Button)dialog.findViewById(R.id.search_submit);
		Rankings.textView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem)arg1).getText1().getText().toString();
				Rankings.textView.setText(text);
			}
		});
		searchSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				/**
				 * On item select, get the name
				 */
				if(Rankings.holder.parsedPlayers.contains(Rankings.textView.getText().toString()))
				{
					dialog.dismiss();
					PlayerInfo.outputResults(Rankings.textView.getText().toString(), false, (Rankings)Rankings.newCont, Rankings.holder, false, true);
				}
				else
				{
					Toast.makeText(oCont, "Not a valid player name", Toast.LENGTH_SHORT).show();
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
					String nameStr = name.getText().toString();
					if(!Draft.isDrafted(nameStr, holder.draft))
					{
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
		               	boolean isAuction = ReadFromFile.readIsAuction(act);
		               	if(isAuction)
		               	{
		               		datum.put("main", df.format(copy.values.worth) + ":  " + copy.info.name);
		               	}
		               	else if(!isAuction && copy.values.ecr != -1)
		               	{
		               		datum.put("main", df.format(copy.values.ecr) + ":  " + copy.info.name);
		               	}
		               	else if(!isAuction && copy.values.ecr == -1)
		               	{
		               		datum.put("main", copy.info.name);
		               	}
		               	datum.put("sub", copy.info.position + " - " + copy.info.team + "\n" + "Bye: " + holder.bye.get(copy.info.team));
						Rankings.handleDrafted(datum, 
								holder, (Activity)Rankings.context, dialog, index);
						return true;
					}
					else
					{
						Toast.makeText(act, "That player is already drafted", Toast.LENGTH_SHORT).show();
					}
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
		BounceListView results = (BounceListView)dialog.findViewById(R.id.listview_search);
		results.setOverscrollHeader(act.getResources().getDrawable(R.drawable.overscroll_header));
		results.setOverscrollFooter(act.getResources().getDrawable(R.drawable.overscroll_header));
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
				else if(input.contains("Risk"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("The 'Risk' of a player is the relative variation in the total set of expert rankings. The logic is, the less established the value of the player is (a higher variance), the riskier he is.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("PAA per dollar"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("PAA attempts to quantify the value a player has, cross-positions. This attempts to get an idea of that actual value, taking the price into account. It's simply PAA divided by the auction value.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("projected points"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This projection is the weighted average of a series of experts.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("PAA"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("PAA attempts to quantify the value has cross-positions. It means points above average. For example, tight ends are generally not highly valued. There are a few, though, that give such a large edge over the alternative that they should be valued highly. Their PAA is high.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("oTD"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("oTD means opportunity-adjusted touchdowns, serving as a true replacement for red zone stats. It looks at the probability of a touchdown based on context of usage, and consequently how many touchdowns a player should have scored in that context.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("Average Draft Position"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This is the average draft position of a player over thousands and thousands of mock drafts.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("Average Expert Ranking"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This is the average ranking of over one hundred different experts.");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
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
		/*String low = String.valueOf(searchedPlayer.values.low);
		if(searchedPlayer.values.low == 100)
		{
			low = String.valueOf(searchedPlayer.values.high);
		}*/
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
		//Team, position, and bye
		if(searchedPlayer.info.position.length() > 1 && searchedPlayer.info.position.length() > 1)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.info.position + " - " + searchedPlayer.info.team);
			String sub = "";
			if(holder.bye.get(searchedPlayer.info.team) != null && !holder.bye.get(searchedPlayer.info.team).contains("null") &&  
					!holder.bye.get(searchedPlayer.info.team).equals("Not set"))
			{
				sub = "Bye: " + holder.bye.get(searchedPlayer.info.team);
			}
			if(!searchedPlayer.info.age.equals("0") && !searchedPlayer.info.position.equals("D/ST") && 
					!searchedPlayer.info.age.equals("") && searchedPlayer.info.age.length() >= 2)
			{
				sub += "\n" + "Age: " + searchedPlayer.info.age;
			}
			datum.put("sub", sub);
			data.add(datum);
		} 
		if(!searchedPlayer.injuryStatus.contains("Healthy"))
		{
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", searchedPlayer.injuryStatus);
			datum2.put("sub", "");
			data.add(datum2);
		}
		//Worth
		Map<String, String> datumWorth = new HashMap<String, String>(2);
		datumWorth.put("main", "$" + df.format(searchedPlayer.values.worth));
		datumWorth.put("sub", "Ranked " + rankCostPos(searchedPlayer, holder) + " positionally, " + rankCostAll(searchedPlayer, holder) + " overall"
				+ "\nShowed up in " + searchedPlayer.values.count + " rankings");
		data.add(datumWorth);
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
		//Positional SOS
		if(holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position)!= null && 
				holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position) > 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "Positional SOS: " + holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position));
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
		if(searchedPlayer.values.paa != 0.0 && searchedPlayer.values.points != 0.0 && searchedPlayer.values.worth >= 1.0)
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
			datum.put("main", "Target-based oTD: " + searchedPlayer.values.oTD + " \n(" + result + " relative to last year's numbers)");
			datum.put("sub", "Ranked " + rankoTD(searchedPlayer, holder) + " positionally\nTargeted an average of " + searchedPlayer.values.tADEZ + " yards from the endzone");
			data.add(datum);
		}
		//Catch oTD stuff
		if(searchedPlayer.values.cADEZ != 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String result = String.valueOf(searchedPlayer.values.ctdDiff);
			if(!result.contains("-"))
			{
				result = "+" + result; 
			}
			datum.put("main", "Catch-based oTD: " + searchedPlayer.values.coTD + " \n(" + result + " relative to last year's numbers)");
			datum.put("sub", "Ranked " + rankcoTD(searchedPlayer, holder) + " positionally\nCatches were an average of " + searchedPlayer.values.cADEZ + " yards from the endzone");
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
			datum.put("sub", "Ranked " + rankroTD(searchedPlayer, holder) + " positionally\nCarried an average of " + searchedPlayer.values.rADEZ + " yards from the endzone");
			data.add(datum);
		}
		if(searchedPlayer.values.cADEZ != 0.0 && searchedPlayer.values.tADEZ != 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			double diff = 0.0;
			int rank = 0;
			diff = searchedPlayer.values.tADEZ - searchedPlayer.values.cADEZ;
			rank = rankCatchDepth(searchedPlayer,holder);
			datum.put("main", "Average depth of catch relative to tADEZ: " + df.format(diff));
			datum.put("sub", "Ranked " + rank + " positionally");
			data.add(datum);
		}
		//Risk
		if(searchedPlayer.risk > 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(searchedPlayer.risk != -1.0)
			{
				datum.put("main", searchedPlayer.risk + " Risk (" + rankRiskAll(searchedPlayer, holder) + ")");
				datum.put("sub", searchedPlayer.riskPos + " relative to his position (" + rankRiskPos(searchedPlayer, holder) + ")");
				data.add(datum);
			}
			else
			{
				datum.put("main", searchedPlayer.risk + " Risk");
				datum.put("sub", searchedPlayer.riskPos + " relative to his position");
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
			data.add(datum);
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

		}
		//O line data
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
		{ 
			if(holder.oLineRanks.get(searchedPlayer.info.team) != null && holder.oLineRanks.get(searchedPlayer.info.team).length() > 3 && 
					holder.oLineAdv.get(searchedPlayer.info.team)!= null && holder.oLineAdv.get(searchedPlayer.info.team).length() > 3)
			{
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("main", holder.oLineAdv.get(searchedPlayer.info.team) + "\n");
				datum.put("sub", holder.oLineRanks.get(searchedPlayer.info.team));
				data.add(datum);
			}
		}
		//Draft class
		if(holder.draftClasses.get(searchedPlayer.info.team) != null && !holder.draftClasses.get(searchedPlayer.info.team).contains("null") &&
				!searchedPlayer.info.position.equals("K") && holder.draftClasses.get(searchedPlayer.info.team).length() > 4)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String draft = holder.draftClasses.get(searchedPlayer.info.team).substring(holder.draftClasses.get(searchedPlayer.info.team).indexOf('\n'), holder.draftClasses.get(searchedPlayer.info.team).length());
			String gpa = holder.draftClasses.get(searchedPlayer.info.team).split("\n")[0];
			datum.put("main", draft);
			datum.put("sub", gpa);
			data.add(datum);
		}
		//Free agency classes
		List<String> fa = holder.fa.get(searchedPlayer.info.team);
		if(fa != null)
		{
			if(fa.size() > 1)
			{
		    	if(fa.get(0).contains("\n"))
		    	{
	                Map<String, String> datum = new HashMap<String, String>(2);
	                datum.put("main", fa.get(0));
		    		datum.put("sub", "");
		    		data.add(datum);
		    	}
		    	if(fa.get(1).contains("\n"))
		    	{
	                Map<String, String> datum = new HashMap<String, String>(2);
	                datum.put("main",  fa.get(1));
		    		datum.put("sub", "");
		    		data.add(datum);
		    	}
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
	
	public static int rankroTD(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.roTD > player.values.roTD && iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
	}
	
	public static int rankoTD(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.oTD > player.values.oTD && iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
	}
	
	public static int rankcoTD(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.coTD > player.values.coTD && iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
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
			if(iter.risk < player.risk && iter.risk >0)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Calculates the rank of adoc
	 * @param player
	 * @param holder
	 * @return
	 */
	public static int rankCatchDepth(PlayerObject player, Storage holder)
	{
		int rank = 1;
		double diff = player.values.tADEZ - player.values.cADEZ;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.cADEZ != 0.0 && iter.values.tADEZ != 0.0 && iter.info.position.equals(player.info.position))
			{
				double possDiff = iter.values.tADEZ - iter.values.cADEZ;
				if(possDiff > diff)
				{
					rank++;
				}
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
	 * Ranks the worth positionally
	 */
	public static int rankCostPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position) && iter.values.worth > player.values.worth)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the worth overall
	 */
	public static int rankCostAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.worth > player.values.worth)
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
		TwitterWork obj = new TwitterWork();
		obj.twitterInitial(act, 3, name, false);
		//ParseNews.startTwitterSearchAsync(act, name, "Twitter Search: " + name, false, name, obj);
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
	    BounceListView tweetResults = (BounceListView)dialog.findViewById(R.id.listview_search);
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
	    tweetResults.setOverscrollHeader(act.getResources().getDrawable(R.drawable.overscroll_header));
	    tweetResults.setOverscrollHeader(act.getResources().getDrawable(R.drawable.overscroll_header));
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
