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

import android.app.Dialog;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * @author picon.software
 */
public abstract class AbstractDialogRetrofitAsyncTask extends AbstractRetrofitAsyncTask<Void, Void, Void> {

	protected WeakReference<Dialog> mDialog;
	protected Context context;

	public AbstractDialogRetrofitAsyncTask(Dialog dialog) {
		mDialog = new WeakReference<>(dialog);
		context = dialog.getContext().getApplicationContext();
	}

	protected AbstractDialogRetrofitAsyncTask(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	protected Context getCurrentContext() {
		return context;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(mDialog != null && mDialog.get() != null) mDialog.get().dismiss();
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			if (mDialog != null && mDialog.get() != null && mDialog.get().isShowing()) {
				mDialog.get().dismiss();
			}
		} catch (final Exception e) {
			// Handle or log or ignore
		} finally {
			this.mDialog = null;
		}

	}
}
