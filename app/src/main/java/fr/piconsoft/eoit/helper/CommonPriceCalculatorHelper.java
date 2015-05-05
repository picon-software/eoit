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

import fr.piconsoft.eoit.util.DecryptorUtil;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 */
public class CommonPriceCalculatorHelper extends PriceCalculatorHelperBase {

	/* (non-Javadoc)
	 * @see fr.piconsoft.eoit.helper.PriceCalculatorHelperBase#calculateItemPrice(int)
	 */
	@Override
	public double calculateItemPrice(int itemId, int groupId, int portionSize) {
		Cursor cursorMaterial = retreiveData(getContentResolver(), itemId);
		double produceUnitPrice = calculateBlueprintPriceForOneRun(cursorMaterial);
		produceUnitPrice += calculateMaterialPrice(cursorMaterial);

		return produceUnitPrice;
	}

	public static Cursor retreiveData(ContentResolver contentResolver, int itemId) {

		return contentResolver.query(
				ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE_PRICES, itemId),
				new String[]{ItemMaterials._ID, ItemMaterials.ITEM_ID,
						ItemMaterials.MATERIAL_ITEM_ID, ItemMaterials.FLOAT_QUANTITY,
						Blueprint.ML,
						Blueprint.RESEARCH_PRICE, Blueprint.MAX_PRODUCTION_LIMIT,
						Blueprint.UNIT_PER_BATCH, Blueprint.DECRYPTOR_ID,
						Item.NAME,
						Item.CHOSEN_PRICE_ID, Item.VOLUME, Item.GROUP_ID, Groups.CATEGORIE_ID,
						Prices.BUY_PRICE, Prices.OWN_PRICE, Prices.PRODUCE_PRICE, Prices.SELL_PRICE},
				null,
				null,
				null);
	}

	private double calculateMaterialPrice(Cursor cursor) {
		double produceUnitPrice = 0;

		try {
			if (DbUtil.hasAtLeastOneRow(cursor)) {

				double totalPrice = 0;
				int unitPerBatch = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.UNIT_PER_BATCH));

				while (!cursor.isAfterLast()) {
					double itemQantity = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemMaterials.FLOAT_QUANTITY));
					double unitPrice = PricesUtil.getPriceOrNaN(cursor);

					totalPrice += Math.ceil(itemQantity) * unitPrice;

					cursor.moveToNext();
				}

				produceUnitPrice = totalPrice / unitPerBatch;
			}
		} finally {
			cursor.close();
		}

		return produceUnitPrice;
	}

	private double calculateBlueprintPriceForOneRun(Cursor cursor) {
		if (DbUtil.hasAtLeastOneRow(cursor) &&
				!cursor.isNull(cursor.getColumnIndexOrThrow(Blueprint.RESEARCH_PRICE))) {
			int numberOfRuns = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.MAX_PRODUCTION_LIMIT));
			double blueprintPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(Blueprint.RESEARCH_PRICE));
			int unitPerBatch = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.UNIT_PER_BATCH));
			int decryptorId = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.DECRYPTOR_ID));

			int calculatedUnitPerBatch =
					!Double.isNaN(blueprintPrice) && blueprintPrice > 0 ?
							(numberOfRuns + DecryptorUtil.getDecryptorBonusesOrDefault(decryptorId).maxRunModifier) * unitPerBatch
							: unitPerBatch;

			return blueprintPrice / calculatedUnitPerBatch;
		}

		return 0;
	}
}
