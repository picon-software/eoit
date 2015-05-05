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

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import fr.piconsoft.eoit.Const;

/**
 * @author picon.software
 */
public class ItemBean implements Comparable<ItemBean>, Serializable, Cloneable {

	private static final List<Integer> MINERAL_ITEM_IDS = Arrays.asList(Const.Items.MINERAL_IDS);
	private static final long serialVersionUID = 8345139374040130638L;

	public int id;
	public String name;
	public double price;
	public short chosenPriceId;
	public long quantity;
	public double floatQuantity = Double.NaN;
	public double volume;
	public long redQuantity;
	public double damagePerJob;
	public int batchSize;
	public int parentBatchSize;

	public ItemBean() { }

	public ItemBean(ItemBean other) {
		this();
		this.id = other.id;
		this.name = other.name;
		this.price = other.price;
		this.chosenPriceId = other.chosenPriceId;
		this.quantity = other.quantity;
		this.floatQuantity = other.floatQuantity;
		this.volume = other.volume;
		this.redQuantity = other.redQuantity;
		this.damagePerJob = other.damagePerJob;
		this.batchSize = other.batchSize;
		this.parentBatchSize = other.parentBatchSize;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ItemBean)) {
			return false;
		}

		ItemBean other = (ItemBean) o;

		return this.id == other.id;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	@Override
	public int compareTo(@NonNull ItemBean another) {
		int thisId = this.id;
		int anotherId = another.id;
		return (thisId < anotherId ? -1 : (thisId == anotherId ? 0 : 1));
	}

	@Override
	public String toString() {
		return name + "(" + id + ") " + price + " ISK x" + quantity + " " + volume + "m3";
	}

	@SuppressWarnings({"CloneDoesntCallSuperClone"})
	@Override
	public ItemBean clone() throws CloneNotSupportedException {
		return new ItemBean(this);
	}

	public boolean isMineral() {
		return MINERAL_ITEM_IDS.contains(id);
	}
}
