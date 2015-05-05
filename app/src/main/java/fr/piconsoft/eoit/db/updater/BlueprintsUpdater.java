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

import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.db.updater.bean.BlueprintState;

/**
 * @author picon.software
 */
public class BlueprintsUpdater extends AbstractDatabaseUpdater<BlueprintState> {

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return true;
	}

	@Override
	protected String getName() {
		return "blueprint";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT " +
				Blueprint._ID + ", " +
				Blueprint.ML + ", " +
				Blueprint.PL + ", " +
				//Blueprint.INVENTION_ITEM_META_LEVEL + ", " +
				Blueprint.RESEARCH_PRICE + ", " +
				Blueprint.DECRYPTOR_ID +
				" FROM " +
				Blueprint.TABLE_NAME +
				" where " +
				Blueprint.ML +
				" is not null;";
	}

	@Override
	protected BlueprintState buildStateFromCursor(Cursor cursor) {
		return new BlueprintState(
				cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint._ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.ML)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.PL)),
				0,//cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.INVENTION_ITEM_META_LEVEL)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Blueprint.DECRYPTOR_ID)),
				cursor.getDouble(cursor.getColumnIndexOrThrow(Blueprint.RESEARCH_PRICE)));
	}

	@Override
	protected void restore(SQLiteDatabase db, BlueprintState state) {
		db.execSQL("UPDATE " +
				Blueprint.TABLE_NAME +
				" SET " +
				Blueprint.ML +
				" = " + state.ml + ", " +
				Blueprint.PL +
				" = " + state.pl + ", " +
				Blueprint.RESEARCH_PRICE +
				" = " + state.researchPrice + ", " +
				Blueprint.DECRYPTOR_ID +
				" = " + state.decryptorId +
				" WHERE " +
				Blueprint._ID + " = " + state.id + ";");
	}

	@Override
	protected BlueprintState unserialize(String string) {
		return BlueprintState.unserialize(string);
	}
}
