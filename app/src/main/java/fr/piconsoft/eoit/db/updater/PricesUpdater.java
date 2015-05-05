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

package fr.piconsoft.eoit.db.updater;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.db.DatabaseVersions;
import fr.piconsoft.eoit.db.updater.bean.SerializableBean;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.model.Station;

import static fr.piconsoft.eoit.util.DbUtil.formatDate;
import static fr.piconsoft.eoit.util.DbUtil.getDate;

/**
 * @author picon.software
 *
 */
public class PricesUpdater extends AbstractDatabaseUpdater {

	private static final String LOG_TAG = PricesUpdater.class.getSimpleName();
	private List<PriceBean> prices;
	private int tradeStationSolarSystemId = EOITConst.SolarSystem.JITA_SOLAR_SYSTEM_ID;

	@Override
	protected boolean isActive(SQLiteDatabase db) {
		return true;
	}

	@Override
	protected void backup(SQLiteDatabase db) {

		if(db.getVersion() < DatabaseVersions.V_2_5.version) {
			Cursor cursor = db.rawQuery(
					"SELECT " +
							Station.SOLAR_SYSTEM_ID +
							" FROM " + Station.TABLE_NAME +
							" WHERE " + Station.ROLE +
							" IN (" + EOITConst.Stations.TRADE_ROLE + ", " + EOITConst.Stations.BOTH_ROLES + ")",
							null);
			try {
				if(DbUtil.hasAtLeastOneRow(cursor)) {
					while (!cursor.isAfterLast()) {
						tradeStationSolarSystemId = cursor.getInt(cursor.getColumnIndexOrThrow(Station.SOLAR_SYSTEM_ID));

						cursor.moveToNext();
					}
				}
			} finally {
				cursor.close();
			}
		}

		StringBuilder sb = new StringBuilder("SELECT ")
			.append(Prices.ITEM_ID).append(", ")
			.append(Prices.BUY_PRICE).append(", ")
			.append(Prices.SELL_PRICE).append(", ")
			.append(Prices.OWN_PRICE).append(", ")
			.append(Prices.PRODUCE_PRICE);

		if(db.getVersion() >= DatabaseVersions.V_2_5.version) {
			sb.append(", ")
				.append(Prices.BUY_VOLUME).append(", ")
				.append(Prices.SELL_VOLUME).append(", ")
				.append(Prices.SOLAR_SYSTEM_ID).append(", ")
				.append(Prices.LAST_UPDATE);
		}

		sb.append(" FROM ").append(Prices.TABLE_NAME).append(" WHERE ").append(Prices.SOLAR_SYSTEM_ID).append(" IS NOT NULL");

		Cursor cursor = db.rawQuery(sb.toString(), null);
		prices = new ArrayList<>();

		if(DbUtil.hasAtLeastOneRow(cursor)) {

			while (!cursor.isAfterLast()) {
				PriceBean price = new PriceBean(
						cursor.getInt(cursor.getColumnIndexOrThrow(Prices.ITEM_ID)),
						cursor.getDouble(cursor.getColumnIndexOrThrow(Prices.BUY_PRICE)),
						cursor.getDouble(cursor.getColumnIndexOrThrow(Prices.SELL_PRICE)),
						cursor.getDouble(cursor.getColumnIndexOrThrow(Prices.OWN_PRICE)),
						cursor.getDouble(cursor.getColumnIndexOrThrow(Prices.PRODUCE_PRICE)));

				if(db.getVersion() >= DatabaseVersions.V_2_5.version) {
					price.buyVolume = cursor.getInt(cursor.getColumnIndexOrThrow(Prices.BUY_VOLUME));
					price.sellVolume = cursor.getInt(cursor.getColumnIndexOrThrow(Prices.SELL_VOLUME));
					price.solarSystemId = cursor.getInt(cursor.getColumnIndexOrThrow(Prices.SOLAR_SYSTEM_ID));
					price.lastUpdate = getDate(cursor, Prices.LAST_UPDATE);
				}

				prices.add(price);

				cursor.moveToNext();
			}
		}
		cursor.close();

		Log.i(LOG_TAG, prices.size() + " price values backuped.");
	}

	@Override
	protected void restore(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder("INSERT OR REPLACE INTO ");
		sb.append(Prices.TABLE_NAME)
			.append("(")
			.append(Prices.ITEM_ID).append(", ")
			.append(Prices.BUY_PRICE).append(", ")
			.append(Prices.SELL_PRICE).append(", ")
			.append(Prices.OWN_PRICE).append(", ")
			.append(Prices.PRODUCE_PRICE).append(", ")
			.append(Prices.SOLAR_SYSTEM_ID);

		if(db.getVersion() >= DatabaseVersions.V_2_5.version) {
			sb.append(", ")
				.append(Prices.BUY_VOLUME).append(", ")
				.append(Prices.SELL_VOLUME).append(", ")
				.append(Prices.LAST_UPDATE);
		}

		sb.append(") VALUES (?,?,?,?,?,?");

		if(db.getVersion() >= DatabaseVersions.V_2_5.version) {
			sb.append(",?,?,?");
		}

		sb.append(");");

		SQLiteStatement statement = db.compileStatement(sb.toString());

		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(PriceBean price : prices) {
			statement.clearBindings();
			statement.bindLong(1, price.itemId);
			statement.bindDouble(2, price.buyPrice);
			statement.bindDouble(3, price.sellPrice);
			statement.bindDouble(4, price.ownPrice);
			statement.bindDouble(5, price.producePrice);

			if(db.getVersion() >= DatabaseVersions.V_2_5.version) {
				statement.bindLong(6, price.solarSystemId);
				statement.bindLong(7, price.buyVolume);
				statement.bindLong(8, price.sellVolume);
				statement.bindString(9, formatDate(price.lastUpdate, iso8601Format));
			} else {
				statement.bindLong(6, tradeStationSolarSystemId);
			}

			statement.executeInsert();
		}

		statement.close();

		Log.i(LOG_TAG, prices.size() + " price values restored.");
	}

	@Override
	protected void clear() {
		prices.clear();
		prices = null;
	}

	@Override
	protected SerializableBean unserialize(String string) {
		return null;
	}
}
