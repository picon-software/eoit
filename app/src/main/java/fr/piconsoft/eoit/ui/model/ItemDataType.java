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

package fr.piconsoft.eoit.ui.model;

/**
 * @author picon.software
 */
public interface ItemDataType {

	// Item info
    int LIST_ITEM = 0;
    int ITEM_INFO_PRICES = 1;
    int ITEM_INFO_BLUEPRINT = 2;
    int ITEM_INFO = 3;
    int LIST_SECTION = 4;

	// Blueprint invention
	int BLUEPRINT_INVENTION_CHANCES = 5;
	int BLUEPRINT_INVENTION_SPEC = 6;
	int BLUEPRINT_INVENTION_DECRYPTOR = 7;
	int LIST_SKILL = 8;
	int BLUEPRINT_INVENTION_INITIATOR = 9;

}
