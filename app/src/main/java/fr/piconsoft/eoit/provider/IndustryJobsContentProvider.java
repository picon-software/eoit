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

package fr.piconsoft.eoit.provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.IndustryJobs;

import static fr.piconsoft.eoit.model.ColumnsNames.IndustryJobs.TABLE_NAME;

/**
 * @author picon.software
 */
public class IndustryJobsContentProvider extends AbstractGenericDbContentProvider {

	private static final String LOG_TAG =
			buildLogTag(IndustryJobsContentProvider.class);

	public static final String AUTHORITY =
			buildAuthority(IndustryJobsContentProvider.class);

	//----------------------
	//----------------- Uris
	public static final Uri JOBS_URI =
			Uri.parse(EOITConst.SCHEME + AUTHORITY + IndustryJobs.PATH);

	//--------------------------------
	//----------------- Uri mapping id
	private static final int JOBS = 1;

	//------------------------------
	//----------------- Static init.
	static {
		sUriMatcher.addURI(AUTHORITY, TABLE_NAME, JOBS);
	}

	@Override
	public boolean onCreate() {
		super.onCreate();

		registerQueryHandler(JOBS, new QueryHandler() {
			@Override
			public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
				qb.setTables(TABLE_NAME);
			}
		});

		registerInsertHandler(JOBS, new InsertHandler() {
			@Override
			public void onInsert(SQLiteDatabase db, ContentValues initialValues) {
				db.insertWithOnConflict(TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
			}
		});

		registerDeleteHandler(JOBS, new DeleteHandler() {
			@Override
			public int onDelete(SQLiteDatabase db, String selection, String[] selectionArgs) {
				return db.delete(TABLE_NAME, selection, selectionArgs);
			}
		});

		return true;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}
}
