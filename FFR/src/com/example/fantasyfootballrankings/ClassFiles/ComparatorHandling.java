package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * Handles the player comparing
 * @author Jeff
 *
 */
public class ComparatorHandling 
{
	/**
	 * Does the initial setting up of the dialog itself
	 */
	public static void handleComparingInit(Storage holder, Context cont)
	{
		final Dialog dialog = new Dialog(cont);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		dialog.setContentView(R.layout.comparator_view);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		//For when this is called back by the comparing part:
		final AutoCompleteTextView player1Input = (AutoCompleteTextView)dialog.findViewById(R.id.player1_input);
		final AutoCompleteTextView player2Input = (AutoCompleteTextView)dialog.findViewById(R.id.player2_input);
		player1Input.setText("");
		player2Input.setText("");
		final TextView player1 = (TextView)dialog.findViewById(R.id.first_player_compare);
		final TextView player2 = (TextView)dialog.findViewById(R.id.second_player_compare);
		player1.setText("First Player: ");
		player2.setText("Second Player: ");
		player1.setVisibility(TextView.INVISIBLE);
		player2.setVisibility(TextView.INVISIBLE);
		//Get rid of dialog
		Button close = (Button)dialog.findViewById(R.id.compare_close);
		close.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				return;
	    	}	
		});
		//Clear kills all thus far inputted data
		Button clear = (Button)dialog.findViewById(R.id.clear_compare);
		clear.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				player1.setText("First Player: ");
				player2.setText("Second Player: ");
				player1.setVisibility(TextView.INVISIBLE);
				player2.setVisibility(TextView.INVISIBLE);
				final AutoCompleteTextView player1Input = (AutoCompleteTextView)dialog.findViewById(R.id.player1_input);
				final AutoCompleteTextView player2Input = (AutoCompleteTextView)dialog.findViewById(R.id.player2_input);
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
	public static void setAdapter(final Storage holder, final Context cont, final Dialog dialog)
	{
		List<String> adapter = new ArrayList<String>(700);
		for(String name : holder.parsedPlayers)
		{
			adapter.add(name);
		}
		ArrayAdapter<String> doubleAdapter = new ArrayAdapter<String>(cont,
                android.R.layout.simple_dropdown_item_1line, adapter);
		final AutoCompleteTextView player1 = (AutoCompleteTextView)dialog.findViewById(R.id.player1_input);
		final AutoCompleteTextView player2 = (AutoCompleteTextView)dialog.findViewById(R.id.player2_input);
		player1.setAdapter(doubleAdapter);
		player1.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView list2 = (TextView)dialog.findViewById(R.id.second_player_compare);
				showInput(player1, dialog, cont, holder, R.id.first_player_compare, "First Player: ");
				if(list2.getText().toString().contains("\n"))
				{
					startBackEnd(dialog, cont, holder);
				}
			}
		});
		player2.setAdapter(doubleAdapter);
		player2.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView list1 = (TextView)dialog.findViewById(R.id.first_player_compare);
				showInput(player2, dialog, cont, holder, R.id.second_player_compare, "Second Player: ");
				if(list1.getText().toString().contains("\n"))
				{
					startBackEnd(dialog, cont, holder);
				}
			}
		});
	}
	
	/**
	 * Sets the textview at the bottom to be the inputted player
	 */
	public static void showInput(AutoCompleteTextView player, Dialog dialog, Context cont, Storage holder, int id,
			String text) 
	{
		String input = player.getText().toString();
		TextView list = (TextView)dialog.findViewById(id);
		list.setText(text + input + "\n");
		list.setVisibility(TextView.VISIBLE);
	}
	
	/**
	 * Starts the back-end work of the comparator
	 */
	public static void startBackEnd(Dialog dialog, Context cont, Storage holder) 
	{
		AutoCompleteTextView player1 = (AutoCompleteTextView)dialog.findViewById(R.id.player1_input);
		AutoCompleteTextView player2 = (AutoCompleteTextView)dialog.findViewById(R.id.player2_input);
		String name1 = player1.getText().toString();
		String name2 = player2.getText().toString();
		if(name1.equals(name2))
		{
			Toast.makeText(cont, "Please enter 2 different players", Toast.LENGTH_SHORT).show();
			return;
		}
		//First player fetching and other lists
		PlayerObject firstPlayer = getPlayer(name1, holder);
		List<PlayerObject> firstTeam = getPlayerTeam(holder, firstPlayer);
		List<PlayerObject> firstPos = getPlayerPosition(holder, firstPlayer);
		//Fetch second player and other lists
		PlayerObject secondPlayer = getPlayer(name2, holder);
		List<PlayerObject> secTeam = getPlayerTeam(holder, secondPlayer);
		List<PlayerObject> secPos = getPlayerPosition(holder, secondPlayer);
		//Get the stats
		handleStats(dialog, cont, holder, firstPlayer, secondPlayer, firstTeam, secTeam, firstPos, secPos);
	}
	
	/**
	 * Handles the various stats of the players to be compared
	 */
	public static void handleStats(Dialog dialog, Context cont,	Storage holder, 
			PlayerObject player1, PlayerObject player2, List<PlayerObject> firstTeam,
			List<PlayerObject> secTeam, List<PlayerObject> firstPos, List<PlayerObject> secPos) 
	{
		StringBuilder p1 = new StringBuilder(1000);
		StringBuilder p2 = new StringBuilder(1000);
		int rank1 = posRank(player1, firstPos);
		int rank2 = posRank(player2, secPos);
		if(rank1 != rank2)
		{
			if(rank1 < rank2)
			{
				if(rank2 - rank1 > 10)
				{
					p1.append("-Positionally much higher ranked" + "\n");
				}
				else
				{
					p1.append("-Positionally higher ranked\n");
				}
			}
			else
			{
				if(rank1 - rank2 > 10)
				{
					p2.append("-Positionally much higher ranked" + "\n");
				}
				else
				{
					p2.append("-Positionally higher ranked\n");
				}		
			}
		}
		double worth1 = player1.values.worth;
		double worth2 = player2.values.worth;
		if(Math.abs(worth1 - worth2) > 3.0)
		{
			if(worth1 > worth2)
			{
				if(worth1 - worth2 > 10)
				{
					p2.append("-Costs much less\n");
				}
				else
				{
					p2.append("-Costs less" + "\n");
				}
			}
			else
			{
				if(worth2 - worth1 > 10)
				{
					p1.append("-Costs much less\n");
				}
				else
				{
					p1.append("-Costs less" + "\n");
				}
			}
		}
		double paa1 = player1.values.paa;
		double paa2 = player2.values.paa;
		if(paa1 != 0.0 && paa2 != 0.0)
		{
			if(paa1 > paa2)
			{
				if(paa1 - paa2 > 15.0)
				{
					p1.append("-Much higher PAA\n");
				}
				else
				{
					p1.append("-Higher PAA\n");
				}
			}
			if(paa2 > paa1)
			{
				if(paa2 - paa1 > 15.0)
				{
					p2.append("-Much higher PAA\n");
				}
				else
				{
					p2.append("-Higher PAA\n");
				}
			}
			double paapd1 = player1.values.paapd;
			double paapd2 = player2.values.paapd;
			if(paapd1 > paapd2)
			{
				if(paapd1 - paapd2 > 1.0)
				{
					p1.append("-Much higher PAAPD\n");
				}
				else
				{
					p1.append("-Higher PAAPD\n");
				}
			}
			if(paapd2 > paapd1)
			{
				if(paapd2 - paapd1 > 1.0)
				{
					p2.append("-Much higher PAAPD\n");
				}
				else
				{
					p2.append("-Higher PAAPD\n");
				}
			}
		}
		if(player1.info.position.equals(player2.info.position) && (player1.info.position.equals("WR") || 
				player1.info.position.equals("RB") || player1.info.position.equals("TE")))
		{
			double oTD1 = player1.values.oTD;
			double oTD2 = player2.values.oTD;
			if(oTD1 > oTD2)
			{
				if(oTD1 - oTD2 > 3.0)
				{
					p1.append("-Much higher adjusted touchdowns\n");
				}
				else
				{
					p1.append("-Higher adjusted touchdowns\n");
				}
			}
			if(oTD2 > oTD1)
			{
				if(oTD2 - oTD1 > 3.0)
				{
					p2.append("-Much higher adjusted touchdowns\n");
				}
				else
				{
					p2.append("-Higher adjusted touchdowns\n");
				}
			}
		}
		try{
			int age1 = Integer.parseInt(player1.info.age);
			int age2 = Integer.parseInt(player2.info.age);
			if(age1 != age2)
			{
				if(age1 > age2)
				{
					if(age1 - age2 > 5)
					{
						p2.append("-Much younger\n");
					}
					else
					{
						p2.append("-Younger" + "\n");
					}
				}
				else
				{
					if(age2 - age1 > 5)
					{
						p1.append("-Much younger\n");
					}
					else
					{
						p1.append("-Younger" + "\n");
					}		
				}
			}
		}
		catch(NumberFormatException e)
		{
			//Nothing, just don't use the data
		}
		int depth1 = teamDepth(player1, firstTeam);
		int depth2 = teamDepth(player2, secTeam);
		if(depth1 != depth2)
		{
			if(depth1 < depth2)
			{
				p1.append("-Higher on his team's depth chart" + "\n");
			}
			else
			{
				p2.append("-Higher on his team's depth chart" + "\n");
			}
		}
		double cast1 = teamWorth(firstTeam);
		double cast2 = teamWorth(secTeam);
		if(Math.abs(cast1 - cast2) > 4.0)
		{
			if(cast1 > cast2)
			{
				if(cast1 - cast2 > 20.0)
				{
					p1.append("-Much better supporting cast\n");
				}
				else
				{
					p1.append("-Better supporting cast\n");
				}
			}
			else
			{
				if(cast2 - cast1 > 20.0)
				{
					p2.append("-Much better supporting cast\n");
				}
				else
				{
					p2.append("-Better supporting cast\n");
				}
			}
		}
		String cy1 = player1.info.contractStatus;
		String cy2 = player2.info.contractStatus;
		if(!cy1.contains("Under"))
		{
			p1.append("-In a contract year\n");
		}
		if(!cy2.contains("Under"))
		{
			p2.append("-In a contract year\n");
		}
		boolean sameBye1 = teamBye(holder, player1);
		boolean sameBye2 = teamBye(holder, player2);
		if(sameBye1)
		{
			p1.append("-Same bye as a player you've drafted\n");
		}
		if(sameBye2)
		{
			p2.append("-Same bye as a player you've drafted\n");
		}
		double risk1 = player1.risk;
		double risk2 = player2.risk;
		if(risk1 != 0 && risk2 != 0)
		{
			if(risk1 > risk2)
			{
				if(risk1 - risk2 > 3.0)
				{
					p2.append("-Much lower risk\n");
				}
				else
				{
					p2.append("-Lower risk\n");
				}
			}
			if(risk2 > risk1)
			{
				if(risk2 - risk1 > 3.0)
				{
					p1.append("-Much lower risk\n");
				}
				else
				{
					p1.append("-Lower risk\n");
				}
			}
		}
		double riskPos1 = player1.riskPos;
		double riskPos2 = player2.riskPos;
		if(riskPos1 > riskPos2)
		{
			if(riskPos1 - riskPos2 > 3.0)
			{
				p2.append("-Much lower positional risk\n");
			}
			else
			{
				p2.append("-Lower positional risk\n");
			}
		}
		if(riskPos2 > riskPos1)
		{
			if(riskPos2 - riskPos1 > 3.0)
			{
				p1.append("-Much lower positional risk\n");
			}
			else
			{
				p1.append("-Lower positional risk\n");
			}
		}
		double trend1 = trend(player1);
		double trend2 = trend(player2);
		if(trend1 != trend2)
		{
			if(trend1 > trend2)
			{
				if(trend1 - trend2 > 10.0)
				{
					p1.append("-Value is trending in a much\n better direction\n");
				}
				else
				{
					p1.append("-Value is trending in a better direction\n");
				}
			}
			else
			{
				if(trend2 - trend1 > 10.0)
				{
					p2.append("-Value is trending in a much\n better direction\n");
				}
				else
				{
					p2.append("-Value is trending in a better direction\n");
				}		
			}
		}
		double quantity1 = player1.values.count;
		double quantity2 = player2.values.count;
		if(quantity1 > quantity2)
		{
			if(quantity1 - quantity2 > 3)
			{
				p1.append("-Shows up in a lot more rankings\n");
			}
			else
			{
				p1.append("-Shows up in more rankings\n");
			}
		}
		else if(quantity2 > quantity1)
		{
			if(quantity2 - quantity1 > 3)
			{
				p2.append("-Shows up in a lot more rankings\n");
			}
			else
			{
				p2.append("-Shows up in more rankings\n");
			}	
		}
		int lineRank1 = lineRank(player1);
		int lineRank2 = lineRank(player2);
		if(lineRank1 < lineRank2)
		{
			if(lineRank2 - lineRank1 > 6)
			{
				p1.append("-Much better offensive line\n");
			}
			else
			{
				p1.append("-Better offensive line\n");
			}
		}
		else if(lineRank2 < lineRank1)
		{
			if(lineRank1 - lineRank2 > 6)
			{
				p2.append("-Much better offensive line\n");
			}
			else
			{
				p2.append("-Better offensive line\n");
			}	
		}
		int pr1 = prRatio(player1);
		int pr2 = prRatio(player2);
		if((player1.info.position.equals("QB") || player1.info.position.equals("WR") ||
				player1.info.position.equals("TE")) && pr1 > pr2)
		{
			p1.append("-Team passes more often\n");
		}
		if((player2.info.position.equals("QB") || player2.info.position.equals("WR") ||
				player2.info.position.equals("TE")) && pr2 > pr1)
		{
			p2.append("-Team passes more often\n");
		}
		if(player1.info.position.equals("RB") && pr1 < pr2)
		{
			p1.append("-Team runs more often\n");
		}
		if(player2.info.position.equals("RB") && pr2 < pr1)
		{
			p2.append("-Team runs more often\n");
		}
		int draft1 = draftRank(player1);
		int draft2 = draftRank(player2);
		if(draft1 < draft2)
		{
			if(draft2 - draft1 > 5)
			{
				p1.append("-Much better graded draft\n");
			}
			else
			{
				p1.append("-Better graded draft\n");
			}
		}
		else if(draft2 < draft1)
		{
			if(draft1 - draft2 > 5)
			{
				p2.append("-Much better graded draft\n");
			}
			else
			{
				p2.append("-Better graded draft\n");
			}	
		}
		double diff1 = player1.values.high - player1.values.low;
		double diff2 = player2.values.high - player2.values.low;
		if(Math.abs(diff1 - diff2) > 5.0)
		{
			if(diff1 < diff2)
			{
				if(diff2 - diff1 > 15.0)
				{
					p1.append("-Much more consistently ranked value\n");
				}
				else
				{
					p1.append("-More consistent value\n");
				}
			}
			else if(diff2 < diff1)
			{
				if(diff1 - diff2 > 15.0)
				{
					p2.append("-Much more consistently ranked value\n");
				}
				else
				{
					p2.append("-More consistent value\n");
				}
			}
		}
		double adp1 = adp(player1);
		double adp2 = adp(player2);
		if(Math.abs(adp1 - adp2) > 2.0)
		{
			if(adp1 < adp2)
			{
				if(adp2 - adp1 > 15.0)
				{
					p1.append("-Much higher ADP\n");
				}
				else
				{
					p1.append("-Higher ADP\n");
				}
			}
			else
			{
				if(adp1 - adp2 > 15.0)
				{
					p2.append("-Much higher ADP\n");
				}
				else
				{
					p2.append("-Higher ADP\n");
				}		
			}
		}
		double ecr1 = player1.values.ecr;
		double ecr2 = player2.values.ecr;
		if(Math.abs(ecr1 - ecr2) > 2)
		{
			if(ecr1 < ecr2)
			{
				if(ecr2 - ecr1 > 10)
				{
					p1.append("-Much higher ECR\n");
				}
				else
				{
					p1.append("-Higher ECR\n");
				}
			}
			if(ecr2 < ecr1)
			{
				if(ecr1 - ecr2 > 10)
				{
					p2.append("-Much higher ECR\n");
				}
				else
				{
					p2.append("-Higher ECR\n");
				}
			}
		}
		int sos1 = player1.info.sos;
		int sos2 = player2.info.sos;
		if(sos1 > sos2)
		{
			if(sos1 - sos2 < 5)
			{
				p2.append("-Easier positional SOS\n");
			}
			else
			{
				p2.append("-Much easier positional SOS\n");
			}
		}
		else if(sos2 > sos1)
		{
			if(sos2 - sos1 < 5)
			{
				p1.append("-Easier positional SOS\n");
			}
			else
			{
				p1.append("-Much easier positional SOS\n");
			}
		}
		boolean inj1 = injury(player1);
		boolean inj2 = injury(player2);
		if(inj1)
		{
			p1.append("-Injured\n");
		}
		if(inj2)
		{
			p2.append("-Injured\n");
		}
		if(player1.stats.contains("Broken Tackles") && player2.stats.contains("Broken Tackles") &&
				player1.info.position.equals(player2.info.position))
		{
			int bt1 = bt(player1);
			int bt2 = bt(player2);
			if(bt1 > bt2)
			{
				if(bt1 - bt2 > 15)
				{
					p1.append("-Broke many more tackles last year\n");
				}
				else
				{
					p1.append("-Broke more tackles last year\n");
				}
			}
			else if(bt2 > bt1)
			{
				if(bt2 - bt1 > 15)
				{
					p2.append("-Broke many more tackles last year\n");
				}
				else
				{
					p2.append("-Broke more tackles last year\n");
				}
			}
		}
		if(player1.info.additionalStat.contains("%") && player2.info.additionalStat.contains("%"))
		{
			double mib1 = mib(player1);
			double mib2 = mib(player2);
			if(mib1 > mib2)
			{
				if(mib1 - mib2 > 10.0)
				{
					p2.append("-Faced 8+ in the box much less \noften last year\n");
				}
				else
				{
					p2.append("-Faced 8+ in the box less often last year\n");
				}
			}
			else if(mib2 > mib1)
			{
				if(mib2 - mib1 > 10.0)
				{
					p1.append("-Faced 8+ in the box much less \noften last year\n");
				}
				else
				{
					p1.append("-Faced 8+ in the box less often last year\n");
				}
			}
		}
		boolean pos1 = samePos(player1, holder);
		boolean pos2 = samePos(player2, holder);
		if(!pos1)
		{
			p1.append("-You have not yet drafted a player \nof this position\n");
		} 
		if(!pos2)
		{
			p2.append("-You have not yet drafted a player \nof this position\n");
		}
		fixOutput(dialog, cont, holder, player1, player2, p1, p2);
	}
	
	/**
	 * Sets the output of the results
	 */
	public static void fixOutput(final Dialog dialog, final Context cont, final Storage holder,
			final PlayerObject player1, final PlayerObject player2, StringBuilder p1,
			StringBuilder p2) 
	{
		dialog.setContentView(R.layout.comparator_output);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		TextView header1 = (TextView)dialog.findViewById(R.id.compare_header_1);
		header1.setText(player1.info.name);
		TextView header2 = (TextView)dialog.findViewById(R.id.compare_header_2);
		header2.setText(player2.info.name);
		Button back = (Button)dialog.findViewById(R.id.compare_back);
		back.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				handleComparingInit(holder, cont);
	    	}	
		});
		TextView output1 = (TextView)dialog.findViewById(R.id.compare_output_1);
		TextView output2 = (TextView)dialog.findViewById(R.id.compare_output_2);
		output1.setText(p1.toString());
		output2.setText(p2.toString());
		Button close = (Button)dialog.findViewById(R.id.compare_close);
		close.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button add1 = (Button)dialog.findViewById(R.id.add_comparator_1);
		Button add2 = (Button)dialog.findViewById(R.id.add_comparator_2);
		add1.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				List<String> watch = ReadFromFile.readWatchList(cont);
				if(!watch.contains(player1.info.name))
				{
					Toast.makeText(cont, player1.info.name + " added to the watch list", Toast.LENGTH_SHORT).show();
					watch.add(player1.info.name);
					WriteToFile.writeWatchList(cont, watch);
				}
				else
				{ 
					Toast.makeText(cont, player1.info.name + " already in the watch list", Toast.LENGTH_SHORT).show();
				}
	    	}	
		});
		add2.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				List<String> watch = ReadFromFile.readWatchList(cont);
				if(!watch.contains(player2.info.name))
				{
					Toast.makeText(cont, player2.info.name + " added to the watch list", Toast.LENGTH_SHORT).show();
					watch.add(player2.info.name);
					WriteToFile.writeWatchList(cont, watch);
				}
				else
				{
					Toast.makeText(cont, player2.info.name + " already in the watch list", Toast.LENGTH_SHORT).show();
				}
	    	}	
		});
		dialog.show();
	}
	
	/**
	 * Gets the broken tackle total
	 */
	public static int bt(PlayerObject player)
	{
		String btStr = player.stats.split("Broken Tackles: ")[1];
		String total = btStr.split(", ")[0];
		return Integer.parseInt(total);
	}
	
	/**
	 * Gets the 8+ men in box percentage
	 */
	public static double mib(PlayerObject player)
	{
		String statWhole = player.info.additionalStat;
		String percentOn = statWhole.split(": ")[1];
		String relativeLeft = percentOn.split(", ")[0];
		return Double.parseDouble(relativeLeft.substring(0, relativeLeft.length()-1));
	}
	
	/**
	 * Determines if you've drafted a player of a certain position
	 */
	public static boolean samePos(PlayerObject player, Storage holder)
	{
		List<PlayerObject> draft = new ArrayList<PlayerObject>();
		if(player.info.position.equals("QB"))
		{
			draft = holder.draft.qb;
		}
		else if(player.info.position.equals("RB"))
		{
			draft = holder.draft.rb;
		}
		else if(player.info.position.equals("WR"))
		{
			draft = holder.draft.wr;
		}
		else if(player.info.position.equals("TE"))
		{
			draft = holder.draft.te;
		}
		else if(player.info.position.equals("D/ST"))
		{
			draft = holder.draft.def;
		}
		else
		{
			draft = holder.draft.k;
		}
		if(draft.size() == 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Determines if a player is injured or not
	 */
	public static boolean injury(PlayerObject player)
	{
		if(!player.injuryStatus.contains("Healthy"))
		{
			return true;
		}
		return false;
	}

	/**
	 * Parses draft rank
	 */
	public static int draftRank(PlayerObject player)
	{
		String[] split = player.draftClass.split("\n");
		String average = split[0];
		String rank = average.split("\\(")[1].substring(0, average.split("\\(")[1].length() - 1);
		return Integer.parseInt(rank);
	}
	
	/**
	 * returns the overall line rank
	 */
	public static int lineRank(PlayerObject player)
	{
		String[] split = player.info.oLineStatus.split("\n");
		String overall = split[0];
		String result = overall.split(": ")[1];
		return Integer.parseInt(result);
	}
	
	/**
	 * Returns the team pass/run ratio
	 */
	public static int prRatio(PlayerObject player)
	{
		String ratio = player.info.passRunRatio.replace("%", "");
		String[] split = ratio.split(": ");
		return Integer.parseInt(split[1]);
	}
	
	/**
	 * Parses the adp
	 */
	public static double adp(PlayerObject player)
	{
		if(player.info.adp.equals("Not set"))
		{
			return 500.0;
		}
		return Double.parseDouble(player.info.adp);
	}
	
	/**
	 * Returns the trend of a player
	 */
	public static double trend(PlayerObject player)
	{
		if(player.info.trend.contains("+") || player.info.trend.contains("-"))
		{
			String trendStr = player.info.trend.replace("+", "");
			if(trendStr.contains("-"))
			{
				return 0.0 - Double.parseDouble(trendStr.replace("-", ""));
			}
			return Double.parseDouble(trendStr);
		}
		return 0.0;
	}
	
	/**
	 * Sees if you've drafted a player with the same bye as the player considered
	 */
	public static boolean teamBye(Storage holder, PlayerObject player)
	{
		List<PlayerObject> draft = new ArrayList<PlayerObject>();
		if(player.info.position.equals("QB"))
		{
			draft = holder.draft.qb;
		}
		else if(player.info.position.equals("RB"))
		{
			draft = holder.draft.rb;
		}
		else if(player.info.position.equals("WR"))
		{
			draft = holder.draft.wr;
		}
		else if(player.info.position.equals("TE"))
		{
			draft = holder.draft.te;
		}
		else if(player.info.position.equals("D/ST"))
		{
			draft = holder.draft.def;
		}
		else
		{
			draft = holder.draft.k;
		}
		boolean sameBye = false;
		for(PlayerObject iter : draft)
		{
			if(iter.info.position.equals(player.info.position))
			{
				return true;
			}
		}
		return sameBye;
	}
	
	/**
	 * Finds the sum of the worth of a team (supporting cast)
	 */
	public static double teamWorth(List<PlayerObject> teamList)
	{
		double sum = 0.0;
		for(PlayerObject player : teamList)
		{
			sum+=player.values.worth;
		}
		return sum;
	}
	
	/**
	 * Finds if a player on the same team with the same position
	 * has a higher worth
	 */
	public static int teamDepth(PlayerObject player, List<PlayerObject> teamList)
	{
		int depth = 1;
		for(PlayerObject iter : teamList)
		{
			if(iter.info.position.equals(player.info.position) && iter.values.worth > player.values.worth)
			{
				depth++;
			}
		}
		return depth;
	}
	
	/**
	 * Finds the positional rank of a player
	 */
	public static int posRank(PlayerObject player, List<PlayerObject> posList)
	{
		double worth = player.values.worth;
		int rank = 1;
		for(PlayerObject iter : posList)
		{
			if(iter.values.worth > worth)
			{
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Gets the player given a name
	 */
	public static PlayerObject getPlayer(String name, Storage holder)
	{
		for(PlayerObject player : holder.players)
		{
			if(player.info.name.equals(name))
			{
				return player;
			}
		}
		return null;
	}
	
	/**
	 * Gets all players of the same position of the player
	 */
	public static List<PlayerObject> getPlayerPosition(Storage holder, PlayerObject player)
	{
		List<PlayerObject> positionList = new ArrayList<PlayerObject>();
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position))
			{
				positionList.add(iter);
			}
		}
		return positionList;
	}
	
	/**
	 * Gets all the players on the same team as the player
	 */
	public static List<PlayerObject> getPlayerTeam(Storage holder, PlayerObject player)
	{
		List<PlayerObject> teamList = new ArrayList<PlayerObject>();
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.team.equals(player.info.team))
			{
				teamList.add(iter);
			}
		}
		return teamList;
	}
}
