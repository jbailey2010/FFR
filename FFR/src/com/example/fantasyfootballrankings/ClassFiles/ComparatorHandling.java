package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
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
		dialog.setContentView(R.layout.comparator_view);
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
				showInput(player1, dialog, cont, holder, R.id.first_player_compare);
				if(list2.getText().toString().contains("\n"))
				{
					System.out.println("Call something");
					//Call function to do comparing
				}
			}
		});
		player2.setAdapter(doubleAdapter);
		player2.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView list1 = (TextView)dialog.findViewById(R.id.first_player_compare);
				showInput(player2, dialog, cont, holder, R.id.second_player_compare);
				if(list1.getText().toString().contains("\n"))
				{
					System.out.println("Call something");
					//Call function to do comparing
				}
			}
		});
	}
	
	/**
	 * Sets the textview at the bottom to be the inputted player
	 */
	public static void showInput(AutoCompleteTextView player, Dialog dialog, Context cont, Storage holder, int id) 
	{
		String input = player.getText().toString();
		TextView list = (TextView)dialog.findViewById(id);
		list.setText(list.getText().toString() + input + "\n");
		list.setVisibility(TextView.VISIBLE);
	}
}
