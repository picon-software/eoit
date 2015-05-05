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

import butterknife.InjectView;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ListSkillData;

/**
 * @author picon.software
 */
public class ListSkillViewHolder extends AbstractViewHolder<ListSkillData> {

	public static final int LAYOUT_ID = R.layout.required_skills_row_premium;

	@InjectView(R.id.item_name) protected TextView itemName;
	@InjectView(R.id.skill_chk_ok) protected ImageView skillChkOkIcon;
	@InjectView(R.id.skill_chk_ko) protected ImageView skillChkKoIcon;
	@InjectView(R.id.required_skill_level_icon) protected ImageView requiredSkillLvlIcon;
	@InjectView(R.id.skill_level_icon) protected ImageView skillLvlIcon;

	public ListSkillViewHolder(View itemView) {
		super(itemView);
	}

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
    }

	@Override
	public void bind(ListSkillData itemData) {
        itemName.setText(itemData.name);
		skillChkOkIcon.setVisibility(itemData.learntOk ? View.VISIBLE : View.GONE);
		skillChkKoIcon.setVisibility(itemData.learntKo ? View.VISIBLE : View.GONE);
		skillLvlIcon.setImageResource(EOITConst.skillIconsResourceIds[itemData.skillLevel]);
		requiredSkillLvlIcon.setImageResource(EOITConst.requiredSkillIconsResourceIds[itemData.requiredSkillLevel]);
		requiredSkillLvlIcon.setVisibility(
				itemData.requiredSkillLevel > itemData.skillLevel ? View.VISIBLE : View.GONE);
	}
}
