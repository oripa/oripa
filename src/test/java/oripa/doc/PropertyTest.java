/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.doc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class PropertyTest {
	Property property;

	@BeforeEach
	void setup() {
		property = new Property("dummyPath");
	}

	@Test
	void testExtractFrontColorCode() {

		final String VALUE = "#123456";

		var memo = "//" + OptionParser.Keys.FRONT_COLOR + ":" + VALUE + System.lineSeparator()
				+ "memo12345";
		property.setMemo(memo);

		assertEquals(VALUE, property.extractFrontColorCode());
	}

	@Test
	void testExtractFrontColorCode_notExistInMemo() {
		var memo = "memo12345";
		property.setMemo(memo);

		assertNull(property.extractFrontColorCode());
	}

	@Test
	void testPutFrontColorCode_update() {
		final String OLD_VALUE = "#987654";
		final String VALUE = "#123456";

		var memo = "//" + OptionParser.Keys.FRONT_COLOR + ":" + OLD_VALUE + System.lineSeparator()
				+ "memo12345";

		var updatedMemo = "//" + OptionParser.Keys.FRONT_COLOR + ":" + VALUE + System.lineSeparator()
				+ "memo12345";

		property.setMemo(memo);

		property.putFrontColorCode(VALUE);

		assertEquals(updatedMemo, property.getMemo());
	}

	@Test
	void testPutFrontColorCode_insert() {
		final String VALUE = "#123456";

		var memo = "memo12345";

		var updatedMemo = "//" + OptionParser.Keys.FRONT_COLOR + ":" + VALUE + System.lineSeparator()
				+ "memo12345";

		property.setMemo(memo);

		property.putFrontColorCode(VALUE);

		assertEquals(updatedMemo, property.getMemo());
	}

}
