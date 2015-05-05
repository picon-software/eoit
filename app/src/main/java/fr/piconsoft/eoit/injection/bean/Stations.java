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

package fr.piconsoft.eoit.injection.bean;

/**
 * @author picon.software
 *
 */
public final class Stations {

	private static final float reprocessEfficiency = 0.5F;
	private static final float reprocessingStationsTake = 0.05F;

	private Station tradeStation = new Stations.Station();
	private Station productionStation = new Stations.Station();

	public Stations() { }

	public Station getProductionStation() {
		return productionStation;
	}

	public Station getTradeStation() {
		return tradeStation;
	}

	public void initTradeStation(int regionId, int solarSystemId, int stationId, String stationName, float standing) {
		tradeStation.regionId = regionId;
		tradeStation.solarSystemId = solarSystemId;
		tradeStation.stationId = stationId;
		tradeStation.stationName = stationName;
		tradeStation.standing = standing;
		tradeStation.initialized = true;
	}

	public void initProdStation(int regionId, int solarSystemId, int stationId, String stationName, float standing) {
		productionStation.regionId = regionId;
		productionStation.solarSystemId = solarSystemId;
		productionStation.stationId = stationId;
		productionStation.stationName = stationName;
		productionStation.standing = standing;
		productionStation.initialized = true;
	}

	public static class Station {
		public int regionId;
		public int solarSystemId;
		public int stationId;
		public String stationName;
		public float standing;
		public float reprocessEfficiency = Stations.reprocessEfficiency;
		public float reprocessingStationsTake = Stations.reprocessingStationsTake;
		public boolean initialized = false;

		private Station() { }

		public Station(int regionId, int solarSystemId, int stationId, String stationName) {
			this.regionId = regionId;
			this.solarSystemId = solarSystemId;
			this.stationId = stationId;
			this.stationName = stationName;
		}

		public Station(int regionId, int solarSystemId, int stationId, String stationName, float standing) {
			this(regionId, solarSystemId, stationId, stationName);
			this.standing = standing;
		}
	}
}
