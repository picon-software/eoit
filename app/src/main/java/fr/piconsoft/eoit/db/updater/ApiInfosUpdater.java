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
import fr.piconsoft.eoit.db.updater.bean.ApiKeyInfoState;
import fr.piconsoft.eoit.model.ApiKey;

/**
 * @author picon.software
 */
public class ApiInfosUpdater extends AbstractDatabaseUpdater<ApiKeyInfoState> {

	private static final String LOG_TAG = "ApiInfosUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_9_0.version;
	}

	@Override
	protected String getName() {
		return "apis";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT * FROM " +
				ApiKey.TABLE_NAME;
	}

	@Override
	protected ApiKeyInfoState buildStateFromCursor(Cursor cur) {
		return new ApiKeyInfoState(
				cur.getLong(cur.getColumnIndexOrThrow(ApiKey._ID)),
				cur.getString(cur.getColumnIndexOrThrow(ApiKey.V_CODE)),
				cur.getString(cur.getColumnIndexOrThrow(ApiKey.MASK)));
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		SQLiteStatement statement =
				db.compileStatement(
						"INSERT OR REPLACE INTO " +
								ApiKey.TABLE_NAME + "(" + ApiKey._ID + ", " + ApiKey.V_CODE + ", " + ApiKey.MASK +
								") VALUES (?,?,?);");

		for (ApiKeyInfoState bean : serializableBeans) {
			statement.clearBindings();
			statement.bindLong(1, bean.keyId);
			statement.bindString(2, bean.vCode);
			statement.bindString(3, bean.mask);

			statement.executeInsert();
		}

		statement.close();

		Log.i(LOG_TAG, serializableBeans.size() + " apis restored.");
	}

	@Override
	protected ApiKeyInfoState unserialize(String string) {
		return ApiKeyInfoState.unserialize(string);
	}
}
