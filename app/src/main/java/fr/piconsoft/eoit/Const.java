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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author picon.software
 *
 */
public abstract class Const extends BaseConst {

	public static final String APP_ROOT_DIR = "/sdcard/eoit";

	public static final String APIKEY_FILE_PATH = APP_ROOT_DIR + "/api_key.txt";
	public static final String AD_REMOVER_FILE_PATH = APP_ROOT_DIR + "/.ad_remover";

	public static final String IMAGE_SERVER_PATH = "https://image.eveonline.com/";
	public static final String IMAGE_SERVER_PATH_INVENTORY_TYPE = IMAGE_SERVER_PATH + "Type/";
	public static final String IMAGE_SERVER_PATH_RENDER = IMAGE_SERVER_PATH + "Render/";
	public static final String IMAGE_SERVER_PATH_CHARACTER = IMAGE_SERVER_PATH + "Character/";

	public static final String VALUES_WITHOUT_SEPARATOR_PATTERN = "#0.##";

	public static final String DUMP_FILE_DESCRIPTOR_NAME = "descriptor";
	public static final String DB_STRUCTURE_SQL_FILE_NAME = "dump_structure_bdd.sql";

	//Iab
	public static final String IAB_INFOS_FILENAME = "iab.infos.crypt";
	public static final String KEYFILENAME = "key.crypt";

	//skills
	public static final int PRODUCTION_EFFICIENCY_SKILL = 3388;
	public static final int INDUSTRY_SKILL = 3380;
	public static final int ADVANCED_INDUSTRY = 3388;
	public static final int SCIENCE_SKILL = 3402;
	public static final int REFINING_SKILL = 3385;
	public static final int REFINING_EFFICIENCY_SKILL = 3389;
	public static final int METALLURGY_SKILL = 3409;

	//prices
	public static final short SELL_PRICE_ID = 0;
	public static final short BUY_PRICE_ID = 1;
	public static final short PRODUCE_PRICE_ID = 2;
	public static final short OWN_PRICE_ID = 3;

	// fieldIds
	public static final short FIELD_ID_ITEM_INFO_FAVORITE = 0;
	public static final short FIELD_ID_ITEM_INFO_OWN_PRICE = 1;
	public static final short FIELD_ID_ITEM_INFO_USED_PRICE = 2;
	public static final short FIELD_ID_ITEM_INFO_ML = 3;
	public static final short FIELD_ID_ITEM_INFO_PL = 4;
	public static final short FIELD_ID_STOCK_QUANTITY = 5;
	public static final short FIELD_ID_BLUEPRINT_METALEVEL = 6;
	public static final short FIELD_ID_BLUEPRINT_DECRYPTOR = 7;
	public static final short FIELD_ID_PARAMETERS_CHARACTER_ID = 8;

	// beanIds
	public static final short BEAN_ID_BLUEPRINT = 0;
	public static final short BEAN_ID_ITEM = 1;
	public static final short BEAN_ID_ITEM_MATERIALS = 2;
	public static final short BEAN_ID_PRICES = 3;
	public static final short BEAN_ID_STOCKS = 4;
	public static final short BEAN_ID_PARAMETERS = 5;

	public static final int PARAM_USER_ID_ID = 1;
	public static final int PARAM_API_KEY_ID = 2;
	public static final int PARAM_CHARACTER_ID_ID = 3;
	public static final int PARAM_TRADE_STATION_ID = 4;
	public static final int PARAM_PROD_STATION_ID = 5;

	public static final class Invention {
		public static final Map<Integer, Float> BASE_CHANCE_MAP = new HashMap<>();
		public static final Set<Long> DATA_INTERFACE_IDS = new HashSet<>();

		static {
			//Frigates
			//Assault Ship
			BASE_CHANCE_MAP.put(324, 0.3F);
			//Cover Ops
			BASE_CHANCE_MAP.put(830, 0.3F);
			//Electronic Attack Ship
			BASE_CHANCE_MAP.put(893, 0.3F);
			//Interceptor
			BASE_CHANCE_MAP.put(831, 0.3F);
			//Stealth bomber
			BASE_CHANCE_MAP.put(834, 0.3F);

			//Destroyers
			//Interdictor
			BASE_CHANCE_MAP.put(541, 0.3F);

			//Freighters
			BASE_CHANCE_MAP.put(902, 0.3F);

			BASE_CHANCE_MAP.put(22546, 0.3F); //Skiff

			//Cruisers
			//Combat Recon Ship
			BASE_CHANCE_MAP.put(906, 0.25F);
			//Force Recon Ship
			BASE_CHANCE_MAP.put(833, 0.25F);
			//Heavy Assault Ship
			BASE_CHANCE_MAP.put(358, 0.25F);
			//Heavy Interdictor
			BASE_CHANCE_MAP.put(894, 0.25F);
			//Logistics
			BASE_CHANCE_MAP.put(832, 0.25F);

			//Industrials
			//Transport Ship
			BASE_CHANCE_MAP.put(380, 0.25F);

			BASE_CHANCE_MAP.put(22548, 0.25F); //Mackinaw

			//Battlecruisers
			//Command Ship
			BASE_CHANCE_MAP.put(506, 0.2F);

			//Battleships
			//Black Ops
			BASE_CHANCE_MAP.put(898, 0.18F);
			//Marauder
			BASE_CHANCE_MAP.put(900, 0.18F);
			BASE_CHANCE_MAP.put(22544, 0.18F); //Hulk

			Long[] idArray = {
					25553L,25587L,25857L,25858L,26597L,26598L,25555L,25584L,
					25853L,25854L,26599L,26600L,25556L,25586L,25855L,25856L,
					26601L,26602L,25554L,25585L,25851L,25852L,26603L,26604L
			};
			DATA_INTERFACE_IDS.addAll(Arrays.asList(idArray));
		}
	}

	public static class Categories {
		public static final int MATERIAL_CATEGORIE_ID = 4;
		public static final int SHIP_CATEGORIE_ID = 6;
		public static final int SKILL_CATEGORIE_ID = 16;
		public static final int COMMODITY_CATEGORIE_ID = 17;
		public static final int DRONE_CATEGORIE_ID = 18;
		public static final int STRUCTURE_CATEGORIE_ID = 23;
		public static final int ASTEROID_CATEGORIE_ID = 25;
		public static final int APPAREL_CATEGORIE_ID = 30;
		public static final int PLANETARY_COMMODITIES_CATEGORIE_ID = 43;
	}

	public static class Groups {
		public static final int ENERGY_WEAPON_GROUP_ID = 53;
		public static final int PROJECTILE_WEAPON_GROUP_ID = 55;
		public static final int HYBRID_WEAPON_GROUP_ID = 74;
		public static final Integer[] MISSILE_LAUNCHER_GROUP_IDS =
				new Integer[] {506, 507, 508, 509, 510, 511, 512, 524};
		public static final Integer[] MISSILE_GROUP_IDS =
				new Integer[] {88, 384, 385, 386, 394, 395, 396, 653, 654, 655, 656, 772};
		public static final Integer[] TURRETS_GROUP_IDS =
				new Integer[] {53, 55, 74, 737, 54, 464, 1122, 483, 650};
		public static final int GENERAL_COMMODITY_GROUP_ID = 280;
		public static final int FUEL_BLOCK_GROUP_ID = 1136;
		public static final int CONSTRUCTION_PLATFORM_GROUP_ID = 307;
	}

	public static class Items {
		// Asteroid item ids
		public static final int PLAGIOCLASE = 18;
		public static final int SPODUMAIN = 19;
		public static final int KERNITE = 20;
		public static final int HEDBERGITE = 21;
		public static final int ARKONOR = 22;
		public static final int BISTOT = 1223;
		public static final int PYROXERES = 1224;
		public static final int CROKITE = 1225;
		public static final int JASPET = 1226;
		public static final int OMBER = 1227;
		public static final int SCORDITE = 1228;
		public static final int GNEISS = 1229;
		public static final int VELDSPAR = 1230;
		public static final int HEMORPHITE = 1231;
		public static final int DARK_OCHRE = 1232;
		public static final int MERCOXIT = 11396;
		public static final Integer[] ASTEROID_IDS = {
				PLAGIOCLASE, SPODUMAIN, KERNITE, HEDBERGITE, ARKONOR, BISTOT, PYROXERES, CROKITE, JASPET,
				OMBER, SCORDITE, GNEISS, VELDSPAR, HEMORPHITE, DARK_OCHRE, MERCOXIT
			};

		// Minerals item ids
		public static final int TRITANIUM = 34;
		public static final int PYERITE = 35;
		public static final int MEXALLON = 36;
		public static final int ISOGEN = 37;
		public static final int NOCXIUM = 38;
		public static final int ZYDRINE = 39;
		public static final int MEGACYTE = 40;
		public static final int MORPHITE = 11399;
		public static final Integer[] MINERAL_IDS = {
			TRITANIUM, PYERITE, MEXALLON, ISOGEN, NOCXIUM, ZYDRINE, MEGACYTE, MORPHITE
		};
	}

	public static class Activity {
		public static final int MANUFACTORING_ACTIVITY_ID = 1;
		public static final int RESEARCH_TIME_ACTIVITY_ID = 3;
		public static final int RESEARCH_MATERIAL_ACTIVITY_ID = 4;
		public static final int COPY_ACTIVITY_ID = 5;
		public static final int REVERSE_ENGINEERING_ACTIVITY_ID = 7;
		public static final int INVENTION_ACTIVITY_ID = 8;
	}

	public static class Asteroid {

		public static int[][] DISTRIBUTION_IN_GALLENTE_SPACE =
			{
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.JASPET, Items.HEMORPHITE, Items.DARK_OCHRE, Items.CROKITE, Items.BISTOT, Items.MERCOXIT, Items.ARKONOR}, // 0.0
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.JASPET, Items.HEMORPHITE}, // 0.1
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.JASPET, Items.HEMORPHITE}, // 0.2
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.JASPET}, // 0.3
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.JASPET}, // 0.4
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.5
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.6
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.7
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE}, // 0.8
				{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE}, // 0.9
				{Items.VELDSPAR, Items.SCORDITE} // 1.0
			};
		public static int[][] DISTRIBUTION_IN_AMARR_SPACE =
			{
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE, Items.JASPET, Items.HEMORPHITE, Items.GNEISS, Items.SPODUMAIN, Items.CROKITE, Items.BISTOT, Items.MERCOXIT, Items.ARKONOR}, // 0.0
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE, Items.JASPET, Items.HEMORPHITE}, // 0.1
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE, Items.JASPET, Items.HEMORPHITE}, // 0.2
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE, Items.JASPET}, // 0.3
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE, Items.JASPET}, // 0.4
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE}, // 0.5
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE}, // 0.6
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.KERNITE}, // 0.7
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES}, // 0.8
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES}, // 0.9
			{Items.VELDSPAR, Items.SCORDITE} // 1.0
		};
		public static int[][] DISTRIBUTION_IN_CALDARI_SPACE =
			{
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE, Items.KERNITE, Items.HEDBERGITE, Items.DARK_OCHRE, Items.CROKITE, Items.BISTOT, Items.MERCOXIT, Items.SPODUMAIN}, // 0.0
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE, Items.KERNITE, Items.HEDBERGITE}, // 0.1
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE, Items.KERNITE, Items.HEDBERGITE}, // 0.2
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE, Items.KERNITE}, // 0.3
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE, Items.KERNITE}, // 0.4
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE}, // 0.5
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE}, // 0.6
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES, Items.PLAGIOCLASE}, // 0.7
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES}, // 0.8
			{Items.VELDSPAR, Items.SCORDITE, Items.PYROXERES}, // 0.9
			{Items.VELDSPAR, Items.SCORDITE} // 1.0
		};
		public static int[][] DISTRIBUTION_IN_MINMATAR_SPACE =
			{
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.KERNITE, Items.HEDBERGITE, Items.SPODUMAIN, Items.BISTOT, Items.GNEISS, Items.MERCOXIT, Items.ARKONOR}, // 0.0
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.KERNITE, Items.HEDBERGITE}, // 0.1
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.KERNITE, Items.HEDBERGITE}, // 0.2
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.KERNITE}, // 0.3
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER, Items.KERNITE}, // 0.4
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.5
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.6
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE, Items.OMBER}, // 0.7
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE}, // 0.8
			{Items.VELDSPAR, Items.SCORDITE, Items.PLAGIOCLASE}, // 0.9
			{Items.VELDSPAR, Items.SCORDITE} // 1.0
		};

		public static final int[][][] DISTRIBUTION = {
				DISTRIBUTION_IN_AMARR_SPACE,
				DISTRIBUTION_IN_CALDARI_SPACE,
				DISTRIBUTION_IN_GALLENTE_SPACE,
				DISTRIBUTION_IN_MINMATAR_SPACE
		};
	}

	public static class Territories {
		public static final int AMARR_SPACE = 0;
		public static final int CALDARI_SPACE = 1;
		public static final int GALLENTE_SPACE = 2;
		public static final int MINMATAR_SPACE = 3;
	}

	public static class Stations {
		public static final int JITA_STATION_ID = 60003760;
		public static final int[] MAIN_HUBS_STATION_IDS =
			{JITA_STATION_ID, 60008494, 60004588, 60011866, 60005686, 60011740, 60001096};

		public static final CharSequence[] ROLE_NAMES = {"Production", "Trade", "Both"};

		public static final int PRODUCTION_ROLE = 0;
		public static final int TRADE_ROLE = 1;
		public static final int BOTH_ROLES = 2;
	}

	public static class SolarSystem {
		public static final int JITA_SOLAR_SYSTEM_ID = 30000142;
	}

	public static class ApiMasks {
		public static final int ASSET_LIST = 2;
		public static final int CHARACTER_SHEET = 8;
		public static final int STANDINGS = 524288;
	}
}
