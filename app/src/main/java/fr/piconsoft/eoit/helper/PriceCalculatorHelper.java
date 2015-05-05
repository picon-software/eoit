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

package fr.piconsoft.eoit.helper;

import android.content.ContentResolver;
import android.util.Log;
import android.util.SparseArray;

import static fr.piconsoft.eoit.Const.Categories.ASTEROID_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.COMMODITY_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.MATERIAL_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Categories.PLANETARY_COMMODITIES_CATEGORIE_ID;
import static fr.piconsoft.eoit.Const.Groups.FUEL_BLOCK_GROUP_ID;
import static fr.piconsoft.eoit.Const.Groups.GENERAL_COMMODITY_GROUP_ID;

/**
 * @author picon.software
 */
public class PriceCalculatorHelper {

	private static final String LOG_TAG = PriceCalculatorHelper.class.getSimpleName();
	private static SparseArray<PriceCalculatorHelperBase> helperMap = new SparseArray<>();

	private static final PriceCalculatorHelperBase COMMON = new CommonPriceCalculatorHelper();
	private static final PriceCalculatorHelperBase REACTION = new ReactionPriceCalculatorHelper();

	static {
		helperMap.put(ASTEROID_CATEGORIE_ID, new AsteroidPriceCalculatorHelper());
		helperMap.put(PLANETARY_COMMODITIES_CATEGORIE_ID, new PlanetaryCommoditiesPriceCalculatorHelper());
	}

	private PriceCalculatorHelper() {
	}

	public static double calculateItemPrice(int itemId, int categoryId, int groupId, int portionSize,
											ContentResolver contentResolver) {

		//ContextHelper.populateSkills(contentResolver);

		PriceCalculatorHelperBase helper = helperMap.get(categoryId);

		if (categoryId == COMMODITY_CATEGORIE_ID && groupId == GENERAL_COMMODITY_GROUP_ID ||
				categoryId == MATERIAL_CATEGORIE_ID && groupId != FUEL_BLOCK_GROUP_ID) {

			helper = REACTION;
		}

		if (helper == null) {
			helper = COMMON;
		}

		Log.d(LOG_TAG, "Using calculator : " + helper.getClass().getSimpleName());

		return helper
				.setContentResolver(contentResolver)
				.calculateItemPrice(itemId, groupId, portionSize);
	}
}
