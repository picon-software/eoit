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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.ui.listener.ItemOnItemClickListener;

import static fr.piconsoft.eoit.util.ViewUtil.hide;
import static fr.piconsoft.eoit.util.ViewUtil.show;

/**
 * @author picon.software
 */
public abstract class ItemListFragment<A extends ListAdapter> extends LoaderFragment<Cursor> {

	protected int layoutId = R.layout.item_info_materials;
	protected int listViewId = R.id.item_materials_list;
	protected int sectionTitleId = R.string.manufacture_section_label;
	protected int numberOfColumn = 1;
	protected int elementLayoutId = R.layout.item_row_small;
	protected boolean showIfCursorIsEmpty = false;
	protected boolean itemclickable = true;

	@InjectView(R.id.section_title) @Optional protected TextView sectionTitle;
	@InjectView(R.id.section_title_line) @Optional protected View sectionTitleLine;

	private A adapter;

	/**
	 * @return the adapter
	 */
	public A getAdapter() {
		return adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragment = inflater.inflate(layoutId, container, false);

		ButterKnife.inject(this, fragment);

		adapter = adapteurInit();

		if (sectionTitle != null && sectionTitleId > 0) {
			sectionTitle.setText(sectionTitleId);
		} else {
			hide(sectionTitle);
			hide(sectionTitleLine);
		}

		refreshAdapteur(adapter, fragment);
		AbsListView itemMaterialsList = getListView(fragment);

		if (itemclickable)
			itemMaterialsList.setOnItemClickListener(new ItemOnItemClickListener(getActivity()));

		return fragment;
	}

	protected AbsListView getListView(View view) {
		return (AbsListView) view.findViewById(listViewId);
	}

	protected AbsListView getListView() {
		return getListView(getView());
	}

	protected abstract A adapteurInit();

	protected void refreshAdapteur(A adapter) {
		refreshAdapteur(adapter, getView());
	}

	@SuppressWarnings("RedundantCast")
	protected void refreshAdapteur(A adapter, View view) {
		this.adapter = adapter;
		AbsListView itemMaterialsList = getListView(view);
		if (itemMaterialsList instanceof ListView) {
			((ListView) itemMaterialsList).setAdapter(adapter);
		} else if (itemMaterialsList instanceof GridView) {
			((GridView) itemMaterialsList).setAdapter(adapter);
		}

	}

	@Override
	public void onLoadFinished(Cursor cursor) {
		if ((DbUtil.hasAtLeastOneRow(cursor)) || showIfCursorIsEmpty) {
			onLoadFinishedAdapteur(cursor, adapter);
			resizeListView();
			show(getView());
		} else {
			hide(getView());
		}
	}

	protected abstract void onLoadFinishedAdapteur(Cursor cursor, A adapter);

	protected void resizeListView() {
		setListViewHeightBasedOnChildren(getListView(), numberOfColumn);
	}

	public static void setListViewHeightBasedOnChildren(AbsListView listView, int numberOfColumn) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			assert listItem != null;
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		if (numberOfColumn > 1 && listAdapter.getCount() > 0) {
			int rowHeight = totalHeight / listAdapter.getCount();
			int numberOfRow = (int) Math.ceil(listAdapter.getCount() / (float) numberOfColumn);
			totalHeight = rowHeight * numberOfRow;
		}

		assert params != null;
		params.height = totalHeight + listView.getListPaddingTop() + listView.getListPaddingBottom();
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	/**
	 * @deprecated use {@link fr.piconsoft.eoit.db.util.ItemCursorBuilder}
	 */
	@Deprecated
	public static MatrixCursor getEmptyCursor() {

		return new MatrixCursor(new String[]{
				Item._ID, Item.NAME,
				ItemMaterials.QUANTITY, Item.VOLUME,
				Item.CHOSEN_PRICE_ID,
				Prices.BUY_PRICE,
				Prices.SELL_PRICE,
				Prices.OWN_PRICE,
				Prices.PRODUCE_PRICE,
				Blueprint.ML
		});
	}

	/**
	 * @deprecated use {@link fr.piconsoft.eoit.db.util.ItemCursorBuilder}
	 */
	@Deprecated
	public static void addRowToMatrixCursor(MatrixCursor cursor, final ItemBean item) {
		DbUtil.addRowToMatrixCursor(cursor,
				item.id, item.name, item.quantity, item.volume, item.chosenPriceId,
				item.price, item.price, item.price, item.price, 0);
	}

	/**
	 * @deprecated use {@link fr.piconsoft.eoit.db.util.ItemCursorBuilder}
	 */
	@Deprecated
	public static void addRowToMatrixCursor(MatrixCursor cursor, final ItemBean item, int ml) {
		DbUtil.addRowToMatrixCursor(cursor,
				item.id, item.name, item.quantity, item.volume, item.chosenPriceId,
				item.price, item.price, item.price, item.price, ml);
	}
}
