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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;

/**
 * @author picon.software
 */
public class ProductionStepFragment extends EnhancedMaterialListFragment {

	@InjectView(R.id.separator) protected View separator;
	@InjectView(android.R.id.content) protected View content;

	public ProductionStepFragment() {
		dataColumns = new String[]{Item._ID, Item.NAME, ItemMaterials.QUANTITY,
				Item.CHOSEN_PRICE_ID, Item.CHOSEN_PRICE_ID, Item.VOLUME};
		viewIDs = new int[]{R.id.item_icon, R.id.item_name, R.id.item_quantity, R.id.warn_icon, R.id.ITEM_PRICE,
				R.id.ITEM_VOLUME};
		layoutId = R.layout.manufacture_production_plan_step;
		elementLayoutId = R.layout.item_row_small_premium;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment = super.onCreateView(inflater, container, savedInstanceState);

		ButterKnife.inject(this, fragment);

		return fragment;
	}

	public void hide() {
		content.setVisibility(View.GONE);
	}

	public void show() {
		content.setVisibility(View.VISIBLE);
	}

	public void hideSeparator() {
		separator.setVisibility(View.GONE);
	}
}
