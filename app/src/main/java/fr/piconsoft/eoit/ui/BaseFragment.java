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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import fr.piconsoft.eoit.EOITApplication;

/**
 * @author picon.software
 */
public abstract class BaseFragment extends Fragment {

	protected boolean isCreated = false;
	protected Context context;
	private ObjectGraph applicationGraph;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isCreated = true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		context = activity.getApplicationContext();
		applicationGraph = ((EOITApplication) activity.getApplication()).getApplicationGraph();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
		isCreated = false;
	}

	@Override
	public void onDestroy() {
		applicationGraph = null;

		super.onDestroy();
	}
	
	protected void inject(Object object) {
		applicationGraph.inject(object);
	}
}
