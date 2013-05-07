package AsyncTasks;

import java.io.IOException;

import org.htmlcleaner.XPatherException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;

/**
 * Handles the conditional parsing of the permanent data
 * (won't change week to week, only year to year)
 * @author Jeff
 *
 */
public class PermanentDataAsyncTask 
{

	/**
	 * This handles the parsing of the offensive line
	 * rankings, pass blocking, run blocking, and last year's
	 * rankings. NOT YET IMPLEMENTED OR CALLED
	 * @author Jeff
	 *
	 */
	public class ParseLineRanks extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		Storage hold;
	    public ParseLineRanks(Activity activity, Storage holder) 
	    {
	        act = activity;
	        hold = holder;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];

			return null;
	    }
	  }
}
