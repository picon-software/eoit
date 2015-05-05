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
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.task.Callback;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.ui.model.ItemInfoBlueprintData;

/**
 * @author picon.software
 */
public class METEDialog extends SimpleOkDialog<Void> {

	private static final NumberFormat nfPercent = new DecimalFormat("##0%");

	private int itemId, blueprintId, me, te;
	private View inflatedLayout;
	private ItemInfoBlueprintData itemInfoBlueprintData;
	private Callback<ItemInfoBlueprintData> callBack;

	@SuppressWarnings("UnusedDeclaration")
	public METEDialog() {
		super();
	}

	public METEDialog(int itemId, int blueprintId, int me, int te) {
		super(-1, R.layout.item_info_blueprint_me_te_dialog_premium);
		this.itemId = itemId;
		this.blueprintId = blueprintId;
		this.me = me;
		this.te = te;
	}

	public METEDialog(int itemId, ItemInfoBlueprintData itemInfoBlueprintData) {
		this(itemId, itemInfoBlueprintData.blueprintId,
				itemInfoBlueprintData.me,
				itemInfoBlueprintData.te);
		this.itemInfoBlueprintData = itemInfoBlueprintData;
	}

	public void setCallBack(Callback<ItemInfoBlueprintData> callBack) {
		this.callBack = callBack;
	}

	@Override
	protected void onCreateSimpleDialog(View inflatedLayout,
										Bundle savedInstanceState) {

		this.inflatedLayout = inflatedLayout;

		SeekBar bar = (SeekBar) inflatedLayout.findViewById(R.id.me_seekBar);
		bar.setProgress(me);
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				me = progress;
				updateViews();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		bar = (SeekBar) inflatedLayout.findViewById(R.id.te_seekBar);
		bar.setProgress(te);
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				te = progress;
				updateViews();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		updateViews();
	}

	@Override
	protected void onSubmit() {
		ContentValues values = new ContentValues();
		values.put(Blueprint.ML, me);
		values.put(Blueprint.PL, te);
		itemInfoBlueprintData.me = me;
		itemInfoBlueprintData.te = te;
		new ValueUpdaterTask().execute(values);
		if(callBack != null) {
			callBack.success(itemInfoBlueprintData);
		}
	}

	private void updateViews() {
		TextView meTv = (TextView) inflatedLayout.findViewById(R.id.me);
		meTv.setText(nfPercent.format(me / 100f));
		TextView teTv = (TextView) inflatedLayout.findViewById(R.id.te);
		teTv.setText(nfPercent.format(te / 100f));
	}

	private class ValueUpdaterTask extends AsyncTask<ContentValues, Void, Void> {

		@Override
		protected Void doInBackground(ContentValues... values) {
			Uri updateBluePrintUri = ContentUris.withAppendedId(Blueprint.CONTENT_ID_URI_BASE, blueprintId);

			context.getContentResolver().update(
					updateBluePrintUri,
					values[0],
					null,
					null);

			context.getContentResolver()
					.notifyChange(ContentUris.withAppendedId(Blueprint.CONTENT_ITEM_ID_URI_BASE, itemId), null);
			context.getContentResolver()
					.notifyChange(ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE_PRICES, itemId), null);

			return null;
		}
	}
}
