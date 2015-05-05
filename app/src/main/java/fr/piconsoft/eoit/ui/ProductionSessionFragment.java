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
import android.support.v4.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ManufacturingPlans;
import fr.piconsoft.eoit.provider.ManufacturingContentProvider;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;

/**
 * @author picon.software
 */
public class ProductionSessionFragment extends ItemListFragment<SimpleCursorAdapter> {

    private String[] dataColumns = ManufacturingPlans.COLUMNS;
    private int[] viewIDs = {R.id.item_icon, R.id.item_name, R.id.item_quantity, R.id.warn_icon};
    private RedQuantityBehavior behavior= RedQuantityBehavior.NONE;

    public ProductionSessionFragment() {
        super();
		layoutId = R.layout.item_info_materials_premium;
		elementLayoutId = R.layout.item_row_small_premium;
		sectionTitleId = -1;

		initLoader();
    }

	@Override
    public void onLoaderReset(Loader<Cursor> loader) {
		clearCursor();
    }

	public void clearCursor() {
		if (getAdapter() != null)
			getAdapter().changeCursor(null);
	}

    @Override
    protected SimpleCursorAdapter adapteurInit() {
        return new SimpleCursorAdapter(
				getActivity(),
                elementLayoutId,
                null,
                dataColumns,
                viewIDs,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    protected Loader<Cursor> getCursorLoader(int id, Bundle args) {
        return new CursorLoader(
				context,
				ManufacturingContentProvider.SESSION_URI,
                dataColumns,
                null, null, null);
    }

    @Override
    protected void onLoadFinishedAdapteur(Cursor cursor,
                                          SimpleCursorAdapter adapter) {
        adapter.setViewBinder(new ItemListViewBinder(behavior));
        adapter.changeCursor(cursor);
    }
}
