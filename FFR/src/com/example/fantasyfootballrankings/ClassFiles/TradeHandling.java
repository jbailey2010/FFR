package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import jeff.isawesome.fantasyfootballrankings.R;
import FileIO.ReadFromFile;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
	static AutoCompleteTextView inputView;
	static AutoCompleteTextView outputView;
	/**
	 * Sets up the dialog views and buttons and sets
	 * the onclicklisteners
	 * @param holder
	 * @param cont
	 */
	public static void handleTradeInit(Storage holder, Context cont)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trade_calculator);
		dialog.show(); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		tradeInput = (TextView)dialog.findViewById(R.id.trade_input);
		tradeOutput = (TextView)dialog.findViewById(R.id.trade_output);
		inputView = (AutoCompleteTextView)dialog.findViewById(R.id.your_loss_input);
		outputView = (AutoCompleteTextView)dialog.findViewById(R.id.your_gain_input);
		inputView.setAdapter(null);
		outputView.setAdapter(null);
		inputView.setThreshold(1);
		outputView.setThreshold(1);
		WindowManager wm = (WindowManager) cont.getSystemService(Context.WINDOW_SERVICE);
		 Display display = wm.getDefaultDisplay();
		 @SuppressWarnings("deprecation")
		 int width = display.getWidth();
		 Resources r = cont.getResources();
		 int newWidth = 0;
		 if(width < 500)
		 {
			 newWidth = 140;
		 }
		 else if(width < 780)
		 {
			 newWidth = 170;
		 }
		 else
		 {
			 newWidth = 240;
		 }
		 float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newWidth, r.getDisplayMetrics());
		 android.view.ViewGroup.LayoutParams params1 = inputView.getLayoutParams();
		 params1.width = (int) px;
		 inputView.setLayoutParams(params1);
		 android.view.ViewGroup.LayoutParams params2 = outputView.getLayoutParams();
		 params2.width = (int)px;
		 outputView.setLayoutParams(params2);
		setAdapter(holder, cont, dialog);
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
				tradeInput.setText("What you're giving: ");
				tradeOutput.setText("What you're getting: ");
				tradeInput.setVisibility(4);
				tradeOutput.setVisibility(4);
				TextView fairness = (TextView)dialog.findViewById(R.id.calc_header);
				fairness.setText("Trade Calculator");
	    	}	
		});
	}

	/**
	 * Sets the adapters of both inputs
	 */
	public static void setAdapter(final Storage holder, Context cont, final Dialog dialog)
	{
		List<String> adapter = new ArrayList<String>(700);
		boolean isAuction = ReadFromFile.readIsAuction(cont);
		if(isAuction)
		{
			for(int i = 1; i < 201; i++)
			{
				adapter.add("$" + i);
			}
		}
		for(String name : holder.parsedPlayers)
		{
			adapter.add(name);
		}
		ArrayAdapter<String> doubleAdapter = new ArrayAdapter<String>(cont,
                android.R.layout.simple_dropdown_item_1line, adapter);
		inputView.setAdapter(doubleAdapter);
		inputView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				handleInput(holder, dialog);
				inputView.setText("");
			}
		});
		outputView.setAdapter(doubleAdapter);
		outputView.setOnItemClickListener(new OnItemClickListener(){
 
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				handleInput(holder, dialog);	
				outputView.setText("");
			}
		});
		inputView.setThreshold(1);
		outputView.setThreshold(1);
	}

	/**
	 * Handles the input once it's been selected
	 * @param holder
	 * @param dialog
	 */
	public static void handleInput(Storage holder, Dialog dialog)
	{
		String newInput = inputView.getText().toString();
		String oldInput = tradeInput.getText().toString();
		if(holder.parsedPlayers.contains(newInput) || newInput.contains("$"))
		{
			if(!oldInput.contains(newInput))
			{
				tradeInput.setVisibility(0);
				tradeInput.setText(oldInput + "\n" + newInput);
			}
		}
		String newOutput = outputView.getText().toString();
		String oldOutput = tradeOutput.getText().toString();
		if(holder.parsedPlayers.contains(newOutput) || newOutput.contains("$"))
		{
			if(!oldOutput.contains(newOutput))
			{
				tradeOutput.setVisibility(0);
				tradeOutput.setText(oldOutput + "\n" + newOutput);
			}
		}
		TextView fairness = (TextView)dialog.findViewById(R.id.calc_header);
		if(tradeOutput.getText().toString().contains("\n") && tradeInput.getText().toString().contains("\n"))
		{
			fairness.setText(findRelativeValue(holder));
		}
	}

	/**
	 * Returns the string to set the bottom textview to
	 * based on the input texts
	 * @param holder
	 * @return
	 */
	public static String findRelativeValue(Storage holder)
	{
		String output = "";
		String inputText = tradeInput.getText().toString();
		String outputText = tradeOutput.getText().toString();
		double total = totalValue(inputText, outputText, holder);
		if(total < - 25)
		{
			output = "This looks bad. Unless there's a player you must have, don't do it.";
		}
		else if(total < -9)
		{ 
			output = "Not in your favor, but not terrible. If there's a player you want, pull the trigger, but be wary.";
		}
		else if(total < -4)
		{
			output = "Against your favor, but not by a lot.";
		}
		else if(total < -2)
		{
			output = "Marginally against your favor.";
		}
		else if(total > 25)
		{
			output = "Pull the trigger. This in very much in your favor.";
		}
		else if(total > 9)
		{
			output = "In your favor, though not by a huge amount.";
		}
		else if(total > 4)
		{
			output = "In your favor, but not by a lot.";
		}
		else if(total > 2)
		{
			output = "Marginally in your favor.";
		}
		else
		{
			output = "This trade is relatively fair, pull the trigger if you see fit.";
		}
		return output;
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
			String[] inputSet = ManageInput.tokenize(input, '\n', 1);
			for(int i = 0; i < inputSet.length; i++)
			{
				total -= findValue(inputSet[i], holder);
			}
		}
		if(output.contains("\n") && !output.equals("Enter what you'd get above"))
		{
			String[] outputSet = ManageInput.tokenize(output, '\n', 1);
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
					value = player.values.worth * 3.5;
					break;
				}
			}
		}
		return value;
	}
}