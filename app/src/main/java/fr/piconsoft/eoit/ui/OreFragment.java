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

import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.ui.model.AsteroidItemBean;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.util.IconUtil;

/**
 * @author picon.software
 */
public class OreFragment extends BaseFragment {

	private static final String[] DATA_COLUMNS = {"_id", "quantity"};
	private static final int[] VIEW_IDS = {R.id.item_icon, R.id.item_quantity};

	@InjectView(R.id.asteroid_layout) protected View layout;
	@InjectView(R.id.asteroid_icon) protected ImageView asteroidIcon;
	@InjectView(R.id.asteroid_name) protected TextView asteroidName;
	@InjectView(R.id.item_refine_list) protected GridView refineGrid;

	private AsteroidItemBean asteroid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.mining_asteroid_compare_item, container, false);
		ButterKnife.inject(this, view);

		return view;
	}

	public void setAsteroid(AsteroidItemBean asteroid) {
		this.asteroid = asteroid;

		initFragment();
	}

	private void initFragment() {
		if (asteroid != null) {
			layout.setVisibility(View.VISIBLE);

			IconUtil.initIcon(asteroid.id, asteroidIcon);
			asteroidName.setText(asteroid.name);

			MatrixCursor cursor = new MatrixCursor(DATA_COLUMNS);

			for (ItemBean item : asteroid.materials.values()) {
				DbUtil.addRowToMatrixCursor(cursor, item.id, item.quantity);
			}

			SimpleCursorAdapter adapter =
					new SimpleCursorAdapter(
							context,
							R.layout.item_cell_extra_compact_premium,
							cursor,
							DATA_COLUMNS,
							VIEW_IDS,
							SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(new ItemListViewBinder(ItemListViewBinder.RedQuantityBehavior.NONE));

			refineGrid.setAdapter(adapter);
			ItemListFragment.setListViewHeightBasedOnChildren(refineGrid, 4);
		} else {
			layout.setVisibility(View.GONE);
		}
	}
}
