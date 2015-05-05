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

import java.util.Comparator;
import java.util.TreeSet;

import fr.piconsoft.eoit.ui.model.ItemBean;

public class PriceRepartitionHelper {

	private PriceRepartitionHelper() {
	}

	public static TreeSet<ItemBean> getGreaterValues(TreeSet<ItemBean> items) {
		TreeSet<ItemBean> result = new TreeSet<>(new ItemBeanPriceComparator());
		TreeSet<ItemBean> set = new TreeSet<>(new ItemBeanPriceComparator());
		set.addAll(items);

		result.add(pollFirst(set));
		if (set.size() > 0) {
			result.add(pollFirst(set));
		}
		if (set.size() > 0) {
			result.add(pollFirst(set));
		}

		return result;
	}

	public static <E> E pollFirst(TreeSet<E> set) {
		if (set.isEmpty()) {
			return null;
		}

		E object = set.first();

		if (object != null) {
			set.remove(object);
		}

		return object;
	}

	public static class ItemBeanPriceComparator implements Comparator<ItemBean> {

		@Override
		public int compare(ItemBean item1, ItemBean item2) {
			return Long.valueOf(Math.round(item2.price * item2.quantity)).compareTo(Math.round(item1.price
					* item1.quantity));
		}
	}

}
