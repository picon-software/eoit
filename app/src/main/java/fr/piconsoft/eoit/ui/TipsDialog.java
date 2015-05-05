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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import fr.piconsoft.eoit.R;

public class TipsDialog extends SimpleTipsDialog {

	private Class<? extends BaseActivity> intentClass;

	public TipsDialog() {
		super();
	}

	public TipsDialog(int titleId, int layoutId,
					  int actionStringId,
					  Context context, String preferenceName, int threshold) {
		super(titleId, layoutId, actionStringId, R.string.tips_later, R.string.tips_hide, context, preferenceName, threshold);
	}

	public TipsDialog(int titleId, int layoutId,
					  int actionStringId,
					  Context context, String preferenceName, int threshold,
					  Class<? extends BaseActivity> intentClass) {
		this(titleId, layoutId, actionStringId, context, preferenceName, threshold);
		this.intentClass = intentClass;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable("intentClass", intentClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null && intentClass == null) {
			intentClass = (Class<? extends BaseActivity>) savedInstanceState.getSerializable("intentClass");
		}
	}

	@Override
	protected void onPositive(DialogInterface dialog, int whichButton) {
		onAction(dialog, whichButton);
	}

	@Override
	protected void onNeutral(DialogInterface dialog, int whichButton) {
		delayNextTipsDisplay();
		dialog.dismiss();
	}

	@Override
	protected void onNegative(DialogInterface dialog, int whichButton) {
		hideTips();
		dialog.dismiss();
	}

	@SuppressWarnings("unused")
	protected void onAction(DialogInterface dialog, int whichButton) {
		openActivity(intentClass);
	}
}
