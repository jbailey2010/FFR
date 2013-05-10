package AsyncTasks;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.Pages.Rankings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
/**
 * Offline async tasks that work on the data
 * @author Jeff
 *
 */
public class AlteringDataAsyncTasks 
{
	/**
	 * Handles the high level operations that operate on all
	 * player rankings
	 * @author Jeff
	 *
	 */
	public class OfflineHighLevel extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
		Activity act;
		Storage hold;
	    public OfflineHighLevel(Activity activity, Storage holder) 
	    {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        act = activity;
	        hold = holder;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the rankings...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   if(hold.players.size() > 1)
		   {
			   ((Rankings)act).intermediateHandleRankings(act);
		   }
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
			HighLevel.setStatus(holder);
		    HighLevel.getParsedPlayers(holder);
		    HighLevel.setPermanentData(holder, cont);
			return null;
	    }
	  }

}

