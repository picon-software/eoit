/*
 * Copyright (C) 2014 Picon software
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

import java.util.Arrays;
import java.util.List;

import fr.piconsoft.eoit.Const;

/**
 * @author picon.software
 */
public class AsteroidItemBean extends ItemBeanWithMaterials {

	private static final List<Integer> ASTEROID_ITEM_IDS =
			Arrays.asList(Const.Items.ASTEROID_IDS);

	public int groupId;

	public AsteroidItemBean() {
		materials = new SparseItemBeanArray();
	}

	public AsteroidItemBean(AsteroidItemBean other) {
		super(other);
		this.groupId = other.groupId;
	}

	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public AsteroidItemBean clone() {
		return new AsteroidItemBean(this);
	}

	public boolean isAsteroid() {
		return ASTEROID_ITEM_IDS.contains(id);
	}

	public boolean containsMineral(int mineralId) {
		return materials.containsKey(mineralId);
	}

	public void addMineral(ItemBeanWithMaterials item) {
		if(item.isMineral() && item.quantity > 0) {
			materials.append(item.id, item);
		}
	}

	public ItemBean getMineral(int mineralId) {
		return materials.get(mineralId);
	}
}
