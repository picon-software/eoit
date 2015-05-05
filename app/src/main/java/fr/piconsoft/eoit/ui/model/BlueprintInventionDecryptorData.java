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
public class BlueprintInventionDecryptorData extends AbstractItemData {

	public int blueprintId;
	public int decryptorId;

	private Map<Integer, String> decryptorMap = new TreeMap<>();
	private OnDecryptorSelectedListener onItemSelectedListener;

	public interface OnDecryptorSelectedListener {
		void onDecryptorSelected(int blueprintId, int decryptorId, boolean hasChanged);
	}

	@Override
	public int getType() {
		return ItemDataType.BLUEPRINT_INVENTION_DECRYPTOR;
	}

	public Map<Integer, String> getDecryptorMap() {
		return decryptorMap;
	}

	public OnDecryptorSelectedListener getOnItemSelectedListener() {
		return onItemSelectedListener;
	}

	public void setOnItemSelectedListener(OnDecryptorSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}
}
