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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.Const;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.AsteroidItemBean;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.model.AsteroidConstitution;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.util.QueryBuilderUtil;

/**
 * @author picon.software
 */
public class OreCompareActivity extends LoaderActivity<Cursor> {

	private SparseItemBeanArray asteroids = new SparseItemBeanArray();
	private OreInFactionFragment oreInFactionFragment;

	private static void loadMineral(AsteroidItemBean asteroid, Cursor cursor,
									String columnName, int mineralId) {
		ItemBeanWithMaterials itemBean = new ItemBeanWithMaterials();
		itemBean.id = mineralId;
		itemBean.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(columnName));

		if (itemBean.quantity > 0) {
			asteroid.addMineral(itemBean);
		}
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mining_asteroid_compare_faction);

		initLoader();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				this,
				AsteroidConstitution.CONTENT_URI,
				new String[]{AsteroidConstitution._ID, AsteroidConstitution.NAME,
						AsteroidConstitution.VOLUME, AsteroidConstitution.PORTION_SIZE,
						AsteroidConstitution.TRITANIUM_QUANTITY, AsteroidConstitution.PYERITE_QUANTITY,
						AsteroidConstitution.MEXALLON_QUANTITY, AsteroidConstitution.ISOGEN_QUANTITY,
						AsteroidConstitution.NOCXIUM_QUANTITY, AsteroidConstitution.ZYDRINE_QUANTITY,
						AsteroidConstitution.MEGACYTE_QUANTITY, AsteroidConstitution.MORPHITE_QUANTITY},
				QueryBuilderUtil.buildInClause(AsteroidConstitution._ID, EOITConst.Items.ASTEROID_IDS),
				null,
				null);
	}

	@Override
	public void onLoadFinished(Cursor cursor) {
		for (Cursor cur : new CursorIteratorWrapper(cursor)) {
			AsteroidItemBean asteroid = new AsteroidItemBean();
			asteroid.id = cur.getInt(cur.getColumnIndexOrThrow(AsteroidConstitution._ID));
			asteroid.name = cur.getString(cur.getColumnIndexOrThrow(AsteroidConstitution.NAME));
			asteroid.volume = cur.getInt(cur.getColumnIndexOrThrow(AsteroidConstitution.VOLUME));
			asteroid.batchSize = cur.getInt(cur.getColumnIndexOrThrow(AsteroidConstitution.PORTION_SIZE));
			loadMineral(asteroid, cur, AsteroidConstitution.TRITANIUM_QUANTITY, Const.Items.TRITANIUM);
			loadMineral(asteroid, cur, AsteroidConstitution.PYERITE_QUANTITY, Const.Items.PYERITE);
			loadMineral(asteroid, cur, AsteroidConstitution.MEXALLON_QUANTITY, Const.Items.MEXALLON);
			loadMineral(asteroid, cur, AsteroidConstitution.ISOGEN_QUANTITY, Const.Items.ISOGEN);
			loadMineral(asteroid, cur, AsteroidConstitution.NOCXIUM_QUANTITY, Const.Items.NOCXIUM);
			loadMineral(asteroid, cur, AsteroidConstitution.ZYDRINE_QUANTITY, Const.Items.ZYDRINE);
			loadMineral(asteroid, cur, AsteroidConstitution.MEGACYTE_QUANTITY, Const.Items.MEGACYTE);
			loadMineral(asteroid, cur, AsteroidConstitution.MORPHITE_QUANTITY, Const.Items.MORPHITE);

			asteroids.append(asteroid);
		}

		oreInFactionFragment =
				(OreInFactionFragment) getSupportFragmentManager().findFragmentById(R.id.faction_ores);

		oreInFactionFragment.setAsteroids(getAsteroidInFactionSpace(2));
		setActionBarSubtitle(R.string.gallente_space);
	}

	private List<AsteroidItemBean> getAsteroidInFactionSpace(int factionId) {
		List<AsteroidItemBean> asteroids = new ArrayList<>();

		for (int asteroidId : EOITConst.Asteroid.DISTRIBUTION[factionId][0]) {
			asteroids.add((AsteroidItemBean) this.asteroids.get(asteroidId));
		}

		return asteroids;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ore_compare_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.amarr_space_option:
				oreInFactionFragment.setAsteroids(
						getAsteroidInFactionSpace(
								Const.Territories.AMARR_SPACE));
				setActionBarSubtitle(R.string.amarr_space);
				break;
			case R.id.caldari_space_option:
				oreInFactionFragment.setAsteroids(
						getAsteroidInFactionSpace(
								Const.Territories.CALDARI_SPACE));
				setActionBarSubtitle(R.string.caldari_space);
				break;
			case R.id.gallente_space_option:
				oreInFactionFragment.setAsteroids(
						getAsteroidInFactionSpace(
								Const.Territories.GALLENTE_SPACE));
				setActionBarSubtitle(R.string.gallente_space);
				break;
			case R.id.minmatar_space_option:
				oreInFactionFragment.setAsteroids(
						getAsteroidInFactionSpace(
								Const.Territories.MINMATAR_SPACE));
				setActionBarSubtitle(R.string.minmatar_space);
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
