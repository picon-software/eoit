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

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.Histo;

/**
 * @author picon.software
 */
public class HistoContentProvider extends AbstractGenericDbContentProvider {

	private static final String LOG_TAG =
			buildLogTag(HistoContentProvider.class);

	public static final String AUTHORITY =
			buildAuthority(HistoContentProvider.class);

	//----------------------
	//----------------- Uris
	public static final Uri HISTO_URI =
			Uri.parse(EOITConst.SCHEME + AUTHORITY + Histo.PATH);

	//--------------------------------
	//----------------- Uri mapping id
	private static final int HISTO = 1;

	//---------------------------------
	//----------------- Projection maps
	private static Map<String, String> projectionMapHisto = new HashMap<String, String>();

	//------------------------------
	//----------------- Static init.
	static {
		sUriMatcher.addURI(AUTHORITY, Histo.TABLE_NAME, HISTO);

		addProjectionMapping(projectionMapHisto, "h.", Histo.COLUMNS);
	}

	@Override
	public boolean onCreate() {
		super.onCreate();

        registerQueryHandler(HISTO, new QueryHandler() {
            @Override
            public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
                qb.setTables(Histo.TABLE_NAME + " h");
                qb.setProjectionMap(projectionMapHisto);
            }
        });

        registerInsertHandler(HISTO, new InsertHandler() {
            @Override
            public void onInsert(SQLiteDatabase db, ContentValues initialValues) {
                db.insertWithOnConflict(Histo.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        });

		registerDeleteHandler(HISTO, new DeleteHandler() {
			@Override
			public int onDelete(SQLiteDatabase db, String selection, String[] selectionArgs) {
				return db.delete(Histo.TABLE_NAME, selection, selectionArgs);
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
