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

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import fr.eo.api.credential.CharacterCredential;
import fr.eo.api.manager.Manager;
import fr.eo.api.model.AssetList;
import fr.eo.api.services.CharacterService;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.listener.ItemOnItemClickListener;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.common.adapter.SectionnedSimpleCursorAdapter;
import fr.piconsoft.eoit.activity.common.adapter.SectionnedSimpleCursorAdapter.OrderType;
import fr.piconsoft.eoit.ui.task.EveApiProcessWithCredentialAsyncTask;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Stock;
import fr.piconsoft.eoit.ui.model.Stations;
import fr.piconsoft.eoit.provider.ItemContentProvider;
import fr.piconsoft.eoit.ui.widget.AmazingListView;
import fr.piconsoft.eoit.ui.widget.ProgressActionView;

/**
 * @author picon.software
 */
public class StockActivity extends LoaderActivity<Cursor> implements ProgressActionView.OnProgressListener {

	private static final String LOG_TAG = "StockActivity";

	private static final String[] dataColumns = {Stock.COLUMN_NAME_ITEM_ID, Item.NAME, Stock.COLUMN_NAME_QUANTITY};
	private static final int[] viewIDs = {R.id.item_icon, R.id.item_name, R.id.item_quantity};

	private OrderType orderType = OrderType.CATEGORY;
	private SectionnedSimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.stock);

		// Creates the backing adapter for the ListView.
		adapter
				= new SectionnedSimpleCursorAdapter(
				this,
				R.layout.row_premium,
				dataColumns,
				viewIDs);

		adapter.setViewBinder(new ItemListViewBinder());

		AmazingListView stockListView = (AmazingListView) findViewById(R.id.ITEM_LIST);
		stockListView.setOnItemClickListener(new ItemOnItemClickListener(this, false));

		View emptyView = findViewById(android.R.id.empty);
		stockListView.setEmptyView(emptyView);

		// Sets the ListView's adapter to be the cursor adapter that was just created.
		stockListView.setAdapter(adapter);

		initOrRestart();
	}

	@Override
	protected void onReload() {
		initOrRestart();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				this,
				Stock.CONTENT_URI,
				new String[]{Stock.COLUMN_NAME_ITEM_ID + " AS " + Stock._ID, Item.NAME,
						Stock.COLUMN_NAME_ITEM_ID, Stock.COLUMN_NAME_QUANTITY,
						ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS},
				null,
				null,
				orderType == OrderType.NAME_ALPHA ? "i." + Item.NAME : ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS);
	}

	@Override
	public void onLoadFinished(Cursor data) {
		setActionBarTitle(R.string.stock);
		setActionBarSubtitle(Stations.getProductionStation().stationName);

		adapter.changeCursor(data);

		setActionBarInderterminate(false);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stockmenu, menu);

		initProgressActionView(menu.findItem(R.id.refresh_option), this, false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.PARAMETERS_OPTION:
				final Intent paramIntent = new Intent(this, SettingsActivity.class);
				startActivity(paramIntent);
				return true;
			case R.id.LOCATION_MANAGEMENT_OPTION:
				final Intent locationManagementIntent =
						new Intent(this, LocationManagementActivity.class);
				startActivity(locationManagementIntent);
				return true;

			default:
				return false;
		}
	}

	@Override
	public void onRefreshStart() {
		new StockUpdaterAsyncTask().execute();
	}

	private class StockUpdaterAsyncTask extends EveApiProcessWithCredentialAsyncTask {

		public StockUpdaterAsyncTask() {
			super(getApplicationContext());
		}

		@Override
		protected void onPreExecute() {
			setActionBarInderterminate(true);
		}

		@Override
		protected void doInBackground(CharacterCredential characterCredential) {
			CharacterService characterService = new Manager().characterService();
			AssetList assetList = characterService.assetList(characterCredential.keyId, characterCredential.vCode,
					characterCredential.characterId);

			Map<Long, Map<Long, Long>> assets = assetList.getAssets();

			ArrayList<ContentProviderOperation> operations = new ArrayList<>();

			operations.add(ContentProviderOperation.newDelete(Stock.CONTENT_URI).build());

			for (Map.Entry<Long, Map<Long, Long>> assetsEntry : assets.entrySet()) {
				long locationId = assetsEntry.getKey();
				for (Map.Entry<Long, Long> assetAtLocation : assetsEntry.getValue().entrySet()) {
					long itemId = assetAtLocation.getKey();
					long quantity = assetAtLocation.getValue();

					ContentValues values = new ContentValues();
					values.put(Stock.COLUMN_NAME_ITEM_ID, itemId);
					values.put(Stock.COLUMN_NAME_QUANTITY, quantity);
					values.put(Stock.COLUMN_NAME_LOCATION_ID, locationId);

					operations.add(ContentProviderOperation.newInsert(Stock.CONTENT_URI).withValues(values).build());
				}
			}

			try {
				getContentResolver().applyBatch(ItemContentProvider.AUTHORITY, operations);
			} catch (RemoteException | OperationApplicationException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}

			getContentResolver().notifyChange(Stock.CONTENT_URI, null);
		}

		@Override
		protected void onPostExecute(Void result) {
			setActionBarInderterminate(false);
		}
	}
}
