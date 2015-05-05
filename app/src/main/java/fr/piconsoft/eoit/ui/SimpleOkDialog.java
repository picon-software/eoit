/*
 * Copyright (C) 2013 Picon software
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

import android.content.DialogInterface;

/**
 * @author picon.software
 *
 */
public class SimpleOkDialog<T> extends SimpleDialog {

	private OnDismissListener<T> listener;
	protected T returnObject;

	/**
	 *
	 */
	public SimpleOkDialog() {
		super();
	}

	public SimpleOkDialog(int titleId, int layoutId) {
		super(titleId, layoutId, android.R.string.ok, -1, -1);
	}

	public SimpleOkDialog(int titleId, int layoutId, OnDismissListener<T> listener) {
		this(titleId, layoutId);
		this.listener = listener;
	}

    @Override
    protected void onPositive(DialogInterface dialog, int whichButton) {
        onSubmit();
        if(listener != null) listener.onDismiss(returnObject);
        dialog.dismiss();
    }

    protected void onSubmit() { }

	public static interface OnDismissListener<T> {
		void onDismiss(T returnedObject);
	}
}
