/*
 * Copyright (C) 2015 Picon software
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.piconsoft.eoit.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.task.BasicContextAsyncTask;
import fr.piconsoft.eoit.db.DatabaseHelper;
import fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks;
import fr.piconsoft.eoit.receiver.HistoSenderBroadcastReceiver;

public class EOITActivity extends StrictModeActivity {

	private static final String LOG_TAG = "EOITActivity";

	private TextView infoTextView;
	private boolean updated = false;
	private ProgressBar bar;
	private LoadItemsAsyncTask task;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		loadParametersOnCreate = false;

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		enableHttpResponseCache();

		bar = (ProgressBar) findViewById(R.id.progressBar);
		bar.setIndeterminate(true);
		infoTextView = (TextView) findViewById(R.id.textView);

		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		if (blockSize * availableBlocks > EOITConst.APPLICATION_SIZE) {
			task = new LoadItemsAsyncTask();
			task.execute(this);
		} else {
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setTitle(R.string.database_no_space)
					.setMessage(getResources().getString(R.string.database_no_space_message,
							EOITConst.APPLICATION_SIZE / (1024 * 1024)))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNegativeButton(R.string.database_locked_close_button_message,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									finish();
								}
							}).create().show();
		}
	}

	@Override
	protected void onDestroy() {
		task.cancel = true;
		task = null;

		super.onDestroy();
	}

	private void enableHttpResponseCache() {
		new BasicContextAsyncTask<Void, Void>() {
			@Override
			protected Void doInBackgroundSingleParam(Context context) {
				try {
					long httpCacheSize = 3 * 1024 * 1024; // 3 MiB
					File httpCacheDir = new File(getCacheDir(), "http");
					Class.forName("android.net.http.HttpResponseCache")
							.getMethod("install", File.class, long.class)
							.invoke(null, httpCacheDir, httpCacheSize);
				} catch (Exception httpResponseCacheNotAvailable) {
					// nothing done
				}
				return null;
			}
		}.execute(this);
	}

	private class LoadItemsAsyncTask extends AsyncTask<EOITActivity, Integer,
			EOITActivity> implements DatabaseUpgradeCallbacks {

		private boolean cancel;

		@Override
		protected EOITActivity doInBackground(EOITActivity... params) {
			EOITActivity activity = params[0];

			try {
				// Create / recreate the database
				SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext())
						.attachCallBacks(this)
						.getWritableDatabase();

				DatabaseHelper.getInstance(getApplicationContext())
						.clearCallBacks();

				if (updated) {
					onInfoTextUpdate(R.string.compact_db);
					onProgressBarIndeterminate(true);
					db.execSQL("VACUUM;");
					onInfoTextUpdate(R.string.optimize_db);
					db.execSQL("ANALYZE;");
					db.close();
				}
			} catch (SQLiteException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				this.cancel(true);
				return activity;
			}

			return activity;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			bar.setIndeterminate(false);
			infoTextView.setText(values[0]);
		}

		@Override
		protected void onPostExecute(EOITActivity activity) {
			Intent intent = new Intent(EOITActivity.this, ItemListActivity.class);
			activateHistoSender();
			startActivity(intent);
			EOITActivity.this.finish();
		}

		@Override
		protected void onCancelled() {
			new AlertDialog.Builder(EOITActivity.this)
					.setCancelable(false)
					.setTitle(R.string.database_locked)
					.setMessage(R.string.database_locked_message)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNegativeButton(R.string.database_locked_close_button_message,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									finish();
								}
							}).create().show();
		}

		/**
		 * @see fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks#onProgressBarReset(int)
		 */
		@Override
		public void onProgressBarReset(int max) {
			bar.setMax(max);
		}

		/**
		 * @see fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks#onProgressBarIncrementUpdate(int)
		 */
		@Override
		public void onProgressBarIncrementUpdate(int diff) {
			bar.incrementProgressBy(diff);
		}

		/**
		 * @see fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks#onProgressBarIndeterminate(boolean)
		 */
		@Override
		public void onProgressBarIndeterminate(boolean indeterminate) {
			bar.setIndeterminate(indeterminate);
		}

		/**
		 * @see fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks#onInfoTextUpdate(int)
		 */
		@Override
		public void onInfoTextUpdate(int resId) {
			publishProgress(resId);
		}

		@Override
		public void onUpgradeStart() {
			updated = true;
		}

		@Override
		public void onUpgradeEnd() {
		}

		@Override
		public boolean isCanceled() {
			return cancel;
		}
	}

	private void activateHistoSender() {
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		PendingIntent alarmIntent =
				PendingIntent.getBroadcast(this, 0,
						new Intent(this, HistoSenderBroadcastReceiver.class), 0);

		alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, AlarmManager.INTERVAL_HALF_DAY,
				alarmIntent);
	}
}