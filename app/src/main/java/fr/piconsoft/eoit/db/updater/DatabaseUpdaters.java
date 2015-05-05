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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author picon.software
 *
 */
public final class DatabaseUpdaters {

	private static final String LOG_TAG = DatabaseUpdaters.class.getSimpleName();
	private static List<AbstractDatabaseUpdater> updaters = new ArrayList<>();

	static {
		updaters.add(new FavoriteItemsUpdater());
		updaters.add(new ChosenPricesUpdater());
		updaters.add(new BlueprintsUpdater());
		updaters.add(new StationsUpdater());
		updaters.add(new StationsRoleUpdater());
		updaters.add(new PricesUpdater());
		updaters.add(new ApiInfosUpdater());
		updaters.add(new CorporationsUpdater());
		updaters.add(new CharactersUpdater());
		updaters.add(new SkillsUpdater());
	}

	private DatabaseUpdaters() { }

	public static void backup(SQLiteDatabase db) {
		Log.i(LOG_TAG, "Starting backup.");
		for(AbstractDatabaseUpdater updater : updaters) {
			if(updater.isActive(db))
				updater.backup(db);
		}
	}

	public static void restore(SQLiteDatabase db) {
		Log.i(LOG_TAG, "Starting restore.");
		for(AbstractDatabaseUpdater updater : updaters) {
			if(updater.isActive(db))
				updater.restore(db);
		}
	}

	public static void clear() {
		for(AbstractDatabaseUpdater updater : updaters) {
			updater.clear();
		}
	}
}
