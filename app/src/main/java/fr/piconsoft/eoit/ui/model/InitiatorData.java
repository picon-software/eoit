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

package fr.piconsoft.eoit.ui.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author picon.software
 */
public class InitiatorData extends AbstractItemData {

	public int blueprintId;
	public Integer relicId;

	private Map<Integer, InitiatorBean> initiatorMap = new TreeMap<>();
	private OnInitiatorSelectedListener onItemSelectedListener;

	public interface OnInitiatorSelectedListener {
		void onInitiatorSelected(int blueprintId, int relicId);
	}

	@Override
	public int getType() {
		return ItemDataType.BLUEPRINT_INVENTION_INITIATOR;
	}

	public Map<Integer, InitiatorBean> getInitiatorMap() {
		return initiatorMap;
	}

	public OnInitiatorSelectedListener getOnItemSelectedListener() {
		return onItemSelectedListener;
	}

	public void setOnItemSelectedListener(OnInitiatorSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}
}
