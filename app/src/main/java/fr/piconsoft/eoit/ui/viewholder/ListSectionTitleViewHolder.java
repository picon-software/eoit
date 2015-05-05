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
import fr.piconsoft.eoit.ui.model.ListSectionTitleData;

/**
 * @author picon.software
 */
public class ListSectionTitleViewHolder extends AbstractViewHolder<ListSectionTitleData> {

	public static final int LAYOUT_ID = R.layout.row_header_items_premium;

	//--- Views
	@InjectView(R.id.header) protected TextView header;
	@InjectView(R.id.separator) protected View separator;

	public ListSectionTitleViewHolder(View itemView) {
		super(itemView);
	}

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
    }

	@Override
	public void bind(ListSectionTitleData data) {
		header.setText(data.name);
		separator.setVisibility(data.separator ? View.VISIBLE : View.GONE);
	}
}
