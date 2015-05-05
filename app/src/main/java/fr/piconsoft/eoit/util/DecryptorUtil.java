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

import java.util.HashMap;
import java.util.Map;

/**
 * @author picon.software
 */
public final class DecryptorUtil {

	private static final Map<Integer, DecryptorBonuses> mapDecryptorBonuses = new HashMap<>();

	public static final DecryptorBonuses NO_DECRYPTOR = new DecryptorUtil().new DecryptorBonuses(1, 0, 0, 0);

	private static final DecryptorBonuses decryptorAccelerant = new DecryptorUtil().new DecryptorBonuses(1.2, 1, 2, 5);
	private static final DecryptorBonuses decryptorAttainment = new DecryptorUtil().new DecryptorBonuses(1.8, 4, -1, 2);
	private static final DecryptorBonuses decryptorAugmentation = new DecryptorUtil().new DecryptorBonuses(0.6, 9, -2, 1);
	private static final DecryptorBonuses decryptorParity = new DecryptorUtil().new DecryptorBonuses(1.5, 3, 1, -1);
	private static final DecryptorBonuses decryptorProcess = new DecryptorUtil().new DecryptorBonuses(1.1, 0, 3, 3);
	private static final DecryptorBonuses decryptorSymmetry = new DecryptorUtil().new DecryptorBonuses(1, 2, 1, 4);
	private static final DecryptorBonuses decryptorOptimizedAugmentation = new DecryptorUtil().new DecryptorBonuses(1.9, 2, 1, -1);
	private static final DecryptorBonuses decryptorOptimizedAttainment = new DecryptorUtil().new DecryptorBonuses(0.9, 7, 2, 0);

	/*

new DecryptorUtil().new DecryptorBonuses(1.1, 0, 3, 6);   //decryptorProcess
new DecryptorUtil().new DecryptorBonuses(1.2, 1, 2, 10);  //decryptorAccelerant
new DecryptorUtil().new DecryptorBonuses(1, 2, 1, 8);     //decryptorSymmetry
new DecryptorUtil().new DecryptorBonuses(0.6, 9, -2, 2);  //decryptorAugmentation
new DecryptorUtil().new DecryptorBonuses(1.8, 4, -1, 4);  //decryptorAttainment
new DecryptorUtil().new DecryptorBonuses(1.5, 3, 1, -2);  //decryptorParity
new DecryptorUtil().new DecryptorBonuses(0.9, 7, 2, 0);   //decryptorOptimizedAugmentation
new DecryptorUtil().new DecryptorBonuses(1.9, 2, 1, -2);  //decryptorOptimizedAttainment

	*/

	static {
		//Caldari Encryption Methods 731
		//Minmatar Encryption Methods 729
		//Amarr Encryption Methods 728
		//Gallente Encryption Methods 730

		mapDecryptorBonuses.put(34201, decryptorAccelerant);
		mapDecryptorBonuses.put(34202, decryptorAttainment);
		mapDecryptorBonuses.put(34203, decryptorAugmentation);
		mapDecryptorBonuses.put(34204, decryptorParity);
		mapDecryptorBonuses.put(34205, decryptorProcess);
		mapDecryptorBonuses.put(34206, decryptorSymmetry);
		mapDecryptorBonuses.put(34207, decryptorOptimizedAttainment);
		mapDecryptorBonuses.put(34208, decryptorOptimizedAugmentation);
	}

	private DecryptorUtil() {
	}

	public static Integer getDecryptorGroupId() {
		return 1304;
	}

	public static DecryptorBonuses getDecryptorBonuses(long decryptorId) {
		return mapDecryptorBonuses.get((int) decryptorId);
	}

	public static DecryptorBonuses getDecryptorBonusesOrDefault(long decryptorId) {
		if (!mapDecryptorBonuses.containsKey((int) decryptorId)) {
			return NO_DECRYPTOR;
		}
		return mapDecryptorBonuses.get((int) decryptorId);
	}

	public class DecryptorBonuses {
		public float probabilityMultiplier;
		public short maxRunModifier;
		public short meModifier;
		public short peModifier;

		private DecryptorBonuses(double probabilityMultiplier, int maxRunModifier, int meModifier, int peModifier) {
			super();
			this.probabilityMultiplier = (float) probabilityMultiplier;
			this.maxRunModifier = (short) maxRunModifier;
			this.meModifier = (short) meModifier;
			this.peModifier = (short) peModifier;
		}
	}
}
