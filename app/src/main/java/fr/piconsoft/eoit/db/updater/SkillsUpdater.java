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
import fr.piconsoft.eoit.db.updater.bean.SkillState;
import fr.piconsoft.eoit.model.Skill;

/**
 * @author picon.software
 */
public class SkillsUpdater extends AbstractDatabaseUpdater<SkillState> {

	private static final String LOG_TAG = "ApiInfosUpdater";

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return db.getVersion() >= DatabaseVersions.V_2_9_0.version;
	}

	@Override
	protected String getName() {
		return "skills";
	}

	@Override
	protected String getBackupRequest() {
		return "SELECT * FROM " +
				Skill.TABLE_NAME;
	}

	@Override
	protected SkillState buildStateFromCursor(Cursor cur) {
		return new SkillState(
				cur.getInt(cur.getColumnIndexOrThrow(Skill.SKILL_ID)),
				cur.getLong(cur.getColumnIndexOrThrow(Skill.CHARACTER_ID)),
				cur.getShort(cur.getColumnIndexOrThrow(Skill.LEVEL))
		);
	}

	@Override
	protected void restore(SQLiteDatabase db) {

		SQLiteStatement statement =
				db.compileStatement(
						"INSERT OR REPLACE INTO " +
								Skill.TABLE_NAME + "(" + Skill.CHARACTER_ID + ", " + Skill.SKILL_ID + ", " + Skill.LEVEL +
								") VALUES (?,?,?);");

		for (SkillState bean : serializableBeans) {
			statement.clearBindings();
			statement.bindLong(1, bean.charId);
			statement.bindLong(2, bean.id);
			statement.bindLong(3, bean.level);

			statement.executeInsert();
		}

		statement.close();

		Log.i(LOG_TAG, serializableBeans.size() + " skills restored.");
	}

	@Override
	protected SkillState unserialize(String string) {
		return SkillState.unserialize(string);
	}
}
