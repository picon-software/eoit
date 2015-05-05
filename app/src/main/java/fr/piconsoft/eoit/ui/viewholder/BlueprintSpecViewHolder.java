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
import android.widget.TextView;

import butterknife.InjectView;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.util.DecryptorUtil;
import fr.piconsoft.eoit.ui.model.BlueprintSpecData;

/**
 * @author picon.software
 */
public class BlueprintSpecViewHolder extends AbstractViewHolder<BlueprintSpecData> {

	public static final int LAYOUT_ID = R.layout.blueprint_spec_premium;

	//--- Views
	@InjectView(R.id.blueprint_spec) protected TextView blueprintSpec;

	public BlueprintSpecViewHolder(View itemView) {
		super(itemView);
	}

	public static View inflate(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
	}

	@Override
	public void bind(BlueprintSpecData itemData) {
		DecryptorUtil.DecryptorBonuses bonuses = DecryptorUtil.getDecryptorBonusesOrDefault(itemData.decryptorId);
		String blueprintSpecStr = itemView.getContext().getString(R.string.blueprint_desc,
				2 + bonuses.meModifier, 4 + bonuses.peModifier, itemData.maxProductionLimit + bonuses.maxRunModifier);
		if (blueprintSpec != null) {
			blueprintSpec.setText(blueprintSpecStr);
		}
	}
}
