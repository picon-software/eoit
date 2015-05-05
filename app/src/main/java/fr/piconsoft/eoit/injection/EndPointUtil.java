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
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.R;

/**
 * @author picon.software
 */
public class EndPointUtil {

	private static Map<String, EndPointLocation> urlCountryMap = new HashMap<>();

	private enum EndPointLocation { US, EU }

	static {
		// US Endpoint
		urlCountryMap.put("US", EndPointLocation.US);
		urlCountryMap.put("CA", EndPointLocation.US);
		urlCountryMap.put("AU", EndPointLocation.US);
		urlCountryMap.put("BR", EndPointLocation.US);

		// EU EndPoint
		urlCountryMap.put("FR", EndPointLocation.EU);
		urlCountryMap.put("RU", EndPointLocation.EU);
		urlCountryMap.put("UK", EndPointLocation.EU);
		urlCountryMap.put("DE", EndPointLocation.EU);
		urlCountryMap.put("NL", EndPointLocation.EU);
		urlCountryMap.put("SE", EndPointLocation.EU);
	}

	private EndPointUtil() {
	}

	private static String getCurrentCountry(@NonNull Context context) {
		return context.getResources().getConfiguration().locale.getCountry();
	}

	public static String getEndpointUrl(@NonNull Context context) {
		EndPointLocation location = urlCountryMap.get(getCurrentCountry(context));

		if (location == EndPointLocation.EU) {
			return context.getString(R.string.eoit_endpoint_eu);
		}

		return context.getString(R.string.eoit_endpoint_us);
	}
}
