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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.task.OutpostUpdaterAsyncTask;

/**
 * @author picon.software
 */
public class StationAddRemoveActivity extends StrictModeActivity {

	private OutpostUpdaterAsyncTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.station_add_remove);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.station_add_remove_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.STOCK_OPTION:
				final Intent intent = new Intent(this, StockActivity.class);
				startActivity(intent);
				return true;

			case R.id.UPDATE_OUTPOSTS_OPTION:
				ProgressDialog waitDialog = new ProgressDialog(this);
				waitDialog.setCancelable(true);
				waitDialog.setMessage(getString(R.string.updatingOutposts));
				waitDialog.show();

				task = new OutpostUpdaterAsyncTask(waitDialog);
				task.execute();

			default:
				return false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(task != null) {
			task.cancel(true);
		}
	}
}
