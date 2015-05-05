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

package fr.piconsoft.eoit.ui.task;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import javax.inject.Inject;

import fr.piconsoft.eoit.api.model.AdjustedPrice;
import fr.piconsoft.eoit.api.model.CostIndex;
import fr.piconsoft.eoit.api.model.Tax;
import fr.piconsoft.eoit.api.services.IndustryService;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.ui.model.Stations;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;

/**
 * @author picon.software
 */
public class JobCostCalculationTask extends AbstractRetrofitAsyncTask<Integer, Void, JobCostCalculationTask.JobCosts> {

	@Inject protected Context context;
	@Inject protected IndustryService industryService;

	private Callback<JobCosts> callback;

	@Override
	protected Context getCurrentContext() {
		return context;
	}

	@Override
	protected JobCosts doInBackground(Integer itemId) {
		return doSync(itemId);
	}

	@Override
	protected void onPostExecute(JobCosts jobCosts) {
		if(callback != null) {
			callback.success(jobCosts);
		}
	}

	public JobCosts doSync(Integer itemId) {
		final Cursor cursor = context.getContentResolver().query(
				ContentUris.withAppendedId(ItemMaterials.CONTENT_ITEM_ID_URI_BASE, itemId),
				new String[]{ItemMaterials.ITEM_ID,
						ItemMaterials.MATERIAL_ITEM_ID, ItemMaterials.QUANTITY},
				null, null, null);

		int stationId = Stations.getProductionStation().stationId;
		int solarSystemId = Stations.getProductionStation().solarSystemId;

		CostIndex costIndex = industryService.costIndex(solarSystemId, 1);
		Tax tax = industryService.tax(stationId);

		costIndex = costIndex == null ? CostIndex.EMPTY : costIndex;
		tax = tax == null ? Tax.EMPTY : tax;

		double jobCost = 0;
		for (Cursor data : new CursorIteratorWrapper(cursor)) {
			int materialId = data.getInt(data.getColumnIndex(ItemMaterials.MATERIAL_ITEM_ID));
			long quantity = data.getLong(data.getColumnIndex(ItemMaterials.QUANTITY));

			AdjustedPrice price = industryService.price(materialId);

			if(price != null) {
				jobCost += price.adjustedPrice * quantity;
			}
		}

		Log.d("JobCostCalculationTask", "base cost : " + jobCost);

		jobCost *= costIndex.costIndex;

		Log.d("JobCostCalculationTask", "base cost + with cost index : " + jobCost);

		jobCost *= 1 + tax.tax;

		Log.d("JobCostCalculationTask", "base cost + with cost index + tax: " + jobCost);

		cursor.close();

		JobCosts jobCosts = new JobCosts();
		jobCosts.jobCost = jobCost;
		jobCosts.costIndex = costIndex.costIndex;
		jobCosts.tax = tax.tax;

		return jobCosts;
	}

	public void setCallback(Callback<JobCosts> callback) {
		this.callback = callback;
	}

	public static class JobCosts {
		public double jobCost;
		public float costIndex;
		public float tax;
	}
}
