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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemInfoBlueprintData;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.util.PricesUtil;

import static fr.piconsoft.eoit.util.Formatter.formatTime;
import static fr.piconsoft.eoit.formula.FormulaCalculator.calculateProductionTime;

/**
 * @author picon.software
 */
public class ItemInfoBlueprintViewHolder extends AbstractViewHolder<ItemInfoBlueprintData> {

	public static final int LAYOUT_ID = R.layout.item_info_blueprint_premium;
	private static final NumberFormat nf = new DecimalFormat("##0%");

	//--- Views
	@InjectView(R.id.blueprint_icon) protected ImageView icon;
	@InjectView(R.id.blueprint_name) protected TextView name;
	@InjectView(R.id.blueprint_produce_time) protected TextView produceTime;
	@InjectView(R.id.invent_icon) protected ImageView inventIcon;
	@InjectView(R.id.ml) protected TextView me;
	@InjectView(R.id.pl) protected TextView te;
	@InjectView(R.id.job_cost) protected TextView jobCost;

	public ItemInfoBlueprintViewHolder(View itemView) {
		super(itemView);
	}

	public static View inflate(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
	}

	@Override
	public void bind(ItemInfoBlueprintData itemData) {
		IconUtil.initIcon(itemData.blueprintId, icon);
		name.setText(itemData.blueprintName);
		produceTime.setText(formatTime(calculateProductionTime(itemData.productionTime, itemData.te)));
		inventIcon.setVisibility(itemData.canBeInvented() ? View.VISIBLE : View.GONE);
		me.setText(nf.format(itemData.me / 100f));
		te.setText(nf.format(itemData.te / 100f));
		PricesUtil.setPrice(jobCost, itemData.jobCost, true);
	}
}
