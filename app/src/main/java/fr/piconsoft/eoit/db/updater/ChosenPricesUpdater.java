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

package fr.piconsoft.eoit.db.updater;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.db.updater.bean.ItemState;

/**
 * @author picon.software
 */
public class ChosenPricesUpdater extends AbstractDatabaseUpdater<ItemState> {

	private static final String LOG_TAG = ChosenPricesUpdater.class.getSimpleName();

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return true;
	}

	@Override
	protected String getName() {
		return "chosen prices values";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT " +
				Item._ID +
				", " +
				Item.CHOSEN_PRICE_ID +
				" FROM " +
				Item.TABLE_NAME +
				" where " +
				Item.CHOSEN_PRICE_ID +
				" IS NOT NULL;";
	}

	@Override
	protected ItemState buildStateFromCursor(Cursor cursor) {
		int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
		int chosenPriceId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.CHOSEN_PRICE_ID));

		return new ItemState(itemId, chosenPriceId);
	}

	@Override
	protected void restore(SQLiteDatabase db, ItemState state) {
		db.execSQL("UPDATE " +
				Item.TABLE_NAME +
				" SET " +
				Item.CHOSEN_PRICE_ID +
				" = " + state.stateId +
				" WHERE " +
				Item._ID + " = " + state.itemId + ";");
	}

	@Override
	protected ItemState unserialize(String string) {
		return ItemState.unserialize(string);
	}
}
