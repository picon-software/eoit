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

package fr.piconsoft.eoit.ui.listener;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.ui.model.ItemInfoPricesData;
import fr.piconsoft.eoit.util.ValueCache;
import fr.piconsoft.eoit.ui.task.PriceUpdaterAsyncTask;

/**
 * @author picon.software
 *
 */
public class PriceRadiogroupOnCheckedChangeListener implements OnCheckedChangeListener {

	private int itemId;
	private ItemInfoPricesData itemData;

	public PriceRadiogroupOnCheckedChangeListener(int itemId) {
		super();
		this.itemId = itemId;
	}

	public PriceRadiogroupOnCheckedChangeListener(ItemInfoPricesData itemData) {
		this(itemData.id);
		this.itemData = itemData;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(ValueCache.hasChangedAndUpdateCache(EOITConst.BEAN_ID_ITEM, EOITConst.FIELD_ID_ITEM_INFO_USED_PRICE, itemId, checkedId)
				&& group != null) {
			int id = -1;

			switch (checkedId) {
			case fr.piconsoft.eoit.R.id.sell_price:
				id = EOITConst.SELL_PRICE_ID;
				break;
			case fr.piconsoft.eoit.R.id.buy_price:
				id = EOITConst.BUY_PRICE_ID;
				break;
			case fr.piconsoft.eoit.R.id.produce_price:
				id = EOITConst.PRODUCE_PRICE_ID;
				break;
			case fr.piconsoft.eoit.R.id.fixed_price:
				id = EOITConst.PRODUCE_PRICE_ID;
				break;

			default:
				break;
			}

			itemData.chosenPriceId = id;

			PriceUpdaterAsyncTask.updateChosenPricesForItemId(itemId, id);

			new ChosenPriceUpdaterTask(group.getContext(), itemId).execute(id);
		}
	}

	public static class ChosenPriceUpdaterTask extends AsyncTask<Integer, Void, Void> {

		private Context context;
		private int itemId;

		/**
		 * @param context
		 * @param itemId
		 */
		public ChosenPriceUpdaterTask(Context context, int itemId) {
			super();
			this.context = context;
			this.itemId = itemId;
		}

		@Override
		protected Void doInBackground(Integer... id) {
			Uri updateItemUri = ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId);
			ContentValues values = new ContentValues();
			values.put(Item.CHOSEN_PRICE_ID, id[0]);

			if(context != null) {
				context.getContentResolver().update(
						updateItemUri,
						values,
						null,
						null);
			}

			return null;
		}
	}
}
