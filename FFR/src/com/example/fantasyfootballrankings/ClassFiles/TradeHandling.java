package com.example.fantasyfootballrankings.ClassFiles;

import com.example.fantasyfootballrankings.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Handles the parsing of trade 
 * input from a user
 * @author Jeff
 *
 */
public class TradeHandling 
{
	static TextView tradeInput;
	static TextView tradeOutput;
	/**
	 * Sets up the dialog views and buttons and sets
	 * the onclicklisteners
	 * @param holder
	 * @param cont
	 */
	public static void handleTradeInit(Storage holder, Context cont)
	{
		final Dialog dialog = new Dialog(cont);
		dialog.setContentView(R.layout.trade_calculator);
		dialog.show(); 
		tradeInput = (TextView)dialog.findViewById(R.id.trade_input);
		tradeOutput = (TextView)dialog.findViewById(R.id.trade_output);
		Button dismiss = (Button)dialog.findViewById(R.id.search_close);
		dismiss.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				return;
	    	}	
		});
		Button clearTrade = (Button)dialog.findViewById(R.id.clear_trade);
		clearTrade.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				tradeInput.setText("Enter what you'd give above");
				tradeOutput.setText("Enter what you'd get above");
	    	}	
		});
	}
	
	
	
	/**
	 * Returns the aggregate worth of the players in the output
	 * minus that of those in the input
	 */
	public static double totalValue(String input, String output, Storage holder)
	{
		double total = 0.0;
		if(input.contains("\n") && !input.equals("Enter what you'd give above"))
		{
			String[] inputSet = input.split("\n");
			for(int i = 0; i < inputSet.length; i++)
			{
				total -= findValue(inputSet[i], holder);
			}
		}
		if(output.contains("\n") && !output.equals("Enter what you'd get above"))
		{
			String[] outputSet = output.split("\n");
			for(int i = 0; i < outputSet.length; i++)
			{
				total += findValue(outputSet[i], holder);
			}
		}
		return total;
	}
	
	/**
	 * Finds the value of the player input or value input, and returns it
	 * @param input
	 * @param holder
	 * @return
	 */
	public static double findValue(String input, Storage holder)
	{
		double value = 0.0;
		if(input.contains("$"))
		{
			value = Double.parseDouble(input.replace("$", ""));
		}
		else
		{
			for(PlayerObject player : holder.players)
			{
				if(player.info.name.equals(input))
				{
					value = player.values.worth;
					break;
				}
			}
		}
		return value;
	}
}
