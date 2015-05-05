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

package fr.piconsoft.eoit;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import fr.piconsoft.eoit.injection.AndroidModule;
import fr.piconsoft.eoit.injection.EOITServicesModule;
import fr.piconsoft.eoit.injection.EveOnlineServicesModule;
import fr.piconsoft.eoit.injection.MarketServicesModule;

/**
 * @author picon.software
 */
public class EOITApplication extends Application {

	private ObjectGraph applicationGraph;

	@Override
	public void onCreate() {
		super.onCreate();

		applicationGraph = ObjectGraph.create(getModules().toArray());
	}

	protected List<?> getModules() {
		return Arrays.asList(
				new AndroidModule(this),
				new EOITServicesModule(),
				new EveOnlineServicesModule(),
				new MarketServicesModule()
		);
	}

	public void inject(Object object) {
		applicationGraph.inject(object);
	}

	public ObjectGraph getApplicationGraph() {
		return applicationGraph;
	}
}
