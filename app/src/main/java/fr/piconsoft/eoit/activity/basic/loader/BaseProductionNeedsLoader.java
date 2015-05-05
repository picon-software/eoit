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

package fr.piconsoft.eoit.activity.basic.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.helper.AsteroidPriceCalculatorHelper;
import fr.piconsoft.eoit.helper.CommonPriceCalculatorHelper;
import fr.piconsoft.eoit.helper.PlanetaryCommoditiesPriceCalculatorHelper;
import fr.piconsoft.eoit.helper.ReactionPriceCalculatorHelper;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;

import static fr.piconsoft.eoit.Const.Categories.ASTEROID_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.COMMODITY_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.MATERIAL_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.PLANETARY_COMMODITIES_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Groups.FUEL_BLOCK_GROUP_ID;
import static fr.piconsoft.eoit.Const.Groups.GENERAL_COMMODITY_GROUP_ID;
import static fr.piconsoft.eoit.util.ManufactureUtils.addItemToMap;
import static fr.piconsoft.eoit.util.ItemUtil.getItemWithPrices;

/**
 * @author picon.software
 */
public class BaseProductionNeedsLoader extends AsyncTaskLoader<SparseItemBeanArray> {

	private SparseItemBeanArray data;
	private int itemId, groupId, categoryId;

	public BaseProductionNeedsLoader(Context context, int itemid, int groupId, int categoryId) {
		super(context);
		this.itemId = itemid;
		this.groupId = groupId;
		this.categoryId = categoryId;
	}

	@Override
	public SparseItemBeanArray loadInBackground() {
		data = getBaseProductionNeeds(getContext(), itemId, groupId, categoryId, 1);
		return data;
	}

	@Override
	protected void onStartLoading() {
		if (data == null) {
			forceLoad();
		} else {
			deliverResult(data);
		}
	}

	public static SparseItemBeanArray getBaseProductionNeeds(Context context, int itemId, int groupId, int categoryId, long numberOfItems) {
		final Cursor cursorItemMaterials = getMaterialCursor(context, itemId, groupId, categoryId);

		SparseItemBeanArray items = new SparseItemBeanArray();

		try {
			for (Cursor cursor : new CursorIteratorWrapper(cursorItemMaterials)) {
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials._ID));
				int materialGrpId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.GROUP_ID));
				int materialCatId = cursor.getInt(cursor.getColumnIndexOrThrow(Groups.CATEGORIE_ID));
				int chosenPrice = cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID));
				ItemBeanWithMaterials item = getItemWithPrices(cursor, numberOfItems);
				items = addItemToMap(items, item, numberOfItems, null);
				if (chosenPrice == EOITConst.PRODUCE_PRICE_ID) {
					item.setMaterials(getBaseProductionNeeds(context, id, materialGrpId, materialCatId, numberOfItems));
				}
			}
		} finally {
			cursorItemMaterials.close();
		}

		return items;
	}

	private static Cursor getMaterialCursor(Context context, int itemId, int groupId, int categoryId) {
		if (categoryId == ASTEROID_CATEGORIE_ID) {
			return getAsteroidMaterialCursor(context, itemId);
		} else if (categoryId == PLANETARY_COMMODITIES_CATEGORIE_ID) {
			return getPlanetaryCommoditiesMaterialCursor(context, itemId);
		} else if (categoryId == COMMODITY_CATEGORIE_ID && groupId == GENERAL_COMMODITY_GROUP_ID ||
				categoryId == MATERIAL_CATEGORIE_ID && groupId != FUEL_BLOCK_GROUP_ID) {
			return getReactionMaterialCursor(context, itemId);
		} else {
			return getCommonMaterialCursor(context, itemId);
		}
	}

	private static Cursor getCommonMaterialCursor(Context context, int itemId) {
		return CommonPriceCalculatorHelper.retreiveData(context.getContentResolver(), itemId);
	}

	private static Cursor getReactionMaterialCursor(Context context, int itemId) {
		return ReactionPriceCalculatorHelper.retreiveData(context.getContentResolver(), itemId);
	}

	private static Cursor getAsteroidMaterialCursor(Context context, int itemId) {
		return AsteroidPriceCalculatorHelper.retreiveData(context.getContentResolver(), itemId);
	}

	private static Cursor getPlanetaryCommoditiesMaterialCursor(Context context, int itemId) {
		return PlanetaryCommoditiesPriceCalculatorHelper.retreiveData(
				context.getContentResolver(), itemId);
	}

	public static class BaseProductionNeedsLoaderCallBacks implements LoaderCallbacks<SparseItemBeanArray> {

		private Context context;
		private int itemId, groupId, categorieId;
		private OnLoadFinishedListener listener;

		public BaseProductionNeedsLoaderCallBacks(Context context, OnLoadFinishedListener listener, int itemId, int groupId, int categorieId) {
			this.context = context;
			this.itemId = itemId;
			this.groupId = groupId;
			this.categorieId = categorieId;
			this.listener = listener;
		}

		@Override
		public Loader<SparseItemBeanArray> onCreateLoader(int id, Bundle args) {
			return new BaseProductionNeedsLoader(context, itemId, groupId, categorieId);
		}

		@Override
		public void onLoadFinished(Loader<SparseItemBeanArray> loader,
								   SparseItemBeanArray data) {
			listener.onLoadFinishedBaseProductionNeeds(itemId, data);
		}

		@Override
		public void onLoaderReset(Loader<SparseItemBeanArray> loader) {
		}
	}

	public interface OnLoadFinishedListener {
		public void onLoadFinishedBaseProductionNeeds(int itemId, SparseItemBeanArray data);
	}
}
