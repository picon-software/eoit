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
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.db.updater.bean.CharacterState;

/**
 * @author picon.software
 */
public class CharactersUpdater extends AbstractDatabaseUpdater<CharacterState> {

	private static final String LOG_TAG = "ApiInfosUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_9_0.version;
	}

	@Override
	protected String getName() {
		return "characters";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT * FROM " +
				ColumnsNames.Character.TABLE_NAME;
	}

	@Override
	protected CharacterState buildStateFromCursor(Cursor cur) {
		return new CharacterState(
				cur.getLong(cur.getColumnIndexOrThrow(ColumnsNames.Character._ID)),
				cur.getString(cur.getColumnIndexOrThrow(ColumnsNames.Character.NAME)),
				cur.getLong(cur.getColumnIndexOrThrow(ColumnsNames.Character.KEY_ID)),
				cur.getLong(cur.getColumnIndexOrThrow(ColumnsNames.Character.CORP_ID)),
				cur.isNull(cur.getColumnIndexOrThrow(ColumnsNames.Character.ACTIVE)) ||
						cur.getInt(cur.getColumnIndexOrThrow(ColumnsNames.Character.ACTIVE)) == 1
		);
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		SQLiteStatement statement =
				db.compileStatement(
						"INSERT OR REPLACE INTO " +
								ColumnsNames.Character.TABLE_NAME + "(" + ColumnsNames.Character._ID + ", " + ColumnsNames.Character.KEY_ID + ", " + ColumnsNames.Character.NAME + ", " + ColumnsNames.Character.CORP_ID + ", " + ColumnsNames.Character.ACTIVE +
								") VALUES (?,?,?,?,?);");

		for (CharacterState bean : serializableBeans) {
			statement.clearBindings();
			statement.bindLong(1, bean.id);
			statement.bindLong(2, bean.keyId);
			statement.bindString(3, bean.name);
			statement.bindLong(4, bean.corpId);
			statement.bindLong(5, bean.active ? 1 : 0);

			statement.executeInsert();
		}

		statement.close();

		Log.i(LOG_TAG, serializableBeans.size() + " characters restored.");
	}

	@Override
	protected CharacterState unserialize(String string) {
		return CharacterState.unserialize(string);
	}
}
