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
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;

/**
 * @author picon.software
 */
public class ObjectivesFragment extends EnhancedMaterialListFragment {

	private HashMap<Integer, Long> objectives = new HashMap<Integer, Long>();

	public ObjectivesFragment() {
		dataColumns = new String[]{Item._ID, Item.NAME, ItemMaterials.QUANTITY};
		viewIDs = new int[]{R.id.item_icon, R.id.item_name, R.id.item_quantity};
		sectionTitleId = R.string.mining_session_objectives;
		behavior = RedQuantityBehavior.NONE;
		layoutId = R.layout.item_info_materials_grid;
		elementLayoutId = R.layout.item_cell_compact;
		showIfCursorIsEmpty = false;
		numberOfColumn = 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragment = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState != null) {
			objectives = (HashMap<Integer, Long>) savedInstanceState.getSerializable("objectives");
		}

		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (objectives == null || objectives.isEmpty()) {
			getView().setVisibility(View.GONE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable("objectives", objectives);
	}

	public void setObjectives(HashMap<Integer, Long> objectives) {
		this.objectives = objectives;
		initOrRestart();
	}

	@Override
	public Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				context,
				Item.CONTENT_URI,
				new String[]{Item._ID, Item.NAME},
				QueryBuilderUtil.buildInClause(Item._ID, objectives.keySet()),
				null,
				null);
	}

	@Override
	protected void onLoadFinishedAdapteur(Cursor cursor, SimpleCursorAdapter adapter) {
		MatrixCursor objectivesItemCursur = getEmptyCursor();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			int id = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(Item.NAME));
			ItemBeanWithMaterials item = new ItemBeanWithMaterials();
			item.id = id;
			item.name = name;
			item.quantity = objectives.get(id);

			addRowToMatrixCursor(objectivesItemCursur, item);

			cursor.moveToNext();
		}

		adapter.setViewBinder(new MaterialsListViewBinderWithTotalPriceAndVolume());
		adapter.changeCursor(objectivesItemCursur);
	}
}
