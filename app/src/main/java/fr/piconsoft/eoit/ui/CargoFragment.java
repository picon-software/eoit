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

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.ui.SimpleOkDialog.OnDismissListener;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder;
import fr.piconsoft.eoit.activity.basic.util.ItemListViewBinder.RedQuantityBehavior;

/**
 * @author picon.software
 *
 */
public class CargoFragment extends EnhancedMaterialListFragment implements OnDismissListener<ItemBeanWithMaterials> {

	private SparseItemBeanArray cargo = new SparseItemBeanArray();

	private OnCargoChangedListener listener;

	public CargoFragment() {
		dataColumns = new String[] { Item._ID, Item.NAME, ItemMaterials.QUANTITY};
		viewIDs = new int[] { R.id.item_icon, R.id.item_name, R.id.item_quantity };
		sectionTitleId = R.string.mining_session_cargo;
		behavior = RedQuantityBehavior.NONE;
		layoutId = R.layout.mining_cargo_item_list;
		showIfCursorIsEmpty = true;
		numberOfColumn = 2;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(OnCargoChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragment = super.onCreateView(inflater, container, savedInstanceState);

		Button addItemButton = (Button) fragment.findViewById(R.id.add_item_button);
		addItemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AddItemToCargoDialog(CargoFragment.this).show(getFragmentManager(), "add_item_cargo");
			}});
		getListView(fragment).setOnItemClickListener(new ItemOnItemListClickListener());
		getListView(fragment).setOnItemLongClickListener(new ItemOnItemLongClickListener());

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			cargo = (SparseItemBeanArray) savedInstanceState.getSerializable("cargo");
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable("cargo", cargo);
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshList();
        resizeListView();

		if(cargo != null) {
			listener.onCargoChanged(cargo);
		}
	}

	@Override
	protected SimpleCursorAdapter adapteurInit() {
		return new SimpleCursorAdapter(
				getActivity(),
				R.layout.item_cell_compact,
				null,
				dataColumns,
				viewIDs,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	private void refreshList() {
		MatrixCursor cargoCursor = getEmptyCursor();

		double totalPrice = 0;
		double totalVolume = 0;

		if(cargo != null) {
			for(ItemBeanWithMaterials item : cargo.values()) {
				addRowToMatrixCursor(cargoCursor, item);

				totalPrice += item.quantity * item.price;
				totalVolume += item.quantity * item.volume;
			}
		}

		getAdapter().setViewBinder(new ItemListViewBinder(behavior));
		getAdapter().changeCursor(cargoCursor);
		getAdapter().notifyDataSetChanged();

		updateTotalMissingInfos(totalPrice, totalVolume);
	}

	/* (non-Javadoc)
	 * @see fr.piconsoft.eoit.activity.basic.fragment.ItemListFragment#onLoadFinished(android.database.Cursor)
	 */
	@Override
	public void onLoadFinished(Cursor cursor) { }

	@Override
	public void onDismiss(ItemBeanWithMaterials returnedObject) {

		if(cargo != null) {
			cargo.append(returnedObject);
		}

		refreshList();
		resizeListView();

		listener.onCargoChanged(cargo);
	}

	public static interface OnCargoChangedListener {
		void onCargoChanged(SparseItemBeanArray cargo);
	}

	private class ItemOnItemListClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			new AddItemToCargoDialog((int) id, cargo.get((int) id).quantity, CargoFragment.this)
					.show(getFragmentManager(), "add_item_cargo");
		}
	}

	private class ItemOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

			new AlertDialog
					.Builder(context)
					.setTitle("Item")
					.setItems(new String[] {"Show item", "Remove item"}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0) {
								final Intent intent = new Intent(context, ItemInfoActivity.class);
								intent.setData(ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, id));

								context.startActivity(intent);
							}
							if(which == 1) {
								cargo.remove((int) id);
								refreshList();
								listener.onCargoChanged(cargo);
							}
						}
					})
					.create().show();

			return true;
		}
	}
}
