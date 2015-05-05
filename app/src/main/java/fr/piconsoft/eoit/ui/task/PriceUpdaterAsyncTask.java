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

package fr.piconsoft.eoit.ui.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import fr.piconsoft.eoit.ui.model.PriceBean;
import fr.eo.evecentral.model.MarketStat;
import fr.eo.evecentral.service.EveCentralAPI;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.activity.basic.loader.BaseProductionNeedsLoader;
import fr.piconsoft.eoit.util.PricesUtil;
import fr.piconsoft.eoit.util.ItemUtil;
import fr.piconsoft.eoit.api.model.MarketPrice;
import fr.piconsoft.eoit.api.services.MarketService;
import fr.piconsoft.eoit.ui.model.ItemBeanWithMaterials;
import fr.piconsoft.eoit.helper.ContextHelper;
import fr.piconsoft.eoit.helper.PriceCalculatorHelper;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.ui.model.Stations;
import fr.piconsoft.eoit.ui.model.SparseItemBeanArray;

import static fr.eo.evecentral.model.MarketStat.Type;
import static fr.piconsoft.eoit.util.DbUtil.formatDate;
import static fr.piconsoft.eoit.util.NetworkUtil.isNetworkAvailable;

/**
 * @author picon.software
 */
public class PriceUpdaterAsyncTask extends AbstractRetrofitAsyncTask<Void, Void, PriceBean> {

	public static final String NAME = "PriceUpdaterAsyncTask";
	private static final int FIVE_MIN = 5 * 60 * 1000;
	private static final int THIRTY_MIN = 30 * 60 * 1000;

	private static LruCache<Integer, PriceBean> itemPricesCache = new LruCache<>(500);

	private enum UpdateType {
		DEEP_FULL,
		EVE_CENTRAL_ONLY,
		CALCULATE
	}

	private UpdateType updateType;

	private Context context;

	private Callback<PriceBean> callback;

	protected @Inject EveCentralAPI eveCentralApiService;
	protected @Inject MarketService marketService;

	private static WeakReference<SparseArray<SparseItemBeanArray>> baseProductionNeedItemMap;

    private int itemId, groupId, categorieId;

    public PriceUpdaterAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected Context getCurrentContext() {
		return context;
	}

	public void setCallback(Callback<PriceBean> callback) {
		this.callback = callback;
	}

	public void deepFullUpdateForItemId(int itemId, int groupId, int categorieId) {
        this.itemId = itemId;
        this.groupId = groupId;
        this.categorieId = categorieId;

		updateType = UpdateType.DEEP_FULL;
		execute();
	}

	public void reCalculatePriceForItemId(int itemId) {
        this.itemId = itemId;

        updateType = UpdateType.CALCULATE;
		execute();
	}

	public void cancelCurrentUpdate() {
		cancel(true);
	}

	@Override
	protected PriceBean doInBackground(Void param) {

		try {
			if (itemId > 0) {
				switch (updateType) {
					case DEEP_FULL:
						return deepFullPriceUpdate(itemId, groupId, categorieId);

					case CALCULATE:
						return calculatePriceForItemId(context, itemId).get(itemId);
				}
			}

			return null;
		} finally {
			context = null;

		}
	}

	@Override
	protected void onPostExecute(PriceBean priceBean) {
		if(callback != null) {
			callback.success(priceBean);
		}
	}

	public List<PriceBean> updatePricesForItemId(int regionId, int solarSystemId, Integer... itemIds) {
		ContextHelper.populateStationBeans(context, false);

		List<PriceBean> prices = Collections.emptyList();
		if (isNetworkAvailable(context)) {
			prices = getMarketPricesFromEve(regionId, solarSystemId, itemIds);
		}

		SparseArray<PriceBean> oldPrices = getPricesForIds(context, itemIds);
		if (prices != null && !prices.isEmpty()) {
			for (PriceBean price : prices) {

				ContentValues values = new ContentValues();
				values.put(Prices.BUY_PRICE, price.buyPrice);
				values.put(Prices.SELL_PRICE, price.sellPrice);
				values.put(Prices.SELL_VOLUME, price.sellVolume);
				values.put(Prices.BUY_VOLUME, price.buyVolume);
				values.put(Prices.SOLAR_SYSTEM_ID, solarSystemId);
				values.put(Prices.LAST_UPDATE, formatDate(new Date()));

				PriceBean oldPrice = oldPrices.get(price.itemId);

				if ((oldPrice == null || oldPrice.chosenPriceIsEmpty) &&
						!Double.isNaN(price.buyPrice) &&
						!Double.isNaN(price.sellPrice)) {
					price.chosenPrice = EOITConst.SELL_PRICE_ID;
				} else if ((oldPrice == null || oldPrice.chosenPriceIsEmpty) &&
						!Double.isNaN(price.buyPrice)) {
					price.chosenPrice = EOITConst.BUY_PRICE_ID;
				}

				if (oldPrice == null ||
						oldPrice.buyPrice != price.buyPrice ||
						oldPrice.sellPrice != price.sellPrice ||
						!wasUpdatedLessThanThirtyMin(oldPrice)) {
					Log.v(NAME, "Updating price for item id : " + price.itemId);

					Uri uri = ContentUris.withAppendedId(
							ContentUris.withAppendedId(Prices.CONTENT_ITEM_ID_URI_BASE, price.itemId), solarSystemId);
					context.getContentResolver().update(
							uri,
							values,
							null,
							null);

					if (oldPrice == null) {
						price.lastUpdate = new Date();
						itemPricesCache.put(price.itemId, price);
					} else {
						oldPrice.lastUpdate = new Date();
						oldPrice.buyPrice = price.buyPrice;
						oldPrice.sellPrice = price.sellPrice;
						itemPricesCache.put(oldPrice.itemId, oldPrice);
					}

					if (oldPrice == null || oldPrice.chosenPriceIsEmpty) {
						values = new ContentValues();
						values.put(Item.CHOSEN_PRICE_ID, price.chosenPrice);
						context.getContentResolver().update(
								ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, price.itemId),
								values,
								null,
								null);
					}
				}
			}

			return prices;
		}

		return Collections.emptyList();
	}

	private List<PriceBean> getPricesFromEveCentral(int solarSystemId, Integer... itemIds) {
		Log.d(NAME, "downloading prices for ids : " + Arrays.asList(itemIds));

		if(eveCentralApiService == null) {
			return null;
		}

		MarketStat marketStat = eveCentralApiService.marketstat(null, solarSystemId, itemIds);

		return toPriceBeans(marketStat.types);
	}

	private List<PriceBean> getMarketPricesFromEve(final int regionId, final int solarSystemId, Integer... itemIds) {
		Log.d(NAME, "downloading prices for ids : " + Arrays.asList(itemIds));

		return FluentIterable.from(Arrays.asList(itemIds)).transform(new Function<Integer, PriceBean>() {
			@Override
			public PriceBean apply(@Nullable Integer itemId) {
				if(itemId == null) return null;
				return toPriceBean(itemId, solarSystemId, regionId);
			}
		}).filter(new Predicate<PriceBean>() {
			@Override
			public boolean apply(PriceBean input) {
				return input != null;
			}
		}).toList();
	}

	private PriceBean toPriceBean(int itemId, int solarSystemId, int regionId) {
		PriceBean bean = new PriceBean();
		bean.itemId = itemId;
		MarketPrice marketPrice = marketService.price(regionId, itemId);
		if(marketPrice != null) {
			bean.buyPrice = marketPrice.higherBuyPrice;
			bean.buyVolume = marketPrice.buyVolume;
			bean.sellPrice = marketPrice.lowerSellPrice;
			bean.sellVolume = marketPrice.sellVolume;
		}
		bean.solarSystemId = solarSystemId;

		return bean;
	}

	private static List<PriceBean> toPriceBeans(List<Type> types) {
		List<PriceBean> prices = new ArrayList<>();
		for (Type type : types) {
			PriceBean bean = new PriceBean();
			bean.itemId = type.id;
			bean.buyPrice = type.buy.max;
			bean.buyVolume = type.buy.volume;
			bean.sellPrice = type.sell.min;
			bean.sellVolume = type.sell.volume;

			prices.add(bean);
		}

		return prices;
	}

	private static SparseArray<PriceBean> getPricesForIds(Context context, Integer... ids) {
		SparseArray<PriceBean> beans = new SparseArray<>(ids.length);

		List<Integer> idToQueryInDb = new ArrayList<>();
		for (Integer id : ids) {
			PriceBean bean = itemPricesCache.get(id);

			if (bean != null) {
				beans.append(id, bean);
			} else {
				idToQueryInDb.add(id);
			}
		}

		if (!idToQueryInDb.isEmpty()) {
			SparseArray<PriceBean> dbBeans = getDbPricesForIds(context, idToQueryInDb);
			for (Integer id : idToQueryInDb) {
				beans.append(id, dbBeans.get(id));
				itemPricesCache.put(id, dbBeans.get(id));
			}
		}

		return beans;
	}

	private static SparseArray<PriceBean> getDbPricesForIds(Context context, List<Integer> ids) {
		SparseArray<PriceBean> prices = new SparseArray<>();

		Log.d(NAME, "Querying db prices for ids : " + ItemUtil.toIdsListString(ids));
		final Cursor cursor = context.getContentResolver().query(Item.CONTENT_URI,
				new String[]{Item._ID,
						Prices.BUY_PRICE, Prices.PRODUCE_PRICE,
						Prices.SELL_PRICE, Prices.LAST_UPDATE,
						Item.CHOSEN_PRICE_ID},
				QueryBuilderUtil.buildInClause(Item._ID, ids), null, null);

		try {
			if (DbUtil.hasAtLeastOneRow(cursor)) {

				while (!cursor.isAfterLast()) {

					int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));

					PriceBean price = PricesUtil.getPriceBean(cursor);

					prices.put(itemId, price);

					cursor.moveToNext();
				}
			}
		} finally {
			cursor.close();
		}

		return prices;
	}

	public PriceBean deepFullPriceUpdate(int itemId, int groupId, int categoryId) {
		if (baseProductionNeedItemMap == null || baseProductionNeedItemMap.get() == null) {
			baseProductionNeedItemMap = new WeakReference<>(new SparseArray<SparseItemBeanArray>());
		}

		PriceBean result;

		if (baseProductionNeedItemMap.get() != null &&
				baseProductionNeedItemMap.get().get(itemId) != null) {

			Collection<Integer> itemIdsToRecalculate = new LinkedHashSet<>();
			Set<Integer> itemIdsToRefreshPrice = new TreeSet<>();

			findItemIds(baseProductionNeedItemMap.get().get(itemId), itemIdsToRecalculate, itemIdsToRefreshPrice);
			// refresh the current item prices
			itemIdsToRefreshPrice.add(itemId);

			// removes item already uptodate
			SparseArray<PriceBean> oldPrices =
					getPricesForIds(context,
							itemIdsToRefreshPrice.toArray(new Integer[itemIdsToRefreshPrice.size()]));
			for (Integer id : new TreeSet<>(itemIdsToRefreshPrice)) {
				PriceBean priceBean = oldPrices.get(id);
				if (priceBean != null && wasUpdatedLessThanThirtyMin(priceBean)) {
					itemIdsToRefreshPrice.remove(id);
				}
			}

			// Escape early if cancel() is called
			if (isCancelled()) return null;

			updatePricesForItemId(Stations.getTradeStation().regionId, Stations.getTradeStation().solarSystemId,
					itemIdsToRefreshPrice.toArray(new Integer[itemIdsToRefreshPrice.size()]));
			calculatePriceForItemId(context, itemIdsToRecalculate.toArray(new Integer[itemIdsToRecalculate.size()]));
			PriceBean calculatedPriceBean = calculatePriceForItemId(context, itemId).get(itemId);

			result = getPricesForIds(context, itemId).get(itemId);
			result.producePrice = calculatedPriceBean.producePrice;
		} else {
			baseProductionNeedItemMap.get().put(itemId,
					BaseProductionNeedsLoader.getBaseProductionNeeds(context, itemId, groupId, categoryId, -1));

			result = deepFullPriceUpdate(itemId, groupId, categoryId);
		}

		context.getContentResolver()
				.notifyChange(ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId), null);

		return result;
	}

	private void findItemIds(SparseItemBeanArray data, Collection<Integer> itemIdsToRecalculate, Set<Integer> itemIdsToRefreshPrice) {
		for (ItemBeanWithMaterials item : data.values()) {
			if (item.materials == null || item.materials.isEmpty()) {
				itemIdsToRefreshPrice.add(item.id);
			} else {
				itemIdsToRecalculate.add(item.id);

				findItemIds(item.materials, itemIdsToRecalculate, itemIdsToRefreshPrice);
			}
		}
	}

	private SparseArray<PriceBean> calculatePriceForItemId(Context context, Integer... itemIds) {

		if (itemIds == null || itemIds.length == 0) {
			return new SparseArray<>();
		}

		SparseArray<PriceBean> oldPrices =
				getPricesForIds(context, itemIds);

		final Cursor cursor = context.getContentResolver().query(
				Item.CONTENT_URI,
				new String[]{
						Item._ID, Item.GROUP_ID, Item.PORTION_SIZE,
						Groups.CATEGORIE_ID, Prices.PRODUCE_PRICE},
				QueryBuilderUtil.buildInClause(Item._ID, itemIds),
				null,
				null);

		try {
			if (DbUtil.hasAtLeastOneRow(cursor)) {

				while (!cursor.isAfterLast()) {
					// Escape early if cancel() is called
					if (isCancelled()) break;

					int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Item._ID));
					int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Groups.CATEGORIE_ID));
					int groupId = cursor.getInt(cursor.getColumnIndexOrThrow(Item.GROUP_ID));
					int portionSize = cursor.getInt(cursor.getColumnIndexOrThrow(Item.PORTION_SIZE));
					double oldProducePrice = PricesUtil.getPriceOrNaN(cursor, Prices.PRODUCE_PRICE);

					PriceBean bean = oldPrices.get(itemId);

					Log.d(NAME, "Calculating item with id : " + itemId);
					double produceUnitPrice = PriceCalculatorHelper.calculateItemPrice(
							itemId, categoryId, groupId, portionSize, context.getContentResolver());

					if (!Double.valueOf(oldProducePrice).equals(Double.valueOf(produceUnitPrice))) {

						ContentValues values = new ContentValues();
						values.put(Prices.PRODUCE_PRICE, produceUnitPrice);

						context.getContentResolver().update(
								ContentUris.withAppendedId(Prices.CONTENT_ITEM_ID_URI_BASE, itemId),
								values,
								null,
								null
						);

						bean.producePrice = produceUnitPrice;

						itemPricesCache.put(itemId, bean);
					}

					cursor.moveToNext();
				}
			}
		} finally {
			cursor.close();
		}

		return oldPrices;
	}

	public static void updateChosenPricesForItemId(int itemId, int chosenPrice) {
		PriceBean priceBean = itemPricesCache.get(itemId);

		if (priceBean != null) {
			priceBean.chosenPrice = chosenPrice;
		}
	}

	@SuppressWarnings("UnusedDeclaration")
    private static boolean wasCaculatedLessThanFiveMin(PriceBean priceBean) {
		return (new Date().getTime() - priceBean.lastUpdate.getTime()) < FIVE_MIN;
	}

	private static boolean wasUpdatedLessThanThirtyMin(PriceBean priceBean) {
		return (new Date().getTime() - priceBean.lastUpdate.getTime()) < THIRTY_MIN;
	}
}
