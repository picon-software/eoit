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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.listener.FavoriteStationsOnCheckedChangeListener;
import fr.piconsoft.eoit.ui.listener.StationRoleOnItemClickListener;
import fr.piconsoft.eoit.activity.basic.util.StationListViewBinder;
import fr.piconsoft.eoit.ui.task.StationRoleUpdaterTask;
import fr.piconsoft.eoit.activity.common.adapter.SectionnedSimpleCursorAdapter;
import fr.piconsoft.eoit.ui.task.OutpostUpdaterAsyncTask;
import fr.piconsoft.eoit.ui.task.UpdateStandingAsyncTask;
import fr.piconsoft.eoit.helper.ContextHelper;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.model.ColumnsNames.Region;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.ui.model.Stations;

/**
 * @author picon.software
 */
public class LocationManagementActivity extends LoaderActivity<Cursor> implements StationListViewBinder.OnStationMenuItemClickListener {

	private SimpleCursorAdapter adapter;
	private boolean standingUpdated = false;
	private OutpostUpdaterAsyncTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			standingUpdated = savedInstanceState.getBoolean("standingUpdated");
		}

		setContentView(R.layout.location_management_premium);

		// The names of the cursor columns to display in the view, initialized to the title column
		String[] dataColumns = {Station.STATION_TYPE_ID,
				Station.NAME, Region.COLUMN_NAME_NAME_ALIAS,
				Station.ROLE, Station.ROLE, Station.ROLE,
				Station.STANDING, Station.FAVORITE};

		// The view IDs that will display the cursor columns, initialized to the TextView in
		// noteslist_item.xml
		int[] viewIDs = {R.id.station_icon, R.id.station_name, R.id.location_name,
				R.id.station_prod_icon, R.id.station_trade_icon, R.id.station_both_icon, R.id.corp_standing,
				R.id.favorite_station};

		// Creates the backing adapter for the ListView.
		adapter
				= new SectionnedSimpleCursorAdapter(
				this,
				R.layout.station_row_premium,
				dataColumns,
				viewIDs);

		adapter.setViewBinder(new StationListViewBinder(this, true));

		ListView listView = (ListView) findViewById(R.id.location_list);
		//listView.setOnItemClickListener(new ItemOnItemClickListener());

		// Sets the ListView's adapter to be the cursor adapter that was just created.
		listView.setAdapter(adapter);
		//listView.setEmptyView();
		listView.setOnItemClickListener(new StationRoleOnItemClickListener());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("standingUpdated", standingUpdated);
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				this,
				Station.CONTENT_ALL_URI,
				new String[]{Station._ID, Station.STATION_TYPE_ID, Station.NAME,
						Region.COLUMN_NAME_NAME_ALIAS, Station.ROLE, Station.STANDING, ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS,
						Station.FAVORITE},
				null,
				null,
				null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		standingUpdated = false;

		if(task != null) {
			task.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!standingUpdated) {
			ProgressDialog waitDialog = new ProgressDialog(this);
			waitDialog.setCancelable(true);
			waitDialog.setMessage(getString(R.string.retreiving_standing));
			waitDialog.show();

			new UpdateStandingAsyncTask(waitDialog){
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					initOrRestart();
				}
			}.execute();
			standingUpdated = true;
		} else {
			initOrRestart();
		}
	}

	@Override
	public void onLoadFinished(Cursor data) {
		adapter.changeCursor(data);
		validateStations();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

	private void validateStations() {
		ContextHelper.populateStationBeans(this, true);

		if (!Stations.getTradeStation().initialized) {
			Crouton.makeText(this, R.string.no_trade_station, Style.ALERT).show();
		} else if (!Stations.getProductionStation().initialized) {
			Crouton.makeText(this, R.string.no_prod_station, Style.ALERT).show();
		}
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
	public boolean onMenuItemClick(int stationId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.favorite_option:
				new FavoriteStationsOnCheckedChangeListener
						.FavoriteUpdaterTask(stationId, getApplicationContext())
						.execute(!item.isChecked());
				break;

			case R.id.prod_option:
				new StationRoleUpdaterTask(getApplicationContext(), stationId).execute(EOITConst.Stations.PRODUCTION_ROLE);
				break;
			case R.id.trade_option:
				new StationRoleUpdaterTask(getApplicationContext(), stationId).execute(EOITConst.Stations.TRADE_ROLE);
				break;
			case R.id.both_option:
				new StationRoleUpdaterTask(getApplicationContext(), stationId).execute(EOITConst.Stations.BOTH_ROLES);
				break;
		}

		return false;
	}
}
