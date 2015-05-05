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

package fr.piconsoft.eoit.ui.viewholder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.listener.ItemOnClickListener;
import fr.piconsoft.eoit.ui.model.ListItemData;
import fr.piconsoft.eoit.util.IconUtil;

/**
 * @author picon.software
 */
public class ListItemViewHolder extends AbstractViewHolder<ListItemData> {

	public static final int LAYOUT_ID = R.layout.item_row_small_premium;

	@InjectView(R.id.item_layout) protected RelativeLayout itemLayout;
	@InjectView(R.id.item_name) protected TextView itemName;
	@InjectView(R.id.item_quantity) protected TextView itemQuantity;
	@InjectView(R.id.item_icon) protected ImageView itemIcon;
	@InjectView(R.id.warn_icon) protected ImageView warnIcon;

	private Activity activity;

	public ListItemViewHolder(Activity activity, View itemView) {
		super(itemView);
		this.activity = activity;
	}

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
    }

	@Override
	public void bind(ListItemData itemData) {
		itemLayout.setOnClickListener(new ItemOnClickListener(itemData.id, activity));
        itemName.setText(itemData.name);
        NumberFormat nf = DecimalFormat.getInstance();
        itemQuantity.setText("×" + nf.format(itemData.quantity));
		itemQuantity.setVisibility(View.VISIBLE);
        IconUtil.initIcon(itemData.id, itemIcon);
        warnIcon.setVisibility(itemData.chosenPriceId != null ? View.GONE : View.VISIBLE);
		itemLayout.setVisibility(itemData.quantity == 0 ? View.GONE : View.VISIBLE);
	}
}
