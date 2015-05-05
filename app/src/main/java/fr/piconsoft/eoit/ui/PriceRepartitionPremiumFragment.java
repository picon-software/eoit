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
import android.widget.ImageView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;
import fr.piconsoft.eoit.util.ManufactureUtils;
import fr.piconsoft.eoit.ui.listener.ItemOnItemClickListener;
import fr.piconsoft.eoit.activity.basic.loader.BaseProductionNeedsLoader;

import static fr.piconsoft.eoit.helper.PriceRepartitionHelper.ItemBeanPriceComparator;
import static fr.piconsoft.eoit.helper.PriceRepartitionHelper.pollFirst;

/**
 * @author picon.software
 */
public class PriceRepartitionPremiumFragment extends LoaderFragment<SparseItemBeanArray> {

	private static final int FIRST_SECTION_COLOR = 0xFFd94d4c;
	private static final int SECOND_SECTION_COLOR = 0xBBd94d4c;
	private static final int THIRD_SECTION_COLOR = 0x77d94d4c;
	private static final int OTHER_SECTION_COLOR = 0x33151515;

	@InjectView(R.id.piegraph) protected PieGraph pieGraph;
	@InjectView(R.id.item_render) protected ImageView itemRender;

	private int[] legendItemsFragmentIds = {R.id.item1, R.id.item2, R.id.item3};
	protected List<SingleItemPremiumFragment> legendItemsFragment = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment = inflater.inflate(R.layout.manufacture_price_repartition_premium, container, false);

		ButterKnife.inject(this, fragment);

		initLoader(getArguments());

		pieGraph.setOnSliceClickedListener(new ItemOnItemClickListener(getActivity()));

		bindFragments();

		return fragment;
	}

	protected void bindFragments() {
		for (int fragmentId : legendItemsFragmentIds) {
			SingleItemPremiumFragment fragment = (SingleItemPremiumFragment) getChildFragmentManager().findFragmentById(fragmentId);
			fragment.getView().setVisibility(View.GONE);
			legendItemsFragment.add(fragment);
		}
	}

	@Override
	public Loader<SparseItemBeanArray> getCursorLoader(int id, Bundle args) {
		if (args == null) {
			return null;
		}

		int itemId = args.getInt("itemId", -1);
        int categorieId = args.getInt("categorieId");
        int groupId = args.getInt("groupId");
		if (itemId == -1) {
			return null;
		}

		IconUtil.initIconOrRender(itemId, categorieId, groupId, itemRender);
		return new BaseProductionNeedsLoader(context, itemId, groupId, categorieId);
	}

	@Override
	public void onLoadFinished(SparseItemBeanArray data) {

		TreeSet<ItemBean> productionNeeds = new TreeSet<>(new ItemBeanPriceComparator());
		ManufactureUtils.getProductionNeeds(data, 1).populateSetWithValues(productionNeeds);

		for (PieSlice pieSlice : buildSlices(productionNeeds)) {
			pieGraph.addSlice(pieSlice);
		}
	}

	@Override
	public void onLoaderReset(Loader<SparseItemBeanArray> loader) {
	}

	private List<PieSlice> buildSlices(TreeSet<ItemBean> items) {
		List<PieSlice> slices = new ArrayList<>();

		if(items != null && !items.isEmpty()) {
			TreeSet<ItemBean> set = new TreeSet<>(new ItemBeanPriceComparator());
			set.addAll(items);

			ItemBean itemBean = pollFirst(set);
			slices.add(buildSlice(itemBean, FIRST_SECTION_COLOR));
			initializeLegend(0, itemBean, FIRST_SECTION_COLOR);
			if (set.size() > 0) {
				itemBean = pollFirst(set);
				slices.add(buildSlice(itemBean, SECOND_SECTION_COLOR));
				initializeLegend(1, itemBean, SECOND_SECTION_COLOR);
			}
			if (set.size() > 0) {
				itemBean = pollFirst(set);
				slices.add(buildSlice(itemBean, THIRD_SECTION_COLOR));
				initializeLegend(2, itemBean, THIRD_SECTION_COLOR);
			}
			if (set.size() > 0) {
				slices.add(buildOtherSlice(set, OTHER_SECTION_COLOR));
			}
		}

		return slices;
	}

	private static PieSlice buildOtherSlice(Set<ItemBean> items, int color) {
		double totalValue = 0;
		for (ItemBean item : items) {
			totalValue += item.price * item.quantity;
		}

		return buildSlice(-1L, totalValue, color, false);
	}

	private static PieSlice buildSlice(ItemBean item, int color) {
		return buildSlice(item.id, item.price * item.quantity, color, true);
	}

	private static PieSlice buildSlice(long id, double value, int color, boolean bold) {
		PieSlice slice = new PieSlice();

		slice.setInternalId(id);
		slice.setValue(value);
		slice.setColor(color);
		slice.setBold(bold);

		return slice;
	}

	private void initializeLegend(int index, ItemBean itemBean, int color) {
		itemBean.price = itemBean.quantity * itemBean.price;
		legendItemsFragment.get(index).setItem(itemBean, color);
		legendItemsFragment.get(index).getView().setVisibility(View.VISIBLE);
	}
}
