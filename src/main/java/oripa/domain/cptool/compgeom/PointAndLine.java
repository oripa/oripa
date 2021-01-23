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
 * For efficient computation. keyPoint0 is an OriPoint for the end point as a
 * key of SharedPointsMap. keyPoint1 is the one for the opposite side.
 *
 * @author OUCHI Koji
 *
 */
public class PointAndLine {
	private final OriPoint point;
	private OriPoint keyPoint;
	private OriPoint oppositKeyPoint;
	private final OriLine line;

	public PointAndLine(final OriPoint point, final OriLine line) {
		this.point = point;
		this.line = line;
	}

	/**
	 * @return point
	 */
	public OriPoint getPoint() {
		return point;
	}

	/**
	 * @return line
	 */
	public OriLine getLine() {
		return line;
	}

	public double getX() {
		return point.x;
	}

	public double getY() {
		return point.y;
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
		return oppositKeyPoint;
	}

	/**
	 * @param keyPoint
	 *            Sets keyPoint
	 */
	public void setOppositeKeyPoint(final OriPoint keyPoint) {
		this.oppositKeyPoint = keyPoint;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PointAndLine other = (PointAndLine) obj;
		if (line == null) {
			if (other.line != null) {
				return false;
			}
		} else if (!line.equals(other.line)) {
			return false;
		}
//			if (point == null) {
//				if (other.point != null) {
//					return false;
//				}
//			} else if (!point.equals(other.point)) {
//				return false;
//			}
		return true;
	}

}