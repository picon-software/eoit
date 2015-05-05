--Copyright (C) 2012 Picon software
--
--This program is free software; you can redistribute it and/or modify
--it under the terms of the GNU General Public License as published by
--the Free Software Foundation; either version 2 of the License, or
--(at your option) any later version.
--
--This program is distributed in the hope that it will be useful,
--but WITHOUT ANY WARRANTY; without even the implied warranty of
--MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
--GNU General Public License for more details.
--
--You should have received a copy of the GNU General Public License along
--with this program; if not, write to the Free Software Foundation, Inc.,
--51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

UPDATE stations SET favorite = 1, role = 2 WHERE _id = 60003760;
UPDATE item SET meta_group_id = 14 WHERE _id IN ( SELECT _id FROM item_info WHERE group_id IN (963, 954, 955, 956, 957, 958, 1305) AND meta_group_id IS NULL);