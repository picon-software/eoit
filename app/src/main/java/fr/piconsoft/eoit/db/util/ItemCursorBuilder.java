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

package fr.piconsoft.eoit.db.util;

import android.database.MatrixCursor;

import java.util.Collection;

import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 */
public final class ItemCursorBuilder<T extends ItemBean> {

	public interface ListFilter {
		boolean filter(ItemBean itemBean, int count);
	}

	private ItemCursorBuilder() {
	}

	public static <T extends ItemBean> ItemCursorWrapper<T> init() {
		return new ItemCursorWrapper<T>();
	}

	private static ItemCursorWrapper use(MatrixCursor cursor) {
		return new ItemCursorWrapper(cursor);
	}

	public static class ItemCursorWrapper<T extends ItemBean> {

		private MatrixCursor cursor;

		private ItemCursorWrapper() {
			cursor = new MatrixCursor(new String[]{
					ColumnsNames.Item._ID, ColumnsNames.Item.NAME,
					ColumnsNames.ItemMaterials.QUANTITY, ColumnsNames.Item.VOLUME,
					ColumnsNames.Item.CHOSEN_PRICE_ID,
					ColumnsNames.Prices.BUY_PRICE,
					ColumnsNames.Prices.SELL_PRICE,
					ColumnsNames.Prices.OWN_PRICE,
					ColumnsNames.Prices.PRODUCE_PRICE,
					ColumnsNames.Blueprint.ML
			});
		}

		private ItemCursorWrapper(MatrixCursor cursor) {
			this.cursor = cursor;
		}

		public MatrixCursor build() {
			return cursor;
		}

		public ItemCursorWrapper addRow(ItemBean item) {
			DbUtil.addRowToMatrixCursor(cursor,
					item.id, item.name, item.quantity, item.volume, item.chosenPriceId,
					item.price, item.price, item.price, item.price, 0);

			return this;
		}

		public ItemCursorWrapper addAllRows(SparseItemBeanArray items) {
			for (ItemBeanWithMaterials item : items.values()) {
				addRow(item);
			}

			return this;
		}

		public ItemCursorWrapper addAllRows(Collection<T> items) {
			for (T item : items) {
				addRow(item);
			}

			return this;
		}

		public ItemCursorWrapper addAllRows(Collection<T> items, ListFilter filter) {
			return addAllRows(items, 0, filter);
		}

		public ItemCursorWrapper addAllRows(Collection<T> items, int ml, ListFilter filter) {
			for (T item : items) {
				if (filter.filter(item, cursor.getCount())) {
					addRow(item, ml);
				}
			}

			return this;
		}

		public ItemCursorWrapper addRow(T item, int ml) {
			DbUtil.addRowToMatrixCursor(cursor,
					item.id, item.name, item.quantity, item.volume, item.chosenPriceId,
					item.price, item.price, item.price, item.price, ml);

			return this;
		}

	}
}
