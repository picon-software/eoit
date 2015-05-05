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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.ui.model.ProductionStep;

/**
 * @author picon.software
 */
public class ProductionPlanPremiumFragment extends ProductionPlanFragment {

	private int[] stepFragmentIds = {R.id.production_step0, R.id.production_step1, R.id.production_step2,
			R.id.production_step3, R.id.production_step4, R.id.production_step5};

	private ProductionStepFragment miningStepfragment;
	private ShoppingStepFragment shoppingStepfragment;

	public ProductionPlanPremiumFragment() {
		super();

		layoutId = R.layout.manufacture_production_plan_premium;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment = super.onCreateView(inflater, container, savedInstanceState);

		IconUtil.initIcon(itemId,
				(ImageView) fragment.findViewById(R.id.item_icon));

		return fragment;
	}

	protected void bindStepFragments() {
		miningStepfragment =
				(ProductionStepFragment) getChildFragmentManager().findFragmentById(R.id.production_mining_list);
		shoppingStepfragment =
				(ShoppingStepFragment) getChildFragmentManager().findFragmentById(R.id.production_shopping_list);

		for (int stepFragmentId : stepFragmentIds) {
            ProductionStepFragment fragment = (ProductionStepFragment) getChildFragmentManager().findFragmentById(stepFragmentId);
			stepsFragments.add(fragment);
		}
	}

	protected void initializeSteps(ProductionStep miningStep, ProductionStep shoppingStep) {
		if (miningStep != null && !miningStep.isEmpty()) {
			miningStepfragment.initialize(miningStep);
			miningStepfragment.initSectionTitle("Mine");
			miningStepfragment.hideSeparator();
            miningStepfragment.show();
		} else {
			miningStepfragment.hide();
		}
		if (shoppingStep != null && !shoppingStep.isEmpty()) {
			shoppingStepfragment.initialize(shoppingStep);
			shoppingStepfragment.initSectionTitle("Shop");
			if (miningStep == null || miningStep.isEmpty()) {
				shoppingStepfragment.hideSeparator();
			}
            shoppingStepfragment.show();
		} else {
			shoppingStepfragment.hide();
		}

		List<ProductionStep> steps = new ArrayList<>();
		steps.addAll(findSteps(true));

		int index = 0;
		for (EnhancedMaterialListFragment fragment : stepsFragments) {
			if (steps.size() > index) {
				fragment.initialize(steps.get(index));
				fragment.initSectionTitle("Manufacture");
                ((ProductionStepFragment) fragment).show();
			} else {
				((ProductionStepFragment) fragment).hide();
			}
			index++;
		}

		if((miningStep == null || miningStep.isEmpty()) && (shoppingStep == null || shoppingStep.isEmpty())) {
			((ProductionStepFragment) stepsFragments.get(0)).hideSeparator();
		}
	}
}
