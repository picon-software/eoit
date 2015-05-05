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

package fr.piconsoft.eoit.formula;

import org.junit.Assert;
import org.junit.Test;

import fr.piconsoft.eoit.Const;
import fr.piconsoft.eoit.ui.model.Skills;

import static org.junit.Assert.assertEquals;

/**
 * @author picon.software
 */
public class FormulaCalculatorTest {

	private final static int viatorItemId = 12743, viatorGroupId = 380;
	private final static int nemesisItemId = 11377, nemesisGroupId = 834;
	private final static int hulkItemId = 22544, hulkGroupId = 543;
	private final static int traumaFuryHeavyMissileItemId = 2629, traumaFuryHeavyMissileId = 655;

	@Test
	public void testGetBaseInventionChances() {

		Assert.assertEquals(0.25f, FormulaCalculator.getBaseInventionChances(viatorItemId, viatorGroupId), 0f);
		assertEquals(0.3f, FormulaCalculator.getBaseInventionChances(nemesisItemId, nemesisGroupId), 0f);
		assertEquals(0.2f, FormulaCalculator.getBaseInventionChances(hulkItemId, hulkGroupId), 0f);
		assertEquals(0.4f, FormulaCalculator.getBaseInventionChances(traumaFuryHeavyMissileItemId, traumaFuryHeavyMissileId), 0f);

	}

	@Test
	public void testCalculateNeededMaterial() {
		Skills.initSkill(Const.PRODUCTION_EFFICIENCY_SKILL, (short) 5);

		assertEquals(15168, FormulaCalculator.calculateNeededMaterial(-4, 10112));

		assertEquals(4314714, FormulaCalculator.calculateNeededMaterial(30, 4300840));
		assertEquals(1079365, FormulaCalculator.calculateNeededMaterial(30, 1075894));
		assertEquals(271779, FormulaCalculator.calculateNeededMaterial(30, 270905));
		assertEquals(67616, FormulaCalculator.calculateNeededMaterial(30, 67399));
		assertEquals(16844, FormulaCalculator.calculateNeededMaterial(30, 16790));
		assertEquals(3856, FormulaCalculator.calculateNeededMaterial(30, 3844));
		assertEquals(1412, FormulaCalculator.calculateNeededMaterial(30, 1407));

	}
}
