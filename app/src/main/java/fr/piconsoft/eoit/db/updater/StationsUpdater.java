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
import android.util.Log;

import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.db.DatabaseVersions;
import fr.piconsoft.eoit.db.updater.bean.FavoriteState;
import fr.piconsoft.eoit.model.Station;

/**
 * @author picon.software
 */
public class StationsUpdater extends AbstractDatabaseUpdater<FavoriteState> {

	private static final String LOG_TAG = "StationsUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_4.version;
	}

	@Override
	protected String getName() {
		return "favorite stations";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT " +
				Station._ID +
				" FROM " +
				Station.TABLE_NAME +
				" where " +
				Station.FAVORITE +
				" = 1;";
	}

	@Override
	protected FavoriteState buildStateFromCursor(Cursor cursor) {
		return new FavoriteState(
				cursor.getInt(cursor.getColumnIndexOrThrow(Station._ID)));
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		// For first init if there was nothing selected.
		if (serializableBeans.isEmpty()) {
			db.execSQL("UPDATE " +
					Station.TABLE_NAME +
					" set " +
					Station.FAVORITE +
					" = 0, " +
					Station.ROLE +
					" = NULL;");
		} else {
			db.execSQL("UPDATE " +
					Station.TABLE_NAME +
					" SET " +
					Station.FAVORITE +
					" = 1 WHERE " +
					QueryBuilderUtil.buildInClause(Station._ID, serializableBeans) + ";");
		}

		Log.i(LOG_TAG, serializableBeans.size() + " favorite stations restored.");
	}

	@Override
	protected FavoriteState unserialize(String string) {
		return FavoriteState.unserialize(string);
	}
}
