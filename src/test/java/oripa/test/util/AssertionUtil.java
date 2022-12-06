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
package oripa.test.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import javax.vecmath.Vector2d;

import oripa.geom.Segment;

/**
 * @author OUCHI Koji
 *
 */
public class AssertionUtil {

	public static <T> void assertAnyMatch(final T expected, final Collection<T> actuals,
			final BiFunction<T, T, Boolean> equalityComparer) {
		assertTrue(
				actuals.stream()
						.anyMatch(actual -> equalityComparer.apply(expected, actual)),
				() -> "expected: " + expected + ", no match on actuals: " + actuals.toString());

	}

	public static void assertSegmentEquals(final Segment expected, final Segment actual,
			final BiFunction<Vector2d, Vector2d, Boolean> equalityComparer) {
		var actuals = List.of(actual.getP0(), actual.getP1());
		assertAnyMatch(expected.getP0(), actuals, equalityComparer);
		assertAnyMatch(expected.getP1(), actuals, equalityComparer);
	}
}
