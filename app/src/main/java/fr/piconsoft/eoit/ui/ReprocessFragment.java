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

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.helper.MiningHelper;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 */
public class ReprocessFragment extends EnhancedMaterialListFragment {

	private SparseItemBeanArray minerals;
	private double totalPrice, totalVolume;

	public ReprocessFragment() {

		dataColumns = new String[]{Item._ID, Item.NAME,
				ItemMaterials.QUANTITY,
				Item.CHOSEN_PRICE_ID, Item.CHOSEN_PRICE_ID,
				Item.VOLUME};
		viewIDs = new int[]{R.id.item_icon, R.id.item_name,
				R.id.item_quantity, R.id.warn_icon,
				R.id.item_price, R.id.item_volume};
		sectionTitleId = R.string.mining_session_reprocess;
		behavior = RedQuantityBehavior.NONE;
		layoutId = R.layout.item_info_materials_grid;
		elementLayoutId = R.layout.item_cell_compact;
		showIfCursorIsEmpty = false;
		numberOfColumn = 2;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment = super.onCreateView(inflater, container, savedInstanceState);

		MiningHelper.loadMiningData(getActivity(), MiningHelper.FULL_LOADING);

		return fragment;
	}

	public void setAsteroidToReprocess(SparseItemBeanArray asteroids) {

		minerals = MiningHelper.refine(asteroids);

		totalPrice = 0;
		totalVolume = 0;

		initOrRestart();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				context,
				Item.CONTENT_URI,
				new String[]{Item._ID, Item.NAME,
						Item.CHOSEN_PRICE_ID, Item.VOLUME,
						Prices.BUY_PRICE, Prices.OWN_PRICE,
						Prices.SELL_PRICE, Prices.PRODUCE_PRICE},
				QueryBuilderUtil.buildInClause(Item._ID, minerals.keySet()),
				null,
				null);
	}

	@Override
	protected void onLoadFinishedAdapteur(Cursor cursor,
										  SimpleCursorAdapter adapter) {
		MatrixCursor objectivesItemCursur = getEmptyCursor();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			int id = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(Item.NAME));
			ItemBeanWithMaterials item = new ItemBeanWithMaterials();
			item.id = id;
			item.name = name;
			item.quantity = minerals.get(id).quantity;
			item.price = PricesUtil.getPriceOrNaN(cursor);
			item.volume = cursor.getDouble(cursor.getColumnIndexOrThrow(Item.VOLUME));

			totalPrice += item.quantity * item.price;
			totalVolume += item.quantity * item.volume;

			addRowToMatrixCursor(objectivesItemCursur, item);

			cursor.moveToNext();
		}

		adapter.setViewBinder(new MaterialsListViewBinderWithTotalPriceAndVolume());
		adapter.changeCursor(objectivesItemCursur);
	}

	@Override
	protected void updateTotalMissingInfos(double totalMissingPrice, double totalMissingVolume) {
		super.updateTotalMissingInfos(totalPrice, totalVolume);
	}
}
