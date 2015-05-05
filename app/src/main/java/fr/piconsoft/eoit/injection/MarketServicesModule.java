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

package fr.piconsoft.eoit.injection;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.eo.evecentral.service.EveCentralAPI;
import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.api.services.MarketService;
import fr.piconsoft.eoit.model.ItemBeanPrice;
import fr.piconsoft.eoit.ui.task.PriceUpdaterAsyncTask;
import fr.piconsoft.eoit.ui.model.PriceBeanKey;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;

/**
 * @author picon.software
 */
@Module(library = true,
		addsTo = AndroidModule.class,
		injects = PriceUpdaterAsyncTask.class)
public class MarketServicesModule {

	@Provides
	@Singleton
	public LruCache<PriceBeanKey, ItemBeanPrice> providePriceCache() {
		return new LruCache<>(500);
	}

	@Provides
	@Singleton
	public EveCentralAPI provideEveCentralAPI(Context context) {
		Log.i("EveOnlineServicesModule", "New Instance of EveCentral service.");

		// Create an HTTP client that uses a cache on the file system. Android applications should use
		// their Context to get a cache directory.
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			Cache cache = new Cache(context.getCacheDir(), 100 * 1024);
			okHttpClient.setCache(cache);
		} catch (IOException e) {
			Log.w("EveOnlineServicesModule", "Can't create cache.", e);
		}

		// Create a Retrofit RestAdapter.
		Executor executor = Executors.newCachedThreadPool();

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setExecutors(executor, executor)
				.setClient(new OkClient(okHttpClient))
				.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)
				.setEndpoint(EveCentralAPI.URL)
				.setConverter(new SimpleXMLConverter())
				.build();

		return restAdapter.create(EveCentralAPI.class);
	}

	@Provides
	@Singleton
	public MarketService provideMarketService(Context context) {
		Log.i("MarketService", "New Instance of Market service.");

		// Create an HTTP client that uses a cache on the file system. Android applications should use
		// their Context to get a cache directory.
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			Cache cache = new Cache(context.getCacheDir(), 500 * 1024);
			okHttpClient.setCache(cache);
		} catch (IOException e) {
			Log.w("MarketService", "Can't create cache.", e);
		}

		// Create a Retrofit RestAdapter.
		Executor executor = Executors.newCachedThreadPool();
		return new RestAdapter.Builder()
				.setExecutors(executor, executor)
				.setClient(new OkClient(okHttpClient))
				.setEndpoint(EndPointUtil.getEndpointUrl(context))
				.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)
				.build()
				.create(MarketService.class);
	}
}
