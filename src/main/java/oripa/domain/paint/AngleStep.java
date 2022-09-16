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
package oripa.domain.paint;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author OUCHI Koji
 *
 */
public enum AngleStep {
	PI_OVER_12(12),
	PI_OVER_10(10),
	PI_OVER_8(8),
	PI_OVER_6(6),
	PI_OVER_4(4),
	PI_OVER_2(2);

	private final int divNum;

	private AngleStep(final int divNum) {
		this.divNum = divNum;
	}

	/**
	 *
	 * @return the n for PI/n.
	 */
	public int getDivNum() {
		return divNum;
	}

	public double getDegreeStep() {
		return 180.0 / divNum;
	}

	public double getRadianStep() {
		return Math.PI / divNum;
	}

	public static Optional<AngleStep> fromString(final String s) {
		return Stream.of(values())
				.filter(type -> type.toString().equals(s))
				.findFirst();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDegreeStep() + " Deg.";
	}
}
