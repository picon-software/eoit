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

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.db.DatabaseVersions;
import fr.piconsoft.eoit.db.updater.bean.ItemState;
import fr.piconsoft.eoit.model.Station;

/**
 * @author picon.software
 */
public class StationsRoleUpdater extends AbstractDatabaseUpdater<ItemState> {

	private static final String LOG_TAG = "StationsUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_4.version;
	}

	@Override
	protected String getName() {
		return "station roles";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT " +
				Station._ID + ", " + Station.ROLE +
				" FROM " +
				Station.TABLE_NAME +
				" where " +
				Station.ROLE +
				" IS NOT NULL;";
	}

	@Override
	protected ItemState buildStateFromCursor(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndexOrThrow(Station._ID));
		int role = cursor.getInt(cursor.getColumnIndexOrThrow(Station.ROLE));

		return new ItemState(id, role);
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		db.execSQL("UPDATE " +
				Station.TABLE_NAME +
				" set " +
				Station.ROLE +
				" = NULL;");

		if (serializableBeans.isEmpty()) {
			db.execSQL("UPDATE " +
					Station.TABLE_NAME +
					" set " +
					Station.ROLE +
					" = " + EOITConst.Stations.BOTH_ROLES +
					" where " +
					Station._ID +
					" = " +
					EOITConst.Stations.JITA_STATION_ID + ";");
		}

		for (ItemState serializableBean : serializableBeans) {
			db.execSQL("UPDATE " +
					Station.TABLE_NAME +
					" SET " +
					Station.ROLE +
					" = " + serializableBean.stateId +
					" WHERE " +
					Station._ID + " = " + serializableBean.itemId + ";");
		}

		Log.i(LOG_TAG, serializableBeans.size() + " station roles restored.");
	}

	@Override
	protected ItemState unserialize(String string) {
		return ItemState.unserialize(string);
	}
}
