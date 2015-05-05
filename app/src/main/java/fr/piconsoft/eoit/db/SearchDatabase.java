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

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.db.util.DbCompatUtil;

/**
 * @author picon.software
 *
 */
public class SearchDatabase {

	private static final String FTS_VIRTUAL_TABLE = "FTSitem";

	//The columns we'll include in the dictionary table
	public static final String KEY_ITEM_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String KEY_ITEM_INFO = SearchManager.SUGGEST_COLUMN_TEXT_2;
	public static final String ITEM_ID = "item_id";

	/* Note that FTS3 does not support column constraints and thus, you cannot
	 * declare a primary key. However, "rowid" is automatically used as a unique
	 * identifier, so when making requests, we will use "_id" as an alias for "rowid"
	 */
	private static final String FTS_TABLE_CREATE =
			"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
			" USING fts3 (" +
			KEY_ITEM_NAME + ", " + KEY_ITEM_INFO + ", " + ITEM_ID + ", " + SearchManager.SUGGEST_COLUMN_ICON_1 + ");";

	private static final String FTS_TABLE_IMPORT =
			"INSERT INTO " + FTS_VIRTUAL_TABLE +
			" (" + KEY_ITEM_NAME + ", " + KEY_ITEM_INFO + ", " + ITEM_ID + ")" +
			"SELECT t.text, t_c.text || ' > ' || t_g.text, i._id \n" +
			"FROM item i\n" +
			"JOIN groups g ON i.group_id = g._id\n" +
			"  JOIN translation_key tk ON i._id = tk.key_id AND tk.tc_id = 8\n" +
			"  JOIN languages l ON tk.language_id = l._id AND l.current = 1\n" +
			"  JOIN translation t ON tk.tra_id = t.tra_id\n" +
			"  JOIN translation_key tk_g ON g._id = tk_g.key_id AND tk_g.tc_id = 7 AND tk_g.language_id = l._id\n" +
			"  JOIN translation t_g ON tk_g.tra_id = t_g.tra_id\n" +
			"  JOIN translation_key tk_c ON g.categorie_id = tk_c.key_id AND tk_c.tc_id = 6 AND tk_c.language_id = l._id\n" +
			"  JOIN translation t_c ON tk_c.tra_id = t_c.tra_id;";

	private final DatabaseHelper databaseHelper;
	private static final Map<String,String> mColumnMap = buildColumnMap();

	public SearchDatabase(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given to the
	 * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
	 * all columns, even if the value is the key. This allows the ContentProvider to request
	 * columns w/o the need to know real column names and create the alias itself.
	 */
	private static Map<String,String> buildColumnMap() {
		Map<String,String> map = new HashMap<>();
		map.put(KEY_ITEM_NAME, KEY_ITEM_NAME);
		map.put(KEY_ITEM_INFO, KEY_ITEM_INFO);
		map.put(ITEM_ID, ITEM_ID);
		map.put(BaseColumns._ID, "rowid AS " +
				BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, ITEM_ID + " AS " +
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
				SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		map.put(SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_ICON_1);
		return map;
	}

	/**
	 * Returns a Cursor over all words that match the given query
	 *
	 * @param query The string to search for
	 * @param columns The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public Cursor getWordMatches(String query, String[] columns, String sortOrder) {
		String selectionItem = KEY_ITEM_NAME + " MATCH ? ";
		String selectionCategory = KEY_ITEM_INFO + " MATCH ? ";
		String[] selectionArgs = new String[] {query+"*", query+"*"};

		return query(selectionItem, selectionCategory, selectionArgs, columns, sortOrder);
	}

	/**
	 * Performs a database query.
	 * @param selectionItem The selection clause
	 * @param selectionArgs Selection arguments for "?" components in the selection
	 * @param columns The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selectionItem, String selectionCategory, String[] selectionArgs, String[] columns, String sortOrder) {
		/* The SQLiteBuilder provides a map for all possible columns requested to
		 * actual columns in the database, creating a simple column alias mechanism
		 * by which the ContentProvider does not need to know the real column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(FTS_VIRTUAL_TABLE);
		builder.setProjectionMap(mColumnMap);

		if(!checkSearchTableExists()) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();

			db.execSQL(FTS_TABLE_CREATE);
			db.execSQL(FTS_TABLE_IMPORT);
		}

		String queryItemName = DbCompatUtil.buildQuery(builder, columns, selectionItem, null, null, null, null);
		String queryCategory = DbCompatUtil.buildQuery(builder, columns, selectionCategory, null, null, null, null);
		String unionQuery = builder.buildUnionQuery(new String[] {queryItemName, queryCategory}, sortOrder, null);

		Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(unionQuery, selectionArgs);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	private boolean checkSearchTableExists() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables("sqlite_master");

		Cursor cursor = builder.query(databaseHelper.getReadableDatabase(),
				new String[] {"name"}, "type='table' AND name='" + FTS_VIRTUAL_TABLE + "'", null, null, null, null);

		try {
			return cursor.getCount() == 1;
		} finally {
			cursor.close();
		}
	}
}
