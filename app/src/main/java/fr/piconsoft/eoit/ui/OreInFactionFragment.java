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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.AsteroidItemBean;

/**
 * @author picon.software
 */
public class OreInFactionFragment extends AutoReloadOnPauseFragment {

	private List<AsteroidItemBean> asteroids = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.mining_asteroid_compare, container, false);
	}

	private void initFragment(View view) {
		if (!asteroids.isEmpty() && view != null) {

			OreFragment[] oreFragments = new OreFragment[12];
			int cpt = 0;
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_10_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_10_2);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_9_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_7_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_4_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_2_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_1);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_2);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_3);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_4);
			oreFragments[cpt++] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_5);
			oreFragments[cpt] =
					(OreFragment) getChildFragmentManager().findFragmentById(R.id.ore_0_6);

			cpt = 0;
			for (OreFragment oreFragment : oreFragments) {
				if(asteroids.size() > cpt) {
					oreFragment.setAsteroid(asteroids.get(cpt++));
				} else {
					oreFragment.setAsteroid(null);
				}
			}
		}
	}

	public void setAsteroids(List<AsteroidItemBean> asteroids) {
		this.asteroids = asteroids;

		initFragment(getView());
	}
}
