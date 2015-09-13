package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.WriteNewPAA;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.ffr.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMath;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.Utils.MathUtils;
import com.example.fantasyfootballrankings.ClassFiles.Utils.PlayerNewsActivity;
import com.example.fantasyfootballrankings.ClassFiles.Utils.TwitterWork;
import com.example.fantasyfootballrankings.InterfaceAugmentations.ActivitySwipeDetector;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.socialize.ActionBarUtils;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarOptions;

/**
 * Handles the player info function
 * 
 * @author Jeff
 * 
 */
public class PlayerInfo {
	double aucFactor;
	PlayerObject searchedPlayer;
	static List<Map<String, String>> data;
	public Storage holder;
	public Dialog dialog;
	static SimpleAdapter adapter;
	public static Button ranking;
	public static Button info;
	public static Button team;
	public static Button other;
	public boolean isImport = false;
	public Context cont;
	static Context a;
	public ImportedTeam newImport;
	public static List<NewsObjects> newsList = new ArrayList<NewsObjects>();

	/**
	 * Abstracted out of the menu handler as this could get ugly once the stuff
	 * is added to the dropdown
	 * 
	 * @throws IOException
	 */
	public void searchCalled(final Context oCont, final boolean isMyLeague,
			ImportedTeam it) throws IOException {
		newImport = it;
		cont = oCont;
		isImport = isMyLeague;
		// ReadFromFile.fetchNames(holder, oCont);
		final Dialog dialog = new Dialog(oCont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.search_players);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);

		Rankings.textView = (AutoCompleteTextView) (dialog)
				.findViewById(R.id.player_input);
		ManageInput.setupAutoCompleteSearch(holder, holder.players,
				Rankings.textView, oCont);
		Button searchDismiss = (Button) dialog.findViewById(R.id.search_cancel);
		searchDismiss.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button searchSubmit = (Button) dialog.findViewById(R.id.search_submit);
		Rankings.textView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem) arg1).getText1().getText()
						.toString();
				Rankings.textView.setText(text
						+ ", "
						+ ((TwoLineListItem) arg1).getText2().getText()
								.toString());
			}
		});
		searchSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/**
				 * On item select, get the name
				 */
				if (holder.parsedPlayers.contains(Rankings.textView.getText()
						.toString().split(", ")[0])) {
					dialog.dismiss();
					outputResults(Rankings.textView.getText().toString(),
							false, (Activity) oCont, holder, false, true, false);
				} else {
					Toast.makeText(oCont, "Not a valid player name",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}

	/**
	 * Makes a hey, here's how you draft popup
	 */
	public void checkIfFirstOpening(Activity act, boolean isRegularSeason) {
		if (!ReadFromFile.readFirstOpen(act) && !isRegularSeason) {
			dialog = new Dialog(act, R.style.RoundCornersFull);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.one_line_text);
			TextView text = (TextView) dialog.findViewById(R.id.textView1);
			text.setText("It appears to be your first time opening this pop up. If you're drafting/prepping for a draft, you can click and hold the name up top here to draft this player to a team. This information is also available in help.");
			WriteToFile.writeFirstOpen(act);
		}
	}

	/**
	 * outputs the results to the search dialog
	 * 
	 * @param namePlayer
	 * @param b
	 */
	public void outputResults(final String namePlayer, boolean flag,
			final Activity act, final Storage hold, final boolean watchFlag,
			final boolean draftable, boolean canRand) {
		newsList = new ArrayList<NewsObjects>();
		checkIfFirstOpening(act, hold.isRegularSeason);
		holder = hold;
		a = act;
		dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.search_output);
		aucFactor = ReadFromFile.readAucFactor(act);
		if (ManageInput.confirmInternet(act)) {
			// Your entity key. May be passed as a Bundle parameter to your
			// activity
			String entityKey = "http://www.ffr.com/"
					+ namePlayer.split(" - ")[0] + "/pi" + Home.yearKey;

			Entity entity = Entity.newInstance(entityKey, namePlayer);

			// Create an options instance to disable comments
			ActionBarOptions options = new ActionBarOptions();

			// Hide sharing
			options.setHideShare(true);
			options.setFillColor(Color.parseColor("#272727"));
			options.setBackgroundColor(Color.parseColor("#191919"));
			options.setAccentColor(Color.parseColor("#ff0000"));

			View actionBarWrapped = ActionBarUtils.showActionBar(act,
					R.layout.search_output, entity, options);

			// Now set the view for your activity to be the wrapped view.
			dialog.setContentView(actionBarWrapped);
		}
		Button addWatch = (Button) dialog.findViewById(R.id.add_watch);
		// If the add to list boolean exists
		if (isImport) {
			addWatch.setVisibility(View.GONE);
		} else if (!watchFlag) {
			addWatch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Check if the player is in the watchList
					int i = -1;
					for (String name : Rankings.watchList) {
						if (name.equals(namePlayer.split(", ")[0])) {
							i++;
							break;
						}
					}

					// if not, add him on the click of the button
					if (i == -1) {
						for (PlayerObject iter : holder.players) {
							if (iter.info.name.equals(namePlayer.split(", ")[0])
									&& iter.info.team.equals(namePlayer
											.split(" - ")[1])) {
								Rankings.bumpEntityValue(iter, act);
								break;
							}
						}
						Rankings.watchList.add(namePlayer.split(", ")[0]);
						WriteToFile.writeWatchList(act, Rankings.watchList);
						Toast.makeText(act,
								namePlayer + " added to watch list",
								Toast.LENGTH_SHORT).show();
						for (Map<String, String> datum : Rankings.data) {
							if (datum.get("main").contains(
									namePlayer.split(", ")[0])) {
								datum.put("hidden", "W");
								Rankings.adapter.notifyDataSetChanged();
							}
						}
					} else// if so, ignore the click
					{
						Toast.makeText(act,
								namePlayer + " already in watch list",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		// Otherwise, the call is from the watch list, so it gives the option to
		// remove it
		else {
			addWatch.setText("Oust");
			addWatch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(Rankings.context,
							namePlayer + " removed from watch list",
							Toast.LENGTH_SHORT).show();
					Rankings.watchList.remove(namePlayer.split(", ")[0]);
					WriteToFile.writeWatchList((Context) act,
							Rankings.watchList);
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context) act,
							Rankings.watchList);
				}
			});
		}
		// Create the output, make sure it's valid
		final TextView name = (TextView) dialog.findViewById(R.id.name);
		if (namePlayer.equals("")) {
			return;
		}
		// Set up the header, and make a mock object with the set name
		String playerName = namePlayer.split(", ")[0];
		String pos = "";
		if (namePlayer.split(", ").length > 1) {
			pos = namePlayer.split(", ")[1].split(" -")[0];
		}
		String team = "";
		if (namePlayer.split("- ").length > 1) {
			team = namePlayer.split("- ")[1];
		}
		name.setText(playerName);
		searchedPlayer = new PlayerObject("", "", "", 0);
		for (PlayerObject player : holder.players) {
			if (player.info.name.equals(playerName)
					&& player.info.team.equals(team)
					&& player.info.position.equals(pos)) {
				searchedPlayer = player;
				break;
			} else if (player.info.name.equals(playerName) && team.equals("")
					&& pos.equals("") && player.info.position.equals("D/ST")) {
				searchedPlayer = player;
				break;
			}
		}
		final PlayerObject copy = searchedPlayer;
		final Roster r = ReadFromFile.readRoster(act);
		if (draftable && !holder.isRegularSeason && !isImport) {
			name.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (r.isRostered(copy)) {
						String nameStr = name.getText().toString();
						if (!Draft.isDrafted(nameStr, holder.draft)) {
							int index = 0;
							boolean notShown = false;
							for (int i = 0; i < holder.players.size(); i++) {
								if (i >= Rankings.data.size()) {
									notShown = true;
									break;
								}
								if (Rankings.data.get(i).get("main")
										.contains(namePlayer.split(", ")[0])) {
									index = i;
									break;
								}
							}
							DecimalFormat df = new DecimalFormat("#.##");
							if (!notShown) {
								Rankings.data.remove(index);
								Rankings.adapter.notifyDataSetChanged();
							}
							Map<String, String> datum = new HashMap<String, String>();
							boolean isAuction = ReadFromFile.readIsAuction(act);
							if (isAuction) {
								datum.put("main",
										df.format(copy.values.secWorth) + ":  "
												+ copy.info.name);
							} else if (!isAuction && copy.values.ecr != -1) {
								datum.put("main", df.format(copy.values.ecr)
										+ ":  " + copy.info.name);
							} else if (!isAuction && copy.values.ecr == -1) {
								datum.put("main", copy.info.name);
							}
							datum.put("sub", Rankings.generateOutputSubtext(
									copy, holder, df));
							Rankings.handleDrafted(datum, holder,
									(Activity) Rankings.context, dialog, index,
									notShown);
							return true;
						} else {
							Toast.makeText(act,
									"That player is already drafted",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(
								act,
								"Your league has no roster spots for this position. Are the roster settings up to date?",
								Toast.LENGTH_SHORT).show();
					}
					return true;
				}
			});
		}
		// If it's called from trending or watch list, ignore back
		if (flag) {
			Button backButton = (Button) dialog.findViewById(R.id.search_back);
			if (canRand) {
				backButton.setText("Random Player");
				backButton.setTextSize(12);
				backButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Rankings.randomPlayer();
					}

				});
			} else {
				backButton.setVisibility(Button.GONE);
			}
		}
		// Set the data in the list
		data = new ArrayList<Map<String, String>>();
		RelativeLayout base = (RelativeLayout) dialog
				.findViewById(R.id.info_sub_header_dark);
		if (searchedPlayer.info.position.length() >= 1
				&& searchedPlayer.info.team.length() > 1) {
			Button leftPos = (Button) dialog.findViewById(R.id.dummy_btn_left);
			leftPos.setText("Age:\n" + searchedPlayer.info.age);
			Button rightPos = (Button) dialog
					.findViewById(R.id.dummy_btn_right);
			rightPos.setText("Bye:\n"
					+ holder.bye.get(searchedPlayer.info.team));
			Button centerPos = (Button) dialog
					.findViewById(R.id.dummy_btn_center);
			centerPos.setText(searchedPlayer.info.team + "\n"
					+ searchedPlayer.info.position);
			if (searchedPlayer.info.position.equals("D/ST")
					|| searchedPlayer.info.age == null
					|| searchedPlayer.info.age.length() <= 1) {
				leftPos.setText("Age:\nN/A");
			}
			if (searchedPlayer.info.team.equals("None")
					|| searchedPlayer.info.team.equals("---")
					|| searchedPlayer.info.team.equals("FA")) {
				if (searchedPlayer.info.position.length() >= 1) {
					centerPos.setText(searchedPlayer.info.position);
					rightPos.setText("Bye:\nN/A");
				} else {
					base.setVisibility(View.GONE);
				}
			}
		} else {
			base.setVisibility(View.GONE);
		}
		if (ManageInput.confirmInternet(act)) {
			PlayerNewsActivity objNews = new PlayerNewsActivity();
			objNews.startNews(searchedPlayer.info.name,
					searchedPlayer.info.team, act);
		}
		adapter = new SimpleAdapter(act, data, R.layout.web_listview_item,
				new String[] { "main", "sub" }, new int[] { R.id.text1,
						R.id.text2 });
		setSearchContent(searchedPlayer, data, holder, dialog, adapter, act);
		// Show the dialog, then set the list
		dialog.show();
		ListView results = (ListView) dialog.findViewById(R.id.listview_search);
		results.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String input = ((TextView) ((RelativeLayout) arg1)
						.findViewById(R.id.text1)).getText().toString();
				String sub = ((TextView) ((RelativeLayout) arg1)
						.findViewById(R.id.text2)).getText().toString();
				// Show tweets about the player
				if (input.contains("See tweets about")) {
					playerTweetSearchInit(namePlayer.split(", ")[0], act);
				} else if (sub.contains("Click to change")) {
					changeNote(searchedPlayer, holder, act,
							(RelativeLayout) arg1);
				}
				// Bring up the interweb to show highlights of the player
				else if (input.contains("See highlights of")) {
					String[] nameArr = namePlayer.split(", ")[0].split(" ");
					String url = "http://www.youtube.com/results?search_query=";
					for (String name : nameArr) {
						url += name + "+";
					}
					url += "highlights";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					act.startActivity(i);
				}

			}
		});
		results.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String sub = ((TextView) ((RelativeLayout) arg1)
						.findViewById(R.id.text2)).getText().toString();
				if (sub.contains("Click and hold to clear")) {
					clearNote(searchedPlayer, holder, act,
							(RelativeLayout) arg1);
				}
				return true;
			}

		});
		// ManageInput.handleArray(output, results, act);
		results.setAdapter(adapter);
		ActivitySwipeDetector asd = new ActivitySwipeDetector(act, this);
		asd.origin = "Popup";
		results.setOnTouchListener(asd);
		Button back = (Button) dialog.findViewById(R.id.search_back);
		if (!canRand) {
			// If it isn't gone, set that it goes back
			back.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						dialog.dismiss();
						searchCalled(act, isImport, newImport);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		// Setting up close
		Button close = (Button) dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// If it is called from trending or rankings, dismiss it
				if (draftable) {
					Rankings.updateView(searchedPlayer);
				}
				if (!watchFlag) {
					dialog.dismiss();
				} else // otherwise it was from watch list, so call back from
						// there
				{
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context) act,
							Rankings.watchList);
				}
			}
		});
	}

	/**
	 * Makes a popup that will handle the changing of the player note
	 */
	public void changeNote(final PlayerObject searchedPlayer2,
			final Storage holder2, final Activity act, final RelativeLayout arg1) {
		final Dialog dialog = new Dialog(act, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.note_change);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button close = (Button) dialog.findViewById(R.id.search_cancel);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		final AutoCompleteTextView input = (AutoCompleteTextView) dialog
				.findViewById(R.id.player_input);
		String old = ((TextView) ((RelativeLayout) arg1)
				.findViewById(R.id.text1)).getText().toString();
		if (!old.equals("No note entered")) {
			input.setText(old);
		}
		Button submit = (Button) dialog.findViewById(R.id.search_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = input.getText().toString();
				if (str.length() > 0) {
					handleNote(str, searchedPlayer2, holder2, act, arg1);
					dialog.dismiss();
				} else {
					Toast.makeText(act, "Please enter a note",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Clears the note of the player to some default
	 */
	public void clearNote(final PlayerObject searchedPlayer2,
			final Storage holder2, final Activity act, final RelativeLayout arg1) {
		if (holder.notes.containsKey(searchedPlayer2.info.name
				+ searchedPlayer2.info.position)) {
			holder.notes.remove(searchedPlayer2.info.name
					+ searchedPlayer2.info.position);
		}
		TextView text = (TextView) arg1.findViewById(R.id.text1);
		text.setText("No note entered");
		StorageAsyncTask obj = new StorageAsyncTask();
		WriteNewPAA task2 = obj.new WriteNewPAA(act, false, true);
		task2.execute(holder, act);
	}

	/**
	 * Saves the note to the object and writes it to file
	 */
	public void handleNote(String note, PlayerObject player, Storage holder,
			Activity act, RelativeLayout elem) {
		TextView text = (TextView) elem.findViewById(R.id.text1);
		text.setText(note);
		holder.notes.put(player.info.name + player.info.position, note);
		StorageAsyncTask obj = new StorageAsyncTask();
		WriteNewPAA task2 = obj.new WriteNewPAA(act, false, true);
		task2.execute(holder, act);
	}

	/**
	 * Sets the output of the search
	 * 
	 * @param searchedPlayer
	 * @param data
	 * @param holder
	 * @param dialog
	 * @param adapter
	 */
	public void setSearchContent(PlayerObject searchedPlayer,
			List<Map<String, String>> data, Storage holder, Dialog dialog,
			SimpleAdapter adapter, final Context cont) {
		ranking = (Button) dialog.findViewById(R.id.category_ranking);
		info = (Button) dialog.findViewById(R.id.category_info);
		team = (Button) dialog.findViewById(R.id.category_team);
		other = (Button) dialog.findViewById(R.id.category_other);
		ranking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contentRankings(cont);
				ranking.setTypeface(null, Typeface.BOLD);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(14);
				info.setTextSize(13);
				team.setTextSize(13);
				other.setTextSize(13);
				info.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				team.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				other.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				ranking.setBackgroundColor(0XFFFF5454);
			}
		});
		info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contentInfo();
				ranking.setTypeface(null, Typeface.NORMAL);
				info.setTypeface(null, Typeface.BOLD);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(13);
				info.setTextSize(14);
				team.setTextSize(13);
				other.setTextSize(13);
				ranking.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				team.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				other.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				info.setBackgroundColor(0XFFFF5454);
			}
		});
		team.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contentTeam();
				ranking.setTypeface(null, Typeface.NORMAL);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.BOLD);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(13);
				info.setTextSize(13);
				team.setTextSize(14);
				other.setTextSize(13);
				ranking.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				info.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				other.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				team.setBackgroundColor(0XFFFF5454);
			}
		});
		other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contentOther();
				ranking.setTypeface(null, Typeface.NORMAL);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.BOLD);
				ranking.setTextSize(13);
				info.setTextSize(13);
				team.setTextSize(13);
				other.setTextSize(14);
				ranking.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				info.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				team.setBackgroundDrawable(cont.getResources().getDrawable(
						R.drawable.menu_btn_black));
				other.setBackgroundColor(0XFFFF5454);
			}
		});
		contentRankings(cont);
	}

	public void swipeLeftToRight(Context act) {
		if (isMax(ranking)) {
			contentOther();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.BOLD);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(14);
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundColor(0XFFFF5454);
		} else if (isMax(info)) {
			contentRankings(act);
			ranking.setTypeface(null, Typeface.BOLD);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(14);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(13);
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			ranking.setBackgroundColor(0XFFFF5454);
		} else if (isMax(team)) {
			contentInfo();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.BOLD);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(14);
			team.setTextSize(13);
			other.setTextSize(13);
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			info.setBackgroundColor(0XFFFF5454);
		} else if (isMax(other)) {
			contentTeam();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.BOLD);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(14);
			other.setTextSize(13);
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundColor(0XFFFF5454);
		}
	}

	/**
	 * Determines if a button passed in is currently selected
	 * 
	 * @param b
	 * @return
	 */
	public boolean isMax(Button b) {
		if (b.getTextSize() >= ranking.getTextSize()
				&& b.getTextSize() >= info.getTextSize()
				&& b.getTextSize() >= team.getTextSize()
				&& b.getTextSize() >= other.getTextSize()) {
			return true;
		}
		return false;
	}

	public void swipeRightToLeft(Context act) {
		if (isMax(ranking)) {
			contentInfo();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.BOLD);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(14);
			team.setTextSize(13);
			other.setTextSize(13);
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			info.setBackgroundColor(0XFFFF5454);
		} else if (isMax(info)) {
			contentTeam();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.BOLD);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(14);
			other.setTextSize(13);
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundColor(0XFFFF5454);
		} else if (isMax(team)) {
			contentOther();
			ranking.setTypeface(null, Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.BOLD);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(14);
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			ranking.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundColor(0XFFFF5454);
		} else if (isMax(other)) {
			contentRankings(act);
			ranking.setTypeface(null, Typeface.BOLD);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(14);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(13);
			info.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			team.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			other.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.menu_btn_black));
			ranking.setBackgroundColor(0XFFFF5454);
		}
	}

	/**
	 * Fills the adapter with ranking based information
	 * 
	 * @param cont2
	 */
	public void contentRankings(Context cont2) {
		data.clear();
		DecimalFormat df = new DecimalFormat("#.##");
		// Worth
		if (!holder.isRegularSeason) {
			Map<String, String> datumWorth = new HashMap<String, String>(2);
			if (searchedPlayer.values.secWorth <= 0.0 && !(aucFactor == 0.0)) {
				datumWorth.put(
						"main",
						"$"
								+ df.format(searchedPlayer.values.worth
										/ aucFactor));
			} else {
				datumWorth.put("main",
						"$" + df.format(searchedPlayer.values.secWorth));
			}
			StringBuilder sub = new StringBuilder(1000);
			if (searchedPlayer.info.position.length() > 0) {
				if (ParseMath.zMap == null || ParseMath.zMap.size() == 0) {
					ParseMath.initZMap(holder);
				}
				sub.append("$"
						+ df.format(ParseMath.avgPAAMap(holder,
								ReadFromFile.readRoster(cont2), searchedPlayer))
						+ " according to PAA\n");
			}
			if (searchedPlayer.values.ecr > 0.0) {
				sub.append("$"
						+ df.format(ParseMath
								.convertRanking(searchedPlayer.values.ecr))
						+ " according to ECR\n");
			}
			if (ManageInput.isInteger(searchedPlayer.info.adp)
					|| ManageInput.isDouble(searchedPlayer.info.adp)) {
				sub.append("$"
						+ df.format(ParseMath.convertRanking(Double
								.parseDouble(searchedPlayer.info.adp)))
						+ " according to ADP\n");
			}
			if (searchedPlayer.info.position.length() >= 1) {
				sub.append("Ranked " + rankCostPos(searchedPlayer, holder)
						+ " positionally, "
						+ rankCostAll(searchedPlayer, holder) + " overall");
			} else {
				sub.append("Ranked " + rankCostAll(searchedPlayer, holder)
						+ " overall");
			}
			if (searchedPlayer.values.count < 10) {
				sub.append("\nShowed up in " + searchedPlayer.values.count
						+ " rankings");
			}
			datumWorth.put("sub", sub.toString());
			data.add(datumWorth);
		}
		if (holder.isRegularSeason) {
			if (searchedPlayer.values.rosRank > 0) {
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("main", "ROS Positional Ranking: "
						+ searchedPlayer.values.rosRank);
				datum.put("sub", "");
				data.add(datum);
			}
		}
		// Rank ecr
		if (searchedPlayer.values.ecr != -1) {
			Map<String, String> datum = new HashMap<String, String>(2);
			if (!holder.isRegularSeason) {
				datum.put("main", "Preseason Ranking: "
						+ searchedPlayer.values.ecr);
				if (rankECRPos(searchedPlayer, holder) != -1
						&& searchedPlayer.info.position.length() >= 1) {
					datum.put("sub",
							"Ranked " + rankECRPos(searchedPlayer, holder)
									+ " positionally");
				} else {
					datum.put("sub", "");
				}
			} else {
				datum.put("main", "Weekly Positional Ranking: "
						+ searchedPlayer.values.ecr.intValue());
				datum.put("sub", "");
			}
			data.add(datum);
		}
		// ADP Rankings
		if (!searchedPlayer.info.adp.equals("Not set")) {
			Map<String, String> datum = new HashMap<String, String>(2);
			if (!holder.isRegularSeason) {
				if (rankADPPos(searchedPlayer, holder) != -1) {
					datum.put("main", "Average Draft Position: "
							+ searchedPlayer.info.adp);
					datum.put("sub",
							"Ranked " + rankADPPos(searchedPlayer, holder)
									+ " positionally");
					data.add(datum);
				} else {
					datum.put("main", "Average Draft Position: "
							+ searchedPlayer.info.adp);
					datum.put("sub", "");
					data.add(datum);
				}
			} else if (!searchedPlayer.info.adp.equals("Bye Week")) {
				if (!searchedPlayer.info.position.equals("D/ST")) {
					datum.put("main", "Playing The " + searchedPlayer.info.adp);
					if (holder.sos.get(searchedPlayer.info.adp + ","
							+ searchedPlayer.info.position) != null) {
						datum.put(
								"sub",
								"Positional SOS: "
										+ holder.sos
												.get(searchedPlayer.info.adp
														+ ","
														+ searchedPlayer.info.position)
										+ "\n1 is Easiest, 32 Hardest");
					}
					data.add(datum);
				} else {
					datum.put("main", searchedPlayer.info.adp);
					datum.put("sub", "");
					data.add(datum);
				}
			}
		}
		// Projections
		if (searchedPlayer.values.points >= 0.0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			if (!holder.isRegularSeason) {
				datum.put("main", searchedPlayer.values.points
						+ " Projection This Year");
			} else {
				datum.put("main", searchedPlayer.values.points
						+ " Projection This Week");
			}
			if (searchedPlayer.info.position.length() >= 1) {
				datum.put("sub", "Ranked "
						+ rankProjPos(searchedPlayer, holder) + " positionally");
			} else {
				datum.put("sub", "");
			}
			data.add(datum);
		}
		// PAA and PAAPD
		if (searchedPlayer.values.paa != 0.0
				&& searchedPlayer.values.points != 0.0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", df.format(searchedPlayer.values.paa) + " PAA");
			StringBuilder paaSub = new StringBuilder(100);
			if (!holder.isRegularSeason && searchedPlayer.values.secWorth > 0) {
				paaSub.append(df.format(searchedPlayer.values.paa
						/ searchedPlayer.values.secWorth)
						+ " PAA per dollar\n");
			}
			if (searchedPlayer.info.position.length() >= 1) {
				paaSub.append("Ranked " + rankPAAPos(searchedPlayer, holder)
						+ " positionally, "
						+ rankPAAAll(searchedPlayer, holder) + " overall");
			} else {
				paaSub.append("Ranked " + rankPAAAll(searchedPlayer, holder)
						+ " overall");
			}
			datum.put("sub", paaSub.toString());
			data.add(datum);
		}

		if (!holder.isRegularSeason
				&& (searchedPlayer.values.points > 0 && searchedPlayer.values.secWorth > 0)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put(
					"main",
					"Leverage: "
							+ MathUtils.getLeverage(searchedPlayer, holder,
									cont2));
			datum.put("sub",
					"Leverage relates price and auction value to the top positional scorer");
			data.add(datum);
		}
		// Risk
		if (searchedPlayer.risk >= 0.0
				&& !searchedPlayer.info.adp.equals("Bye Week")
				&& searchedPlayer.values.points > 0.0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.risk + " Risk");
			if (searchedPlayer.info.position.length() >= 1) {
				datum.put("sub", "Ranked "
						+ rankRiskAll(searchedPlayer, holder) + " overall");
			} else {
				datum.put("sub", "");
			}
			data.add(datum);
		}

		adapter.notifyDataSetChanged();
	}

	/**
	 * Fills the pop up up with basic player information
	 */
	public void contentInfo() {
		data.clear();
		Map<String, String> basicDatum = new HashMap<String, String>(2);
		basicDatum.put("main", searchedPlayer.info.position + " - "
				+ searchedPlayer.info.team);
		String sub = "";
		if (ManageInput.isInteger(searchedPlayer.info.age)
				&& Integer.valueOf(searchedPlayer.info.age) > 0) {
			sub += "Age: " + searchedPlayer.info.age + "\n";
		}
		if (searchedPlayer.info.team.length() > 4) {
			sub += "Bye: " + holder.bye.get(searchedPlayer.info.team);
		}
		basicDatum.put("sub", sub);
		data.add(basicDatum);

		if (isImport) {
			String team = "Free Agent";
			for (TeamAnalysis teamIter : newImport.teams) {
				if (teamIter.team.contains(searchedPlayer.info.name)) {
					team = teamIter.teamName;
					break;
				}
			}
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", team);
			datum.put("sub", "");
			data.add(datum);
		}
		// Contract status
		DecimalFormat df = new DecimalFormat("#.##");
		if (!holder.isRegularSeason
				&& Draft.draftedMe(searchedPlayer.info.name, holder.draft)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED BY YOU");
			data.add(datum);
		}
		// See if they're drafted by someone else
		else if (!holder.isRegularSeason
				&& Draft.isDrafted(searchedPlayer.info.name, holder.draft)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED");
			data.add(datum);
		}
		// Is he in the watch list?
		if (Rankings.watchList.contains(searchedPlayer.info.name)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "He is in your watch list");
			datum.put("sub", "");
			data.add(datum);
		}
		if (!searchedPlayer.injuryStatus.contains("Healthy")) {
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", searchedPlayer.injuryStatus);
			datum2.put("sub", "");
			data.add(datum2);
		}
		if (!searchedPlayer.info.position.equals("D/ST")
				&& !searchedPlayer.info.contractStatus
						.contains("Under Contract")) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.info.contractStatus);
			datum.put("sub", "");
			data.add(datum);
		}
		Scoring s = ReadFromFile.readScoring(a);
		if (!holder.isRegularSeason
				&& !searchedPlayer.info.position.equals("K")
				&& !searchedPlayer.info.position.equals("D/ST")
				&& !(searchedPlayer.pointsSoFar(s) == 0.0)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.pointsSoFar(s)
					+ " Points Scored Last Year");
			datum.put("sub", "");
			data.add(datum);
		}
		if (holder.isRegularSeason && !searchedPlayer.info.position.equals("K")
				&& !searchedPlayer.info.position.equals("D/ST")
				&& !(searchedPlayer.pointsSoFar(s) == 0.0)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.pointsSoFar(s)
					+ " Points Scored So Far");
			datum.put("sub", "");
			data.add(datum);
		}
		// Injury status and stats
		if (!searchedPlayer.info.position.equals("K")
				&& !searchedPlayer.info.position.equals("D/ST")
				&& !searchedPlayer.stats.equals(" ")
				&& searchedPlayer.stats.length() > 5) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.stats);
			datum.put("sub", "");
			if (searchedPlayer.stats.contains("Pass Attempts")
					&& searchedPlayer.stats.contains("Interceptions")) {
				String intsA = searchedPlayer.stats.split("Interceptions: ")[1]
						.split("\n")[0];
				int intA = Integer.parseInt(intsA);
				String compA = searchedPlayer.stats
						.split("Completion Percentage: ")[1].split("\n")[0]
						.replace("%", "");
				double compPercent = Double.parseDouble(compA) / 100.0;
				double attempts = Double.parseDouble(searchedPlayer.stats
						.split("Pass Attempts: ")[1].split("\n")[0]);
				double completions = attempts * compPercent;
				double aDiff = (completions) / ((double) intA);
				int rank = rankIntComp(holder, aDiff);
				datum.put("sub", df.format(aDiff)
						+ " completion to interception ratio (" + rank + ")");
			}
			data.add(datum);

		}

		// Positional SOS
		if (holder.sos.get(searchedPlayer.info.team + ","
				+ searchedPlayer.info.position) != null
				&& holder.sos.get(searchedPlayer.info.team + ","
						+ searchedPlayer.info.position) > 0
				&& !holder.isRegularSeason) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put(
					"main",
					"Positional SOS: "
							+ holder.sos.get(searchedPlayer.info.team + ","
									+ searchedPlayer.info.position));
			datum.put("sub", "1 is Easiest, 32 Hardest");
			data.add(datum);
		}
		if (data.size() == 0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No information available for this player");
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Notifies the adapter that team information is desired
	 */
	public void contentTeam() {
		data.clear();
		// O line data
		if (!searchedPlayer.info.position.equals("K")
				&& !searchedPlayer.info.position.equals("D/ST")) {
			if (holder.oLineAdv.get(searchedPlayer.info.team) != null
					&& holder.oLineAdv.get(searchedPlayer.info.team).length() > 3) {
				Map<String, String> datum = new HashMap<String, String>(2);
				String oLineAdv = holder.oLineAdv.get(searchedPlayer.info.team);
				String oLinePrimary = oLineAdv.split("~~~~")[0];
				String oLineRanks = oLineAdv.split("~~~~")[1];
				datum.put("main", "Offensive Line Data:\n\n" + oLinePrimary);
				datum.put("sub", oLineRanks);
				data.add(datum);
			}
		}
		// Draft class
		if (holder.draftClasses.get(searchedPlayer.info.team) != null
				&& !holder.draftClasses.get(searchedPlayer.info.team).contains(
						"null")
				&& !searchedPlayer.info.position.equals("K")
				&& holder.draftClasses.get(searchedPlayer.info.team).length() > 4) {
			Map<String, String> datum = new HashMap<String, String>(2);
			String draft = holder.draftClasses.get(searchedPlayer.info.team)
					.substring(
							holder.draftClasses.get(searchedPlayer.info.team)
									.indexOf('\n'),
							holder.draftClasses.get(searchedPlayer.info.team)
									.length());
			String gpa = holder.draftClasses.get(searchedPlayer.info.team)
					.split("\n")[0];
			datum.put("main", "Draft Recap:\n" + draft);
			datum.put("sub", gpa);
			data.add(datum);
		}
		// Free agency classes
		List<String> fa = holder.fa.get(searchedPlayer.info.team);
		if (fa != null) {
			if (fa.size() > 1) {
				if (fa.get(0).contains("\n")) {
					Map<String, String> datum = new HashMap<String, String>(2);
					datum.put("main", "Signed Free Agents:\n\n"
							+ fa.get(0).split(": ")[1]);
					datum.put("sub", "");
					data.add(datum);
				}
				if (fa.get(1).contains("\n")) {
					Map<String, String> datum = new HashMap<String, String>(2);
					datum.put("main", "Departing Free Agents:\n\n"
							+ fa.get(1).split(": ")[1]);
					datum.put("sub", "");
					data.add(datum);
				}
			}
		}
		if (data.size() == 0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No team information available for this player");
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Notifies the adapter that other data is desired
	 */
	public void contentOther() {
		data.clear();
		if (holder.notes.containsKey(searchedPlayer.info.name
				+ searchedPlayer.info.position)) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put(
					"main",
					holder.notes.get(searchedPlayer.info.name
							+ searchedPlayer.info.position));
			datum.put("sub",
					"Click to change note for this player\nClick and hold to clear the note");
			data.add(datum);
		} else {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No note entered");
			datum.put("sub",
					"Click to change note for this player\nClick and hold to clear the note");
			data.add(datum);
		}
		if (!searchedPlayer.info.position.equals("K")
				&& !searchedPlayer.info.position.equals("D/ST")) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "See tweets about this player");
			datum.put("sub", "");
			data.add(datum);
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", "See highlights of this player");
			datum2.put("sub", "");
			data.add(datum2);
		}
		if (newsList.size() > 0) {
			for (NewsObjects news : newsList) {
				HashMap<String, String> datum = new HashMap<String, String>();
				datum.put("main", news.news);
				datum.put("sub", news.impact);
				data.add(datum);
			}
		} else if (!ManageInput.confirmInternet(a)) {
			HashMap<String, String> datum = new HashMap<String, String>();
			datum.put("main", "Connect to the internet to see player news here");
			datum.put("sub", "No internet connection available");
			data.add(datum);
		}
		if (data.size() == 0) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main",
					"No additional information available about this player");
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Ranks risk relative to all
	 */
	public static int rankRiskAll(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.risk < player.risk && iter.risk > 0) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks ECR positionally
	 */
	public static int rankECRPos(PlayerObject player, Storage holder) {
		int rank = 1;
		if (player.values.ecr == -1) {
			return -1;
		}
		for (PlayerObject iter : holder.players) {
			if (iter.values.ecr == -1) {
				continue;
			}
			if (iter.info.position.equals(player.info.position)
					&& iter.values.ecr < player.values.ecr) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks the adp positionally
	 */
	public static int rankADPPos(PlayerObject player, Storage holder) {
		int rank = 1;
		if (player.info.adp.equals("Not set")) {
			return -1;
		}
		for (PlayerObject iter : holder.players) {
			if (iter.info.adp.equals("Not set")) {
				continue;
			}
			if (iter.info.position.equals(player.info.position)
					&& Double.parseDouble(iter.info.adp) < Double
							.parseDouble(player.info.adp)) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks the projections positionally
	 */
	public static int rankProjPos(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.values.points != 0.0
					&& iter.values.points > player.values.points
					&& iter.info.position.equals(player.info.position)) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks paa among positional players
	 */
	public static int rankPAAPos(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.values.paa != 0.0
					&& iter.info.position.equals(player.info.position)
					&& iter.values.paa > player.values.paa) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks paa among all players
	 */
	public static int rankPAAAll(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.values.paa != 0.0 && iter.values.paa > player.values.paa) {
				rank++;
			}
		}
		return rank;
	}

	public static int rankIntComp(Storage holder, double aDiff2) {
		int rank = 1;
		for (PlayerObject searchedPlayer : holder.players) {
			if (searchedPlayer.info.position.equals("QB")
					&& searchedPlayer.stats.contains("Pass Attempts")
					&& searchedPlayer.stats.contains("Interceptions")) {
				String intsA = searchedPlayer.stats.split("Interceptions: ")[1]
						.split("\n")[0];
				int intA = Integer.parseInt(intsA);
				String compA = searchedPlayer.stats
						.split("Completion Percentage: ")[1].split("\n")[0]
						.replace("%", "");
				double compPercent = Double.parseDouble(compA) / 100.0;
				double attempts = Double.parseDouble(searchedPlayer.stats
						.split("Pass Attempts: ")[1].split("\n")[0]);
				double completions = attempts * compPercent;
				double aDiff = (completions) / ((double) intA);
				if (aDiff > aDiff2) {
					rank++;
				}
			}
		}
		return rank;
	}

	/**
	 * Ranks the worth positionally
	 */
	public static int rankCostPos(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.info.position.equals(player.info.position)
					&& iter.values.worth > player.values.worth) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Ranks the worth overall
	 */
	public static int rankCostAll(PlayerObject player, Storage holder) {
		int rank = 1;
		for (PlayerObject iter : holder.players) {
			if (iter.values.worth > player.values.worth) {
				rank++;
			}
		}
		return rank;
	}

	/**
	 * Calls the asynchronous search of tweets about a player
	 */
	public static void playerTweetSearchInit(String name, Activity act) {
		TwitterWork obj = new TwitterWork();
		obj.twitterInitial(act, name, false);
	}

	/**
	 * Displays the output of the tweets about the player
	 * 
	 * @param result
	 * @param act
	 */
	public static void playerTweetSearch(List<NewsObjects> result,
			final Activity act, String name) {
		final Dialog dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.player_tweet_search);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		TextView header = (TextView) dialog.findViewById(R.id.name);
		header.setText("Twitter Search: \n" + name);
		ListView tweetResults = (ListView) dialog
				.findViewById(R.id.listview_search);
		Button close = (Button) dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
		});
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (NewsObjects newsObj : result) {
			Map<String, String> datum = new HashMap<String, String>();
			datum.put("news", newsObj.news + "\n\n" + newsObj.impact);
			datum.put("date", newsObj.date);
			data.add(datum);
		}
		final SimpleAdapter adapter = new SimpleAdapter(act, data,
				R.layout.web_listview_item, new String[] { "news", "date" },
				new int[] { R.id.text1, R.id.text2 });
		tweetResults.setAdapter(adapter);
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				false, "Irrelevant", tweetResults,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							data.remove(position);
						}
						adapter.notifyDataSetChanged();
						Toast.makeText(Rankings.context,
								"Temporarily hiding this news piece",
								Toast.LENGTH_SHORT).show();
						if (adapter.isEmpty()) {
							dialog.dismiss();
							Toast.makeText(Rankings.context,
									"You've hidden all of the tweets",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		tweetResults.setOnTouchListener(touchListener);
		tweetResults.setOnScrollListener(touchListener.makeScrollListener());
	}

	public static void populateNews(List<NewsObjects> result) {
		for (NewsObjects news : result) {
			newsList.add(news);
			float px = other.getTextSize();
			float sp = ManageInput.pixelsToSp(a, px);
			if (sp == 14) {
				HashMap<String, String> datum = new HashMap<String, String>();
				datum.put("main", news.news);
				datum.put("sub", news.impact);
				data.add(datum);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
