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
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.activity.basic.loader.MatrixCursorLoader;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;
import fr.piconsoft.eoit.util.PricesUtil;
import fr.piconsoft.eoit.ui.model.ProductionStep;

/**
 * @author picon.software
 */
public class EnhancedMaterialListFragment extends ItemMaterialListFragment {

	private static final NumberFormat nf = new DecimalFormat("#,##0.##");
	private MatrixCursor cursor;
	private double totalPrice, totalVolume;

	public EnhancedMaterialListFragment() {
		dataColumns = new String[]{Item._ID, Item.NAME, ItemMaterials.QUANTITY,
				Item.CHOSEN_PRICE_ID, Item.CHOSEN_PRICE_ID, Item.VOLUME};
		viewIDs = new int[]{R.id.item_icon, R.id.item_name, R.id.item_quantity, R.id.warn_icon, R.id.ITEM_PRICE,
				R.id.ITEM_VOLUME};
		sectionTitleId = R.string.materials;
		layoutId = R.layout.manufacture_missing_materials;
		behavior = RedQuantityBehavior.NONE;
	}

	public void initialize(final ProductionStep step) {
		this.cursor = step.getStepElements();
		initSectionTitle(step.getStepTitle());

        onLoadFinished(cursor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new MatrixCursorLoader(context, cursor);
	}

	@Override
	protected void onLoadFinishedAdapteur(Cursor cursor, SimpleCursorAdapter adapter) {
		adapter.setViewBinder(new MaterialsListViewBinderWithTotalPriceAndVolume());
		adapter.changeCursor(cursor);
	}

	protected void updateTotalMissingInfos(double totalMissingPrice, double totalMissingVolume) {
		PricesUtil.setPrice((TextView) getView().findViewById(R.id.total_missing_price), totalMissingPrice, true);
		((TextView) getView().findViewById(R.id.total_missing_volume)).setText(nf.format(totalMissingVolume) + " " +
						getResources().getText(R.string.volumeUnit));
		if (Double.isNaN(totalMissingPrice) && Double.isNaN(totalMissingVolume)) {
			setVisibilityTotalPriceAndVolume(View.GONE);
		} else {
			setVisibilityTotalPriceAndVolume(View.VISIBLE);
		}
		if (Double.isNaN(totalMissingPrice) || totalMissingPrice == 0) {
			getView().findViewById(R.id.total_missing_price).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.total_missing_price).setVisibility(View.VISIBLE);
		}
		if (Double.isNaN(totalMissingVolume) || totalMissingVolume == 0) {
			getView().findViewById(R.id.total_missing_volume).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.total_missing_volume).setVisibility(View.VISIBLE);
		}
	}

	protected class MaterialsListViewBinderWithTotalPriceAndVolume extends ItemListViewBinder {

		public MaterialsListViewBinderWithTotalPriceAndVolume() {
			super(RedQuantityBehavior.NONE);
		}

		@Override
		protected void addToTotalPrice(long id, double price, long quantity, long redQuantity) {

			super.addToTotalPrice(id, price, quantity, redQuantity);

			totalPrice = getTotalPrice();
			if ((getNumberOfElements() - 1) == getCurrentPosition()) { // we are at the last element of the cursor
				updateTotalMissingInfos(totalPrice, totalVolume);
				reset();
			}
		}

		@Override
		protected void addToTotalVolume(long id, double volume, long quantity) {

			super.addToTotalVolume(id, volume, quantity);

			totalVolume = getTotalVolume();
			if ((getNumberOfElements() - 1) == getCurrentPosition()) { // we are at the last element of the cursor
				updateTotalMissingInfos(totalPrice, totalVolume);
				reset();
			}
		}
	}

	public void initSectionTitle(String title) {
		if(sectionTitle != null) {
			sectionTitle.setText(title);
		}
	}

	private void setVisibilityTotalPriceAndVolume(int visibility) {
		View view = getView().findViewById(R.id.total_missing_price_and_volume);
		if (view != null) {
			view.setVisibility(visibility);
		}
	}
}
