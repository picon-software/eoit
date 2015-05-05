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

/**
 * @author picon.software
 */
public enum SecStatus {
	SEC_1_0(1f),
	SEC_0_9(0.9f),
	SEC_0_8(0.8f),
	SEC_0_7(0.7f),
	SEC_0_6(0.6f),
	SEC_0_5(0.5f),
	SEC_0_4(0.4f),
	SEC_0_3(0.3f),
	SEC_0_2(0.2f),
	SEC_0_1(0.1f),
	SEC_0_0(0);

	public float value;

	SecStatus(float value) {
		this.value = value;
	}
}
