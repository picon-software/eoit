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

/**
 * @author picon.software
 */
public abstract class AutoReloadOnPauseActivity extends BaseActivity {

	protected boolean wasPaused;

	@Override
	protected void onPause() {
		super.onPause();

		wasPaused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (wasPaused) {
			onReload();
			wasPaused = false;
		}
	}

	protected void onReload() {
	}
}
