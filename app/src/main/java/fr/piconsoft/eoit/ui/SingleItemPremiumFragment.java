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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.ui.listener.ItemOnClickListener;
import fr.piconsoft.eoit.util.PricesUtil;

public class SingleItemPremiumFragment extends BaseFragment {

	@InjectView(R.id.item_layout) protected RelativeLayout layout;
	@InjectView(R.id.item_name) protected TextView itemNameTv;
	@InjectView(R.id.ITEM_PRICE) protected TextView itemPriceTv;
	@InjectView(R.id.item_quantity) protected TextView itemQuantityTv;
	@InjectView(R.id.item_icon) protected ImageView itemIcon;
	@InjectView(R.id.legend) protected View legend;

	private ItemBean item;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_row_small_premium, container, false);

		ButterKnife.inject(this, view);

		layout.setBackgroundResource(R.drawable.bg_selector_white);

		return view;
	}

	public void setItem(ItemBean item, int color) {
		this.item = item;
		refreshFragment(color);
	}

	private void refreshFragment(int color) {
		if(item != null && isCreated) {
			IconUtil.initIcon(item.id, itemIcon);
			itemNameTv.setText(item.name);
			itemQuantityTv.setVisibility(View.GONE);
			legend.setBackgroundColor(color);
			legend.setVisibility(View.VISIBLE);
			PricesUtil.setPrice(itemPriceTv, item.price, true);
			layout.setOnClickListener(new ItemOnClickListener(item.id, getActivity()));
		}
	}
}
