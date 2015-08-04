package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.List;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.StorageAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseProjections;
import AsyncTasks.StorageAsyncTask.WriteNewPAA;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Flex;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

public class MyLeagueUtils {
	static boolean isFirst;

	/**
	 * Gets the roster/teams input from the user
	 * 
	 * @param key
	 */
	public static void getRoster(final Context cont, final Storage holder,
			final Roster dummyRoster, final String key,
			final ImportedTeam newImport) {
		isFirst = true;
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.roster_selections);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		List<String> quantitiesQBTE = new ArrayList<String>();
		quantitiesQBTE.add("0");
		quantitiesQBTE.add("1");
		quantitiesQBTE.add("2");
		List<String> quantitiesRBWR = new ArrayList<String>();
		quantitiesRBWR.add("0");
		quantitiesRBWR.add("1");
		quantitiesRBWR.add("2");
		quantitiesRBWR.add("3");
		List<String> quantitiesTeam = new ArrayList<String>();
		quantitiesTeam.add("8");
		quantitiesTeam.add("10");
		quantitiesTeam.add("12");
		quantitiesTeam.add("14");
		quantitiesTeam.add("16");
		List<String> flexFlag = new ArrayList<String>();
		flexFlag.add("Yes");
		flexFlag.add("No");
		List<String> quantitiesK = new ArrayList<String>();
		quantitiesK.add("0");
		quantitiesK.add("1");
		final Spinner qb = (Spinner) dialog.findViewById(R.id.qb_quantity);
		final Spinner te = (Spinner) dialog.findViewById(R.id.te_quantity);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				cont, android.R.layout.simple_spinner_dropdown_item,
				quantitiesQBTE);
		qb.setAdapter(spinnerArrayAdapter);
		te.setAdapter(spinnerArrayAdapter);
		final Spinner rb = (Spinner) dialog.findViewById(R.id.rb_quantity);
		final Spinner wr = (Spinner) dialog.findViewById(R.id.wr_quantity);
		spinnerArrayAdapter = new ArrayAdapter<String>(cont,
				android.R.layout.simple_spinner_dropdown_item, quantitiesRBWR);
		rb.setAdapter(spinnerArrayAdapter);
		wr.setAdapter(spinnerArrayAdapter);
		final Spinner team = (Spinner) dialog.findViewById(R.id.team_quantity);
		spinnerArrayAdapter = new ArrayAdapter<String>(cont,
				android.R.layout.simple_spinner_dropdown_item, quantitiesTeam);
		team.setAdapter(spinnerArrayAdapter);
		final Spinner flex = (Spinner) dialog.findViewById(R.id.flex_quantity);
		final Spinner def = (Spinner) dialog
				.findViewById(R.id.defense_quantity);
		final Spinner k = (Spinner) dialog.findViewById(R.id.kicker_quantity);
		spinnerArrayAdapter = new ArrayAdapter<String>(cont,
				android.R.layout.simple_spinner_dropdown_item, flexFlag);
		flex.setAdapter(spinnerArrayAdapter);
		spinnerArrayAdapter = new ArrayAdapter<String>(cont,
				android.R.layout.simple_spinner_dropdown_item, quantitiesK);
		def.setAdapter(spinnerArrayAdapter);
		k.setAdapter(spinnerArrayAdapter);
		if (dummyRoster.flex == null) {
			flex.setSelection(1);
		} else {
			flex.setSelection(0);
		}
		team.setSelection(quantitiesTeam.indexOf(String
				.valueOf(dummyRoster.teams)));
		wr.setSelection(quantitiesRBWR.indexOf(String.valueOf(dummyRoster.rbs)));
		rb.setSelection(quantitiesRBWR.indexOf(String.valueOf(dummyRoster.wrs)));
		qb.setSelection(quantitiesQBTE.indexOf(String.valueOf(dummyRoster.qbs)));
		te.setSelection(quantitiesQBTE.indexOf(String.valueOf(dummyRoster.tes)));
		k.setSelection(quantitiesK.indexOf(String.valueOf(dummyRoster.k)));
		def.setSelection(quantitiesK.indexOf(String.valueOf(dummyRoster.def)));
		flex.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String selection = ((TextView) arg1).getText().toString();
				if (selection.equals("Yes") && !isFirst) {
					if (dummyRoster.flex == null) {
						dummyRoster.flex = new Flex();
					}
					handleFlexPopUp(cont, dummyRoster.flex);

				} else if (selection.equals("Yes")) {
					isFirst = !isFirst;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		Button submit = (Button) dialog.findViewById(R.id.roster_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dummyRoster.qbs = Integer.parseInt((String) qb
						.getSelectedItem());
				dummyRoster.rbs = Integer.parseInt((String) rb
						.getSelectedItem());
				dummyRoster.wrs = Integer.parseInt((String) wr
						.getSelectedItem());
				dummyRoster.tes = Integer.parseInt((String) te
						.getSelectedItem());
				dummyRoster.teams = Integer.parseInt((String) team
						.getSelectedItem());
				dummyRoster.def = Integer.parseInt((String) def
						.getSelectedItem());
				dummyRoster.k = Integer.parseInt((String) k.getSelectedItem());
				if (((String) flex.getSelectedItem()).equals("No")) {
					dummyRoster.flex = null;
				}
				if (dummyRoster.flex.rbwr == 0 && dummyRoster.flex.rbwrte == 0
						&& dummyRoster.flex.op == 0) {
					dummyRoster.flex = null;
				}
				WriteToFile.writeRoster(key, cont, dummyRoster);
				newImport.roster = dummyRoster;
				Toast.makeText(cont, "Updating starting lineups...",
						Toast.LENGTH_SHORT).show();
				for (TeamAnalysis team : newImport.teams) {
					team.r = newImport.roster;
					team.manageStarters(
							team.team.split("Quarterbacks: ")[1].split("\n")[0]
									.split(", "),
							team.team.split("Running Backs: ")[1].split("\n")[0]
									.split(", "),
							team.team.split("Wide Receivers: ")[1].split("\n")[0]
									.split(", "), team.team
									.split("Tight Ends: ")[1].split("\n")[0]
									.split(", "), team.team.split("D/ST: ")[1]
									.split("\n")[0].split(", "), team.team
									.split("Kickers: ")[1].split("\n")[0]
									.split(", "));
				}
				dialog.dismiss();
			}
		});
	}

	/**
	 * Handles the flex pop up for all three major flexes
	 */
	public static void handleFlexPopUp(Context cont, final Flex newFlex) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.flex_layout_roster);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		final Spinner rbwr = (Spinner) dialog.findViewById(R.id.rb_wr_quantity);
		final Spinner rbwrte = (Spinner) dialog
				.findViewById(R.id.rb_wr_te_quantity);
		final Spinner op = (Spinner) dialog.findViewById(R.id.op_quantity);
		List<String> quantitiesQBTE = new ArrayList<String>();
		quantitiesQBTE.add("0");
		quantitiesQBTE.add("1");
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				cont, android.R.layout.simple_spinner_dropdown_item,
				quantitiesQBTE);
		rbwr.setAdapter(spinnerArrayAdapter);
		rbwrte.setAdapter(spinnerArrayAdapter);
		op.setAdapter(spinnerArrayAdapter);
		if (newFlex != null) {
			rbwr.setSelection(quantitiesQBTE.indexOf(String
					.valueOf(newFlex.rbwr)));
			rbwrte.setSelection(quantitiesQBTE.indexOf(String
					.valueOf(newFlex.rbwrte)));
			op.setSelection(quantitiesQBTE.indexOf(String.valueOf(newFlex.op)));
		} else {
			rbwr.setSelection(0);
			rbwrte.setSelection(0);
			op.setSelection(0);
		}
		Button submit = (Button) dialog.findViewById(R.id.roster_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newFlex.rbwr = Integer.valueOf(rbwr.getSelectedItem()
						.toString());
				newFlex.rbwrte = Integer.valueOf(rbwrte.getSelectedItem()
						.toString());
				newFlex.op = Integer.valueOf(op.getSelectedItem().toString());
				dialog.dismiss();
			}
		});
	}

	/**
	 * Sets up pass settings
	 * 
	 * @param cont
	 * @param scoring
	 */
	public static void passSettings(final Context cont, final Storage holder,
			final Scoring dummyScoring, final String key) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_pass);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		final EditText yards = (EditText) dialog
				.findViewById(R.id.scoring_pass_yards);
		final EditText tds = (EditText) dialog
				.findViewById(R.id.scoring_pass_td);
		final EditText ints = (EditText) dialog
				.findViewById(R.id.scoring_pass_int);
		yards.setText(String.valueOf(dummyScoring.passYards),
				TextView.BufferType.EDITABLE);
		tds.setText(String.valueOf(dummyScoring.passTD),
				TextView.BufferType.EDITABLE);
		ints.setText(String.valueOf(dummyScoring.interception),
				TextView.BufferType.EDITABLE);
		Button toRun = (Button) dialog.findViewById(R.id.scoring_pass_continue);
		toRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if (ManageInput.isInteger(yardStr)
						&& ManageInput.isInteger(tdStr)
						&& ManageInput.isInteger(intStr)) {
					dummyScoring.passYards = Integer.parseInt(yardStr);
					dummyScoring.passTD = Integer.parseInt(tdStr);
					dummyScoring.interception = Integer.parseInt(intStr);
					dialog.dismiss();
					runSettings(cont, holder, dummyScoring, key);
				} else {
					Toast.makeText(cont,
							"Please enter integer values greater than 0",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Sets up run settings
	 * 
	 * @param cont
	 * @param scoring
	 */
	public static void runSettings(final Context cont, final Storage holder,
			final Scoring dummyScoring, final String key) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_run);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		final EditText yards = (EditText) dialog
				.findViewById(R.id.scoring_run_yards);
		final EditText tds = (EditText) dialog
				.findViewById(R.id.scoring_run_td);
		final EditText ints = (EditText) dialog
				.findViewById(R.id.scoring_run_int);
		yards.setText(String.valueOf(dummyScoring.rushYards),
				TextView.BufferType.EDITABLE);
		tds.setText(String.valueOf(dummyScoring.rushTD),
				TextView.BufferType.EDITABLE);
		ints.setText(String.valueOf(dummyScoring.fumble),
				TextView.BufferType.EDITABLE);
		Button back = (Button) dialog.findViewById(R.id.scoring_run_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				passSettings(cont, holder, dummyScoring, key);
			}
		});
		Button toRun = (Button) dialog.findViewById(R.id.scoring_run_continue);
		toRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if (ManageInput.isInteger(yardStr)
						&& ManageInput.isInteger(tdStr)
						&& ManageInput.isInteger(intStr)) {
					dummyScoring.rushYards = Integer.parseInt(yardStr);
					dummyScoring.rushTD = Integer.parseInt(tdStr);
					dummyScoring.fumble = Integer.parseInt(intStr);
					dialog.dismiss();
					recSettings(cont, holder, dummyScoring, key);
				} else {
					Toast.makeText(cont,
							"Please enter integer values greater than 0",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Sets up receiving numbers
	 * 
	 * @param cont
	 * @param scoring
	 */
	public static void recSettings(final Context cont, final Storage holder,
			final Scoring dummyScoring, final String key) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.scoring_rec);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		final EditText yards = (EditText) dialog
				.findViewById(R.id.scoring_rec_yards);
		final EditText tds = (EditText) dialog
				.findViewById(R.id.scoring_rec_td);
		final EditText ints = (EditText) dialog
				.findViewById(R.id.scoring_rec_catch);
		yards.setText(String.valueOf(dummyScoring.recYards),
				TextView.BufferType.EDITABLE);
		tds.setText(String.valueOf(dummyScoring.recTD),
				TextView.BufferType.EDITABLE);
		ints.setText(String.valueOf(dummyScoring.catches),
				TextView.BufferType.EDITABLE);
		Button back = (Button) dialog.findViewById(R.id.scoring_rec_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				runSettings(cont, holder, dummyScoring, key);
			}
		});
		Button toRun = (Button) dialog.findViewById(R.id.scoring_rec_continue);
		toRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String yardStr = yards.getText().toString();
				String tdStr = tds.getText().toString();
				String intStr = ints.getText().toString();
				if (ManageInput.isInteger(yardStr)
						&& ManageInput.isInteger(tdStr)
						&& ManageInput.isInteger(intStr)) {
					dummyScoring.recYards = Integer.parseInt(yardStr);
					dummyScoring.recTD = Integer.parseInt(tdStr);
					dummyScoring.catches = Integer.parseInt(intStr);
					dialog.dismiss();
					WriteToFile.writeScoring(key, cont, dummyScoring);
					ImportLeague.newImport.scoring = dummyScoring;
				} else {
					Toast.makeText(cont,
							"Please enter integer values greater than 0",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
