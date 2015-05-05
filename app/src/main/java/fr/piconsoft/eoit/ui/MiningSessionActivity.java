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

import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.CargoFragment.OnCargoChangedListener;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;

/**
 * @author picon.software
 */
public class MiningSessionActivity extends StrictModeActivity implements OnCargoChangedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mining_session);

		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		HashMap<Integer, Long> objectives = (HashMap<Integer, Long>) intent.getSerializableExtra("objectives");

		ObjectivesFragment objectivesFragment = (ObjectivesFragment) getSupportFragmentManager().findFragmentById(R.id.objectives_fragment);
		if (objectives != null) {
			objectivesFragment.setObjectives(objectives);
		}

		CargoFragment cargoFragment = (CargoFragment) getSupportFragmentManager().findFragmentById(R.id.cargo_fragment);
		cargoFragment.setListener(this);

	}

	/* (non-Javadoc)
	 * @see fr.piconsoft.eoit.ui.CargoFragment.OnCargoChangedListener#onCargoChanged(fr.piconsoft.eoit.ui.model.SparseItemBeanArray)
	 */
	@Override
	public void onCargoChanged(SparseItemBeanArray cargo) {
		ReprocessFragment reprocessFragment = (ReprocessFragment) getSupportFragmentManager().findFragmentById(R.id.reprocess_fragment);
		reprocessFragment.setAsteroidToReprocess(cargo);
	}
}