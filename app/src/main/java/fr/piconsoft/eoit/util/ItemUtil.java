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

import android.database.Cursor;

import java.util.List;

import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterialsAndPrices;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;

/**
 * @author picon.software
 */
public final class ItemUtil {

	public static ItemBeanWithMaterials getItem(Cursor cursor, long numberOfItems) {
		ItemBeanWithMaterials item = new ItemBeanWithMaterials();

		if (cursor.getColumnIndex(ItemMaterials._ID) != -1) {
			item.id = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials._ID));
		}

		if (cursor.getColumnIndex(Item.NAME) != -1) {
			item.name = cursor.getString(cursor.getColumnIndexOrThrow(Item.NAME));
		}

		item.price = PricesUtil.getPriceOrNaN(cursor);

		if (cursor.getColumnIndex(Blueprint.UNIT_PER_BATCH) != -1) {
			item.parentBatchSize = cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.UNIT_PER_BATCH));
		} else if (cursor.getColumnIndex(ItemMaterials.PRODUCE_QUANTITY) != -1) {
			item.parentBatchSize = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials.PRODUCE_QUANTITY));
		}

		if (cursor.getColumnIndex(ItemMaterials.QUANTITY) != -1) {
			item.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemMaterials.QUANTITY))
					* numberOfItems;
		} else if (cursor.getColumnIndex(ItemMaterials.FLOAT_QUANTITY) != -1) {
			item.floatQuantity = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemMaterials.FLOAT_QUANTITY));
			item.quantity = (long) Math.ceil(item.floatQuantity * numberOfItems);
		}

		item.volume = cursor.getDouble(cursor.getColumnIndexOrThrow(Item.VOLUME));

		return item;
	}

	public static ItemBeanWithMaterialsAndPrices getItemWithPrices(Cursor cursor, long numberOfItems) {
		ItemBeanWithMaterialsAndPrices item = new ItemBeanWithMaterialsAndPrices(getItem(cursor, numberOfItems));

		PriceBean price = PricesUtil.getPriceBean(cursor);
		price.itemId = item.id;

		item.prices.add(price);

		return item;
	}

	public static String toIdsListString(Integer... ids) {
		String result = "";

		for (int id : ids) {
			result += " " + id;
		}

		return result;
	}

	public static String toIdsListString(List<Integer> ids) {
		String result = "";

		for (int id : ids) {
			result += " " + id;
		}

		return result;
	}
}
