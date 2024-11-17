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
package oripa.persistence.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.persistence.dao.FileType;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FoldedModelFileSelectionSupportSelectorFactoryTest {

	@InjectMocks
	FoldedModelFileSelectionSupportSelectorFactory selectorFactory;

	@ValueSource(booleans = { true, false })
	@ParameterizedTest
	void selectorContainsAllTypes(final boolean modelFlipped) {
		FileFactory fileFactory = mock();

		var selector = selectorFactory.create(modelFlipped, fileFactory);

		for (var key : FoldedModelFileTypeKey.values()) {
			var type = new FileType<>(key);

			if (modelFlipped && key == FoldedModelFileTypeKey.SVG_FOLDED_MODEL) {
				continue;
			}

			if (!modelFlipped && key == FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP) {
				continue;
			}

			assertTrue(selector.getFileSelectionSupport(type).isPresent());
		}
	}

}
