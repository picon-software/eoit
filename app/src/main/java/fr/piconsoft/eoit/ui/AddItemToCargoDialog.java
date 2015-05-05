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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.ItemUtil;

/**
 * @author picon.software
 */
public class AddItemToCargoDialog extends SimpleOkLoaderDialog<Cursor, ItemBeanWithMaterials> {

	private static final String[] SPINNER_CURSOR_COLUMNS = new String[]{Item.NAME};
	private static final int[] SPINNER_VIEW_IDS = new int[]{android.R.id.text1};

	private SparseItemBeanArray asteroids;

	private View inflatedLayout;

	private int itemId;
	private long quantity;

	public AddItemToCargoDialog() {
		super(R.string.mining_session_add_item, R.layout.mining_cargo_add_item_dialog);
	}

	public AddItemToCargoDialog(OnDismissListener<ItemBeanWithMaterials> listener) {
		super(R.string.mining_session_add_item, R.layout.mining_cargo_add_item_dialog, listener);
	}

	/**
	 *
	 */
	public AddItemToCargoDialog(int itemId, long quantity, OnDismissListener<ItemBeanWithMaterials> listener) {
		super(R.string.mining_session_add_item, R.layout.mining_cargo_add_item_dialog, listener);
		this.itemId = itemId;
		this.quantity = quantity;
	}

	@Override
	protected void onCreateSimpleDialog(View inflatedLayout, Bundle savedInstanceState) {

		this.inflatedLayout = inflatedLayout;

		Spinner itemSpinner = (Spinner) inflatedLayout.findViewById(R.id.item_spinner);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, android.R.layout.simple_dropdown_item_1line, null,
				SPINNER_CURSOR_COLUMNS, SPINNER_VIEW_IDS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		itemSpinner.setAdapter(adapter);
		itemSpinner.setOnItemSelectedListener(new AddItemOnItemSelectedListenet());

		NumberFormat nf = new DecimalFormat(EOITConst.VALUES_WITHOUT_SEPARATOR_PATTERN);
		EditText editText = (EditText) inflatedLayout.findViewById(R.id.item_quantity);
		editText.setText(nf.format(quantity));
		editText.setOnKeyListener(new ItemQuantityOnKeyListener());
	}

	@Override
	public void onResume() {
		super.onResume();
		initOrRestart();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	@Override
	protected Loader<Cursor> getCursorLoader(int id, Bundle args) {
		return new CursorLoader(
				context,
				Item.CONTENT_URI,
				new String[]{
						Item._ID, Item.NAME,
						Item.VOLUME,
						Prices.BUY_PRICE, Prices.SELL_PRICE,
						Prices.OWN_PRICE, Prices.PRODUCE_PRICE,
						Item.CHOSEN_PRICE_ID},
				Groups.CATEGORIE_ID + " = " + EOITConst.Categories.ASTEROID_CATEGORIE_ID + " AND "
						+ Item.PORTION_SIZE + " > 0 AND "
						+ Item.NAME + " NOT LIKE 'Compressed%'"
						+ (itemId > 0 ? (" AND " + Item._ID + " = " + itemId) : ""),
				null,
				Item.GROUP_ID);
	}

	@Override
	public void onLoadFinished(Cursor cursor) {

		asteroids = new SparseItemBeanArray();

		if (DbUtil.hasAtLeastOneRow(cursor)) {
			while (!cursor.isAfterLast()) {
				ItemBeanWithMaterials item = ItemUtil.getItem(cursor, 1);

				item.id = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
				asteroids.append(item);

				cursor.moveToNext();
			}
		}

		cursor.moveToFirst();

		Spinner itemSpinner = (Spinner) inflatedLayout.findViewById(R.id.item_spinner);
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) itemSpinner.getAdapter();
		if(adapter != null) {
			adapter.changeCursor(cursor);
		}
	}

	@Override
	protected void onSubmit() {
		EditText edit = (EditText) inflatedLayout.findViewById(R.id.item_quantity);
		String valueStr = edit.getText().toString().replaceAll(" ", "");

		NumberFormat nf = new DecimalFormat(EOITConst.VALUES_WITHOUT_SEPARATOR_PATTERN);

		if (returnObject != null) {
			try {
				returnObject.quantity = (Long) nf.parse(valueStr);
			} catch (ParseException e) {
				returnObject.quantity = 0;
			}
		}
	}

	private class AddItemOnItemSelectedListenet implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			returnObject = asteroids.get((int) id);
			returnObject.quantity = 0;

			IconUtil.initIcon(id, (ImageView) inflatedLayout.findViewById(R.id.item_icon));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private class ItemQuantityOnKeyListener implements OnKeyListener {

		private NumberFormat nf = new DecimalFormat(EOITConst.VALUES_WITHOUT_SEPARATOR_PATTERN);

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DEL)
					&& event.getAction() == KeyEvent.ACTION_UP) {
				EditText edit = (EditText) v;
				try {
					String valueStr = edit.getText().toString().replaceAll(" ", "");

					returnObject.quantity = nf.parse(valueStr).longValue();

				} catch (ParseException e) {
					returnObject.quantity = 0;
				}
				return false;
			}

			return false;
		}
	}
}
