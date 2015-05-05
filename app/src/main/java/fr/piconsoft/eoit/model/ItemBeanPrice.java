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

package fr.piconsoft.eoit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import fr.piconsoft.eoit.db.util.ColumnName;

/**
 * @author picon.software
 */
public class ItemBeanPrice implements Comparable<ItemBeanPrice>, Serializable {
	private static final long serialVersionUID = 6137096542336033252L;

	public @ColumnName("_id") int id, groupId, categorieId;
	public @Nullable Integer chosenPrice, solarSystemId;
	public @Nullable Long buyVolume, sellVolume;
	public @Nullable Double buyPrice, sellPrice, producePrice;

	public Date lastUpdate, lastCalculationUpdate;

	private List<Integer> dependsOnItemIds;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemBeanPrice)) return false;

		ItemBeanPrice priceBean = (ItemBeanPrice) o;

		return id == priceBean.id;
	}

	public boolean identityEquals(@Nullable ItemBeanPrice other) {
		return other != null &&
				id == other.id &&
				equals(solarSystemId, other.solarSystemId);
	}

	public boolean marketPricesEquals(@Nullable ItemBeanPrice other) {
		return other != null &&
				identityEquals(other) &&
				equals(buyPrice, other.buyPrice) &&
				equals(buyVolume, other.buyVolume) &&
				equals(sellPrice, other.sellPrice) &&
				equals(sellVolume, other.sellVolume);
	}

	private boolean equals(@Nullable Object obj1, @Nullable Object obj2) {
		return !(obj1 == null || obj2 == null) && obj1.equals(obj2);
	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "PriceBean [itemId=" + id + ", buyPrice=" + buyPrice
				+ ", sellPrice=" + sellPrice + ", producePrice=" + producePrice
				+ "]";
	}

	@Override
	public int compareTo(@NonNull ItemBeanPrice o) {
		return Long.valueOf(id).compareTo((long) o.id);
	}
}
