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
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import fr.piconsoft.eoit.db.DatabaseVersions;
import fr.piconsoft.eoit.db.updater.bean.CorporationState;
import fr.piconsoft.eoit.model.Corporation;

/**
 * @author picon.software
 */
public class CorporationsUpdater extends AbstractDatabaseUpdater<CorporationState> {

	private static final String LOG_TAG = "ApiInfosUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_9_0.version;
	}

	@Override
	protected String getName() {
		return "corporations";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT * FROM " +
				Corporation.TABLE_NAME;
	}

	@Override
	protected CorporationState buildStateFromCursor(Cursor cur) {
		return new CorporationState(cur.getLong(cur.getColumnIndexOrThrow(Corporation._ID)),
				cur.getString(cur.getColumnIndexOrThrow(Corporation.NAME)),
				cur.getLong(cur.getColumnIndexOrThrow(Corporation.KEY_ID)));
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		SQLiteStatement statement =
				db.compileStatement(
						"INSERT OR REPLACE INTO " +
								Corporation.TABLE_NAME + "(" + Corporation._ID + ", " + Corporation.KEY_ID + ", " + Corporation.NAME +
								") VALUES (?,?,?);");

		for (CorporationState bean : serializableBeans) {
			statement.clearBindings();
			statement.bindLong(1, bean.id);
			statement.bindLong(2, bean.keyId);
			statement.bindString(3, bean.name);

			statement.executeInsert();
		}

		statement.close();

		Log.i(LOG_TAG, serializableBeans.size() + " corporations restored.");

	}

	@Override
	protected CorporationState unserialize(String string) {
		return CorporationState.unserialize(string);
	}
}
