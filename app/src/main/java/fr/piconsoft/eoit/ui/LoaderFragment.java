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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import fr.piconsoft.eoit.BaseConst;

/**
 * @author picon.software
 *
 */
public abstract class LoaderFragment<D> extends AutoReloadOnPauseFragment implements
		LoaderCallbacks<D> {

	protected int LOADER_ID = BaseConst.getNextLoaderIdSequence();

	protected boolean firstLoad = true, isAttached = false, initRequested = false;

	/**
	 *
	 */
	public void initOrRestart() {
		if(isAttached) {
			if(firstLoad) {
				initLoader();
			} else {
				restartLoader();
			}
		} else {
			initRequested = true;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		isAttached = true;

		if(initRequested) {
			initOrRestart();
			initRequested = false;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();

		isAttached = false;
	}

	@Override
	protected void onReload() {
		super.onReload();

		initOrRestart();
	}

	public void initLoader() {
		getLoaderManager().initLoader(LOADER_ID, null, this);
		firstLoad = false;
	}

	public void initLoader(Bundle args) {
		getLoaderManager().initLoader(LOADER_ID, args, this);
		firstLoad = false;
	}

	public void restartLoader() {
		getLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();

		getLoaderManager().destroyLoader(LOADER_ID);
		firstLoad = true;
	}

	@Override
	public Loader<D> onCreateLoader(int id, Bundle args) {
		if(id == LOADER_ID)
			return getCursorLoader(id, args);
		return null;
	}

	protected Loader<D> getCursorLoader(int id, Bundle args) { return null; }

	@Override
	public void onLoadFinished(Loader<D> loader, D data) {
		if(loader.getId() == LOADER_ID) {
			onLoadFinished(data);
		}
	}

	protected void onLoadFinished(D data) {}
}
