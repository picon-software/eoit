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

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.AsteroidItemBean;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.helper.MiningHelper;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.model.Stock;
import fr.piconsoft.eoit.util.Formatter;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.ManufactureUtils;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.activity.basic.loader.BaseProductionNeedsLoader;
import fr.piconsoft.eoit.activity.basic.loader.BaseProductionNeedsLoader.OnLoadFinishedListener;
import fr.piconsoft.eoit.util.PricesUtil;
import fr.piconsoft.eoit.ui.model.ProductionStep;
import fr.piconsoft.eoit.formula.FormulaCalculator;

/**
 * @author picon.software
 */
public class ProductionPlanFragment extends LoaderFragment<Cursor> implements OnLoadFinishedListener {

	private final static int BASE_PRODUCTION_NEEDS_LOADER_ID = EOITConst.getNextLoaderIdSequence();

	private TextView totalPriceTextView = null;
	private TextView totalTimeTextView = null;

	private double sellPrice, producePrice;
	protected int baseProductionTime, unitPerBatch, itemId, groupId, categorieId, currentNumberOfRuns = 1;

	protected int layoutId = R.layout.manufacture_production_plan;

	private SparseItemBeanArray baseProductionNeedItemMap = null,
			productionNeeds, remainingProductionNeedsItems,
			stockItems = new SparseItemBeanArray();

	private Set<Integer> ids = new TreeSet<>();

	private int[] stepFragmentIds = {R.id.production_step0, R.id.production_step1, R.id.production_step2, R.id.production_step3, R.id.production_step4, R.id.production_step5};
	protected List<EnhancedMaterialListFragment> stepsFragments = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragment = inflater.inflate(layoutId, container, false);

		MiningHelper.loadMiningData(getActivity(), MiningHelper.ONLY_BASIC_ASTEROIDS);

		Bundle args = getArguments();

		if (args != null) {
			itemId = args.getInt("itemId", 0);
			sellPrice = args.getDouble("sellPrice", 0);
			producePrice = args.getDouble("producePrice", 0);
			unitPerBatch = args.getInt("unitPerBatch", 0);
			baseProductionTime = args.getInt("productionTime", 0);
			categorieId = args.getInt("categorieId");
            groupId = args.getInt("groupId");
		}

		TextView runsTextView = (TextView) fragment.findViewById(R.id.runs);
		runsTextView.setOnKeyListener(new RunsListener());
		runsTextView.setText("1");
		totalPriceTextView = (TextView) fragment.findViewById(R.id.total_profit_value);
		totalTimeTextView = (TextView) fragment.findViewById(R.id.total_time_value);

		bindStepFragments();

		getLoaderManager().initLoader(BASE_PRODUCTION_NEEDS_LOADER_ID, null,
				new BaseProductionNeedsLoader.BaseProductionNeedsLoaderCallBacks(
						context, this, itemId, groupId, categorieId)
		);

		return fragment;
	}

	protected void bindStepFragments() {
		for (int stepFragmentId : stepFragmentIds) {
			EnhancedMaterialListFragment fragment = (EnhancedMaterialListFragment) getChildFragmentManager().findFragmentById(stepFragmentId);
			fragment.getView().setVisibility(View.GONE);
			stepsFragments.add(fragment);
		}
	}

	@Override
	public void onLoadFinishedBaseProductionNeeds(int itemId, SparseItemBeanArray data) {
		this.baseProductionNeedItemMap = data;

		if (baseProductionNeedItemMap != null) {
			ids.clear();
			for (ItemBeanWithMaterials item : baseProductionNeedItemMap.values()) {
				ids.addAll(ManufactureUtils.getIds(item));
			}
		}

		initOrRestart();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		stockItems.clear();
		return new CursorLoader(
				context,
				Stock.CONTENT_URI,
				new String[]{Stock._ID, Stock.COLUMN_NAME_ITEM_ID, Stock.COLUMN_NAME_QUANTITY},
				QueryBuilderUtil.buildInClause(Stock.COLUMN_NAME_ITEM_ID, ids),
				null,
				Stock.COLUMN_NAME_ITEM_ID + " ASC");
	}

	@Override
	public void onLoadFinished(Cursor cursor) {

		if (DbUtil.hasAtLeastOneRow(cursor)) {
			while (!cursor.isAfterLast()) {

				int id = cursor.getInt(cursor.getColumnIndexOrThrow(Stock.COLUMN_NAME_ITEM_ID));
				long quantity = cursor.getLong(cursor.getColumnIndexOrThrow(Stock.COLUMN_NAME_QUANTITY));
				ItemBeanWithMaterials item = new ItemBeanWithMaterials();
				item.id = id;
				item.quantity = quantity;

				stockItems.include(item);

				cursor.moveToNext();
			}
		}
		productionNeeds =
				ManufactureUtils.getProductionNeeds(
						baseProductionNeedItemMap, stockItems, currentNumberOfRuns);

		remainingProductionNeedsItems = new SparseItemBeanArray(productionNeeds);

		ProductionStep miningStep = null;
		if (isMiningActive()) {
			miningStep = getMiningStep();
		}
		ProductionStep shoppingStep = getShoppingStep();

		initializeSteps(miningStep, shoppingStep);

		updateProductionInfos(currentNumberOfRuns);
	}

	protected void initializeSteps(ProductionStep miningStep, ProductionStep shoppingStep) {
		List<ProductionStep> steps = new ArrayList<>();
		if (miningStep != null && !miningStep.isEmpty()) steps.add(miningStep);
		if (shoppingStep != null && !shoppingStep.isEmpty()) steps.add(shoppingStep);
		steps.addAll(findSteps(true));

		int index = 0;
		for (EnhancedMaterialListFragment fragment : stepsFragments) {
			if (steps.size() > index) {
				fragment.initialize(steps.get(index));
			} else {
				fragment.getView().setVisibility(View.GONE);
			}
			index++;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@SuppressWarnings("deprecation")
	protected List<ProductionStep> findSteps(boolean insertFinalItem) {
		List<MatrixCursor> steps = new ArrayList<>();
		SparseItemBeanArray nextStepItems = new SparseItemBeanArray();
		int unitPerBatch = getArguments().getInt("unitPerBatch", 0);
		if(insertFinalItem) {
			steps.add(getCursor(
					itemId,
					getArguments().getString("itemName"),
					currentNumberOfRuns * unitPerBatch,
					getArguments().getDouble("volume"),
					sellPrice));
		}

		if (baseProductionNeedItemMap != null) {
			SparseItemBeanArray currentStepItems = new SparseItemBeanArray();
			SparseItemBeanArray currentStepItemsToConvert = new SparseItemBeanArray();
			nextStepItems.putAll(baseProductionNeedItemMap.multiply(currentNumberOfRuns));

			do {

				MatrixCursor currentStep = EnhancedMaterialListFragment.getEmptyCursor();
				currentStepItems.clear();
				currentStepItemsToConvert.clear();
				currentStepItems.putAll(nextStepItems);
				nextStepItems.clear();

				for (ItemBeanWithMaterials item : currentStepItems.values()) {
					if (item.materials != null && !item.materials.isEmpty()) {
						currentStepItemsToConvert = currentStepItemsToConvert.union(item);
						nextStepItems.putAll(item.materials);
					}
				}

				currentStepItemsToConvert = currentStepItemsToConvert.exclude(stockItems);

				for (ItemBeanWithMaterials item : currentStepItemsToConvert.values()) {
					EnhancedMaterialListFragment.addRowToMatrixCursor(currentStep, item);
				}

				if (!currentStepItemsToConvert.isEmpty()) {
					steps.add(currentStep);
				}

			} while (!nextStepItems.isEmpty());

			Collections.reverse(steps);
		}

		List<ProductionStep> prodSteps = new ArrayList<>();

		int cpt = 1;
		for (MatrixCursor step : steps) {
			prodSteps.add(new ProductionStep("STEP " + cpt + " : PRODUCE", step));
			cpt++;
		}

		return prodSteps;
	}

	@SuppressWarnings("deprecation")
	private ProductionStep getMiningStep() {
		MatrixCursor miningStep = EnhancedMaterialListFragment.getEmptyCursor();
		SparseItemBeanArray miningStepItemsToConvert = new SparseItemBeanArray();
		SparseItemBeanArray requiredMinerals = new SparseItemBeanArray();

		for (ItemBeanWithMaterials item : productionNeeds.values()) {
			if (item.isMineral()) {
				requiredMinerals.append(item);
			}
		}

		for (int i = EOITConst.Items.MINERAL_IDS.length - 1; i >= 0; i--) {
			ItemBeanWithMaterials requiredMineral =
					requiredMinerals.get(EOITConst.Items.MINERAL_IDS[i]);
			if (requiredMineral != null) {
				AsteroidItemBean requiredAsteroid = MiningHelper.getMatchingAsteroid(requiredMineral);
				if (requiredAsteroid != null) {
					miningStepItemsToConvert.append(requiredAsteroid);
					SparseItemBeanArray itemToRemove = requiredAsteroid.materials.multiply(requiredAsteroid.quantity / requiredAsteroid.batchSize);
					requiredMinerals =
							requiredMinerals.exclude(itemToRemove);
					remainingProductionNeedsItems =
							remainingProductionNeedsItems.exclude(itemToRemove);
				}
			}
		}

		for (ItemBeanWithMaterials item : miningStepItemsToConvert.values()) {
			EnhancedMaterialListFragment.addRowToMatrixCursor(miningStep, item);
		}

		return new ProductionStep("MINE", miningStep);
	}

	@SuppressWarnings("deprecation")
	private ProductionStep getShoppingStep() {
		MatrixCursor shoppingStep = EnhancedMaterialListFragment.getEmptyCursor();

		for (ItemBeanWithMaterials item : remainingProductionNeedsItems.values()) {
			EnhancedMaterialListFragment.addRowToMatrixCursor(shoppingStep, item);
		}

		return new ProductionStep("SHOPPING", shoppingStep);
	}

	@SuppressWarnings("deprecation")
	private static MatrixCursor getCursor(final int itemId, final String itemName,
										  final long itemQuantity, final double volume, final double sellPrice) {
		MatrixCursor cursor = EnhancedMaterialListFragment.getEmptyCursor();

		double itemSellPrice = sellPrice > 0 ? sellPrice : 0;
		DbUtil.addRowToMatrixCursor(cursor, itemId, itemName, itemQuantity,
				volume, EOITConst.SELL_PRICE_ID, itemSellPrice, itemSellPrice, itemSellPrice, itemSellPrice, 0);

		return cursor;
	}

	private void updateProductionInfos(int numberOfRuns) {
		int te = getArguments().getInt("pl", 0);

		double productionTime = FormulaCalculator.calculateProductionTime(
				baseProductionTime,
				te);

		double sellpriceTotal = sellPrice * unitPerBatch * numberOfRuns;
		double productionPriceTotal = producePrice * unitPerBatch * numberOfRuns;
		double productionTimeTotal = productionTime * numberOfRuns;

		PricesUtil.setPrice(totalPriceTextView, sellpriceTotal - productionPriceTotal, true);
		totalTimeTextView.setText(Formatter.formatTime(productionTimeTotal));
	}

	private boolean isMiningActive() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(PreferencesName.MINING_ACTIVE, true);
	}

	private class RunsListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 ||
					keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DEL)
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String editTextStr = ((EditText) v).getText().toString();

				if (editTextStr.length() != 0) {
					try {
						currentNumberOfRuns = Integer.parseInt(editTextStr);

						updateProductionInfos(currentNumberOfRuns);

						restartLoader();
					} catch (NumberFormatException e) {
						((EditText) v).setError("Enter a valid value");
					}
				}
				return false;
			}

			return false;
		}
	}
}
