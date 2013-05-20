package com.example.fantasyfootballrankings.ClassFiles;

import com.example.fantasyfootballrankings.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ComparatorHandling 
{
	public static void handleComparingInit(Storage holder, Context cont)
	{
		final Dialog dialog = new Dialog(cont);
		dialog.setContentView(R.layout.comparator_view);
		dialog.show();
		Button close = (Button)dialog.findViewById(R.id.comparator_close);
		close.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				return;
	    	}	
		});
	}
}
