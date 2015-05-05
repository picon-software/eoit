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

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.HashSet;
import java.util.Set;

/**
 * @author picon.software
 *
 */
public final class Skills {

	private static final SparseArray<Short> skillsMap = new SparseArray<>();
	private static final Set<Integer> encryptionskills = new HashSet<>();
	private static final SparseIntArray processingSkillMap = new SparseIntArray();

    private static boolean isLevelVChar = false;

	static {
		encryptionskills.add(21790); //Caldari Encryption Methods
		encryptionskills.add(21791); //Minmatar Encryption Methods
		encryptionskills.add(23087); //Amarr Encryption Methods
		encryptionskills.add(23121); //Gallente Encryption Methods
		encryptionskills.add(3408); //Sleeper Encryption Methods

		/*
		 * "450","12180","Arkonor"
		 * "451","12181","Bistot"
		 * "452","12182","Crokite"
		 * "453","12183","Dark Ochre"
		 * "467","12184","Gneiss"
		 * "454","12185","Hedbergite"
		 * "455","12186","Hemorphite"
		 * "456","12187","Jaspet"
		 * "457","12188","Kernite"
		 * "468","12189","Mercoxit"
		 * "469","12190","Omber"
		 * "458","12191","Plagioclase"
		 * "459","12192","Pyroxeres"
		 * "460","12193","Scordite"
		 * "461","12194","Spodumain"
		 * "462","12195","Veldspar"
		 * "465","18025","Ice"
		 */
		processingSkillMap.put(450,12180);
		processingSkillMap.put(451,12181);
		processingSkillMap.put(452,12182);
		processingSkillMap.put(453,12183);
		processingSkillMap.put(467,12184);
		processingSkillMap.put(454,12185);
		processingSkillMap.put(455,12186);
		processingSkillMap.put(456,12187);
		processingSkillMap.put(457,12188);
		processingSkillMap.put(468,12189);
		processingSkillMap.put(469,12190);
		processingSkillMap.put(458,12191);
		processingSkillMap.put(459,12192);
		processingSkillMap.put(460,12193);
		processingSkillMap.put(461,12194);
		processingSkillMap.put(462,12195);
		processingSkillMap.put(465,18025);
	}

	private Skills() { }

	public static boolean isSkillMapEmpty() {
		return skillsMap.size() == 0;
	}

	public static short getSkill(int skillId) {
		if(isLevelVChar) {
			return 5;
		}

		if(skillsMap.get(skillId) != null) {
			return skillsMap.get(skillId);
		}

		return 0;
	}

	public static short getProcessingSkill(int groupId) {
		if(processingSkillMap.get(groupId) == 0) {
			return 0;
		}

		int skillId = processingSkillMap.get(groupId);

		return getSkill(skillId);
	}

	public static void initSkill(int skillId, short skillLevel) {
		skillsMap.put(skillId, skillLevel);
	}

	public static boolean isEncryptionSkill(int skillId) {
		return encryptionskills.contains(skillId);
	}

	public static void clearSkillMap() {
		skillsMap.clear();
	}

	public static boolean isLevelVChar() {
		return isLevelVChar;
	}

	public static void setLevelVChar(boolean value) {
        isLevelVChar = value;
    }
}
