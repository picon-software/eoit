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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.util.PricesUtil;
import fr.piconsoft.eoit.db.BasicAsyncQueryHandler;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.ui.model.Stations;

/**
 * @author picon.software
 */
public class PricesDialog extends SimpleOkLoaderDialog<Cursor, Void> {

	private static final String DEFAULT_UNKNOWN_PRICE = " - ",
			LOG_TAG = PricesDialog.class.getSimpleName();

	private NumberFormat nfPercent;

	private int itemId, chosenPriceId = Integer.MIN_VALUE;
	private double fixedPrice = Double.NaN, producePrice;

	private View inflatedLayout;

	public PricesDialog() {
		super();
	}

	/**
	 *
	 */
	public PricesDialog(int itemId) {
		super(R.string.prices_prompt, R.layout.item_info_prices_dialog);
		this.itemId = itemId;
	}

	@Override
	protected void onCreateSimpleDialog(View inflatedLayout,
										Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			itemId = savedInstanceState.getInt("itemId");
			chosenPriceId = savedInstanceState.getInt("chosenPriceId");
			fixedPrice = savedInstanceState.getDouble("fixedPrice");
		}
		nfPercent = new DecimalFormat("##0.#%");

		this.inflatedLayout = inflatedLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (itemId > 0) {
			initOrRestart();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("itemId", itemId);
		outState.putInt("chosenPriceId", chosenPriceId);
		outState.putDouble("fixedPrice", fixedPrice);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	@Override
	protected Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				context,
				ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId),
				new String[]{Item._ID, Item.CHOSEN_PRICE_ID,
						Prices.BUY_PRICE, Prices.OWN_PRICE,
						Prices.SELL_PRICE, Prices.PRODUCE_PRICE,
						Groups.CATEGORIE_ID},
				null,
				null,
				null);
	}

	@Override
	public void onLoadFinished(Cursor cursor) {

		if (DbUtil.hasAtLeastOneRow(cursor)) {

			if (chosenPriceId == Integer.MIN_VALUE)
				chosenPriceId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID));
			int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Groups.CATEGORIE_ID));

			double buyPrice = PricesUtil.getPriceOrNaN(cursor, Prices.BUY_PRICE);
			double sellPrice = PricesUtil.getPriceOrNaN(cursor, Prices.SELL_PRICE);
			producePrice = PricesUtil.getPriceOrNaN(cursor, Prices.PRODUCE_PRICE);
			if (Double.isNaN(fixedPrice)) {
				fixedPrice = PricesUtil.getPriceOrNaN(cursor, Prices.OWN_PRICE);
				if (Double.isNaN(fixedPrice)) {
					fixedPrice = 0;
				}
			}

			TextView bTv = ((TextView) inflatedLayout.findViewById(R.id.buy_price));
			TextView sTv = ((TextView) inflatedLayout.findViewById(R.id.sell_price));
			TextView pTv = ((TextView) inflatedLayout.findViewById(R.id.produce_price));
			EditText fTv = ((EditText) inflatedLayout.findViewById(R.id.fixed_price));
			TextView prTv = ((TextView) inflatedLayout.findViewById(R.id.profit_sell_produce));

			prTv.setVisibility(View.VISIBLE);

			PricesUtil.setPriceWithDefault(bTv, buyPrice, false, DEFAULT_UNKNOWN_PRICE);
			PricesUtil.setPriceWithDefault(sTv, sellPrice, false, DEFAULT_UNKNOWN_PRICE);
			PricesUtil.setPriceWithDefault(pTv, producePrice, false, DEFAULT_UNKNOWN_PRICE);
			NumberFormat nf = new DecimalFormat(EOITConst.VALUES_PATTERN);
			fTv.setText(nf.format(fixedPrice));

			double sellProduceProfit = 0;
			if (categoryId == EOITConst.Categories.ASTEROID_CATEGORIE_ID && buyPrice > 0 && producePrice > 0 && !Double.isNaN(producePrice)) {
				sellProduceProfit = (buyPrice - producePrice) / producePrice;
			} else if (sellPrice > 0 && producePrice > 0 && !Double.isNaN(producePrice)) {
				sellProduceProfit = (sellPrice - producePrice) / producePrice;
			} else {
				changeSellProduceProfitVisibility(View.GONE);
			}
			prTv.setText(nfPercent.format(sellProduceProfit));

			if (fixedPrice > 0 && producePrice > 0 && !Double.isNaN(producePrice)) {
				updateFixedProduceProfit();
			} else {
				changeFixedProduceProfitVisibility(View.GONE);
			}

			if (producePrice < 0.01) {
				pTv.setText(DEFAULT_UNKNOWN_PRICE);
			}

			positionPriceIndicator();
			bTv.setOnClickListener(new PriceIndicatorOnClickListener(EOITConst.BUY_PRICE_ID));
			sTv.setOnClickListener(new PriceIndicatorOnClickListener(EOITConst.SELL_PRICE_ID));
			pTv.setOnClickListener(new PriceIndicatorOnClickListener(EOITConst.PRODUCE_PRICE_ID));
			fTv.setOnClickListener(new PriceIndicatorOnClickListener(EOITConst.OWN_PRICE_ID));
			fTv.setOnKeyListener(new FixedPriceOnKeyListener());
		}
	}

	@Override
	protected void onSubmit() {
		ContentValues values = new ContentValues();
		values.put(Prices.OWN_PRICE, fixedPrice);
		new BasicAsyncQueryHandler(context).startUpdate(0, null,
				ContentUris.withAppendedId(
						ContentUris.withAppendedId(Prices.CONTENT_ITEM_ID_URI_BASE, itemId),
						Stations.getTradeStation().solarSystemId),
				values,
				null,
				null);

		values = new ContentValues();
		values.put(Item.CHOSEN_PRICE_ID, chosenPriceId);
		new BasicAsyncQueryHandler(context).startUpdate(0, null,
				ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId),
				values,
				null,
				null);
	}

	private void positionPriceIndicator() {
		switch (chosenPriceId) {
			case EOITConst.BUY_PRICE_ID:
				inflatedLayout.findViewById(R.id.sell_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.buy_price_indicator).setVisibility(View.VISIBLE);
				inflatedLayout.findViewById(R.id.produce_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.fixed_price_indicator).setVisibility(View.GONE);
				break;

			case EOITConst.SELL_PRICE_ID:
				inflatedLayout.findViewById(R.id.sell_price_indicator).setVisibility(View.VISIBLE);
				inflatedLayout.findViewById(R.id.buy_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.produce_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.fixed_price_indicator).setVisibility(View.GONE);
				break;

			case EOITConst.PRODUCE_PRICE_ID:
				inflatedLayout.findViewById(R.id.sell_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.buy_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.produce_price_indicator).setVisibility(View.VISIBLE);
				inflatedLayout.findViewById(R.id.fixed_price_indicator).setVisibility(View.GONE);
				break;

			case EOITConst.OWN_PRICE_ID:
				inflatedLayout.findViewById(R.id.sell_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.buy_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.produce_price_indicator).setVisibility(View.GONE);
				inflatedLayout.findViewById(R.id.fixed_price_indicator).setVisibility(View.VISIBLE);
				break;

			default:
				break;
		}
	}

	private void changeSellProduceProfitVisibility(int visibility) {
		inflatedLayout.findViewById(R.id.profit_sell_produce).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.hL1).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.hL2).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.vL1).setVisibility(visibility);
	}

	private void changeFixedProduceProfitVisibility(int visibility) {
		inflatedLayout.findViewById(R.id.profit_fixed_produce).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.hL3).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.hL4).setVisibility(visibility);
		inflatedLayout.findViewById(R.id.vL2).setVisibility(visibility);
	}

	private void updateFixedProduceProfit() {

		double fixedProduceProfit = 0;
		if (fixedPrice > 0 && producePrice > 0 && !Double.isNaN(producePrice)) {
			fixedProduceProfit = (fixedPrice - producePrice) / producePrice;
		}

		TextView pr2Tv = ((TextView) inflatedLayout.findViewById(R.id.profit_fixed_produce));
		pr2Tv.setText(nfPercent.format(fixedProduceProfit));
	}

	private class PriceIndicatorOnClickListener implements OnClickListener {

		private int priceTypeId;

		public PriceIndicatorOnClickListener(int priceTypeId) {
			super();
			this.priceTypeId = priceTypeId;
		}

		@Override
		public void onClick(View v) {
			chosenPriceId = priceTypeId;
			positionPriceIndicator();
		}
	}

	public static class PricesDialogCheckedChangeListener implements OnClickListener, View.OnLongClickListener {

		private int itemid;
		private FragmentManager fragmentManager;

		public PricesDialogCheckedChangeListener(int itemid, FragmentManager fragmentManager) {
			super();
			this.itemid = itemid;
			this.fragmentManager = fragmentManager;
		}

		@Override
		public boolean onLongClick(View v) {
			openDialog();

			return true;
		}

		@Override
		public void onClick(View v) {
			openDialog();
		}

		private void openDialog() {
			PricesDialog dialog = new PricesDialog(itemid);
			dialog.show(fragmentManager, "PriceSelector");
		}
	}

	private class FixedPriceOnKeyListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 ||
					keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DEL)
					&& event.getAction() == KeyEvent.ACTION_UP) {
				EditText edit = (EditText) v;
				try {
					String valueStr = edit.getText().toString();
					if (valueStr.length() == 0) {
						fixedPrice = Double.NaN;
					} else {
						valueStr = valueStr.replaceAll(",", ".");
						fixedPrice = Double.parseDouble(valueStr);
						changeFixedProduceProfitVisibility(View.VISIBLE);
						if (fixedPrice > 0 && producePrice > 0 && !Double.isNaN(producePrice)) {
							updateFixedProduceProfit();
						}
					}
				} catch (NumberFormatException e) {
					Log.e(LOG_TAG, e.getMessage(), e);
					fixedPrice = Double.NaN;
				}
				return false;
			}

			return false;
		}
	}
}
