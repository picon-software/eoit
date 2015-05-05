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

package fr.piconsoft.eoit.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.model.Language;
import fr.piconsoft.eoit.provider.LanguageContentProvider;
import fr.piconsoft.eoit.db.DatabaseHelper;

/**
 * @author picon.software
 */
public class LanguageSelectionActivity extends LoaderListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Creates the backing adapter for the ListView.
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_1,
				null,
				new String[]{Language.NAME},
				new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				LanguageContentProvider.LANGUAGES_URI,
				new String[]{Language._ID, Language.NAME, Language.CURRENT},
				null, null, null);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		database.execSQL("DROP TABLE IF EXISTS FTSitem");
		database.close();

		ContentValues values = new ContentValues();
		values.put(Language.CURRENT, 0);

		getContentResolver().update(
				LanguageContentProvider.LANGUAGES_URI,
				values, null, null);

		values = new ContentValues();
		values.put(Language.CURRENT, 1);

		getContentResolver().update(
				ContentUris.withAppendedId(LanguageContentProvider.LANGUAGES_URI, id),
				values, null, null);

		finish();
	}
}
