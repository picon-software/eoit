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
import fr.piconsoft.eoit.model.Language;

import static fr.piconsoft.eoit.model.Language.ID_PATH_POSITION;

/**
 * @author picon.software
 */
public class LanguageContentProvider extends AbstractGenericDbContentProvider {

	private static final String LOG_TAG =
			buildLogTag(LanguageContentProvider.class);

	public static final String AUTHORITY =
			buildAuthority(LanguageContentProvider.class);

	//----------------------
	//----------------- Uris
	public static final Uri LANGUAGES_URI =
			Uri.parse(EOITConst.SCHEME + AUTHORITY + Language.PATH);

	public static final Uri CURRENT_LANGUAGE_URI =
			Uri.parse(EOITConst.SCHEME + AUTHORITY + Language.PATH + "/" + Language.CURRENT);

	//--------------------------------
	//----------------- Uri mapping id
	private static final int LANGUAGES = 1;
	private static final int LANGUAGE = 2;
	private static final int CURRENT_LANGUAGE = 3;

	//------------------------------
	//----------------- Static init.
	static {
		sUriMatcher.addURI(AUTHORITY, Language.TABLE_NAME, LANGUAGES);
		sUriMatcher.addURI(AUTHORITY, Language.TABLE_NAME + PARAM_PATH_FRAGMENT, LANGUAGE);
		sUriMatcher.addURI(AUTHORITY, Language.TABLE_NAME + "/" + Language.CURRENT, CURRENT_LANGUAGE);
	}

	@Override
	public boolean onCreate() {
		super.onCreate();

		registerQueryHandler(LANGUAGES, new QueryHandler() {
			@Override
			public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
				qb.setTables(Language.TABLE_NAME);
			}
		});

		registerQueryHandler(CURRENT_LANGUAGE, new QueryHandler() {
			@Override
			public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
				qb.setTables(Language.TABLE_NAME);
				qb.appendWhere(Language.CURRENT + " = 1");
			}
		});

		registerUpdateHandler(LANGUAGE, new UpdateHandler() {
			@Override
			public int onUpdate(SQLiteDatabase db, Uri uri, ContentValues values, String where, String[] whereArgs) {
				return db.update(Language.TABLE_NAME, values, Language._ID + "=" + uri.getPathSegments().get(ID_PATH_POSITION), null);
			}
		});

		registerUpdateHandler(LANGUAGES, new UpdateHandler() {
			@Override
			public int onUpdate(SQLiteDatabase db, Uri uri, ContentValues values, String where, String[] whereArgs) {
				return db.update(Language.TABLE_NAME, values, null, null);
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
