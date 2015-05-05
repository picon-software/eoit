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

package fr.piconsoft.eoit.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.ui.model.AsteroidItemBean;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.formula.FormulaCalculator;
import fr.piconsoft.eoit.model.AsteroidConstitution;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;

/**
 * @author picon.software
 */
public final class MiningHelper implements LoaderCallbacks<Cursor> {

	private final static int LOADER_ID = EOITConst.getNextLoaderIdSequence();
	private final static String LOG_TAG = MiningHelper.class.getSimpleName();

	public static final int FULL_LOADING = 0;
	public static final int ONLY_BASIC_ASTEROIDS = 1;

	private static int currentMode = -1;
	private static Context context;
	private static SparseArray<TreeSet<ItemBeanWithMaterials>> asteroidMap;
	private static SparseItemBeanArray asteroids = new SparseItemBeanArray();
	private static int territory;
	private static float securityStatus;

	private MiningHelper() {
	}

	public static void loadMiningData(FragmentActivity pActivity, int mode) {
		if (asteroidMap == null || currentMode != mode) {
			context = pActivity.getApplicationContext();
			currentMode = mode;
			pActivity.getSupportLoaderManager().initLoader(LOADER_ID, null, new MiningHelper());
		}
		loadPrefrences(context);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == LOADER_ID) {
			Log.d(LOG_TAG, "Starting cursorloader...");

			String whereClause = "";

			if (currentMode == ONLY_BASIC_ASTEROIDS) {
				whereClause = QueryBuilderUtil.buildInClause(Item._ID,
						EOITConst.Items.ARKONOR, EOITConst.Items.BISTOT, EOITConst.Items.CROKITE, EOITConst.Items.DARK_OCHRE,
						EOITConst.Items.GNEISS, EOITConst.Items.HEDBERGITE, EOITConst.Items.HEMORPHITE, EOITConst.Items.JASPET,
						EOITConst.Items.KERNITE, EOITConst.Items.MERCOXIT, EOITConst.Items.OMBER, EOITConst.Items.PLAGIOCLASE,
						EOITConst.Items.PYROXERES, EOITConst.Items.SCORDITE, EOITConst.Items.SPODUMAIN, EOITConst.Items.VELDSPAR);
			}

			return new CursorLoader(
					context,
					AsteroidConstitution.CONTENT_URI,
					AsteroidConstitution.COLUMNS,
					whereClause,
					null,
					null
			);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (DbUtil.hasAtLeastOneRow(cursor) && loader.getId() == LOADER_ID) {

			Log.d(LOG_TAG, "Reading cursor...");

			while (!cursor.isAfterLast()) {
				AsteroidItemBean item = getItem(cursor);
				asteroids.append(item);
				cursor.moveToNext();
			}

			asteroidMap =
					new SparseArray<>();
			asteroidMap.append(EOITConst.Items.TRITANIUM,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.TRITANIUM)));
			asteroidMap.append(EOITConst.Items.PYERITE,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.PYERITE)));
			asteroidMap.append(EOITConst.Items.MEXALLON,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.MEXALLON)));
			asteroidMap.append(EOITConst.Items.ISOGEN,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.ISOGEN)));
			asteroidMap.append(EOITConst.Items.NOCXIUM,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.NOCXIUM)));
			asteroidMap.append(EOITConst.Items.ZYDRINE,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.ZYDRINE)));
			asteroidMap.append(EOITConst.Items.MEGACYTE,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.MEGACYTE)));
			asteroidMap.append(EOITConst.Items.MORPHITE,
					new TreeSet<>(new AsteroidMineralComparator(EOITConst.Items.MORPHITE)));

			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.TRITANIUM), EOITConst.Items.TRITANIUM);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.PYERITE), EOITConst.Items.PYERITE);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.MEXALLON), EOITConst.Items.MEXALLON);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.ISOGEN), EOITConst.Items.ISOGEN);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.NOCXIUM), EOITConst.Items.NOCXIUM);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.ZYDRINE), EOITConst.Items.ZYDRINE);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.MEGACYTE), EOITConst.Items.MEGACYTE);
			populateAndRestrictToSelectedMineral(asteroidMap.get(EOITConst.Items.MORPHITE), EOITConst.Items.MORPHITE);

			Log.d(LOG_TAG, "Finish reading cursor...");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private static AsteroidItemBean getItem(Cursor cursor) {
		AsteroidItemBean item = new AsteroidItemBean();

		item.id = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials._ID));
		item.name = cursor.getString(cursor.getColumnIndexOrThrow(Item.NAME));
		item.batchSize = cursor.getInt(cursor.getColumnIndexOrThrow(Item.PORTION_SIZE));
		item.volume = cursor.getDouble(cursor.getColumnIndexOrThrow(Item.VOLUME));
		item.groupId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.GROUP_ID));

		item.addMineral(getMineral(EOITConst.Items.TRITANIUM, cursor, AsteroidConstitution.TRITANIUM_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.PYERITE, cursor, AsteroidConstitution.PYERITE_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.MEXALLON, cursor, AsteroidConstitution.MEXALLON_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.ISOGEN, cursor, AsteroidConstitution.ISOGEN_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.NOCXIUM, cursor, AsteroidConstitution.NOCXIUM_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.ZYDRINE, cursor, AsteroidConstitution.ZYDRINE_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.MEGACYTE, cursor, AsteroidConstitution.MEGACYTE_QUANTITY));
		item.addMineral(getMineral(EOITConst.Items.MORPHITE, cursor, AsteroidConstitution.MORPHITE_QUANTITY));

		return item;
	}

	private static ItemBeanWithMaterials getMineral(int id, Cursor cursor, String colummnName) {
		ItemBeanWithMaterials mineral = new ItemBeanWithMaterials();
		mineral.id = id;
		mineral.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(colummnName));

		return mineral;
	}

	public static AsteroidItemBean getMatchingAsteroid(ItemBean requiredMineral) {
		TreeSet<ItemBeanWithMaterials> restrictedAsteroidsForRequiredMineral = new TreeSet<>(new AsteroidMineralComparator(requiredMineral.id));

		List<Integer> asteroidIdList = new ArrayList<>();
		for (int id : getAsteroidIds()) {
			asteroidIdList.add(id);
		}

		for (ItemBeanWithMaterials asteroid : asteroidMap.get(requiredMineral.id)) {
			if (asteroidIdList.contains(asteroid.id)) {
				restrictedAsteroidsForRequiredMineral.add(asteroid);
			}
		}

		if (restrictedAsteroidsForRequiredMineral.isEmpty()) {
			return null;
		}
		AsteroidItemBean asteroidItemBean = (AsteroidItemBean) restrictedAsteroidsForRequiredMineral.first().clone();

		if (asteroidItemBean == null) {
			return null;
		}

		long refinedMineralQuantity = asteroidItemBean.getMineral(requiredMineral.id).quantity;

		long effectiveRefinedMineralQuantity =
				refinedMineralQuantity -
						FormulaCalculator.calculateRefiningWaste(refinedMineralQuantity, asteroidItemBean.groupId) -
						FormulaCalculator.calculateReprocessStationTake(refinedMineralQuantity);

		asteroidItemBean.quantity = (long) ((Math.floor(requiredMineral.quantity /
				effectiveRefinedMineralQuantity) + 1) * asteroidItemBean.batchSize);

		return asteroidItemBean;
	}

	public static SparseItemBeanArray refine(SparseItemBeanArray asteroidsToReprocess) {

		SparseItemBeanArray minerals = new SparseItemBeanArray();

		for (ItemBeanWithMaterials asteroidToReprocess : asteroidsToReprocess.values()) {
			AsteroidItemBean asteroid = (AsteroidItemBean) asteroids.get(asteroidToReprocess.id);

			// in the case of asteroid that can't be reprocessed.
			if (asteroid == null) {
				return minerals;
			}

			int numberOfPossibleReprocessBatch = (int) Math.floor(asteroidToReprocess.quantity / asteroid.batchSize);

			for (ItemBeanWithMaterials mineral : asteroid.materials.values()) {
				long refinedMineralQuantity = mineral.quantity;

				long effectiveRefinedMineralQuantity =
						refinedMineralQuantity -
								FormulaCalculator.calculateRefiningWaste(refinedMineralQuantity, asteroid.groupId) -
								FormulaCalculator.calculateReprocessStationTake(refinedMineralQuantity);

				ItemBeanWithMaterials refinedMineral = mineral.clone();
				refinedMineral.quantity = effectiveRefinedMineralQuantity * numberOfPossibleReprocessBatch;

				minerals.include(refinedMineral);
			}
		}

		return minerals;
	}

	public static void loadPrefrences(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		securityStatus = Float.parseFloat(preferences.getString(PreferencesName.MINING_REGION_SEC, "0.7"));
		territory = Integer.parseInt(preferences.getString(PreferencesName.TERRITORY, String.valueOf(EOITConst.Territories.CALDARI_SPACE)));
	}

	private static int[] getAsteroidIds() {
		int securityStatusIndex = (int) (securityStatus * 10);

		switch (territory) {
			case EOITConst.Territories.AMARR_SPACE:
				return EOITConst.Asteroid.DISTRIBUTION_IN_AMARR_SPACE[securityStatusIndex];

			case EOITConst.Territories.CALDARI_SPACE:
				return EOITConst.Asteroid.DISTRIBUTION_IN_CALDARI_SPACE[securityStatusIndex];

			case EOITConst.Territories.GALLENTE_SPACE:
				return EOITConst.Asteroid.DISTRIBUTION_IN_GALLENTE_SPACE[securityStatusIndex];

			case EOITConst.Territories.MINMATAR_SPACE:
				return EOITConst.Asteroid.DISTRIBUTION_IN_MINMATAR_SPACE[securityStatusIndex];

			default:
				return null;
		}
	}

	private static void populateAndRestrictToSelectedMineral(Set<ItemBeanWithMaterials> asteroidSet, int mineralId) {
		for (int i = 0; i < asteroids.size(); i++) {
			AsteroidItemBean asteroid = (AsteroidItemBean) asteroids.valueAt(i);
			if (asteroid.containsMineral(mineralId)) {
				asteroidSet.add(asteroid);
			}
		}
	}

	private static class AsteroidMineralComparator implements Comparator<ItemBeanWithMaterials> {

		private int mineralId;

		public AsteroidMineralComparator(int mineralId) {
			this.mineralId = mineralId;
		}

		@Override
		public int compare(ItemBeanWithMaterials lhs, ItemBeanWithMaterials rhs) {

			double lhsQuantity, rhsQuantity;

			if (lhs.materials.containsKey(mineralId)) {
				lhsQuantity = lhs.materials.get(mineralId).quantity / lhs.volume;
			} else {
				lhsQuantity = -1;
			}
			if (rhs.materials.containsKey(mineralId)) {
				rhsQuantity = rhs.materials.get(mineralId).quantity / rhs.volume;
			} else {
				rhsQuantity = -1;
			}

			return lhsQuantity > rhsQuantity ? -1 : (lhsQuantity == rhsQuantity ? 0 : 1);
		}
	}
}
