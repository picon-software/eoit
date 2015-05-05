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

package fr.piconsoft.eoit.util;

/**
 * @author picon.software
 */
public final class Formatter {

	private Formatter() {
	}

	public static String formatTime(double timeInSecond) {
		int years = (int) Math.floor(timeInSecond / (60 * 60 * 24 * 365L));
		int days = (int) Math.floor((timeInSecond - years * 60 * 60 * 24 * 365L) / (60 * 60 * 24));
		int hours = (int) Math.floor((timeInSecond - years * 60 * 60 * 24 * 365L - days * 24 * 60 * 60) / (60 * 60));
		int minutes = (int) Math.floor((timeInSecond - years * 60 * 60 * 24 * 365L - days * 24 * 60 * 60 - hours * 60 * 60) / 60);
		int seconds = (int) Math.ceil(timeInSecond - years * 60 * 60 * 24 * 365L - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60);

		StringBuilder text = new StringBuilder();

		if (years > 100) {
			return "Too much time ...";
		}

		if (years == 1) {
			text.append(1).append("year").append(' ');
		} else if (years > 1) {
			text.append(years).append("years").append(' ');
		}
		if (days == 1) {
			text.append(1).append("day").append(' ');
		} else if (days > 1) {
			text.append(days).append("days").append(' ');
		}
		if (hours > 0 && years < 1) {
			text.append(hours).append('h').append(' ');
		}
		if (minutes > 0 && years < 1) {
			text.append(minutes).append("min").append(' ');
		}
		if (seconds > 0 && years < 1 && days < 1) {
			text.append(seconds).append('s');
		}

		return text.toString();
	}

	public static String formatTimeWithoutSeconds(double timeInSecond) {
		int days = (int) Math.floor(timeInSecond / (60 * 60 * 24L));
		int hours = (int) Math.floor((timeInSecond - days * 24 * 60 * 60L) / (60 * 60));
		int minutes = (int) Math.floor((timeInSecond - days * 24 * 60 * 60L - hours * 60 * 60) / 60);

		StringBuilder text = new StringBuilder();

		if (days == 1) {
			text.append(1).append("day");
		} else if (days > 1) {
			text.append(days).append("days");
		}
		if (hours > 0) {
			text.append(' ').append(hours).append('h');
		}
		if (minutes > 0) {
			text.append(' ').append(minutes).append("min");
		}

		return text.toString();
	}
}
