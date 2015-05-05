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

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.api.services.IndustryService;
import fr.piconsoft.eoit.ui.task.JobCostCalculationTask;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author picon.software
 */
@Module(library = true,
		addsTo = AndroidModule.class,
		injects = JobCostCalculationTask.class)
public class EOITServicesModule {

	@Provides
	@Singleton
	public IndustryService provideIndustryService(Context context) {
		Log.i("EOITServicesModule", "New Instance of EOIT service.");

		// Create an HTTP client that uses a cache on the file system. Android applications should use
		// their Context to get a cache directory.
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			Cache cache = new Cache(context.getCacheDir(), 500 * 1024);
			okHttpClient.setCache(cache);
		} catch (IOException e) {
			Log.w("EOITServicesModule", "Can't create cache.", e);
		}

		// Create a Retrofit RestAdapter.
		Executor executor = Executors.newCachedThreadPool();
		return new RestAdapter.Builder()
				.setExecutors(executor, executor)
				.setClient(new OkClient(okHttpClient))
				.setEndpoint(EndPointUtil.getEndpointUrl(context))
				.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)
				.build()
				.create(IndustryService.class);
	}
}
