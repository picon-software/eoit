/*
 * Copyright (C) 2014 Picon software
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * @author picon.software
 */
public class SimpleDialog extends DialogFragment {

	protected Context context;
	private int layoutId, titleId, positiveStringId, neutralStringId, negativeStringId;

	/**
	 *
	 */
	public SimpleDialog() {
		super();
	}

	public SimpleDialog(int titleId, int layoutId, int positiveStringId,
	                    int neutralStringId, int negativeStringId) {
		this();
		this.layoutId = layoutId;
		this.titleId = titleId;
		this.positiveStringId = positiveStringId;
		this.neutralStringId = neutralStringId;
		this.negativeStringId = negativeStringId;
	}

	/* (non-Javadoc)
	 * @see android.app.DialogFragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("layoutId", layoutId);
		outState.putInt("titleId", titleId);
		outState.putInt("positiveStringId", positiveStringId);
		outState.putInt("neutralStringId", neutralStringId);
		outState.putInt("negativeStringId", negativeStringId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		context = activity.getApplicationContext();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (layoutId == 0 && titleId == 0 && savedInstanceState != null) {
			layoutId = savedInstanceState.getInt("layoutId");
			titleId = savedInstanceState.getInt("titleId");
			positiveStringId = savedInstanceState.getInt("positiveStringId");
			neutralStringId = savedInstanceState.getInt("neutralStringId");
			negativeStringId = savedInstanceState.getInt("negativeStringId");
		}

		View inflatedLayout = getActivity().getLayoutInflater().inflate(layoutId, null, false);

		onCreateSimpleDialog(inflatedLayout, savedInstanceState);

		AlertDialog.Builder builder =
				new AlertDialog
						.Builder(getActivity())
						.setView(inflatedLayout);

		if(titleId > 0) {
			builder.setTitle(titleId);
		}


		if (positiveStringId > 0) {
			builder.setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					onPositive(dialog, whichButton);
				}
			});
		}

		if (neutralStringId > 0) {
			builder.setNeutralButton(neutralStringId, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					onNeutral(dialog, whichButton);
				}
			});
		}

		if (negativeStringId > 0) {
			builder.setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					onNegative(dialog, whichButton);
				}
			});
		}

		return builder.create();
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	protected void onCreateSimpleDialog(View inflatedLayout, Bundle savedInstanceState) {
	}

	protected void onPositive(DialogInterface dialog, int whichButton) {
	}

	protected void onNegative(DialogInterface dialog, int whichButton) {
	}

	protected void onNeutral(DialogInterface dialog, int whichButton) {
	}
}
