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

package fr.piconsoft.eoit.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 *
 */
public class PlanetaryCommoditiesPriceCalculatorHelper extends PriceCalculatorHelperBase {

	/* (non-Javadoc)
	 * @see fr.piconsoft.eoit.helper.PriceCalculatorHelperBase#calculateItemPrice(int)
	 */
	@Override
	public double calculateItemPrice(int itemId, int groupId, int portionSize) {
		Cursor cursor = retreiveData(getContentResolver(), itemId);

		return calculatePrice(cursor);
	}

	public static Cursor retreiveData(ContentResolver contentResolver, int itemId) {

		return contentResolver.query(
				ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE_PRICES_FOR_PLANETARY, itemId),
				new String[]{ItemMaterials._ID, ItemMaterials.ITEM_ID,
						ItemMaterials.QUANTITY, ItemMaterials.PRODUCE_QUANTITY,
                        Item.NAME,
                        Item.CHOSEN_PRICE_ID, Item.VOLUME, Item.GROUP_ID, Groups.CATEGORIE_ID,
						Prices.BUY_PRICE, Prices.OWN_PRICE,
						Prices.PRODUCE_PRICE, Prices.SELL_PRICE},
				null,
				null,
				null);
	}

	private double calculatePrice(Cursor cursor) {
		double produceUnitPrice = Double.MIN_VALUE;

		if (DbUtil.hasAtLeastOneRow(cursor)) {

			double totalPrice = 0;
			int unitPerBatch = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials.PRODUCE_QUANTITY));

			try {
		        while (!cursor.isAfterLast()) {
		        	int itemQantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials.QUANTITY));
		        	double unitPrice = PricesUtil.getPriceOrNaN(cursor);

	        		totalPrice += itemQantity * unitPrice;

		        	cursor.moveToNext();
		        }

		        produceUnitPrice = totalPrice / unitPerBatch;

			} finally {
				cursor.close();
			}
		}

		return produceUnitPrice;
	}
}
