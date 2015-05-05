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

package fr.piconsoft.eoit.ui.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import retrofit.RetrofitError;

import static fr.piconsoft.eoit.activity.common.util.RetrofitErrorHandler.handle;

/**
 * @author picon.software
 */
public abstract class AbstractRetrofitAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private RetrofitError error;

	protected abstract Context getCurrentContext();

	@SafeVarargs
	@Override
	protected final Result doInBackground(Params... params) {
		try {
			if(params != null && params.length == 1) {
				return doInBackground(params[0]);
			} else {
				return doInBackground((Params) null);
			}
		} catch (RetrofitError e) {
			error = e;
			cancel(true);
		} catch (Exception e) {
			Log.w("RetrofitProcessAsyncTask", e);
			Crashlytics.logException(e);
		}

		return null;
	}

	protected abstract Result doInBackground(Params param);

	@Override
	protected void onCancelled() {
		if(error != null) {
			handle(getCurrentContext(), error);
		}
	}
}
