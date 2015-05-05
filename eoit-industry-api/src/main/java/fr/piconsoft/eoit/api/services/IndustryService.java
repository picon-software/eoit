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

package fr.piconsoft.eoit.api.services;

import fr.piconsoft.eoit.api.model.AdjustedPrice;
import fr.piconsoft.eoit.api.model.CostIndex;
import fr.piconsoft.eoit.api.model.Tax;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author picon.software
 */
public interface IndustryService {

	@GET("/api/market/adjusted_prices/{itemId}")
	AdjustedPrice price(@Path("itemId") int itemId);

	@GET("/api/industry/systems/{systemId}/{activityId}")
	CostIndex costIndex(@Path("systemId") int systemId, @Path("activityId") int activityId);

	@GET("/api/industry/facilities/{facilityId}")
	Tax tax(@Path("facilityId") int facilityId);
}
