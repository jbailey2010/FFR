package com.example.fantasyfootballrankings.InterfaceAugmentations;

import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;

//Create a class to capture the action bar reference
public class MyActionBarListener implements ActionBarListener {
	private ActionBarView actionBarView;

	@Override
	public void onCreate(ActionBarView actionBar) {
		// Store a reference to the actionBar view
		this.actionBarView = actionBar;
	}

	public ActionBarView getActionBarView() {
		return actionBarView;
	}
}