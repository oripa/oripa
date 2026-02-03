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
package oripa.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class MathUtilTest {

    @Test
    void testNewtonMethod_XCubicMinus2Eq0() {
        Function<Double, Double> f = (x) -> x * x * x - 2;

        var xAnswer = MathUtil.newtonMethod(f, 2, 1e-4, 1e-6);

        assertEquals(Math.pow(2, 1.0 / 3), xAnswer, 1e-6);
    }

    @Test
    void testPreciseSum() {
        var values = new ArrayList<Double>();
        for (int i = 0; i < 10; i++) {
            values.add(0.1);
        }

        var sum = MathUtil.preciseSum(values);
        assertEquals(1, sum);
    }

    @Test
    void testPreciseSum_BigMagnitudeDifference() {
        var sum = MathUtil.preciseSum(List.of(1.0, 1e100, 1.0, -1e100));
        assertEquals(2.0, sum);
    }

}
