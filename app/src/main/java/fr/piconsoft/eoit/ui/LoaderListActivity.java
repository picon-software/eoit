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

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.EOITConst;

import static android.app.LoaderManager.LoaderCallbacks;

/**
 * @author picon.software
 */
public abstract class LoaderListActivity extends BaseListActivity implements
		LoaderCallbacks<Cursor> {

	protected final static int LOADER_ID = EOITConst.getNextLoaderIdSequence();

	protected boolean firstLoad = true;

	/**
	 *
	 */
	public void initOrRestart() {
		if (firstLoad) {
			initLoader();
		} else {
			restartLoader();
		}
	}

	public void initLoader() {
		initLoader(LOADER_ID, null);
	}

	public void initLoader(Bundle args) {
		initLoader(LOADER_ID, args);
	}

	public void initLoader(int id, Bundle args) {
		getLoaderManager().initLoader(id, args, this);
		firstLoad = false;
	}

	public void restartLoader() {
		restartLoader(LOADER_ID, null);
	}

	public void restartLoader(Bundle args) {
		restartLoader(LOADER_ID, args);
	}

	public void restartLoader(int id, Bundle args) {
		getLoaderManager().initLoader(id, args, this);
		firstLoad = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		getLoaderManager().destroyLoader(LOADER_ID);
		firstLoad = true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		initOrRestart();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == LOADER_ID) {
			onLoadFinished(data);
		}
	}

	public void onLoadFinished(Cursor cursor) {
		getListAdapter().swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		getListAdapter().swapCursor(null);
	}

	@Override
	public SimpleCursorAdapter getListAdapter() {
		return (SimpleCursorAdapter) super.getListAdapter();
	}

	public void setListAdapter(SimpleCursorAdapter adapter) {
		super.setListAdapter(adapter);
	}
}
