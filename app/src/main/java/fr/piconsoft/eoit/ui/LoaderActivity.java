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

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import fr.piconsoft.eoit.EOITConst;

/**
 * @author picon.software
 */
public abstract class LoaderActivity<D> extends StrictModeActivity implements
		LoaderCallbacks<D> {

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
		getSupportLoaderManager().initLoader(id, args, this);
		firstLoad = false;
	}

	public void restartLoader() {
		restartLoader(LOADER_ID, null);
	}

	public void restartLoader(Bundle args) {
		restartLoader(LOADER_ID, args);
	}

	public void restartLoader(int id, Bundle args) {
		getSupportLoaderManager().initLoader(id, args, this);
		firstLoad = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		getSupportLoaderManager().destroyLoader(LOADER_ID);
		firstLoad = true;
	}

	@Override
	public Loader<D> onCreateLoader(int id, Bundle args) {
		if (id == LOADER_ID)
			return getCursorLoader(id, args);
		return null;
	}

	protected Loader<D> getCursorLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<D> loader, D data) {
		if (loader.getId() == LOADER_ID) {
			onLoadFinished(data);
		}
	}

	protected void onLoadFinished(D data) {
	}
}
