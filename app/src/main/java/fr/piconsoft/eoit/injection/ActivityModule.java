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

package fr.piconsoft.eoit.injection;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import dagger.Module;
import dagger.Provides;
import fr.piconsoft.eoit.ui.ItemInfoActivity;

@Module(library = true,
		//includes = FragmentsModule.class,
		injects = ItemInfoActivity.class)
public class ActivityModule {
	private final FragmentActivity activity;

	public ActivityModule(FragmentActivity activity) {
		this.activity = activity;
	}

	@Provides
	public FragmentManager provideFragmentManager() {
		return activity.getSupportFragmentManager();
	}
}
