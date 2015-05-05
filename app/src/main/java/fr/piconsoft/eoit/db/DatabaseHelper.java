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

package fr.piconsoft.eoit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.ref.WeakReference;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.db.updater.DatabaseUpdaters;
import fr.piconsoft.eoit.db.util.DatabaseUpgradeCallbacks;
import fr.piconsoft.eoit.db.util.JsonDumpReader;

/**
 * @author picon.software
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "piconsoft.eoit.db";

	private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

	private final WeakReference<Context> mContext;
	private DatabaseUpgradeCallbacks callbacks;
	private static final int progressIncrement = 500;
	private static DatabaseHelper instance;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DatabaseVersions.current().version);
		mContext = new WeakReference<>(context);
	}

	public static DatabaseHelper getInstance(Context context) {
		if(instance == null) {
			instance = new DatabaseHelper(context);
		}

		return instance;
	}

	public DatabaseHelper attachCallBacks(DatabaseUpgradeCallbacks callbacks) {
		this.callbacks = callbacks;

		return this;
	}

	public void clearCallBacks() {
		this.callbacks = null;
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}

	private void create(SQLiteDatabase db, int... dumpResIds) {
		Log.v(LOG_TAG, "Loading dumpFile");

		if (callbacks != null) {
			callbacks.onUpgradeStart();
		}

		try {
			JsonDumpReader dumpReader = dumpResIds == null || dumpResIds.length == 0 ?
					new JsonDumpReader(mContext.get()) :
					new JsonDumpReader(mContext.get(), dumpResIds);

			if (callbacks != null) {
				callbacks.onInfoTextUpdate(R.string.inserting_dump);
				callbacks.onProgressBarReset(dumpReader.getLineCount());
			}

			int cpt = 0;
			db.beginTransaction();
			long time = System.currentTimeMillis();
			while (dumpReader.hasNextLine() && (callbacks == null || !callbacks.isCanceled())) {
				try {
					db.execSQL(dumpReader.nextLine());
				} catch (SQLiteException e) {
					Log.e(LOG_TAG, dumpReader.nextLine());
					throw e;
				}
				if ((cpt % progressIncrement) == 0 && callbacks != null) {
					callbacks.onProgressBarIncrementUpdate(progressIncrement);
				}
				cpt++;
			}
			time = System.currentTimeMillis() - time;
			db.setTransactionSuccessful();

			Log.v(LOG_TAG, "Loading dump done. " + (dumpReader.getLineCount() * 1000 / time) + " line/s");

		} catch (Exception e) {
			Log.wtf(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException("Error while loading dump.", e);
		} finally {
			if (db.inTransaction()) db.endTransaction();
		}

		if (callbacks != null) {
			callbacks.onUpgradeEnd();
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Logs that the database is being upgraded
		if(!DatabaseVersions.current().diffCanBeApplied(oldVersion)) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data.");
			DatabaseUpdaters.backup(db);

			create(db);

			DatabaseUpdaters.restore(db);
			DatabaseUpdaters.clear();
		} else {
			Log.i(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", applying diff.");

			create(db, DatabaseVersions.current().diffResId);
		}
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onDowngrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// for dev. use
		Log.v(LOG_TAG, "Downgrading database from version " + oldVersion + " to " + newVersion
				+ ".");
	}
}
