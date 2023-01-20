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

/**
 * @author OUCHI Koji
 *
 */
public class ClosedRange implements Range {
	final double min;
	final double max;
	final double eps;

	public ClosedRange(final double min, final double max, final double eps) {
		if (min > max) {
			throw new IllegalArgumentException("min should be smaller than max.");
		}

		if (eps < 0) {
			throw new IllegalArgumentException("eps should be positive");
		}

		this.min = min;
		this.max = max;
		this.eps = eps;
	}

	public ClosedRange(final double min, final double max) {
		this(min, max, 0);
	}

	@Override
	public boolean includes(final double value) {
		if (eps == 0) {
			return value >= min && value <= max;
		}
		return value > min - eps && value < max + eps;
	}
}
