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

import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author picon.software
 *
 */
@SuppressWarnings("serial")
public class SparseItemBeanArray extends SparseArray<ItemBeanWithMaterials> implements Serializable {

	public SparseItemBeanArray() { super(); }

	public SparseItemBeanArray(Collection<ItemBeanWithMaterials> initialValues) {
		this();
		addAll(initialValues);
	}

	public SparseItemBeanArray(SparseItemBeanArray initialValues) {
		this();
		putAll(initialValues);
	}

	/**
	 * @see android.util.SparseArray#clone()
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public SparseItemBeanArray clone() {

		SparseItemBeanArray clone = new SparseItemBeanArray();
		for(int i = 0; i<size(); i++) {
			ItemBeanWithMaterials itemClone = valueAt(i).clone();
			clone.append(keyAt(i), itemClone);
		}

		return clone;
	}

	public Collection<ItemBeanWithMaterials> values() {
		Collection<ItemBeanWithMaterials> items = new ArrayList<>();

		for(int i = 0; i<size(); i++) {
			items.add(valueAt(i));
		}

		return items;
	}

	public Collection<Integer> keySet() {
		Collection<Integer> keys = new ArrayList<>();

		for(int i = 0; i<size(); i++) {
			keys.add(keyAt(i));
		}

		return keys;
	}

	public boolean containsKey(int key) {
		return get(key) != null;
	}

	public void putAll(SparseItemBeanArray map) {
		for(int i = 0; i<map.size(); i++) {
			append(map.keyAt(i), map.valueAt(i));
		}
	}

	public void putAll(Collection<ItemBeanWithMaterials> items) {
		for(ItemBeanWithMaterials item : items) {
			append(item);
		}
	}

	public void addAll(Collection<ItemBeanWithMaterials> items) {
		for(ItemBeanWithMaterials item : items) {
			append(item);
		}
	}

	public void append(ItemBeanWithMaterials item) {
		if(item != null) {
			append(item.id, item);
		}
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void populateMap(Map<Integer, ItemBeanWithMaterials> map) {
		for(int i = 0; i<size(); i++) {
			map.put(keyAt(i), valueAt(i));
		}
	}

	public void populateSet(Set<ItemBeanWithMaterials> set) {
		for(int i = 0; i<size(); i++) {
			set.add(valueAt(i));
		}
	}

	public void populateSetWithKeys(Set<Integer> set) {
		for(int i = 0; i<size(); i++) {
			set.add(keyAt(i));
		}
	}

	public void populateSetWithValues(Set<ItemBean> set) {
		for(int i = 0; i<size(); i++) {
			set.add(valueAt(i));
		}
	}

	@Override
	public String toString() {

		Map<Integer, ItemBeanWithMaterials> map = new TreeMap<>();
		populateMap(map);

		return map.toString();
	}

	public SparseItemBeanArray union(SparseItemBeanArray other) {
		return union(other.values().toArray(new ItemBeanWithMaterials[other.values().size()]));
	}

	public SparseItemBeanArray union(ItemBeanWithMaterials... others) {
		SparseItemBeanArray result = clone();

		result.include(others);

		return result;
	}

	public SparseItemBeanArray exclude(SparseItemBeanArray other) {
		SparseItemBeanArray result = clone();

		for(ItemBeanWithMaterials item : other.values()) {
			if(result.containsKey(item.id)) {
				result.get(item.id).quantity -= item.quantity;
				if(result.get(item.id).quantity <= 0) {
					result.remove(item.id);
				}
			}
		}

		return result;
	}

	public void include(ItemBeanWithMaterials... others) {
		for(ItemBeanWithMaterials item : others) {
			if(!containsKey(item.id)) {
				if(item.quantity > 0)
					append(item);
			} else {
				get(item.id).quantity += item.quantity;
			}
		}
	}

	public SparseItemBeanArray multiply(long quantity) {
		SparseItemBeanArray result = new SparseItemBeanArray();

		for(ItemBeanWithMaterials item : values()) {
			ItemBeanWithMaterials clone = item.clone();

			if(!Double.isNaN(clone.floatQuantity)) {
				clone.quantity = (long) Math.ceil(clone.floatQuantity * quantity);
			} else if(clone.quantity > 0) {
				clone.quantity *= quantity;
			}

			result.append(clone);
		}

		return result;
	}
}
