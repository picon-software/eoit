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

import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.Prices;

/**
 * @author picon.software
 */
public class ItemMaterialListFragment extends ItemListFragment<SimpleCursorAdapter> {

    protected String[] dataColumns = {
            ItemMaterials.MATERIAL_ITEM_ID, Item.NAME,
            ItemMaterials.QUANTITY, Item.CHOSEN_PRICE_ID};
    protected int[] viewIDs = {R.id.item_icon, R.id.item_name, R.id.item_quantity, R.id.warn_icon};
    protected RedQuantityBehavior behavior;

    protected long itemId;
    protected long categoryId;

    public ItemMaterialListFragment() {
        super();
        sectionTitleId = -1;
    }

    /**
     * @return the itemId
     */
    public long getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(long itemId) {
        this.itemId = itemId;
        initOrRestart();
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
		behavior = RedQuantityBehavior.NONE;
	}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
                ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE_PRICES, itemId),
                new String[]{
                        Item._ID, ItemMaterials.MATERIAL_ITEM_ID,
                        Item.NAME, Item.CHOSEN_PRICE_ID,
                        Item.GROUP_ID, Prices.BUY_PRICE,
                        Prices.OWN_PRICE, Prices.SELL_PRICE, Prices.PRODUCE_PRICE,
                        ItemMaterials.QUANTITY, Blueprint.ML},
                null,
                null, Item._ID + " ASC");
    }

    @Override
    protected void onLoadFinishedAdapteur(Cursor cursor,
                                          SimpleCursorAdapter adapter) {
        adapter.setViewBinder(new ItemListViewBinder(behavior));
        adapter.changeCursor(cursor);
    }
}
