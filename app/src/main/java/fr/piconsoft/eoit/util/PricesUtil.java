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

package fr.piconsoft.eoit.util;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;

import static fr.piconsoft.eoit.util.DbUtil.getDate;

/**
 * @author picon.software
 */
public final class PricesUtil {

	private static final String PRICE_PATTERN = "#,##0.##";

	private PricesUtil() {
	}

	public static String formatPrice(double value, Context context, boolean withCurrency) {

		boolean isNegative = value < 0;

		double absValue = Math.abs(value);

		NumberFormat nf = new DecimalFormat(PRICE_PATTERN);
		String suffix = "";
		if (absValue > 1000 && absValue < 1000000) {
			absValue = absValue / 1000;
			suffix = " k";
		}
		if (absValue > 1000000 && absValue < 1000000000L) {
			absValue = absValue / 1000000;
			suffix = " M";
		}
		if (absValue > 1000000000L) {
			absValue = absValue / 1000000000L;
			suffix = " B";
		}
		StringBuilder sb = new StringBuilder();
		if (!Double.isNaN(absValue)) {
			sb.append(isNegative ? '-' : "")
					.append(nf.format(absValue))
					.append(suffix)
					.append(withCurrency ? context.getResources().getString(R.string.isk) : "");
		} else {
			sb.append(context.getResources().getText(R.string.invalid_price));
		}

		return sb.toString();
	}

	public static double getPriceOrNaN(Cursor cursor, String columnName) {
		boolean isNull = cursor.isNull(cursor.getColumnIndexOrThrow(columnName));

		if (isNull) {
			return Double.NaN;
		}
		return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
	}

	public static void setPrice(TextView textView, double price, boolean withCurrency) {
		setPrice(textView, price, withCurrency, true);
	}

	public static void setPrice(TextView textView, double price, boolean withCurrency, boolean hideIfInvalid) {
		if (!Double.isNaN(price) || price < Double.MAX_VALUE || price > Double.MIN_VALUE) {
			String formatedPrice = formatPrice(price, textView.getContext(), withCurrency);
			textView.setText(Html.fromHtml(formatedPrice));
			if (hideIfInvalid) textView.setVisibility(View.VISIBLE);
		} else {
			textView.setText("--");
			if (hideIfInvalid) textView.setVisibility(View.GONE);
		}
	}

	public static void setPriceWithDefault(TextView textView, double price, boolean withCurrency, String defaultValue) {
		textView.setVisibility(View.VISIBLE);
		if (!Double.isNaN(price)) {
			String formatedPrice = formatPrice(price, textView.getContext(), withCurrency);
			textView.setText(formatedPrice);
		} else {
			textView.setText(defaultValue);
		}
	}

	public static void showPrice(TextView textView) {
		textView.setVisibility(View.VISIBLE);
	}

	public static void hidePrice(TextView textView) {
		textView.setVisibility(View.GONE);
	}

	public static double getPriceOrNaN(Cursor cursor) {

		if (cursor.isNull(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID))
				|| cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID)) < 0) {
			return Double.NaN;
		}

		int chosenPrice = cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID));

		return getPriceOrNaN(cursor, chosenPrice);
	}

	public static double getPriceOrNaN(Cursor cursor, int typePrice) {

		String columnName;

		switch (typePrice) {
			case EOITConst.BUY_PRICE_ID:
				columnName = Prices.BUY_PRICE;
				break;
			case EOITConst.OWN_PRICE_ID:
				columnName = Prices.OWN_PRICE;
				break;
			case EOITConst.PRODUCE_PRICE_ID:
				columnName = Prices.PRODUCE_PRICE;
				break;
			case EOITConst.SELL_PRICE_ID:
				columnName = Prices.SELL_PRICE;
				break;
			default:
				return 0;
		}

		return getPriceOrNaN(cursor, columnName);
	}

	public static PriceBean getPriceBean(Cursor cursor) {
		PriceBean price = new PriceBean();
		price.itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
		price.buyPrice = PricesUtil.getPriceOrNaN(cursor, Prices.BUY_PRICE);
		price.sellPrice = PricesUtil.getPriceOrNaN(cursor, Prices.SELL_PRICE);
		price.producePrice = PricesUtil.getPriceOrNaN(cursor, Prices.PRODUCE_PRICE);

		if (cursor.getColumnIndex(Item.CHOSEN_PRICE_ID) != -1) {
			price.chosenPrice = cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID));
			price.chosenPriceIsEmpty = cursor.isNull(cursor.getColumnIndex(Item.CHOSEN_PRICE_ID));
		}

		if (cursor.getColumnIndex(Prices.LAST_UPDATE) != -1) {
			price.lastUpdate = getDate(cursor, Prices.LAST_UPDATE);
		}

		if (cursor.getColumnIndex(Prices.SOLAR_SYSTEM_ID) != -1) {
			price.solarSystemId =
					cursor.getInt(cursor.getColumnIndexOrThrow(Prices.SOLAR_SYSTEM_ID));
		}

		return price;
	}
}
