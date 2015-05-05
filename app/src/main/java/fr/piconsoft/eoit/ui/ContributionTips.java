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
import android.content.Intent;
import android.net.Uri;

import fr.piconsoft.eoit.R;

public class ContributionTips extends SimpleTipsDialog {

	public ContributionTips() {
	}

	public ContributionTips(Context context) {
		super(R.string.contribution_title, R.layout.contribution_dialog,
				R.string.tips_hide,
				R.string.contribution_rate,
				R.string.tips_later,
				context, "contribution", 100);
	}

	@Override
	protected void onPositive(DialogInterface dialog, int whichButton) {
		hideTips();
	}

	@Override
	protected void onNegative(DialogInterface dialog, int whichButton) {
		delayNextTipsDisplay();
		dialog.dismiss();
	}

	@Override
	protected void onNeutral(DialogInterface dialog, int whichButton) {
		delayNextTipsDisplay();
		final Intent intent =
				new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));

		context.startActivity(intent);
	}
}
