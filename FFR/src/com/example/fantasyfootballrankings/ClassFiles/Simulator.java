package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseADP;
import FileIO.ReadFromFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.ffr.fantasyfootballrankings.R;

public class Simulator {

	public static void simulatorInit(final Context cont, final Storage holder) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.simulator_input);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.show();
		Button close = (Button) dialog.findViewById(R.id.simulator_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
		});
		List<String> adapter = new ArrayList<String>(700);
		final EditText round = (EditText) dialog.findViewById(R.id.rd_input);
		final EditText pick = (EditText) dialog.findViewById(R.id.pick_input);
		final TextView header = (TextView) dialog.findViewById(R.id.textView4);
		final AutoCompleteTextView player = (AutoCompleteTextView) dialog
				.findViewById(R.id.simulator_input);
		for (String name : holder.parsedPlayers) {
			adapter.add(name);
		}
		List<String> adapterSorted = ManageInput.sortSingleList(adapter);
		ArrayAdapter<String> doubleAdapter = new ArrayAdapter<String>(cont,
				android.R.layout.simple_dropdown_item_1line, adapterSorted);
		player.setAdapter(doubleAdapter);
		Button submit = (Button) dialog.findViewById(R.id.simulator_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String rdStr = round.getText().toString();
				String pickStr = pick.getText().toString();
				if (!ManageInput.isInteger(rdStr)
						|| !ManageInput.isInteger(pickStr)) {
					Toast.makeText(cont,
							"Please enter numbers for round and pick",
							Toast.LENGTH_SHORT).show();
					return;
				}
				int rd = Integer.parseInt(rdStr);
				int sel = Integer.parseInt(pickStr);
				if (sel > ReadFromFile.readRoster(cont).teams) {
					Toast.makeText(
							cont,
							"The selection can't be later than the number of teams",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (rd * sel > 200) {
					Toast.makeText(
							cont,
							"Pick too high, please enter a selection of at highest 200",
							Toast.LENGTH_LONG).show();
					return;
				}
				String name = player.getText().toString();
				if (!holder.parsedPlayers.contains(name)) {
					Toast.makeText(cont, "Please enter a valid player name",
							Toast.LENGTH_SHORT).show();
					return;
				}
				ParsingAsyncTask stupid = new ParsingAsyncTask();
				ParseADP task = stupid.new ParseADP((Activity) cont, holder,
						header);
				task.execute(rd * sel, name);
			}
		});
	}
}
