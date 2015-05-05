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

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import fr.piconsoft.eoit.ui.model.AbstractItemData;
import fr.piconsoft.eoit.ui.model.ItemDataType;
import fr.piconsoft.eoit.ui.viewholder.AbstractViewHolder;
import fr.piconsoft.eoit.ui.viewholder.InitiatorViewHolder;
import fr.piconsoft.eoit.ui.viewholder.BlueprintInventionChancesViewHolder;
import fr.piconsoft.eoit.ui.viewholder.BlueprintInventionDecryptorViewHolder;
import fr.piconsoft.eoit.ui.viewholder.BlueprintSpecViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ItemInfoBlueprintViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ItemInfoPricesViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ItemInfoViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ListItemViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ListSectionTitleViewHolder;
import fr.piconsoft.eoit.ui.viewholder.ListSkillViewHolder;

/**
 * @author picon.software
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<AbstractViewHolder> {
	private final Activity activity;
	private AbstractItemData[] mDataset;

	// Provide a suitable constructor (depends on the kind of dataset)
	public RecyclerViewAdapter(Activity activity, AbstractItemData... dataset) {
		this.activity = activity;
		mDataset = dataset;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		AbstractViewHolder viewHolder = null;

		switch (viewType) {
			case ItemDataType.LIST_ITEM:
				viewHolder = new ListItemViewHolder(activity, ListItemViewHolder.inflate(parent));
				break;
			case ItemDataType.ITEM_INFO_PRICES:
				viewHolder = new ItemInfoPricesViewHolder(ItemInfoPricesViewHolder.inflate(parent));
				break;
			case ItemDataType.ITEM_INFO_BLUEPRINT:
				viewHolder = new ItemInfoBlueprintViewHolder(ItemInfoBlueprintViewHolder.inflate(parent));
				break;
			case ItemDataType.ITEM_INFO:
				viewHolder = new ItemInfoViewHolder(ItemInfoViewHolder.inflate(parent));
				break;
			case ItemDataType.LIST_SECTION:
				viewHolder = new ListSectionTitleViewHolder(ListSectionTitleViewHolder.inflate(parent));
				break;
			case ItemDataType.BLUEPRINT_INVENTION_DECRYPTOR:
				viewHolder = new BlueprintInventionDecryptorViewHolder(BlueprintInventionDecryptorViewHolder.inflate(parent));
				break;
			case ItemDataType.BLUEPRINT_INVENTION_CHANCES:
				viewHolder = new BlueprintInventionChancesViewHolder(BlueprintInventionChancesViewHolder.inflate(parent));
				break;
			case ItemDataType.BLUEPRINT_INVENTION_SPEC:
				viewHolder = new BlueprintSpecViewHolder(BlueprintSpecViewHolder.inflate(parent));
				break;
			case ItemDataType.LIST_SKILL:
				viewHolder = new ListSkillViewHolder(ListSkillViewHolder.inflate(parent));
				break;
			case ItemDataType.BLUEPRINT_INVENTION_INITIATOR:
				viewHolder = new InitiatorViewHolder(InitiatorViewHolder.inflate(parent));
				break;
		}

		return viewHolder;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(AbstractViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		holder.bind(mDataset[position]);
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset != null ? mDataset.length : 0;
	}

	@Override
	public int getItemViewType(int position) {
		return mDataset[position].getType();
	}
}