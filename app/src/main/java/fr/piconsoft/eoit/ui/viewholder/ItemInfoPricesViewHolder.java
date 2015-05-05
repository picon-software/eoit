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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.InjectView;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemInfoPricesData;
import fr.piconsoft.eoit.ui.listener.PriceRadiogroupOnCheckedChangeListener;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 */
public class ItemInfoPricesViewHolder extends AbstractViewHolder<ItemInfoPricesData> {

	public static final int LAYOUT_ID = R.layout.item_info_prices_premium;

	//--- Views
	@InjectView(R.id.price_radio_group) protected RadioGroup priceRadioGroup;
	@InjectView(R.id.buy_price) protected RadioButton bRa;
	@InjectView(R.id.sell_price) protected RadioButton sRa;
	@InjectView(R.id.produce_price) protected RadioButton pRa;
	@InjectView(R.id.tv_buy_price) protected TextView bTv;
	@InjectView(R.id.tv_sell_price) protected TextView sTv;
	@InjectView(R.id.tv_produce_price) protected TextView pTv;

	public ItemInfoPricesViewHolder(View itemView) {
		super(itemView);
	}

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
    }

	@Override
	public void bind(ItemInfoPricesData itemData) {
		initRadioGroup(itemData.chosenPriceId);
		initPricesTextView(itemData.buyPrice, itemData.sellPrice, itemData.producePrice);

        priceRadioGroup.setOnCheckedChangeListener(new PriceRadiogroupOnCheckedChangeListener(itemData));
	}

	private void initRadioGroup(int chosenPriceId) {
		switch (chosenPriceId) {
			case EOITConst.BUY_PRICE_ID:
				bRa.setChecked(true);
				break;

			case EOITConst.SELL_PRICE_ID:
				sRa.setChecked(true);
				break;

			case EOITConst.PRODUCE_PRICE_ID:
				pRa.setChecked(true);
				break;

			default:
				break;
		}
	}

	private void initPricesTextView(double buyPrice, double sellPrice, double producePrice) {
		PricesUtil.setPrice(bTv, buyPrice, true, false);
		PricesUtil.setPrice(sTv, sellPrice, true, false);
		PricesUtil.setPrice(pTv, producePrice, true, false);

		if (producePrice < 0.01 || Double.isNaN(producePrice)) {
			pTv.setText("--");
		}
	}
}
