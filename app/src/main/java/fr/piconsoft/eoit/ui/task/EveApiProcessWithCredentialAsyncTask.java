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

/**
 *
 */
package fr.piconsoft.eoit.ui.task;

import android.app.Dialog;
import android.content.Context;

import fr.eo.api.credential.CharacterCredential;
import fr.piconsoft.eoit.helper.ContextHelper;

public abstract class EveApiProcessWithCredentialAsyncTask extends EveApiProcessAsyncTask {

	public EveApiProcessWithCredentialAsyncTask(Context context) {
		super(context);
	}

	public EveApiProcessWithCredentialAsyncTask(Dialog dialog) {
		super(dialog);
	}

	@Override
	protected final Void doInBackground(Void param) {

		CharacterCredential characterCredential = ContextHelper.getCurrentCredential(context);

		if (!characterCredential.isLevel5Char()) {
			doInBackground(characterCredential);
		} else {
			setNoCharSelected(true);
		}

		return null;
	}

	protected abstract void doInBackground(CharacterCredential characterCredential);
}
