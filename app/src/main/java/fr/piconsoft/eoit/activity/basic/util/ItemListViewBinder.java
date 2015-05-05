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

package fr.piconsoft.eoit.activity.basic.util;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames.Prices;
import fr.piconsoft.eoit.formula.FormulaCalculator;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 */
public class ItemListViewBinder extends AbstractViewBinder implements ViewBinder {

	public static enum RedQuantityBehavior {
		NONE,
		WITH_REFINE_QUANTITY,
		PRICE
	}

	private static final NumberFormat nf = new DecimalFormat("#,##0.##");

	private RedQuantityBehavior behavior = RedQuantityBehavior.NONE;

	private long id = 0, quantity = 0, redQuantity = 0;
	private double price = 0;

	private double totalPrice = 0, totalVolume = 0;

	private int numberOfElements = -1;
	private int currentPosition = 0;

	public ItemListViewBinder() {
		super();
	}

	public ItemListViewBinder(RedQuantityBehavior behavior) {
		this();
		this.behavior = behavior;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		int viewId = view.getId();

		numberOfElements = cursor.getCount();
		currentPosition = cursor.getPosition();

		switch (viewId) {
			case R.id.item_name:
				String value = cursor.getString(columnIndex);
				initText(view, value);
				break;

			case R.id.item_icon:
				id = cursor.getLong(columnIndex);
				initIcon(view, id);
				break;

			case R.id.item_quantity:
				quantity = cursor.getLong(columnIndex);
				int ml = 0;
				int groupId = 0;
				if (cursor.getColumnIndex(Blueprint.ML) != -1) {
					ml = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.ML));
				}
				if (cursor.getColumnIndex(Item.GROUP_ID) != -1) {
					groupId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.GROUP_ID));
				}
				if (cursor.getColumnIndex(Prices.PRODUCE_PRICE) != -1) {
					price = cursor.getDouble(cursor.getColumnIndexOrThrow(Prices.PRODUCE_PRICE));
				}
				initQuantity(id, view, quantity, ml, groupId, price);
				break;

			case R.id.warn_icon:
				price = PricesUtil.getPriceOrNaN(cursor);
				initWarnIcon(view, price);
				break;

			case R.id.item_price:
			case R.id.ITEM_PRICE:
				addToTotalPrice(id, price, quantity, redQuantity);
				break;

			case R.id.item_volume:
			case R.id.ITEM_VOLUME:
				double volume = cursor.getDouble(columnIndex);
				addToTotalVolume(id, volume, quantity);
				break;

			default:
				throw new IllegalArgumentException("viewId : " + viewId);
		}

		return true;
	}

	@Override
	public boolean setViewValue(View view, Object data,
	                            String textRepresentation) {
		int viewId = view.getId();

		switch (viewId) {
			case R.id.item_name:
				String value = String.valueOf(data);
				initText(view, value);
				break;

			case R.id.item_icon:
				id = (Long) data;
				initIcon(view, id);
				break;

			case R.id.item_quantity:
				quantity = (Integer) data;
				initQuantity(id, view, quantity, 0, 0, 0);
				break;

			case R.id.warn_icon:
				price = (Double) data;
				initWarnIcon(view, price);
				break;

			case R.id.ITEM_PRICE:
				price = (Double) data;
				addToTotalPrice(id, price, quantity, redQuantity);
				break;

			case R.id.ITEM_VOLUME:
				addToTotalVolume(id, (Double) data, quantity);
				break;
			default:
				throw new IllegalArgumentException("viewId : " + viewId);
		}

		return true;
	}

	protected void initQuantity(long id, View view,
	                            long quantity, int ml, int groupId, double price) {
		TextView textView = (TextView) view;
		textView.setText("×" + nf.format(quantity));
		textView.setVisibility(View.VISIBLE);

		TextView itemRedQantityTextView =
				(TextView) ((ViewGroup) view.getParent()).findViewById(R.id.item_red_quantity);
		redQuantity = 0;
		switch (behavior) {
			case NONE:
				break;
			case WITH_REFINE_QUANTITY:
				redQuantity = -FormulaCalculator.calculateRefiningWaste(quantity, groupId);
				long taxe = -FormulaCalculator.calculateReprocessStationTake(quantity);
				String text = (redQuantity != 0 ? nf.format(redQuantity) + " " : "") + (taxe != 0 ? nf.format(taxe) : "");
				redQuantity += taxe;
				itemRedQantityTextView.setText(text);
				break;
			case PRICE:
				redQuantity = 1;
				PricesUtil.setPrice(itemRedQantityTextView, price, true);
				break;
			default:
				break;
		}

		if (itemRedQantityTextView != null) {
			if (redQuantity == 0) {
				itemRedQantityTextView.setVisibility(View.GONE);
			} else {
				itemRedQantityTextView.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void addToTotalPrice(long id, double price, long quantity, long redQuantity) {
		switch (behavior) {
			default:
				totalPrice += (price * quantity);
				break;
		}
	}

	protected void addToTotalVolume(long id, double volume, long quantity) {
		totalVolume += (volume * quantity);
	}

	public void reset() {
		totalPrice = 0;
		totalVolume = 0;
	}

	/**
	 * @return the totalPrice
	 */
	public double getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @return the totalVolume
	 */
	public double getTotalVolume() {
		return totalVolume;
	}

	/**
	 * @return the numberOfElements
	 */
	public int getNumberOfElements() {
		return numberOfElements;
	}

	/**
	 * @return the currentPosition
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

}
