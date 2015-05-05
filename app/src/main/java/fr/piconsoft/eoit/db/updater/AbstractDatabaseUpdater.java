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

import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.util.StringUtils;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.db.updater.bean.SerializableBean;

/**
 * @author picon.software
 */
public abstract class AbstractDatabaseUpdater<T extends SerializableBean> {

	private static final String LOG_TAG = AbstractDatabaseUpdater.class.getSimpleName();

	protected List<T> serializableBeans;

	protected abstract boolean isActive(SQLiteDatabase db);

	protected String getPreferenceName() {
		return "";
	}

	protected String getName() {
		return "";
	}

	protected String getBackupRequest() {
		return "";
	}

	protected T buildStateFromCursor(Cursor cursor) {
		return null;
	}

	protected void backup(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery(getBackupRequest(), null);
		serializableBeans = new ArrayList<>();

		if (DbUtil.hasAtLeastOneRow(cursor)) {

			while (!cursor.isAfterLast()) {
				serializableBeans.add(buildStateFromCursor(cursor));

				cursor.moveToNext();
			}
		}
		cursor.close();

		Log.i(LOG_TAG, serializableBeans.size() + " " + getName() + " backuped.");
	}

	protected void restore(SQLiteDatabase db) {
		for (T state : serializableBeans) {
			restore(db, state);
		}

		Log.i(LOG_TAG, serializableBeans.size() + " " + getName() + " restored.");
	}

	protected void restore(SQLiteDatabase db, T state) {

	}

	protected T unserialize(String string) {
		return null;
	}

	public String[] getBackupStrings() {
		List<String> strings = new ArrayList<>();

		for (T serializableBean : serializableBeans) {
			String string = serializableBean.serialize();
			if(StringUtils.isNotBlank(string)) {
				strings.add(string);
			}
		}

		return strings.toArray(new String[]{""});
	}

	public void restoreFromPreferences(String[] strings, SQLiteDatabase db) {
		clear();
		serializableBeans = new ArrayList<>();

		for (String string : strings) {
			serializableBeans.add(unserialize(string));
		}

		restore(db);
	}

	protected void clear() {
		if (serializableBeans != null) {
			serializableBeans.clear();
			serializableBeans = null;
		}
	}
}
