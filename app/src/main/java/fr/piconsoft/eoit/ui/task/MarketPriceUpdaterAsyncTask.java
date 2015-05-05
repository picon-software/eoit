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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.ui.model.PriceBeanKey;
import fr.piconsoft.eoit.api.model.MarketPrice;
import fr.piconsoft.eoit.api.services.MarketService;
import fr.piconsoft.eoit.db.util.DatabaseMapper;
import fr.piconsoft.eoit.model.EOITContext;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemBeanPrice;
import fr.piconsoft.eoit.model.Prices;

import static fr.piconsoft.eoit.util.DbUtil.formatDate;

/**
 * @author picon.software
 */
public class MarketPriceUpdaterAsyncTask extends AbstractRetrofitAsyncTask<Void, Void, List<ItemBeanPrice>> {

	public static final String TAG = MarketPriceUpdaterAsyncTask.class.getSimpleName();

	private EOITContext context;
	private int solarSystemId;
	private Integer[] itemIds;

	protected @Inject LruCache<PriceBeanKey, ItemBeanPrice> itemPricesCache;
	protected @Inject MarketService marketService;

    public MarketPriceUpdaterAsyncTask(EOITContext context) {
		this.context = context;
        context.getApplicationGraph().inject(this);
	}

	@Override
	protected Context getCurrentContext() {
		return context;
	}

	public MarketPriceUpdaterAsyncTask with(int solarSystemId, Integer... itemIds) {
		this.solarSystemId = solarSystemId;
		this.itemIds = itemIds;

		return this;
	}

	@Override
	protected List<ItemBeanPrice> doInBackground(Void param) {
		return updatePrices();
	}

	public List<ItemBeanPrice> updatePrices() {
		List<ItemBeanPrice> prices = getMarketPricesFromEve(solarSystemId, itemIds);

		fillCacheWithDbPrices(prices);

		for (ItemBeanPrice price : prices) {
			if(hasChanged(price)) {
				ItemBeanPrice dbPrice = persistPriceBean(price);
				putIntoCache(dbPrice);
				prices.add(dbPrice);
			}
		}

		return prices;
	}

	@Nullable
	private ItemBeanPrice getFromCache(int itemId, @Nullable Integer solarSystemId) {
		if(solarSystemId != null) {
			return itemPricesCache.get(new PriceBeanKey(itemId, solarSystemId));
		} else {
			return null;
		}
	}

	@Nullable
	private ItemBeanPrice putIntoCache(@NonNull ItemBeanPrice priceBean) {
		if(priceBean.solarSystemId != null) {
			return itemPricesCache.put(new PriceBeanKey(priceBean.id, priceBean.solarSystemId), priceBean);
		} else {
			return null;
		}
	}

	private boolean hasChanged(@NonNull ItemBeanPrice priceBean) {
		ItemBeanPrice cachedPrice = getFromCache(priceBean.id, priceBean.solarSystemId);

		return !priceBean.marketPricesEquals(cachedPrice);
	}

	private void fillCacheWithDbPrices(List<ItemBeanPrice> priceBeans) {
		for (ItemBeanPrice priceBean : priceBeans) {
			ItemBeanPrice cachedPrice = getFromCache(priceBean.id, priceBean.solarSystemId);
			if(cachedPrice == null) {
				ItemBeanPrice dbPrice = getFromDb(priceBean.id, priceBean.solarSystemId);
				if(dbPrice != null) {
					putIntoCache(dbPrice);
				}
			}
		}
	}

	private ItemBeanPrice persistPriceBean(ItemBeanPrice price) {
		ContentValues values = new ContentValues();
		values.put(Prices.BUY_PRICE, price.buyPrice);
		values.put(Prices.SELL_PRICE, price.sellPrice);
		values.put(Prices.SELL_VOLUME, price.sellVolume);
		values.put(Prices.BUY_VOLUME, price.buyVolume);
		values.put(Prices.SOLAR_SYSTEM_ID, price.solarSystemId);
		values.put(Prices.LAST_UPDATE, formatDate(new Date()));

		ItemBeanPrice cachedPrice = getFromCache(price.id, price.solarSystemId);

		if(cachedPrice == null) {
			cachedPrice = price;
		} else {
			cachedPrice.buyPrice = price.buyPrice;
			cachedPrice.sellPrice = price.sellPrice;
			cachedPrice.buyVolume = price.buyVolume;
			cachedPrice.sellVolume = price.sellVolume;
			cachedPrice.solarSystemId = price.solarSystemId;
			cachedPrice.lastUpdate = new Date();
		}

		Log.v(TAG, "Updating price for item id : " + price.id);

		//noinspection ConstantConditions
		Uri uri = ContentUris.withAppendedId(
				ContentUris.withAppendedId(Prices.CONTENT_ITEM_ID_URI_BASE, price.id),
				price.solarSystemId);
		context.getContentResolver().update(
				uri,
				values,
				null,
				null);

		values = new ContentValues();
		values.put(Item.CHOSEN_PRICE_ID, price.chosenPrice);
		context.getContentResolver().update(
				ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, price.id),
				values,
				null,
				null);

		return cachedPrice;
	}

    private List<ItemBeanPrice> getMarketPricesFromEve(final int solarSystemId, Integer... itemIds) {
        Log.d(TAG, "downloading prices for ids : " + Arrays.asList(itemIds));

        return Lists.transform(Arrays.asList(itemIds), new Function<Integer, ItemBeanPrice>() {
            @Override
            public ItemBeanPrice apply(Integer itemId) {
                return toPriceBean(
                        marketService.price(solarSystemId, itemId), itemId,
                        solarSystemId);
            }
        });
    }

	private static ItemBeanPrice toPriceBean(MarketPrice marketPrice, int itemId, int solarSystemId) {
		ItemBeanPrice bean = new ItemBeanPrice();
		bean.id = itemId;
		bean.buyPrice = marketPrice.higherBuyPrice;
		bean.buyVolume = marketPrice.buyVolume;
		bean.sellPrice = marketPrice.lowerSellPrice;
		bean.sellVolume = marketPrice.sellVolume;
		bean.solarSystemId = solarSystemId;

		return bean;
	}

	@Nullable
	private ItemBeanPrice getFromDb(int itemId, @Nullable Integer solarSystemId) {
		Log.d(TAG, "Querying db prices for id : " + itemId + " in solar system id : " + solarSystemId);
		final Cursor cursor = context.getContentResolver().query(Item.CONTENT_URI,
				new String[]{"*"},
				Item._ID + " = " + itemId + " AND " +
						Prices.SOLAR_SYSTEM_ID + " = " + solarSystemId, null, null);

		ItemBeanPrice priceBean = null;

		try {
			for (Cursor data : new CursorIteratorWrapper(cursor)) {
				priceBean = DatabaseMapper.create(ItemBeanPrice.class, data);
			}
		} finally {
			cursor.close();
		}

		return priceBean;
	}
}
