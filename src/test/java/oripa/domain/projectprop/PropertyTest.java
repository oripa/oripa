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
package oripa.domain.projectprop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
		property = new Property();
	}

	@Test
	void testExtractFrontColorCode() {
		testExtraction(OptionParser.Keys.FRONT_COLOR, property::extractFrontColorCode);
	}

	@Test
	void testExtractBackColorCode() {
		testExtraction(OptionParser.Keys.BACK_COLOR, property::extractBackColorCode);
	}

	void testExtraction(final String key, final Supplier<String> extractor) {
		final String VALUE = "#123456";

		var memo = "//" + key + ":" + VALUE + System.lineSeparator()
				+ "memo12345";
		property.setMemo(memo);

		assertEquals(VALUE, extractor.get());
	}

	@Test
	void testExtractFrontColorCode_notExistInMemo() {
		testExtraction_notExistInMemo(property::extractFrontColorCode);
	}

	@Test
	void testExtractBackColorCode_notExistInMemo() {
		testExtraction_notExistInMemo(property::extractBackColorCode);
	}

	void testExtraction_notExistInMemo(final Supplier<String> extractor) {
		var memo = "memo12345";
		property.setMemo(memo);

		assertNull(extractor.get());
	}

	@Test
	void testPutFrontColorCode_update() {
		testPut_update(OptionParser.Keys.FRONT_COLOR, property::putFrontColorCode);
	}

	@Test
	void testPutBackColorCode_update() {
		testPut_update(OptionParser.Keys.BACK_COLOR, property::putBackColorCode);
	}

	void testPut_update(final String key, final Consumer<String> put) {
		final String OLD_VALUE = "#987654";
		final String VALUE = "#123456";

		var memo = "//" + key + ":" + OLD_VALUE + System.lineSeparator()
				+ "memo12345";

		var updatedMemo = OptionParser.HEAD_COMMENT + System.lineSeparator()
				+ "//" + key + ":" + VALUE + System.lineSeparator()
				+ "memo12345";

		property.setMemo(memo);

		put.accept(VALUE);

		assertEquals(updatedMemo, property.getMemo());
	}

	@Test
	void testPutFrontColorCode_insert() {
		testPut_insert(OptionParser.Keys.FRONT_COLOR, property::putFrontColorCode);
	}

	@Test
	void testPutBackColorCode_insert() {
		testPut_insert(OptionParser.Keys.BACK_COLOR, property::putBackColorCode);
	}

	void testPut_insert(final String key, final Consumer<String> put) {
		final String VALUE = "#123456";

		var memo = "memo12345";

		var updatedMemo = OptionParser.HEAD_COMMENT + System.lineSeparator()
				+ "//" + key + ":" + VALUE + System.lineSeparator()
				+ "memo12345";

		property.setMemo(memo);

		put.accept(VALUE);

		assertEquals(updatedMemo, property.getMemo());
	}

}
