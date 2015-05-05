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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.db.util.DbCompatUtil;
import fr.piconsoft.eoit.model.ApiKey;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.Corporation;
import fr.piconsoft.eoit.model.Skill;

/**
 * @author picon.software
 */
public class EveOnlineApiContentProvider extends AbstractContentProvider {

	private static final String LOG_TAG = "EveOnlineApiContentProvider";
	public static final String AUTHORITY = "fr.piconsoft.eoit.provider.EveOnlineApiContentProvider";

	//db
	private static final int CHARACTER_ID = 0;
	private static final int API_KEY_ID = 2;
	private static final int CORP_ID = 3;
	private static final int SKILL_ID = 4;
	private static final int CHARACTERS_ID = 7;

	private static Map<String, String> charactersProjectionMap = new HashMap<>();
	private static Map<String, String> skillsProjectionMap = new HashMap<>();
	private static Map<String, String> apiKeyProjectionMap = new HashMap<>();

	static {
		//db
		sUriMatcher.addURI(AUTHORITY, Character.TABLE_NAME + PARAM_PATH_FRAGMENT, CHARACTER_ID);
		sUriMatcher.addURI(AUTHORITY, Character.TABLE_NAME, CHARACTERS_ID);
		sUriMatcher.addURI(AUTHORITY, ApiKey.TABLE_NAME, API_KEY_ID);
		sUriMatcher.addURI(AUTHORITY, ColumnsNames.Corporation.TABLE_NAME, CORP_ID);
		sUriMatcher.addURI(AUTHORITY, ColumnsNames.Skill.TABLE_NAME, SKILL_ID);

		charactersProjectionMap.put(Character._ID, "c." + Character._ID);
		charactersProjectionMap.put(Character._COUNT, "c." + Character._COUNT);
		charactersProjectionMap.put(Character.NAME, "c." + Character.NAME);
		charactersProjectionMap.put(Character.ACTIVE, "c." + Character.ACTIVE);
		charactersProjectionMap.put(Character.CORP_ID, "c." + Character.CORP_ID);
		charactersProjectionMap.put(Character.KEY_ID, "c." + Character.KEY_ID);
		charactersProjectionMap.put(ColumnsNames.Corporation.NAME_ALIAS, "co." + ColumnsNames.Corporation.NAME);
		charactersProjectionMap.put(ApiKey.V_CODE, "a." + ApiKey.V_CODE);

		skillsProjectionMap.put(Skill._ID, "s." + Skill._ID);
		skillsProjectionMap.put(Skill._COUNT, "s." + Skill._COUNT);
		skillsProjectionMap.put(Skill.CHARACTER_ID, "s." + Skill.CHARACTER_ID);
		skillsProjectionMap.put(Skill.SKILL_ID, "s." + Skill.SKILL_ID);
		skillsProjectionMap.put(Skill.LEVEL, "s." + Skill.LEVEL);

		apiKeyProjectionMap.put(ApiKey._ID, "a." + ApiKey._ID);
		apiKeyProjectionMap.put(ApiKey.V_CODE, "a." + ApiKey.V_CODE);
		apiKeyProjectionMap.put(ApiKey.MASK, "a." + ApiKey.MASK);

	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (sUriMatcher.match(uri)) {
			case API_KEY_ID:
				db.insertWithOnConflict(ApiKey.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

				return uri;

			case CHARACTERS_ID:
				db.insertWithOnConflict(Character.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

				return uri;

			case CORP_ID:
				db.insertWithOnConflict(ColumnsNames.Corporation.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

				return uri;

			case SKILL_ID:
				db.insertWithOnConflict(ColumnsNames.Skill.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

				return uri;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		String tableName;

		switch (sUriMatcher.match(uri)) {

			case API_KEY_ID:
				tableName = ApiKey.TABLE_NAME;
				break;

			case CHARACTERS_ID:
				tableName = Character.TABLE_NAME;
				break;

			case CORP_ID:
				tableName = Corporation.TABLE_NAME;
				break;

			case SKILL_ID:
				tableName = Skill.TABLE_NAME;
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return db.delete(tableName, selection, selectionArgs);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setDistinct(true);

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		switch (sUriMatcher.match(uri)) {

			// db
			case CHARACTER_ID:
				qb.appendWhere("c." + Character._ID +
						" = " +
						uri.getPathSegments().get(Character.CHARCTER_ID_PATH_POSITION));

			case CHARACTERS_ID:
				qb.setTables(
						QueryBuilderUtil.table(Character.TABLE_NAME + " c")
								.join(ApiKey.TABLE_NAME + " a")
								.on("c." + Character.KEY_ID, "a." + ApiKey._ID)
								.join(ColumnsNames.Corporation.TABLE_NAME + " co")
								.on("c." + Character.CORP_ID, "co." + ColumnsNames.Corporation._ID).toString()
				);
				qb.setProjectionMap(charactersProjectionMap);
				break;

			case SKILL_ID:
				qb.setTables(Skill.TABLE_NAME + " s");
				qb.setProjectionMap(skillsProjectionMap);
				break;

			case API_KEY_ID:
				qb.setTables(ApiKey.TABLE_NAME + " a");
				qb.setProjectionMap(apiKeyProjectionMap);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String query = DbCompatUtil.buildQuery(qb, projection, selection, null, null, sortOrder, null);
		if (BuildConfig.DEBUG) Log.v(LOG_TAG,
				"Launching query : " + query);
		Cursor c;
		long time = System.currentTimeMillis();
		c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
		);
		if (BuildConfig.DEBUG)
			Log.v(LOG_TAG, "Query executed in : " + (System.currentTimeMillis() - time) + "ms");
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		throw new UnsupportedOperationException();
	}
}
