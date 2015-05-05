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

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author picon.software
 *
 */
public class PriceBean implements Comparable<PriceBean>, Serializable {
	private static final long serialVersionUID = 6137096542336033252L;

	public int itemId, solarSystemId, groupId, categorieId, chosenPrice = 0;
	public long buyVolume, sellVolume;
	public double buyPrice, sellPrice, ownPrice, producePrice;

	public Date lastUpdate, lastCalculationUpdate;

	public boolean chosenPriceIsEmpty = true;

	public List<Integer> dependsOnItemIds;

	public PriceBean() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param itemId the item id
	 * @param buyPrice the buy price
	 * @param sellPrice the sell price
	 * @param producePrice the produce price
	 */
	public PriceBean(int itemId, double buyPrice,
			double sellPrice, double ownPrice, double producePrice) {
		this();
		this.itemId = itemId;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.ownPrice = ownPrice;
		this.producePrice = producePrice;
	}

	/**
	 * @param itemId the item id
	 * @param buyPrice the buy price
	 * @param sellPrice the sell price
	 * @param producePrice the produce price
	 * @param solarSystemId the solar system id
	 * @param lastUpdate the last update timestamp
	 */
	public PriceBean(int itemId, int solarSystemId, Date lastUpdate, long buyVolume, long sellVolume, double buyPrice,
			double sellPrice, double ownPrice, double producePrice) {
		this(itemId, buyPrice, sellPrice, ownPrice, producePrice);
		this.solarSystemId = solarSystemId;
		this.lastUpdate = lastUpdate;
		this.buyVolume = buyVolume;
		this.sellVolume = sellVolume;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PriceBean)) return false;

		PriceBean priceBean = (PriceBean) o;

		return itemId == priceBean.itemId;

	}

	public boolean identityEquals(@Nullable PriceBean other) {
		return other != null &&
				itemId == other.itemId &&
				solarSystemId == other.solarSystemId;
	}

	public boolean marketPricesEquals(@Nullable PriceBean other) {
		return other != null &&
				identityEquals(other) &&
				buyPrice == other.buyPrice &&
				buyVolume == other.buyVolume &&
				sellPrice == other.sellPrice &&
				sellVolume == other.sellVolume;
	}

	@Override
	public int hashCode() {
		return itemId;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PriceBean [itemId=" + itemId + ", buyPrice=" + buyPrice
				+ ", sellPrice=" + sellPrice + ", producePrice=" + producePrice
				+ "]";
	}

	@Override
	public int compareTo(PriceBean o) {
		return Long.valueOf(itemId).compareTo((long) o.itemId);
	}
}
