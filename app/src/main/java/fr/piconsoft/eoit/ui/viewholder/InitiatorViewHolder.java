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

package fr.piconsoft.eoit.ui.viewholder;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Map;

import butterknife.InjectView;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.activity.basic.util.AbstractViewBinder;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.ui.model.InitiatorBean;
import fr.piconsoft.eoit.ui.model.InitiatorData;

/**
 * @author picon.software
 */
public class InitiatorViewHolder extends AbstractViewHolder<InitiatorData> {

	public static final int LAYOUT_ID = R.layout.blueprint_required_decryptor_premium;

	private static final String[] from = new String[]{Item._ID, Item.NAME, "relicDesc"};
	private static final int[] to = new int[]{R.id.item_icon, android.R.id.text1, android.R.id.text2};

	//--- Views
	@InjectView(R.id.spinner) protected Spinner relicsSpinner;

	private SimpleCursorAdapter adapter;

	public InitiatorViewHolder(View itemView) {
		super(itemView);
		adapter =
				new SimpleCursorAdapter(
						itemView.getContext(),
						R.layout.two_line_drop_down_item_premium,
						null, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setDropDownViewResource(R.layout.two_line_drop_down_item_premium);
		adapter.setViewBinder(new DecryptorViewBinder());
	}

	public static View inflate(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
	}

	@Override
	public void bind(InitiatorData itemData) {
		relicsSpinner.setAdapter(adapter);
		relicsSpinner.setOnItemSelectedListener(
				new SpinnerOnItemSelectedListener(itemData));
		MatrixCursor decryptorCursor = new MatrixCursor(new String[]{Item._ID, Item.NAME, "relicDesc"});

		int selectedItem = 0, cpt = 0;
		for (Map.Entry<Integer, InitiatorBean> entry : itemData.getInitiatorMap().entrySet()) {
			DbUtil.addRowToMatrixCursor(decryptorCursor,
					entry.getKey(),
					entry.getValue().name,
					getDescription(entry.getValue()));

			if (entry.getKey().equals(itemData.relicId)) {
				selectedItem = cpt;
			}

			cpt++;
		}

		adapter.changeCursor(decryptorCursor);
		relicsSpinner.setSelection(selectedItem);
	}

	private String getDescription(InitiatorBean bean) {
		return itemView.getContext().getString(R.string.relic_desc,
				bean.probability * 100, bean.maxProductionLimit);
	}

	private class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

		private final InitiatorData itemData;

		private SpinnerOnItemSelectedListener(InitiatorData itemData) {
			this.itemData = itemData;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (itemData.relicId == null || itemData.relicId != id) {
				itemData.relicId = (int) id;
				itemData.getOnItemSelectedListener()
						.onInitiatorSelected(itemData.blueprintId, itemData.relicId);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private static class DecryptorViewBinder extends AbstractViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			int viewId = view.getId();

			switch (viewId) {
				case android.R.id.text1:
				case android.R.id.text2:
					String value = cursor.getString(columnIndex);
					initText(view, value);
					break;

				case R.id.item_icon:
					long id = cursor.getLong(columnIndex);

					if(id > 0) {
						initIcon(view, id);
					} else {
						((ImageView) view).setImageResource(android.R.color.transparent);
					}
					break;

				default:
					throw new IllegalArgumentException("viewId : " + viewId);
			}

			return true;
		}
	}
}
