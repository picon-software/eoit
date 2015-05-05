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

package fr.piconsoft.eoit.formula;

import fr.piconsoft.eoit.Const;
import fr.piconsoft.eoit.ui.model.Skills;
import fr.piconsoft.eoit.ui.model.Stations;

/**
 * @author picon.software
 */
public final class FormulaCalculator {

	private FormulaCalculator() {
	}

	public static float calculateWasteFactor(long materialLevel) {
		return materialLevel >= 0 ? 0.1f / (1 + materialLevel) : 0.1f * (1 - materialLevel);
	}

	public static long calculateWastedMaterial(long materialLevel, long baseMaterial) {
		return Math.round((calculateWasteFactor(materialLevel)
				+ 0.25 - (0.05 * Skills.getSkill(Const.PRODUCTION_EFFICIENCY_SKILL))) * baseMaterial);
	}

	public static long calculateNeededMaterial(long materialLevel, long baseMaterial) {
		return baseMaterial + calculateWastedMaterial(materialLevel, baseMaterial);
	}

	public static double calculateSkillProductionTimeModifier() {
		return (1 - (0.04 * Skills.getSkill(Const.INDUSTRY_SKILL))) *
				(1 - (0.03 * Skills.getSkill(Const.ADVANCED_INDUSTRY)));
	}

	public static double calculateProductionTime(float baseProductionTime, long timeEfficiency) {

		return baseProductionTime * (1 - timeEfficiency/100) * calculateSkillProductionTimeModifier();
	}

	public static float calculateInventionChances(float baseChance, short encryptionSkillLevel, short datacore1SkillLevel, short datacore2SkillLevel, float decryptorModifier) {
		return baseChance * (1 + ((datacore1SkillLevel + datacore2SkillLevel) / 30f + encryptionSkillLevel / 40f)) * decryptorModifier;
	}

	public static double calculateMaxRunsCopyTimeInSeconds(long baseCopyTime) {
		return baseCopyTime * (1 - 0.05 * Skills.getSkill(Const.SCIENCE_SKILL)) * 2;
	}

	public static double calculateMaxRunsCopyTimeInHours(long baseCopyTime) {
		return calculateMaxRunsCopyTimeInSeconds(baseCopyTime) / (60 * 60);
	}

	public static double calculateMEResearchTimeInHours(long baseResearchTime) {
		return (baseResearchTime * (1 - 0.05 * Skills.getSkill(Const.METALLURGY_SKILL))) / (60 * 60);
	}

	public static double calculateMaxRunsCopyCost(long baseCopyTime, float installationCost, float usageCost,
												  float discountPerGoodStandingPoint, float surchargePerBadStandingPoint) {
		double basePrice = installationCost + calculateMaxRunsCopyTimeInHours(baseCopyTime) * usageCost;

		return Stations.getProductionStation().standing > 0 ?
				basePrice * (100 - discountPerGoodStandingPoint * Stations.getProductionStation().standing) / 100 :
				basePrice * (1 + (surchargePerBadStandingPoint * Stations.getProductionStation().standing) / 100);
	}

	public static double calculateMEResearchCost(long baseCopyTime, float installationCost, float usageCost,
												 float discountPerGoodStandingPoint, float surchargePerBadStandingPoint) {
		double basePrice = installationCost + calculateMEResearchTimeInHours(baseCopyTime) * usageCost;

		return Stations.getProductionStation().standing > 0 ?
				basePrice * (100 - discountPerGoodStandingPoint * Stations.getProductionStation().standing) / 100 :
				basePrice * (1 + (surchargePerBadStandingPoint * Stations.getProductionStation().standing) / 100);
	}

	/**
	 * Station Equipment x
	 * 		(1 + Refining skill x 0.03) x
	 * 		(1 + Refining Efficiency skill x 0.02) x
	 * 		(1 + Ore Processing skill x 0.02)
	 *
	 * @param groupId the groupId
	 * @return Reprocessing yield
	 */
	public static float calculateEffectiveRefiningYield(int groupId) {
		int refining_skill = Skills.getSkill(Const.REFINING_SKILL);
		int refining_efficiency_skill = Skills.getSkill(Const.REFINING_EFFICIENCY_SKILL);
		int getprocessingskill = Skills.getProcessingSkill(groupId);

		float yield = (float) (
				Stations.getProductionStation().reprocessEfficiency *
						(1 + 0.03 * Skills.getSkill(Const.REFINING_SKILL)) *
						(1 + 0.02 * Skills.getSkill(Const.REFINING_EFFICIENCY_SKILL)) *
						(1 + 0.02 * Skills.getProcessingSkill(groupId)));
		return yield > 1 ? 1 : yield;
	}

	public static int calculateRefiningWaste(long quantity, int groupId) {
		return Math.round(quantity * (1 - calculateEffectiveRefiningYield(groupId)));
	}

	public static float calculateReprocessStationTaxe() {
		float tax = (float) (Stations.getProductionStation().reprocessingStationsTake - (0.75 * Stations.getProductionStation().standing * 0.01));
		return tax > 0 ? tax : 0;
	}

	public static int calculateReprocessStationTake(long quantity) {
		return Math.round(quantity * calculateReprocessStationTaxe());
	}

	public static float getBaseInventionChances(int producedItemId, int producedItemGroupId) {
		float baseInventionChance;

		if (Const.Invention.BASE_CHANCE_MAP.containsKey(producedItemId)) {
			baseInventionChance = Const.Invention.BASE_CHANCE_MAP.get(producedItemId);
		} else if (Const.Invention.BASE_CHANCE_MAP.containsKey(producedItemGroupId)) {
			baseInventionChance = Const.Invention.BASE_CHANCE_MAP.get(producedItemGroupId);
		} else {
			baseInventionChance = 0.4F;
		}

		return baseInventionChance;
	}
}
