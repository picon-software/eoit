/*
 * Copyright (C) 2014 Picon software
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

import android.util.SparseArray;

import java.util.Set;
import java.util.TreeSet;

import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;

/**
 * @author picon.software
 *
 */
public final class ManufactureUtils {

	private ManufactureUtils() { }

	public static SparseItemBeanArray getProductionNeeds(SparseItemBeanArray baseProductionNeedItemMap,
			long numberOfRuns) {

		return getProductionNeeds(baseProductionNeedItemMap, new SparseItemBeanArray(), numberOfRuns);
	}

	public static SparseItemBeanArray getProductionNeeds(SparseItemBeanArray baseProductionNeedItemMap,
			SparseItemBeanArray stockItems, long numberOfRuns) {

		SparseItemBeanArray items = new SparseItemBeanArray(), stockItemsClone = stockItems.clone();

		if(baseProductionNeedItemMap != null) {
			items = getMaterials(
						baseProductionNeedItemMap.multiply(numberOfRuns),
						stockItemsClone);
		}

		return items;
	}

	private static SparseItemBeanArray getMaterials(SparseItemBeanArray items, SparseItemBeanArray stock) {
		SparseItemBeanArray result = new SparseItemBeanArray();

		for(ItemBeanWithMaterials item : items.values()) {
			ItemBeanWithMaterials stockItem = stock.get(item.id);
			if(stockItem != null) {
				if(stockItem.quantity < item.quantity) {
					item.quantity -= stockItem.quantity;
					stockItem.quantity = 0;
					result = includeItemOrMaterials(result, stock, item);
				} else if(stockItem.quantity > item.quantity){
					stockItem.quantity -= item.quantity;
				}
			} else {
				result = includeItemOrMaterials(result, stock, item);
			}
		}

		return result;
	}

	private static SparseItemBeanArray includeItemOrMaterials(SparseItemBeanArray items, SparseItemBeanArray stock, ItemBeanWithMaterials item) {
		SparseItemBeanArray result = items.clone();

		if(item.materials != null && !item.materials.isEmpty()) {
			result = result.union(getMaterials(item.materials.multiply(determineNumberOfBatch(item)), stock));
		} else {
			result.include(item);
		}

		return result;
	}

	public static long determineNumberOfBatch(ItemBeanWithMaterials item) {
		return (long) Math.ceil((double)item.quantity / item.batchSize);
	}

	public static SparseItemBeanArray getProductionNeedsForItem(SparseItemBeanArray items,
			ItemBeanWithMaterials item, long numberOfItems, SparseArray<Long> stockItems) {
		ItemBeanWithMaterials itemClone = item.clone();

		if(item.materials != null && !item.materials.isEmpty()) {
			long stockItemQuantity = 0;
			if(stockItems != null && stockItems.indexOfKey(item.id) > 0) {
				stockItemQuantity = stockItems.get(item.id);
			}

			for(ItemBeanWithMaterials itemMaterial : item.materials.values()) {
				ItemBeanWithMaterials itemMaterialClone = itemMaterial.clone();

				items = getProductionNeedsForItem(
						items,
						itemMaterialClone,
						stockItemQuantity > (itemClone.quantity * numberOfItems / itemMaterialClone.batchSize) ? 0 : itemClone.quantity * numberOfItems  / itemMaterialClone.batchSize - stockItemQuantity,
						stockItems);
			}
		} else {
			items = addItemToMap(items, itemClone, numberOfItems, stockItems);
		}

		return items;
	}

	public static SparseItemBeanArray addItemToMap(SparseItemBeanArray items,
			ItemBeanWithMaterials item, long numberOfItems, SparseArray<Long> stockItems) {

		long stockItemQuantity = 0;
		if(stockItems != null && stockItems.indexOfKey(item.id) > 0) {
			stockItemQuantity = stockItems.get(item.id);
		}

		if(items.containsKey(item.id)) {
			items.get(item.id).quantity += (item.quantity * numberOfItems);
			if(items.get(item.id).quantity < 0) {
				items.remove(item.id);
			}
		} else {
			item.quantity = ((item.quantity * numberOfItems) - stockItemQuantity);
			if(item.quantity > 0) {
				items.put(item.id, item);
			}
		}

		return items;
	}

	public static Set<Integer> getIds(ItemBeanWithMaterials item) {
		Set<Integer> ids = new TreeSet<Integer>();

		ids.add(item.id);
		if(item.materials != null) {
			for(ItemBeanWithMaterials itemMaterial : item.materials.values()) {
				ids.addAll(getIds(itemMaterial));
			}
		}

		return ids;
	}

}
