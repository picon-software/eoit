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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.ui.model.Skills;
import fr.piconsoft.eoit.ui.widget.AmazingListView;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.debug.Debug;
import fr.piconsoft.eoit.ui.listener.ItemOnItemClickListener;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.common.adapter.SectionnedSimpleCursorAdapter;
import fr.piconsoft.eoit.activity.common.adapter.SectionnedSimpleCursorAdapter.OrderType;
import fr.piconsoft.eoit.db.DatabaseHelper;

public class ItemListActivity extends StrictModeActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String[] dataColumns = {Item._ID, Item.NAME};
	private static final String[] dataColumnsSearch = {Item._ID, SearchManager.SUGGEST_COLUMN_TEXT_1};
	private static final int[] viewIDs = {R.id.item_icon, R.id.item_name};

	protected final static int FAVORITE_LOADER_ID = EOITConst.getNextLoaderIdSequence();
	protected final static int SEARCH_LOADER_ID = EOITConst.getNextLoaderIdSequence();

	private OrderType orderType = OrderType.CATEGORY;
	private String query;
	// private SearchView searchView;
	private SectionnedSimpleCursorAdapter adapter;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.itemlist);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			query = intent.getStringExtra(SearchManager.QUERY);
			showResults();
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show word
			Intent itemIntent = new Intent(this, ItemInfoActivity.class);
			itemIntent.setData(intent.getData());
			startActivity(itemIntent);
			finish();
		} else {
			// Creates the backing adapter for the ListView.
			adapter
					= new SectionnedSimpleCursorAdapter(
					this,
					R.layout.row_premium,
					dataColumns,
					viewIDs);

			adapter.setViewBinder(new ItemListViewBinder());

			AmazingListView itemListView = (AmazingListView) findViewById(R.id.item_list);
			itemListView.setOnItemClickListener(new ItemOnItemClickListener(this));
			// Sets the ListView's adapter to be the cursor adapter that was just created.
			itemListView.setAdapter(adapter);

			View emptyView = findViewById(android.R.id.empty);
			itemListView.setEmptyView(emptyView);

			getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
		}

		ApiUpgradeTips updateTips = new ApiUpgradeTips(this);
		if (updateTips.isActive(this)) {
			updateTips.show(getSupportFragmentManager(), "update_api_dialog");
		} else {
			MissingAPITips dialog = new MissingAPITips(this);
			if (dialog.isActive(this) &&
					Skills.isLevelVChar()) {
				dialog.show(getSupportFragmentManager(), "missing_api_dialog");
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == FAVORITE_LOADER_ID) {
			return new CursorLoader(this, Item.CONTENT_URI, new String[]{Item._ID, Item.NAME,
					Item.VOLUME, Item.CHOSEN_PRICE_ID, Prices.BUY_PRICE,
					Prices.SELL_PRICE, Prices.PRODUCE_PRICE, Prices.OWN_PRICE,
					ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS}, Item.FAVORITE + " = 1", null,
					SectionnedSimpleCursorAdapter.getOrderByQuery(orderType));
		} else if (id == SEARCH_LOADER_ID) {
			return new CursorLoader(this, Item.CONTENT_URI_SEARCH, new String[]{Item._ID,
					SearchManager.SUGGEST_COLUMN_TEXT_1}, null, new String[]{query},
					SearchManager.SUGGEST_COLUMN_TEXT_1);
		} else {
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		findViewById(R.id.item_list).setVisibility(View.VISIBLE);

		if (DbUtil.hasAtLeastOneRow(data)) {
			adapter.setOrderType(orderType);
			adapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.setOrderType(orderType);
		adapter.changeCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.homemenu, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.search_option);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		if (BuildConfig.DEBUG) {
			menu.findItem(R.id.dump_database).setVisible(true);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
/*
			case R.id.SEARCH_OPTION:
				onSearchRequested();
				return true;
*/
			case R.id.STOCK_OPTION:
				openActivity(StockActivity.class);
				return true;
			case R.id.mining_session_option:
				openActivity(MiningSessionActivity.class);
				return true;
			case R.id.oremap_option:
				openActivity(OreCompareActivity.class);
				return true;
			case R.id.feedback_option:
				final Intent feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://eoit.uservoice.com"));
				startActivity(feedbackIntent);
				return true;
			case R.id.PARAMETERS_OPTION:
				openActivity(SettingsActivity.class);
				return true;
			case R.id.HELP_OPTION:
				SimpleOkDialog<?> dialog = new SimpleOkDialog<Object>(R.string.itemlisttitle,
						R.layout.help_favorite_list);
				dialog.show(getSupportFragmentManager(), "helpDialog");
				return true;

			case R.id.dump_database:
				Debug.backupDatabase(getPackageName(), DatabaseHelper.DB_NAME, "eoit");

			default:
				return false;
		}
	}

	private void showResults() {
		orderType = OrderType.NAME_ALPHA;
		adapter
				= new SectionnedSimpleCursorAdapter(
				this,
				R.layout.row_premium,
				dataColumnsSearch,
				viewIDs,
				SearchManager.SUGGEST_COLUMN_TEXT_1);

		adapter.setViewBinder(new ItemListViewBinder());

		ListView itemListView = (ListView) findViewById(R.id.item_list);
		itemListView.setAdapter(adapter);
		itemListView.setOnItemClickListener(new ItemOnItemClickListener(this));
		getSupportLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
	}
}
