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

CREATE INDEX idx_item_group_id ON item (group_id);
CREATE INDEX idx_item_parentTypeID ON item (parentTypeID);
--CREATE INDEX idx_item_materials_item_id_material_item_id_activity_id ON item_materials (item_id, material_item_id, activity_id);
CREATE INDEX idx_groups_categorie_id ON groups (categorie_id);
--CREATE INDEX idx_invention_mapping_skill_id ON invention_mapping (skill_id);
--CREATE INDEX idx_installation_cost_station_id ON installation_cost (station_id);
CREATE INDEX idx_prices_item_id ON prices (item_id, solar_system_id);
CREATE INDEX idx_item_chosen_price_id ON item (chosen_price_id);
CREATE INDEX idx_skill_skill_id ON skill (skill_id);
CREATE INDEX idx_stock_item_id_location_id ON stock (item_id, location_id);
CREATE INDEX idx_stations_role ON stations (role);
CREATE UNIQUE INDEX date_item_id_idx ON histo ( date, item_id );
CREATE INDEX idx_manufacturing_session_item_id ON manufacturing_session (item_id);
CREATE INDEX idx_blueprint_product_item_id ON blueprint_product (item_id);
CREATE INDEX idx_blueprint_material_activity_id ON blueprint_material (activity_id);
CREATE INDEX idx_languages_current ON languages (current);