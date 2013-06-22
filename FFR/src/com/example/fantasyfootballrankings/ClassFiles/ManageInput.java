package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * A little class that should help with making user input
 * into searches...etc a little cooler, doing nicer things.
 * @author Jeff
 *
 */
public class ManageInput 
{
	static Scoring dummyScoring = new Scoring();
	/**
	 * This sets up the auto complete search with the given arraylist
	 * so that it autocompletes suggestions based on players who have
	 * already been parsed.
	 */
	public static void setupAutoCompleteSearch(Storage holder, List<String> playerNames, 
			AutoCompleteTextView input, Context cont)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(cont,
                 android.R.layout.simple_dropdown_item_1line, playerNames);
        input.setAdapter(adapter);
	}

	/**
	 * A function that just handles the checking of voice input
	 * relative to parsed players, returning only legitimate 
	 * matches
	 */
	public static List<String> voiceInput(ArrayList<String> matches, Context dialog,
			Storage holder, AutoCompleteTextView textView) 
	{
		List<String> results = new ArrayList<String>();
		for(String e:matches)
		{
			for(String players : holder.parsedPlayers)
			{
				if(players.contains(e))
				{
					results.add(players);
				}
			}
		}
		return results;
	}
	
	/**
	 * Handles the addition of an adapter to the listview
	 * on rankings and trending
	 */
	public static ArrayAdapter<String> handleArray(List<String> list, ListView listView, Activity cont)
	{
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(cont,
	            android.R.layout.simple_list_item_1, list);
	    listView.setAdapter(adapter);
	    return adapter;
	}
	
	/**
	 * Handles the filter quantity dialog
	 * @param cont
	 */
	public static void filterQuantity(final Context cont, final String flag, final int listSize)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.filter_quantity);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		int filterSize = ReadFromFile.readFilterQuantitySize(cont, flag);
		final SeekBar selector = (SeekBar)dialog.findViewById(R.id.seekBar_quantity);
		final TextView display = (TextView)dialog.findViewById(R.id.quantity_display);
		if(flag.equals("Rankings"))
		{
			rankingsSetContent(dialog, filterSize, selector, display, cont, flag, listSize);
		}
		else
		{
			trendingSetContent(dialog, filterSize, selector, display, cont, flag, listSize);
		}

	}

	private static void trendingSetContent(final Dialog dialog, final int filterSize,
			final SeekBar selector, final TextView display, final Context cont, final String flag,
			final int listSize) {
		if(filterSize == 0)
		{
			display.setText("None of the players");
		}
		else if(filterSize == 100)
		{
			display.setText("All of the players");
		}
		else
		{
			display.setText(filterSize + "% of the players");
		}
		selector.setProgress(filterSize);
		selector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    	    	int prog = selector.getProgress();
    	    	String size = "";
    	    	if(prog == 0)
    	    	{
    	    		size = "None of the players";
    	    	}
    	    	else if(prog == 100)
    	    	{
    	    		size = "All of the players";
    	    	}
    	    	else
    	    	{
    	    		size = prog + "% of the players";
    	    	}
    	    	display.setText(size);	
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    	});
		Button cancel = (Button)dialog.findViewById(R.id.filter_size_cancel);
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button submit = (Button)dialog.findViewById(R.id.filter_size_submit);
		submit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				WriteToFile.writeFilterSize(cont, selector.getProgress(), flag);
				dialog.dismiss();
				try {
					Trending.resetTrendingList(ReadFromFile.readLastFilter(cont), cont);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}	
		});
		
	}

	private static void rankingsSetContent(final Dialog dialog, int filterSize,
			final SeekBar selector, final TextView display, final Context cont, final String flag,
			final int listSize) 
	{
		if(filterSize == 0)
		{
			display.setText("None of the players");
		}
		else if(filterSize == 100)
		{
			display.setText("All of the players");
		}
		else
		{
			float percentage = (float)filterSize/100;
    		int total = (int)(listSize * percentage);
    		String newSize = total + " players maximum";
			display.setText(newSize);
		}
		selector.setProgress(filterSize);
		selector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    	    	int prog = selector.getProgress();
    	    	String size = "";
    	    	if(prog == 0)
    	    	{
    	    		size = "None of the players";
    	    	}
    	    	else if(prog == 100)
    	    	{
    	    		size = "All of the players";
    	    	}
    	    	else
    	    	{
    	    		float percentage = (float)prog/100;
    	    		int total = (int)(listSize * percentage);
    	    		size = total + " players maximum";
    	    	}
    	    	display.setText(size);	
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    	});
		Button cancel = (Button)dialog.findViewById(R.id.filter_size_cancel);
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button submit = (Button)dialog.findViewById(R.id.filter_size_submit);
		submit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				WriteToFile.writeFilterSize(cont, selector.getProgress(), flag);
				dialog.dismiss();
				Rankings.intermediateHandleRankings((Activity)cont);
	    	}	
		});
	}
	
	/**
	 * Determines whether to get scoring settings or ask for them, if anything.
	 */
	public static void setUpScoring(Context cont, Scoring scoring)
	{
		if(scoring.passYards != 0 && scoring.rushYards != 0 && scoring.recYards != 0)
		{
			return;
		}
		else if(scoring.passYards == 0 && !cont.getSharedPreferences("FFR", 0).getBoolean("Is Scoring Set?", false))
		{
			passSettings(cont, scoring);
		}
		else
		{
			scoring = ReadFromFile.readScoring(cont);
		}
	}
	
	/**
	 * Sets up pass settings
	 * @param cont
	 * @param scoring
	 */
	public static void passSettings(final Context cont, final Scoring scoring)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_pass);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		dialog.setCancelable(false);
		final EditText yards = (EditText)dialog.findViewById(R.id.scoring_pass_yards);
		final EditText tds = (EditText)dialog.findViewById(R.id.scoring_pass_td);
		final EditText ints = (EditText)dialog.findViewById(R.id.scoring_pass_yards);
		Button toRun = (Button)dialog.findViewById(R.id.scoring_pass_continue);
		toRun.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if(isInteger(yardStr) && isInteger(tdStr) && isInteger(intStr))
				{
					dummyScoring.passYards = Integer.parseInt(yardStr);
					dummyScoring.passTD = Integer.parseInt(tdStr);
					dummyScoring.interception = Integer.parseInt(intStr);
					dialog.dismiss();
					runSettings(cont, scoring);
				}
				else
				{
					Toast.makeText(cont, "Please enter integer values greater than 0", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * Sets up run settings
	 * @param cont
	 * @param scoring
	 */
	public static void runSettings(final Context cont, final Scoring scoring)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_run);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		dialog.setCancelable(false);
		final EditText yards = (EditText)dialog.findViewById(R.id.scoring_run_yards);
		final EditText tds = (EditText)dialog.findViewById(R.id.scoring_run_td);
		final EditText ints = (EditText)dialog.findViewById(R.id.scoring_run_int);
		Button back = (Button)dialog.findViewById(R.id.scoring_run_back);
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				passSettings(cont, scoring);
			}
		});
		Button toRun = (Button)dialog.findViewById(R.id.scoring_run_continue);
		toRun.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if(isInteger(yardStr) && isInteger(tdStr) && isInteger(intStr))
				{
					dummyScoring.rushYards = Integer.parseInt(yardStr);
					dummyScoring.rushTD = Integer.parseInt(tdStr);
					dummyScoring.fumble = Integer.parseInt(intStr);
					dialog.dismiss();
					recSettings(cont, scoring);
				}
				else
				{
					Toast.makeText(cont, "Please enter integer values greater than 0", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * Sets up receiving numbers
	 * @param cont
	 * @param scoring
	 */
	public static void recSettings(final Context cont, final Scoring scoring)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_rec);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		dialog.setCancelable(false);
		final EditText yards = (EditText)dialog.findViewById(R.id.scoring_rec_yards);
		final EditText tds = (EditText)dialog.findViewById(R.id.scoring_rec_td);
		final EditText ints = (EditText)dialog.findViewById(R.id.scoring_rec_catch);
		Button back = (Button)dialog.findViewById(R.id.scoring_run_back);
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				runSettings(cont, scoring);
			}
		});
		Button toRun = (Button)dialog.findViewById(R.id.scoring_run_continue);
		toRun.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if(isInteger(yardStr) && isInteger(tdStr) && isInteger(intStr))
				{
					dummyScoring.recYards = Integer.parseInt(yardStr);
					dummyScoring.recTD = Integer.parseInt(tdStr);
					dummyScoring.catches = Integer.parseInt(intStr);
					dialog.dismiss();
					WriteToFile.writeScoring(cont, dummyScoring);
				}
				else
				{
					Toast.makeText(cont, "Please enter integer values greater than 0", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * Makes sure a string is valid
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
