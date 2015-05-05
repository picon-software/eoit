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

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.TreeSet;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.ui.widget.PriceRepartitionView;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.ManufactureUtils;
import fr.piconsoft.eoit.activity.basic.loader.BaseProductionNeedsLoader;

import static fr.piconsoft.eoit.helper.PriceRepartitionHelper.ItemBeanPriceComparator;

/**
 * @author picon.software
 *
 */
public class PriceRepartitionFragment extends LoaderFragment<SparseItemBeanArray> {

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment = inflater.inflate(R.layout.manufacture_price_repartition, container, false);

		initLoader(getArguments());

		return fragment;
	}

	@Override
	public Loader<SparseItemBeanArray> getCursorLoader(int id, Bundle args) {
		if(args == null) {
			return null;
		}

		int itemId = args.getInt("itemId", -1);
		int categorieId = args.getInt("categorieId");
		int groupId = args.getInt("groupId");
		if(itemId == -1) {
			return null;
		}

		return new BaseProductionNeedsLoader(context, itemId, groupId, categorieId);
	}

	@Override
	public void onLoadFinished(SparseItemBeanArray data) {
        TreeSet<ItemBean> productionNeeds = new TreeSet<>(new ItemBeanPriceComparator());
		ManufactureUtils.getProductionNeeds(data, 1).populateSetWithValues(productionNeeds);

		LinearLayout priceRepartitionContainer =
				(LinearLayout) getView().findViewById(R.id.PRICE_REPARTITION_CONTAINER);

		PriceRepartitionView p =
				new PriceRepartitionView(
						context,
						productionNeeds);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				350);
		p.setLayoutParams(lp);

		priceRepartitionContainer.addView(p);
	}

	@Override
	public void onLoaderReset(Loader<SparseItemBeanArray> loader) { }
}
