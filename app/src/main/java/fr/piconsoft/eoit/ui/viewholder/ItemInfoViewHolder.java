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

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import butterknife.InjectView;
import butterknife.Optional;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemInfoData;
import fr.piconsoft.eoit.util.Formatter;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 */
public class ItemInfoViewHolder extends AbstractViewHolder<ItemInfoData> {

	public static final int LAYOUT_ID = R.layout.item_info_premium_header;
	private static final NumberFormat nfPercent = DecimalFormat.getIntegerInstance();
	private static final NumberFormat nf = DecimalFormat.getNumberInstance();

	//--- Views
	@InjectView(R.id.item_name) @Optional public TextView name;
	@InjectView(R.id.item_volume) @Optional public TextView volume;
	@InjectView(R.id.item_icon) @Optional public ImageView icon;
	@InjectView(R.id.last_update) @Optional protected TextView lastUpdate;
	@InjectView(R.id.profit) @Optional protected TextView prTv;
	@InjectView(R.id.profit_value) @Optional protected TextView prvTv;
	@InjectView(R.id.profit_percent) @Optional protected View prpTv;

	public ItemInfoViewHolder(View itemView) {
		super(itemView);
	}

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
    }

	@Override
	public void bind(ItemInfoData itemData) {
		IconUtil.initIcon(itemData.id, icon);
		setText(name, itemData.name);
		setText(volume, nf.format(itemData.volume) + volume.getContext().getString(R.string.volumeUnit));
		initPricesTextView(itemData.sellPrice, itemData.producePrice);

		long duration = (new Date().getTime() - itemData.lastUpdate.getTime()) / 1000;
		if (duration > 60) {
			setText(lastUpdate, Formatter.formatTimeWithoutSeconds(duration) + " ago in " + itemData.solarSystemName);
		} else {
			setText(lastUpdate, "moment ago in " + itemData.solarSystemName);
		}
	}

	private void initPricesTextView(double sellPrice, double producePrice) {
		setVisibility(prTv, View.VISIBLE);
		setVisibility(prpTv, View.VISIBLE);
		setVisibility(prvTv, View.VISIBLE);

		double profitPercent, profit = 0;
		if (sellPrice > 0 && !Double.isNaN(sellPrice) && producePrice > 0.01 && !Double.isNaN(producePrice)) {
			profit = sellPrice - producePrice;
		} else {
			setVisibility(prTv, View.GONE);
			setVisibility(prpTv, View.GONE);
			setVisibility(prvTv, View.GONE);
		}
		profitPercent = profit / producePrice;
		if (!Double.isNaN(profitPercent)
				|| profitPercent < Double.MAX_VALUE
				|| profitPercent > Double.MIN_VALUE) {
			setText(prTv, nfPercent.format(profitPercent * 100));
		}
		if(prvTv != null) {
			PricesUtil.setPrice(prvTv, profit, true, false);
		}
	}

	private void setText(@Nullable TextView textView, String text) {
		if(textView != null) {
			textView.setText(text);
		}
	}

	private void setVisibility(@Nullable View view, int visibility) {
		if(view != null) {
			view.setVisibility(visibility);
		}
	}
}
