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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.util.PricesUtil;
import fr.piconsoft.eoit.ui.task.Callback;
import fr.piconsoft.eoit.ui.task.PriceUpdaterAsyncTask;
import fr.piconsoft.eoit.ui.task.UpdateDecryptorIdTask;
import fr.piconsoft.eoit.db.DatabaseHelper;
import fr.piconsoft.eoit.db.util.DatabaseMapper;
import fr.piconsoft.eoit.formula.FormulaCalculator;
import fr.piconsoft.eoit.util.DecryptorUtil;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.InventionSkill;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.ui.model.Skills;
import fr.piconsoft.eoit.ui.model.AbstractItemData;
import fr.piconsoft.eoit.ui.model.InitiatorBean;
import fr.piconsoft.eoit.ui.model.InitiatorData;
import fr.piconsoft.eoit.ui.model.BlueprintInventionBean;
import fr.piconsoft.eoit.ui.model.BlueprintInventionChancesData;
import fr.piconsoft.eoit.ui.model.BlueprintInventionDecryptorData;
import fr.piconsoft.eoit.ui.model.BlueprintInventionListItemData;
import fr.piconsoft.eoit.ui.model.BlueprintSpecData;
import fr.piconsoft.eoit.ui.model.DecryptorBean;
import fr.piconsoft.eoit.ui.model.ListSectionTitleData;
import fr.piconsoft.eoit.ui.model.ListSkillData;

import static fr.piconsoft.eoit.db.util.DatabaseMapper.create;
import static fr.piconsoft.eoit.util.DecryptorUtil.getDecryptorBonusesOrDefault;
import static fr.piconsoft.eoit.ui.model.BlueprintInventionDecryptorData.OnDecryptorSelectedListener;
import static fr.piconsoft.eoit.ui.model.InitiatorData.OnInitiatorSelectedListener;
import static fr.piconsoft.eoit.ui.widget.ProgressActionView.OnProgressListener;

/**
 * @author picon.software
 */
public class BlueprintActivity extends StrictModeActivity
		implements OnDecryptorSelectedListener, OnInitiatorSelectedListener, OnProgressListener {

	private static final String LOG_TAG = BlueprintActivity.class.getSimpleName();

	//--- Views
	@InjectView(R.id.profit_value) public TextView profitValue;
	@InjectView(R.id.cost_value) public TextView costValue;
	@InjectView(R.id.recycler_view) protected RecyclerView recyclerView;

	private BlueprintInventionChancesData blueprintInventionChancesData;
	private BlueprintInventionDecryptorData blueprintInventionDecryptorData =
			new BlueprintInventionDecryptorData();
	private BlueprintInventionBean blueprintInventionBean;
	private InitiatorData initiatorData;

	private int blueprintId = -1;
	private int producedItemId;
	private short encryptionSkillLevel = 0, datacore1SkillLevel = 0, datacore2SkillLevel = 0;
	private int numberOfChances;
	private double sellPrice, producePrice;
	private float probability;
	private long maxProductionLimit;

	private boolean firstLoad = true;

	private Map<Integer, BlueprintInventionListItemData> itemDataMap = new HashMap<>();

	private DecryptorUtil.DecryptorBonuses currentDecryptorBonuses = DecryptorUtil.NO_DECRYPTOR;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.blueprint_premium);

		ButterKnife.inject(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setActionBarSubtitle(getIntent().getStringExtra("itemName"));

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new RecyclerViewAdapter(this));

		blueprintId = getIntent().getIntExtra("blueprintId", -1);
		producedItemId = getIntent().getIntExtra("produceItemId", -1);
		sellPrice = getIntent().getDoubleExtra("sellPrice", 0);
		producePrice = getIntent().getDoubleExtra("producePrice", 0);

		blueprintInventionChancesData = new BlueprintInventionChancesData();
		blueprintInventionDecryptorData = new BlueprintInventionDecryptorData();
		blueprintInventionDecryptorData.setOnItemSelectedListener(this);
		initiatorData = new InitiatorData();
		initiatorData.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blueprintmenu, menu);

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

			default:
				return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new ItemInfoAsyncLoader().execute();
	}

	@Override
	public void onDecryptorSelected(int blueprintId, int decryptorId, boolean hasChanged) {
		if(hasChanged || firstLoad) {
			new UpdateDecryptorIdAsyncTask(this).execute(blueprintId, decryptorId);
			firstLoad = false;
		}
	}

	@Override
	public void onInitiatorSelected(int blueprintId, int relicId) {
		new UpdateRelicIdTask().execute(blueprintId, relicId, blueprintInventionBean.decryptorId);
	}

	@Override
	public void onRefreshStart() {
		//new ItemPricesAsyncLoader(recyclerView.getAdapter(), false, true).execute();
	}

	private float getInventionChances() {
		return FormulaCalculator.calculateInventionChances(
				probability,
				encryptionSkillLevel, datacore1SkillLevel,
				datacore2SkillLevel, currentDecryptorBonuses.probabilityMultiplier);
	}

	private void initializeSkills(Cursor cursor) {
		if (DbUtil.hasAtLeastOneRow(cursor)) {
			boolean datacore1SkillSet = false;

			while (!cursor.isAfterLast()) {
				int skillId = cursor.getInt(cursor.getColumnIndexOrThrow(InventionSkill._ID));

				short userSkillLevel = Skills.getSkill(skillId);
				if (Skills.isEncryptionSkill(skillId)) {
					encryptionSkillLevel = userSkillLevel;
				} else {
					if (datacore1SkillSet) {
						datacore2SkillLevel = userSkillLevel;
					} else {
						datacore1SkillLevel = userSkillLevel;
						datacore1SkillSet = true;
					}
				}

				cursor.moveToNext();
			}
		}
	}

	private void fillBlueprintCopyInfos(BlueprintInventionListItemData itemData) {
		if (itemData.time != null) {
			itemData.producePrice = FormulaCalculator.calculateMaxRunsCopyCost(
					itemData.time, 1000, 4343, 1.5F, 0.5F);
		}
	}

	private double getPrice(BlueprintInventionListItemData itemData) {
		if (itemData.chosenPriceId == null) {
			return Double.NaN;
		}

		double price;
		switch (itemData.chosenPriceId.shortValue()) {
			case EOITConst.SELL_PRICE_ID:
				price = itemData.sellPrice;
				break;
			case EOITConst.BUY_PRICE_ID:
				price = itemData.buyPrice;
				break;
			case EOITConst.PRODUCE_PRICE_ID:
				price = itemData.producePrice;
				break;
			case EOITConst.OWN_PRICE_ID:
			default:
				price = Double.NaN;
		}

		return price;
	}

	private double getTotalPrice() {
		double totalPrice = 0;
		for (BlueprintInventionListItemData itemData : itemDataMap.values()) {
			totalPrice += getPrice(itemData) * itemData.initialQuantity * numberOfChances;
		}

		return totalPrice;
	}

	private void updateProfit() {
		double profitOnSingleItem = sellPrice - producePrice;
		double blueprintProfit =
				profitOnSingleItem * blueprintInventionBean.unitPerBatch *
						(maxProductionLimit +
								currentDecryptorBonuses.maxRunModifier);

		PricesUtil.setPrice(profitValue, blueprintProfit, true, false);
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
			Cursor cursor = database.query("blueprint_info",
					new String[]{"_id AS blueprint_id, maxProductionLimit AS max_production_limit, *"}, "_id = " + blueprintId, null, null, null, null);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (blueprint_info)");

			BlueprintSpecData blueprintSpecData = new BlueprintSpecData();
			if (DbUtil.hasAtLeastOneRow(cursor)) {
				blueprintInventionBean = create(BlueprintInventionBean.class, cursor);
				DatabaseMapper.inject(blueprintInventionDecryptorData, cursor);
				DatabaseMapper.inject(initiatorData, cursor);
				DatabaseMapper.inject(blueprintSpecData, cursor);

				if (blueprintInventionBean.decryptorId != 0) {
					currentDecryptorBonuses =
							getDecryptorBonusesOrDefault(blueprintInventionBean.decryptorId);
				}

				blueprintInventionChancesData.inventionChances = getInventionChances();

				dataset.add(blueprintInventionChancesData);
				dataset.add(blueprintSpecData);
			}
			cursor.close();

			dataset.add(new ListSectionTitleData(getString(R.string.required_skills), false));

			if(initiatorData.getInitiatorMap().isEmpty()) {
				time = System.nanoTime();
				cursor = database.query("invention_initiator_price_item",
						new String[]{"_id AS id, *"}, "produce_item_id = " + producedItemId, null, null, null, "_id ASC");
				Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (invention_initiator_price_item)");

				for (Cursor data : new CursorIteratorWrapper(cursor)) {
					InitiatorBean initiatorBean = create(InitiatorBean.class, data);
					initiatorData.getInitiatorMap().put(initiatorBean.id, initiatorBean);
				}
				cursor.close();
			}

			time = System.nanoTime();
			cursor = database.query("invention_skill",
					new String[]{"*"}, "item_id = " + blueprintInventionBean.blueprintId, null, null, null, null);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (invention_skill)");

			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				ListSkillData listSkillData = create(ListSkillData.class, data);
				dataset.add(listSkillData);
			}
			initializeSkills(cursor);
			cursor.close();

			// Initiator
			if(!initiatorData.getInitiatorMap().isEmpty()) {
				dataset.add(new ListSectionTitleData(getString(R.string.initiator), true));
				dataset.add(initiatorData);
			}

			if(initiatorData.getInitiatorMap().size() > 1 && initiatorData.relicId != null) {
				InitiatorBean initiator = initiatorData.getInitiatorMap().get(initiatorData.relicId);
				probability = initiator.probability;
				maxProductionLimit = initiator.maxProductionLimit;
				blueprintSpecData.maxProductionLimit = initiator.maxProductionLimit;
			} else {
				probability = blueprintInventionBean.probability;
				maxProductionLimit = blueprintInventionBean.maxProductionLimit;
			}

			//Decryptor
			dataset.add(new ListSectionTitleData(getString(R.string.decryptor), true));
			dataset.add(blueprintInventionDecryptorData);

			if(blueprintInventionDecryptorData.getDecryptorMap().isEmpty()) {
				time = System.nanoTime();
				cursor = database.query("item_info",
						new String[]{"_id AS id, *"},
						Item.GROUP_ID + " = " + DecryptorUtil.getDecryptorGroupId(),
						null, null, null, "_id ASC");
				Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (decryptors item_info)");

				for (Cursor data : new CursorIteratorWrapper(cursor)) {
					DecryptorBean bean = create(DecryptorBean.class, data);
					blueprintInventionDecryptorData.getDecryptorMap().put(bean.id, bean.name);
				}
				cursor.close();
			}

			// Final update
			blueprintInventionChancesData.inventionChances = getInventionChances();
			numberOfChances = (int) FloatMath.ceil(1 / (blueprintInventionChancesData.inventionChances));

			dataset.add(new ListSectionTitleData(getString(R.string.required_items), true));

			itemDataMap.clear();

			if(initiatorData.getInitiatorMap().size() == 1) {
				time = System.nanoTime();
				cursor = database.query("material_blueprint_copy",
						new String[]{"_id AS id, *"}, "produce_item_id = " + producedItemId, null, null, null, "_id ASC");
				Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (material_blueprint_copy)");

				for (Cursor data : new CursorIteratorWrapper(cursor)) {
					BlueprintInventionListItemData itemData =
							create(BlueprintInventionListItemData.class, data);
					fillBlueprintCopyInfos(itemData);
					itemData.initialQuantity = itemData.quantity;
					itemData.quantity *= numberOfChances;
					itemDataMap.put(itemData.id, itemData);
					dataset.add(itemData);
				}
				cursor.close();
			}

			time = System.nanoTime();
			cursor = database.query("invention_material_price_item",
					new String[]{"_id AS id, *"},
					"produce_item_id = " + producedItemId,
					null, null, null, "_id ASC");
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms (invention_material_price_item)");

			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				BlueprintInventionListItemData itemData =
						create(BlueprintInventionListItemData.class, data);
				itemData.initialQuantity = itemData.quantity;
				itemData.quantity *= numberOfChances;
				itemDataMap.put(itemData.id, itemData);
				dataset.add(itemData);
			}
			cursor.close();

			return new RecyclerViewAdapter(BlueprintActivity.this,
					dataset.toArray(new AbstractItemData[dataset.size()]));
		}

		@Override
		protected void onPostExecute(RecyclerViewAdapter recyclerViewAdapter) {
			recyclerView.setAdapter(recyclerViewAdapter);
			double totalPrice = getTotalPrice();
			PricesUtil.setPrice(costValue, totalPrice, true, false);
			setActionBarInderterminate(false);

			new ItemPricesAsyncLoader(totalPrice).execute();
		}
	}

	private class ItemPricesAsyncLoader extends AsyncTask<Void, Void, Void> {

		private double blueprintCost;

		private ItemPricesAsyncLoader(double blueprintCost) {
			this.blueprintCost = blueprintCost;
		}

		@Override
		protected void onPreExecute() {
			setActionBarInderterminate(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			new UpdateBlueprintPriceTask() {
				@Override
				protected void onPostExecute(Void aVoid) {
					PriceUpdaterAsyncTask task = new PriceUpdaterAsyncTask(getApplicationContext());
 					task.setCallback(new Callback<PriceBean>() {
						@Override
						public void success(PriceBean priceBean) {
							if(priceBean != null) {
								producePrice = priceBean.producePrice;
								updateProfit();
							}
						}
					});
					inject(task);
					task.reCalculatePriceForItemId(producedItemId);
				}
			}.execute(blueprintCost);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setActionBarInderterminate(false);
		}
	}

	private class UpdateDecryptorIdAsyncTask extends UpdateDecryptorIdTask {

		public UpdateDecryptorIdAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(Integer decryptorId) {
			new ItemInfoAsyncLoader().execute();
		}
	}

	public class UpdateRelicIdTask extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			int blueprintId = params[0];
			int relicId = params[1];
			int decryptorId = params[2];

			DecryptorUtil.DecryptorBonuses currentDecryptorBonuses = DecryptorUtil.getDecryptorBonusesOrDefault(decryptorId);

			Uri updateBlueprintUri = ContentUris.withAppendedId(Blueprint.CONTENT_ID_URI_BASE, blueprintId);
			ContentValues values = new ContentValues();
			values.put(Blueprint.RELIC_ID, relicId);
			values.put(Blueprint.ML, (2 + currentDecryptorBonuses.meModifier));
			values.put(Blueprint.PL, (4 + currentDecryptorBonuses.peModifier));

			getContentResolver().update(
					updateBlueprintUri,
					values,
					null,
					null);

			return relicId;
		}

		@Override
		protected void onPostExecute(Integer relicId) {
			new ItemInfoAsyncLoader().execute();
		}
	}

	private class UpdateBlueprintPriceTask extends AsyncTask<Double, Void, Void> {

		@Override
		protected Void doInBackground(Double... params) {
			double price = params[0];

			Uri updateBlueprintUri = ContentUris.withAppendedId(Blueprint.CONTENT_ID_URI_BASE, blueprintId);
			ContentValues values = new ContentValues();
			values.put(Blueprint.RESEARCH_PRICE, price);

			getContentResolver().update(
					updateBlueprintUri,
					values,
					null,
					null);
			return null;
		}
	}
}
