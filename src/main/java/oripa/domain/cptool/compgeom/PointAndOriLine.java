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
package oripa.domain.cptool.compgeom;

import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * For efficient computation. keyPoint is an OriPoint for the end point as a key
 * of SharedPointsMap. oppositeKeyPoint is the one for the opposite side.
 * Equality comparison between instances of this class is done by only
 * {@link OriLine}'s equality.
 *
 * @author OUCHI Koji
 *
 */
public class PointAndOriLine {
	private final OriPoint point;
	private OriPoint keyPoint;
	private OriPoint oppositeKeyPoint;
	private final OriLine line;

	public PointAndOriLine(final OriPoint point, final OriLine line) {
		this.point = point;
		this.line = line;

		if (line.pointStream().noneMatch(point::equals)) {
			throw new IllegalArgumentException("point should be equal to the one of the segment end point.");
		}
	}

	/**
	 * @return point
	 */
	public OriPoint getPoint() {
		return point;
	}

	public OriPoint getOppsitePoint(final OriPoint p, final double eps) {
		return p.equals(line.getOriPoint0(), eps) ? line.getOriPoint1() : line.getOriPoint0();
	}

	/**
	 * @return line
	 */
	public OriLine getLine() {
		return line;
	}

	public double getX() {
		return point.getX();
	}

	public double getY() {
		return point.getY();
	}

	/**
	 * @return keyPoint
	 */
	public OriPoint getKeyPoint() {
		return keyPoint;
	}

	/**
	 * @param keyPoint
	 *            Sets keyPoint
	 */
	public void setKeyPoint(final OriPoint keyPoint) {
		this.keyPoint = keyPoint;
	}

	/**
	 * @return keyPoint
	 */
	public OriPoint getOppositeKeyPoint() {
		return oppositeKeyPoint;
	}

	/**
	 * @param keyPoint
	 *            Sets keyPoint
	 */
	public void setOppositeKeyPoint(final OriPoint keyPoint) {
		this.oppositeKeyPoint = keyPoint;
	}

	@Override
	public int hashCode() {
		return line.hashCode();
	}

	/**
	 * This comparison cares the {@link OriLine} equality only.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof PointAndOriLine other) {
			if (line == null) {
				if (other.line != null) {
					return false;
				}
			} else if (!line.equals(other.line)) {
				return false;
			}

		}
		return true;
	}

}