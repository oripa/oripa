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

import java.util.Objects;

/**
 * @author OUCHI Koji
 *
 */
public class IntPair {
	private final int v1;
	private final int v2;

	public IntPair(final int v1, final int v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public int getV1() {
		return v1;
	}

	public int getV2() {
		return v2;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IntPair o) {
			return v1 == o.v1 && v2 == o.v2;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(v1, v2);
	}
}
