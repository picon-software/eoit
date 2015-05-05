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
import fr.piconsoft.eoit.formula.FormulaCalculator;

/**
 * @author picon.software
 *
 */
public class AsteroidPriceCalculatorHelper extends PriceCalculatorHelperBase {

	@Override
	public double calculateItemPrice(int itemId, int groupId, int portionSize) {
		Cursor cursor = retreiveData(getContentResolver(), itemId);

		return calculatePrice(cursor, groupId, portionSize);
	}

	public static Cursor retreiveData(ContentResolver contentResolver, int itemId) {

		return contentResolver.query(
				ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE_PRICES_FOR_REFINE, itemId),
				new String[]{
						Item._ID,
						Item.NAME, Item.VOLUME, Item.CHOSEN_PRICE_ID,
						Item.GROUP_ID, Groups.CATEGORIE_ID,
						Prices.BUY_PRICE, Prices.OWN_PRICE,
						Prices.SELL_PRICE, Prices.PRODUCE_PRICE,
						ItemMaterials.QUANTITY},
				null,
				null,
				null);
	}

	private double calculatePrice(Cursor cursor, int groupId, int portionSize) {
		double produceUnitPrice = Double.MIN_VALUE;

		try {
			if (DbUtil.hasAtLeastOneRow(cursor)) {

				double totalPrice = 0;

				while (!cursor.isAfterLast()) {
					int itemQantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials.QUANTITY));
					double unitPrice = PricesUtil.getPriceOrNaN(cursor);

					long taxe = -FormulaCalculator.calculateReprocessStationTake(itemQantity);
					long neededValue = itemQantity - FormulaCalculator.calculateRefiningWaste(itemQantity, groupId) - taxe;
					totalPrice += neededValue * unitPrice;

					cursor.moveToNext();
				}

				produceUnitPrice = totalPrice / portionSize;
			}
		} finally {
			cursor.close();
		}

		return produceUnitPrice;
	}
}
