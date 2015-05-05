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
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames.Region;
import fr.piconsoft.eoit.EOITApplication;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.activity.basic.util.StationListViewBinder;
import fr.piconsoft.eoit.ui.task.UpdateFavoriteStationPriceAsyncTask;

/**
 * @author picon.software
 *
 */
public class LocationPricesActivity extends LoaderActivity<Cursor> {

	private SimpleCursorAdapter adapter;
	private boolean pricesUpdated = false;
	private int itemId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			pricesUpdated = savedInstanceState.getBoolean("pricesUpdated");
			itemId = savedInstanceState.getInt("itemId");
		}

		setContentView(R.layout.location_prices);

		itemId = (int) ContentUris.parseId(getIntent().getData());

		// The names of the cursor columns to display in the view, initialized to the title column
		String[] dataColumns = { Station.STATION_TYPE_ID,
				Station.NAME, Region.COLUMN_NAME_NAME_ALIAS,
				Prices.BUY_PRICE, Prices.BUY_VOLUME,
				Prices.SELL_PRICE, Prices.SELL_VOLUME} ;

		// The view IDs that will display the cursor columns, initialized to the TextView in
		// noteslist_item.xml
		int[] viewIDs = { R.id.station_icon, R.id.station_name, R.id.location_name,
				R.id.buy_price, R.id.buy_volume, R.id.sell_price, R.id.sell_volume };

		// Creates the backing adapter for the ListView.
		adapter
		= new SimpleCursorAdapter(
				this,
				R.layout.price_station_row,
				null, dataColumns,
				viewIDs,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		adapter.setViewBinder(new StationListViewBinder(null, false));

		ListView listView = (ListView) findViewById(R.id.location_list);
		//listView.setOnItemClickListener(new ItemOnItemClickListener());

		// Sets the ListView's adapter to be the cursor adapter that was just created.
		listView.setAdapter(adapter);

		initOrRestart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("pricesUpdated", pricesUpdated);
		outState.putInt("itemId", itemId);
	}

	@Override
	protected void onReload() {
		super.onReload();

		initOrRestart();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				this,
				getIntent().getData(),
				new String[] {Station._ID, Station.STATION_TYPE_ID, Station.NAME,
					"r." + Region.NAME + " AS " + Region.COLUMN_NAME_NAME_ALIAS,
					Prices.BUY_PRICE, Prices.BUY_VOLUME,
					Prices.SELL_PRICE, Prices.SELL_VOLUME},
					null,
					null,
					null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!pricesUpdated) {
			ProgressDialog waitDialog = new ProgressDialog(this);
			waitDialog.setCancelable(true);
			waitDialog.setMessage(getString(R.string.retreiving_station_prices));
			waitDialog.show();

			new UpdateFavoriteStationPriceAsyncTask(waitDialog,
					(EOITApplication) getApplication(),
					itemId).execute();
			pricesUpdated = true;
		}
	}

	@Override
	public void onLoadFinished(Cursor data) {
		adapter.changeCursor(data);
		if(DbUtil.hasAtLeastOneRow(data)) {
			findViewById(R.id.location_list).setVisibility(View.VISIBLE);
			findViewById(android.R.id.empty).setVisibility(View.GONE);
		} else {
			findViewById(R.id.location_list).setVisibility(View.GONE);
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_prices_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ADD_REMOVE_OPTION:
			final Intent intent = new Intent(this, StationAddRemoveActivity.class);
			startActivity(intent);
			return true;

		default:
			return false;
		}
	}
}
