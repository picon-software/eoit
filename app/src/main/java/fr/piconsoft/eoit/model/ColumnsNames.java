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

package fr.piconsoft.eoit.model;

/**
 * @author picon.software
 */
@SuppressWarnings("UnusedDeclaration")
public interface ColumnsNames extends ApiColumnsNames {

	interface BaseColumns {
		/**
		 * The unique ID for a row.
		 * <P>Type: INTEGER (long)</P>
		 */
		String _ID = "_id";

		/**
		 * The count of rows in a directory.
		 * <P>Type: INTEGER</P>
		 */
		String _COUNT = "_count";
	}

	interface BaseColumnsWithName extends BaseColumns {
		String NAME = "name";
		String COLUMN_NAME_NAME_ALIAS = "region_name";
	}

	interface Blueprint extends BaseColumnsWithName {
		String TABLE_NAME = "blueprint";
		String PRODUCE_ITEM_ID = "produce_item_id";
		String UNIT_PER_BATCH = "unit_per_batch";
		String MATERIAL_UNIT_PER_BATCH = "material_unit_per_batch";
		String ML = "ml";
		String PL = "pl";
		String TECH_LEVEL = "techLevel";
		String MAX_PRODUCTION_LIMIT = "maxProductionLimit";
		String RESEARCH_PRICE = "research_price";
		String INVENTION_ITEM_META_LEVEL = "metalevel";
		String DECRYPTOR_ID = "decryptor_id";
		String RELIC_ID = "relic_id";
	}

	interface BlueprintActivity {
		String ACTIVITY_ID = "activity_id";
		String TIME = "time";
	}

	interface BlueprintProduct {
		String PROBABILITY = "probability";
	}

	interface ItemMaterials extends BaseColumns {
		String MATERIAL_ITEM_ID = "material_item_id";
		String ITEM_ID = "item_id";
		String QUANTITY = "quantity";
		String FLOAT_QUANTITY = "float_quantity";
		String PRODUCE_QUANTITY = "produce_quantity";
	}

	interface Item extends BaseColumnsWithName {
		String TABLE_NAME = "item";
		String VOLUME = "volume";
		String GROUP_ID = "group_id";
		String META_GROUP_ID = "meta_group_id";
		String PARENT_TYPE_ID = "parentTypeID";
		String CHOSEN_PRICE_ID = "chosen_price_id";
		String FAVORITE = "favorite";
		String PORTION_SIZE = "portion_size";
	}

	interface Categories extends BaseColumnsWithName {
		String TABLE_NAME = "categories";
		String COLUMN_NAME_NAME_ALIAS = "category_name";
	}

	interface Groups extends BaseColumnsWithName {
		String TABLE_NAME = "groups";
		String CATEGORIE_ID = "categorie_id";
	}

	interface InventionSkill extends BaseColumns {
		String TABLE_NAME = "invention_mapping";
		String LEARNT_OK = "learnt_ok";
		String LEARNT_KO = "learnt_ko";
		String ITEM_ID = "item_id";
		String SKILL_LEVEL = "skill_level";
		String REQUIRED_SKILL_LEVEL = "required_skill_level";
	}

	interface ManagedItem extends BaseColumns {
		String TABLE_NAME = "managed_items";
		String COLUMN_NAME_ITEM_ID = "item_id";
	}

	interface Parameter extends BaseColumns {
		String TABLE_NAME = "parameter";
		String COLUMN_NAME_PARAM_VALUE = "param_value";
	}

	interface Prices extends BaseColumns {
		String TABLE_NAME = "prices";
		String ITEM_ID = "item_id";
		String SELL_PRICE = "sell_price";
		String SELL_VOLUME = "sell_volume";
		String BUY_PRICE = "buy_price";
		String BUY_VOLUME = "buy_volume";
		String OWN_PRICE = "own_price";
		String PRODUCE_PRICE = "produce_price";
		String LAST_UPDATE = "last_update";
		String SOLAR_SYSTEM_ID = "solar_system_id";
		String PROFIT_ALIAS = "profit";
	}

	interface Region extends BaseColumnsWithName {
		String TABLE_NAME = "regions";
	}

	interface SolarSystem extends BaseColumnsWithName {
		String TABLE_NAME = "solar_systems";
		String COLUMN_NAME_REGION_ID = "region_id";
	}

	interface Station extends BaseColumnsWithName {
		String TABLE_NAME = "stations";
		String ALL_STATION_VIEW = "all_available_stations";
		String STATION_TYPE_ID = "station_type_id";
		String REGION_ID = "region_id";
		String SOLAR_SYSTEM_ID = "solar_system_id";
		String CORPORATION_ID = "corporation_id";
		String ROLE = "role";
		String FAVORITE = "favorite";
		String STANDING = "standing";
		String OUTPOST = "outpost";
	}

	interface Stock extends BaseColumns {
		String TABLE_NAME = "stock";
		String COLUMN_NAME_ITEM_ID = "item_id";
		String COLUMN_NAME_QUANTITY = "quantity";
		String COLUMN_NAME_LOCATION_ID = "location_id";
	}

	interface AsteroidConstitution extends BaseColumnsWithName {
		String TABLE_NAME = "asteroid_constitution";
		String VOLUME = "volume";
		String PORTION_SIZE = "portion_size";
		String GROUP_ID = "group_id";
		String TRITANIUM_QUANTITY = "tritanium_quantity";
		String PYERITE_QUANTITY = "pyerite_quantity";
		String MEXALLON_QUANTITY = "mexallon_quantity";
		String ISOGEN_QUANTITY = "isogen_quantity";
		String NOCXIUM_QUANTITY = "nocxium_quantity";
		String ZYDRINE_QUANTITY = "zydrine_quantity";
		String MEGACYTE_QUANTITY = "megacyte_quantity";
		String MORPHITE_QUANTITY = "morphite_quantity";
	}

	interface ManufacturingPlans extends BaseColumnsWithName {
		String[] TABLE_NAMES =
				{"manufacturing_plan_lvl0", "manufacturing_plan_lvl1", "manufacturing_plan_lvl2",
						"manufacturing_plan_lvl3", "manufacturing_plan_lvl4"};

		String ITEM_ID = "item_id";
		String CHOSEN_PRICE_ID = "chosen_price_id";
		String TOT_REQUIRED_QUANTITY = "tot_required_quantity";
		String TOT_NEEDED_QUANTITY = "tot_needed_quantity";
		String REMAINING_QUANTITY = "remaining_quantity";
	}

	interface ManufacturingShoppingList extends BaseColumnsWithName {
		String TABLE_NAME = "manufacturing_plan_shopping_list";
		String CHOSEN_PRICE_ID = "chosen_price_id";
		String TOT_NEEDED_QUANTITY = "tot_needed_quantity";
	}

	interface ManufacturingSession extends BaseColumns {
		String TABLE_NAME = "manufacturing_session";
		String ITEM_ID = "item_id";
		String NUMBER_OF_RUNS = "number_of_runs";
	}

	interface Histo extends BaseColumns {
		String TABLE_NAME = "histo";
		String ITEM_ID = "item_id";
		String DATE = "date";
		String NUMBER_OF_TIME = "number_of_time";
	}

	interface Language extends BaseColumnsWithName {
		String TABLE_NAME = "languages";
		String CURRENT = "current";
	}

	interface IndustryJobs extends BaseColumns {
		String TABLE_NAME = "industry_jobs";
		String FACILITY_ID = "facility_id";
		String STATION_ID = "station_id";
		String SOLARSYSTEM_ID = "solarSystem_id";
		String ACTIVITY_ID = "activity_id";
		String BLUEPRINT_ID = "blueprint_id";
		String START_DATE = "start_date";
		String END_DATE = "end_date";
	}
}
