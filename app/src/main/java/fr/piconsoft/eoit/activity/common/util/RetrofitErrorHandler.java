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

package fr.piconsoft.eoit.activity.common.util;

import android.content.Context;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.net.SocketTimeoutException;

import fr.piconsoft.eoit.R;
import retrofit.RetrofitError;

/**
 * @author picon.software
 */
public final class RetrofitErrorHandler {
	private RetrofitErrorHandler() {
	}

	public static void handle(Context context, RetrofitError e) {
		if(context != null) {
			if (e.isNetworkError()) {
				if (e.getCause() instanceof SocketTimeoutException) {
					Toast.makeText(context, R.string.api_timeout_error, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, R.string.api_parsing_error, Toast.LENGTH_SHORT).show();
				Crashlytics.logException(e);
			}
		}
	}
}
