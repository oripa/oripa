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
package oripa.resource;

import java.util.ListResourceBundle;

/**
 * @author OUCHI Koji
 *
 */
public class InformationStringResource_en extends ListResourceBundle {

	private static final Object[][] strings = {
			{ StringID.Information.SIMPLIFYING_CP_ID,
					"edge duplications with the same position are removed." },
			{ StringID.Information.NO_ANSWER_ID,
					"No answer was found." },

			{ StringID.Information.SIMPLIFYING_CP_TITLE_ID,
					"Simplifying CP" },
			{ StringID.Information.FOLD_ALGORITHM_TITLE_ID,
					"Fold algorithm" },
			{ StringID.Information.NOW_FOLDING_TITLE_ID,
					"Now folding..." },
			{ StringID.Information.NOW_FOLDING_ID,
					"Please wait." },
	};

	@Override
	protected Object[][] getContents() {
		return strings;
	}
}
