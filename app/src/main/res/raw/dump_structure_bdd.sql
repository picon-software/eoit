--Copyright (C) 2012 Picon software

--This program is free software; you can redistribute it and/or modify
--it under the terms of the GNU General Public License as published by
--the Free Software Foundation; either version 2 of the License, or
--(at your option) any later version.

--This program is distributed in the hope that it will be useful,
--but WITHOUT ANY WARRANTY; without even the implied warranty of
--MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
--GNU General Public License for more details.

--You should have received a copy of the GNU General Public License along
--with this program; if not, write to the Free Software Foundation, Inc.,
--51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

DROP INDEX IF EXISTS idx_item_materials_item_id;
DROP INDEX IF EXISTS idx_item_materials_item_id_material_item_id_activity_id;
DROP INDEX IF EXISTS idx_item_group_id;
DROP INDEX IF EXISTS idx_item_parentTypeID;
DROP INDEX IF EXISTS idx_groups_categorie_id;
DROP INDEX IF EXISTS idx_invention_mapping_skill_id;
DROP INDEX IF EXISTS idx_installation_cost_station_id;
DROP INDEX IF EXISTS idx_blueprint_produce_item_id;
DROP INDEX IF EXISTS idx_prices_item_id;
DROP INDEX IF EXISTS idx_item_chosen_price_id;
DROP INDEX IF EXISTS idx_skill_skill_id;
DROP INDEX IF EXISTS idx_stock_item_id_location_id;
DROP INDEX IF EXISTS idx_stations_role;
DROP INDEX IF EXISTS date_item_id_idx;
DROP INDEX IF EXISTS idx_manufacturing_session_item_id;
DROP INDEX IF EXISTS idx_blueprint_product_item_id;
DROP INDEX IF EXISTS idx_blueprint_material_activity_id;
DROP INDEX IF EXISTS idx_languages_current;

DROP TABLE IF EXISTS histo;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS corporation;
DROP TABLE IF EXISTS api_key;
DROP TABLE IF EXISTS item_materials;
DROP TABLE IF EXISTS planet_schematics;
DROP TABLE IF EXISTS planet_schematics_type_map;
DROP TABLE IF EXISTS reaction_materials;
DROP TABLE IF EXISTS refine_materials;
DROP TABLE IF EXISTS blueprint;
DROP TABLE IF EXISTS blueprint_activity;
DROP TABLE IF EXISTS blueprint_material;
DROP TABLE IF EXISTS blueprint_product;
DROP TABLE IF EXISTS blueprint_skill;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS prices;
DROP TABLE IF EXISTS managed_items;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS stations;
DROP TABLE IF EXISTS solar_systems;
DROP TABLE IF EXISTS regions;
DROP TABLE IF EXISTS invention_mapping;
DROP TABLE IF EXISTS installation_cost;
DROP TABLE IF EXISTS manufacturing_session;
DROP TABLE IF EXISTS decryptor_bonus;
DROP TABLE IF EXISTS translation;
DROP TABLE IF EXISTS translation_key;
DROP TABLE IF EXISTS languages;
DROP TABLE IF EXISTS industry_jobs;
DROP TABLE IF EXISTS FTSitem;

CREATE TABLE blueprint ( _id INTEGER PRIMARY KEY, maxProductionLimit INTEGER, ml INTEGER, pl INTEGER, research_price DOUBLE, metalevel INTEGER, decryptor_id INTEGER, relic_id INTEGER);

CREATE TABLE blueprint_activity (blueprint_id INTEGER, activity_id INTEGER, time INTEGER, PRIMARY KEY (blueprint_id, activity_id));

CREATE TABLE blueprint_material (blueprint_id INTEGER, activity_id INTEGER, item_id INTEGER, quantity INTEGER, consume BOOLEAN, PRIMARY KEY (blueprint_id, activity_id, item_id));

CREATE TABLE blueprint_product (blueprint_id INTEGER, activity_id INTEGER, item_id INTEGER, probability FLOAT, quantity INTEGER, PRIMARY KEY (blueprint_id, activity_id, item_id));

CREATE TABLE blueprint_skill (blueprint_id INTEGER, activity_id INTEGER, item_id INTEGER, level INTEGER, PRIMARY KEY (blueprint_id, activity_id, item_id));

CREATE TABLE decryptor_bonus (decryptor_id INTEGER PRIMARY KEY, probability FLOAT, maxRun INTEGER, me INTEGER, pe INTEGER);

CREATE TABLE item (_id INTEGER PRIMARY KEY, volume DOUBLE, portion_size INTEGER, group_id INTEGER, meta_group_id INTEGER, parentTypeID INTEGER, chosen_price_id INTEGER, favorite BOOLEAN, invest_price DOUBLE);

CREATE TABLE planet_schematics (schematic_id INTEGER PRIMARY KEY, cycleTime INTEGER);

CREATE TABLE planet_schematics_type_map (schematic_id INTEGER, item_id INTEGER, input BOOLEAN, quantity INTEGER, PRIMARY KEY(schematic_id, item_id, input));

CREATE TABLE reaction_materials (item_id INTEGER, item_material_id INTEGER, quantity INTEGER, produce_quantity INTEGER, PRIMARY KEY(item_id, item_material_id));

CREATE TABLE refine_materials (item_id INTEGER, item_material_id INTEGER, quantity INTEGER, PRIMARY KEY(item_id, item_material_id));

CREATE TABLE prices (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER REFERENCES item NOT NULL, buy_price DOUBLE, own_price DOUBLE, produce_price DOUBLE, sell_price DOUBLE, sell_volume INTEGER, buy_volume INTEGER, solar_system_id INTEGER REFERENCES solar_systems NOT NULL, last_update DATETIME, UNIQUE (item_id, solar_system_id) ON CONFLICT REPLACE);

CREATE TABLE stock (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER REFERENCES item NOT NULL, quantity INTEGER, location_id INTEGER NOT NULL);

CREATE TABLE managed_items (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER REFERENCES item);

CREATE TABLE categories (_id INTEGER PRIMARY KEY);

CREATE TABLE groups (_id INTEGER PRIMARY KEY, categorie_id INTEGER);

CREATE TABLE regions (_id INTEGER PRIMARY KEY, name TEXT);

CREATE TABLE solar_systems (_id INTEGER PRIMARY KEY, region_id INTEGER, name TEXT);

CREATE TABLE stations (_id INTEGER PRIMARY KEY, station_type_id INTEGER NOT NULL, solar_system_id INTEGER, region_id INTEGER, corporation_id INTEGER NOT NULL, name TEXT, role INTEGER, favorite BOOLEAN, standing FLOAT, outpost BOOLEAN);

--CREATE TABLE invention_mapping (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER REFERENCES item, skill_id INTEGER REFERENCES item);

--CREATE TABLE installation_cost (_id INTEGER PRIMARY KEY AUTOINCREMENT, station_id INTEGER REFERENCES stations, install_cost DOUBLE, cost_per_hour DOUBLE, activity_id INTEGER, discount_per_good_standing_point DOUBLE, surcharge_per_bad_standing_point DOUBLE);

CREATE TABLE api_key (_id INTEGER PRIMARY KEY, v_code TEXT, mask INTEGER);

CREATE TABLE corporation (_id INTEGER PRIMARY KEY, key_id INTEGER REFERENCES api_key, name TEXT);

CREATE TABLE character (_id INTEGER PRIMARY KEY, key_id INTEGER REFERENCES api_key NOT NULL, name TEXT, corp_id INTEGER REFERENCES corporation NOT NULL, active BOOLEAN);

CREATE TABLE skill (_id INTEGER PRIMARY KEY AUTOINCREMENT, character_id INTEGER REFERENCES character NOT NULL, skill_id INTEGER REFERENCES item, level INTEGER);

CREATE TABLE manufacturing_session (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER REFERENCES item NOT NULL, number_of_runs INTEGER DEFAULT 1 NOT NULL);

CREATE TABLE histo (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, item_id INTEGER REFERENCES item NOT NULL, number_of_time INTEGER DEFAULT 1 NOT NULL);

CREATE TABLE translation_key (key_id INTEGER, tc_id INTEGER, language_id INTEGER, tra_id INTEGER, PRIMARY KEY(key_id, tc_id, language_id));

CREATE TABLE translation (tra_id INTEGER PRIMARY KEY, text TEXT);

CREATE TABLE languages (_id INTEGER PRIMARY KEY, name TEXT, current BOOLEAN);

INSERT INTO languages VALUES (0, 'Deutsch', 0);
INSERT INTO languages VALUES (1, 'English', 1);
INSERT INTO languages VALUES (2, 'Français', 0);
INSERT INTO languages VALUES (3, 'Pусский', 0);

CREATE TABLE industry_jobs (_id INTEGER PRIMARY KEY, facility_id INTEGER, station_id INTEGER, solarSystem_id INTEGER, activity_id INTEGER, blueprint_id INTEGER, start_date DATETIME, end_date DATETIME);
