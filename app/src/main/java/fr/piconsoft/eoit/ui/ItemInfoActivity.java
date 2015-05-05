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

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.db.DatabaseHelper;
import fr.piconsoft.eoit.db.util.DatabaseMapper;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.ui.model.Stations;
import fr.piconsoft.eoit.ui.model.AbstractItemData;
import fr.piconsoft.eoit.ui.model.ItemInfoBlueprintData;
import fr.piconsoft.eoit.ui.model.ItemInfoData;
import fr.piconsoft.eoit.ui.model.ItemInfoPricesData;
import fr.piconsoft.eoit.ui.model.ListItemData;
import fr.piconsoft.eoit.ui.model.ListSectionTitleData;
import fr.piconsoft.eoit.ui.task.Callback;
import fr.piconsoft.eoit.ui.task.JobCostCalculationTask;
import fr.piconsoft.eoit.ui.task.PriceUpdaterAsyncTask;
import fr.piconsoft.eoit.ui.viewholder.ItemInfoViewHolder;
import fr.piconsoft.eoit.ui.widget.FavoriteActionView;
import fr.piconsoft.eoit.ui.widget.ProgressActionView;
import fr.piconsoft.eoit.util.IconUtil;
import retrofit.RetrofitError;

/**
 * @author picon.software
 */
public class ItemInfoActivity extends StrictModeActivity implements ProgressActionView.OnProgressListener {

	private static final String LOG_TAG = ItemInfoActivity.class.getSimpleName();

	@InjectView(R.id.item_render) protected ImageView itemRender;
	@InjectView(R.id.recycler_view) protected RecyclerView recyclerView;
	@InjectView(R.id.content_frame) protected FrameLayout contentFrame;

	private MenuItem favoriteMenuItem;

	private int itemId;

	private ItemInfoViewHolder itemInfoViewHolder;
	private ItemInfoData itemInfoData = new ItemInfoData();
	private ItemInfoPricesData itemInfoPricesData = new ItemInfoPricesData();
	private ItemInfoBlueprintData itemInfoBlueprintData = new ItemInfoBlueprintData();

	private boolean hasInvalidPrices = false;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.item_info_premium);

		ButterKnife.inject(this);
		itemInfoViewHolder = new ItemInfoViewHolder(new View(this));
		ButterKnife.inject(itemInfoViewHolder, this);

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new RecyclerViewAdapter(this));

		itemId = (int) ContentUris.parseId(getIntent().getData());
		IconUtil.initIcon(itemId, itemRender);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("");

		DefaultStationTips defaultStationTips = new DefaultStationTips(this);
		if (Stations.getProductionStation().stationId == EOITConst.Stations.JITA_STATION_ID &&
				Stations.getTradeStation().stationId == EOITConst.Stations.JITA_STATION_ID &&
				defaultStationTips.isActive(this)) {
			defaultStationTips.show(getSupportFragmentManager(), "default_station");
		}

		ContributionTips contributionTips = new ContributionTips(this);
		if (contributionTips.isActive(this)) {
			contributionTips.show(getSupportFragmentManager(), "contribution");
		}

		Intent histoIntent = new Intent(EOITConst.HISTO_LOGGER_BR_ACTION);
		histoIntent.putExtra(Item._ID, itemId);
		sendBroadcast(histoIntent);

		new ItemInfoAsyncLoader().execute();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("itemId", itemId);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		itemId = savedInstanceState.getInt("itemId");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_info_premium_menu, menu);

		favoriteMenuItem = menu.findItem(R.id.favorite_option);

		initProgressActionView(menu.findItem(R.id.refresh_option), this, false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				intent = new Intent(this, ItemListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			case R.id.PARAMETERS_OPTION:
				onSettings(null);
				break;
			case R.id.HELP_OPTION:
				onHelp(null);
				break;
			case R.id.STATIONS_PRICES_OPTION:
				onMorePrice(null);
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onHelp(View view) {
		SimpleOkDialog<?> dialog = new SimpleOkDialog<Object>(R.string.item_info_activity_name, R.layout.help_item_info);
		dialog.show(getSupportFragmentManager(), "helpDialog");
	}

	public void onSettings(View view) {
		final Intent paramIntent = new Intent(this, SettingsActivity.class);
		startActivity(paramIntent);
	}

	public void onManufacture(View view) {
		Intent intent =
				new Intent(null,
						ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId),
						this,
						ManufactureActivity.class);
		intent.putExtra("itemId", itemId);
		intent.putExtra("metaGroupId", itemInfoBlueprintData.techLevel);
		intent.putExtra("categorieId", itemInfoData.categorieId);
		intent.putExtra("groupId", itemInfoData.groupId);
		intent.putExtra("itemName", itemInfoData.name);
		intent.putExtra("volume", itemInfoData.volume);
		intent.putExtra("sellPrice", itemInfoPricesData.sellPrice);
		intent.putExtra("producePrice", itemInfoPricesData.producePrice);
		intent.putExtra("unitPerBatch", itemInfoBlueprintData.unitPerBatch);
		intent.putExtra("productionTime", itemInfoBlueprintData.productionTime);
		startActivity(intent);
	}

	public void openMETEDialog(View view) {
		METEDialog dialog = new METEDialog(
				itemId,
				itemInfoBlueprintData);
		dialog.setCallBack(new Callback<ItemInfoBlueprintData>() {
			@Override
			public void success(ItemInfoBlueprintData response) {
				onRefreshStart();
			}
		});
		dialog.show(getSupportFragmentManager(), "METEDialog");
	}

	public void openInvention(View view) {
		Intent intent = new Intent(this, BlueprintActivity.class);
		intent.setData(ContentUris.withAppendedId(Blueprint.CONTENT_ID_URI_BASE, itemInfoBlueprintData.blueprintId));
		intent.putExtra("produceItemId", itemId);
		intent.putExtra("metaGroupId", itemInfoBlueprintData.techLevel);
		intent.putExtra("categorieId", itemInfoData.categorieId);
		intent.putExtra("blueprintId", itemInfoBlueprintData.blueprintId);
		intent.putExtra("groupId", itemInfoData.groupId);
		intent.putExtra("itemName", itemInfoData.name);
		intent.putExtra("sellPrice", itemInfoPricesData.sellPrice);
		intent.putExtra("producePrice", itemInfoPricesData.producePrice);
		intent.putExtra("unitPerBatch", itemInfoBlueprintData.unitPerBatch);
		startActivity(intent);
	}

	public void onMorePrice(View view) {
		Intent intent =
				new Intent(null,
						ContentUris.withAppendedId(Station.CONTENT_ID_FAVORITE_STATIONS_PRICES_URI_BASE, itemId),
						this,
						LocationPricesActivity.class);
		startActivity(intent);
	}

	private class ItemInfoAsyncLoader extends AsyncTask<Void, Void, RecyclerViewAdapter> {

		@Override
		protected void onPreExecute() {
			setActionBarInderterminate(true);
		}

		@Override
		protected RecyclerViewAdapter doInBackground(Void... params) {
			SQLiteDatabase database = DatabaseHelper.getInstance(getApplicationContext())
					.getReadableDatabase();

			List<AbstractItemData> dataset = new ArrayList<>();

			long time = System.nanoTime();
			Cursor cursor = database.query("item_blueprint_info",
					new String[]{"*"}, "id = " + itemId, null, null, null, null);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (item_blueprint_info)");

			if(DbUtil.hasAtLeastOneRow(cursor)) {
				DatabaseMapper.inject(itemInfoData, cursor);
				DatabaseMapper.inject(itemInfoPricesData, cursor);
				DatabaseMapper.inject(itemInfoBlueprintData, cursor);

				if(itemInfoViewHolder.icon == null) {
					dataset.add(itemInfoData);
				}
				dataset.add(itemInfoPricesData);
				if(itemInfoBlueprintData.blueprintId > 0) {
					dataset.add(itemInfoBlueprintData);
				}
			}
			cursor.close();

			time = System.nanoTime();
			cursor = database.query("all_item_materials",
					new String[]{"*"}, "item_id = " + itemId, null, null, null, "id ASC");
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (all_item_materials)");

			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				ListItemData listItemData = new ListItemData();
				DatabaseMapper.inject(listItemData, data);
				dataset.add(listItemData);
				hasInvalidPrices |= listItemData.chosenPriceId == null;
			}
			cursor.close();

			time = System.nanoTime();
			cursor = database.query("refine_price_item",
					new String[]{"_id AS id", "*"}, "item_id = " + itemId, null, null, null, null);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (refine_price_item)");

			if(cursor.getCount() > 0) {
				dataset.add(new ListSectionTitleData(getString(R.string.refining_label), true));
			}

			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				ListItemData listItemData = new ListItemData();
				DatabaseMapper.inject(listItemData, data);
				dataset.add(listItemData);
				hasInvalidPrices |= listItemData.chosenPriceId == null;
			}
			cursor.close();

			time = System.nanoTime();
			cursor = database.query("blueprint_info",
					new String[]{"produce_item_id AS id", "unit_per_batch as quantity", "name"}, "_id = " + itemId, null, null, null, null);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (blueprint_info)");

			if(cursor.getCount() > 0) {
				dataset.add(new ListSectionTitleData(getString(R.string.produce_label), false));
			}

			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				ListItemData listItemData = new ListItemData();
				DatabaseMapper.inject(listItemData, data);
				dataset.add(listItemData);
			}
			cursor.close();

			return new RecyclerViewAdapter(ItemInfoActivity.this,
					dataset.toArray(new AbstractItemData[dataset.size()]));
		}

		@Override
		protected void onPostExecute(RecyclerViewAdapter recyclerViewAdapter) {
			recyclerView.setAdapter(recyclerViewAdapter);
			initFavoritActionView(itemInfoData.favorite, itemInfoData.id);
			initRender(itemInfoData.categorieId, itemInfoData.groupId, itemInfoData.id);
			if(itemInfoViewHolder.icon != null) {
				itemInfoViewHolder.bind(itemInfoData);
			}
			setActionBarInderterminate(false);

			new ItemPricesAsyncLoader(recyclerViewAdapter, true, false).execute();
		}
	}

	private class ItemPricesAsyncLoader extends AsyncTask<Void, Void, RecyclerView.Adapter> {

		private final boolean withJobCost;
		private final boolean forceRecalculate;
		private RecyclerView.Adapter recyclerViewAdapter;

		private RetrofitError error;

		private ItemPricesAsyncLoader(RecyclerView.Adapter recyclerViewAdapter, boolean withJobCost, boolean forceRecalculate) {
			this.recyclerViewAdapter = recyclerViewAdapter;
			this.withJobCost = withJobCost;
			this.forceRecalculate = forceRecalculate;
		}

		@Override
		protected void onPreExecute() {
			setActionBarInderterminate(true);
		}

		@Override
		protected RecyclerView.Adapter doInBackground(Void... params) {
			try {
				if (withJobCost) {
					JobCostCalculationTask jobCostCalculationTask = new JobCostCalculationTask();
					boolean injectionOK = inject(jobCostCalculationTask);

					if (!injectionOK) {
						return recyclerViewAdapter;
					}

					JobCostCalculationTask.JobCosts jobCosts = jobCostCalculationTask.doSync(itemId);

					if (jobCosts != null) {
						itemInfoBlueprintData.jobCost = jobCosts.jobCost;
						if (itemInfoPricesData.producePrice > 0) {
							itemInfoPricesData.producePrice +=
									Double.isNaN(jobCosts.jobCost) ?
											0 : jobCosts.jobCost / itemInfoBlueprintData.unitPerBatch;
						}
					}
				}
				long duration = (new Date().getTime() - itemInfoData.lastUpdate.getTime()) / 1000;
				if (forceRecalculate || hasInvalidPrices || duration > (6 * 60 * 60)) {
					updatePrices();
				}
			} catch (RetrofitError retrofitError) {
				error = retrofitError;
				cancel(true);
				if(error.getKind() == RetrofitError.Kind.UNEXPECTED) {
					throw retrofitError;
				}
			}

			return recyclerViewAdapter;
		}

		@Override
		protected void onCancelled(RecyclerView.Adapter adapter) {
			if(error != null) {
				Crouton.makeText(ItemInfoActivity.this,
						getMessage(error.getKind()), Style.ALERT, contentFrame).show();
				Log.w(LOG_TAG, error.getMessage(), error);
			}

			onPostExecute(adapter);
		}

		@Override
		protected void onPostExecute(RecyclerView.Adapter recyclerViewAdapter) {
			recyclerView.swapAdapter(recyclerViewAdapter, false);
			setActionBarInderterminate(false);
		}
	}

	private static int getMessage(RetrofitError.Kind errorKind) {
		switch (errorKind) {
			case CONVERSION:
				return R.string.ec_parsing_error;
			case NETWORK:
				return R.string.ec_network_error;
			default:
				return R.string.ec_download_error;

		}
	}

	@Override
	public void onRefreshStart() {
		new ItemPricesAsyncLoader(recyclerView.getAdapter(), false, true).execute();
	}

	private void initFavoritActionView(boolean favorite, int itemId) {
		if (favoriteMenuItem != null) {
			((FavoriteActionView)MenuItemCompat.getActionView(favoriteMenuItem)).init(favorite, itemId);
		}
	}

	private void updatePrices() {
		if (itemId > 0) {
			PriceUpdaterAsyncTask priceUpdaterAsyncTask = new PriceUpdaterAsyncTask(getApplicationContext());
			inject(priceUpdaterAsyncTask);
			PriceBean price = priceUpdaterAsyncTask.deepFullPriceUpdate(itemInfoData.id, itemInfoData.groupId, itemInfoData.categorieId);
			itemInfoPricesData.buyPrice = price.buyPrice;
			itemInfoPricesData.sellPrice = price.sellPrice;
			itemInfoPricesData.producePrice = price.producePrice;
			itemInfoData.sellPrice = price.sellPrice;
			itemInfoData.producePrice = price.producePrice;
			itemInfoData.lastUpdate = price.lastUpdate;
		}
	}

	private void initRender(int categorieId, int groupId, int itemId) {
		if (IconUtil.isRender(categorieId, groupId)) {
			IconUtil.initRender(itemId, itemRender);
		} else {
			itemRender.setImageResource(R.drawable.render_icn);
		}
	}
}
