/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.domain.fold.halfedge;

import java.util.Optional;

import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * Undirected edge of crease pattern (or graph, more abstractly) with reference
 * to half-edges on it.
 *
 * @author OUCHI Koji
 *
 */
public class OriEdge implements IntOriLineTypeMixin {

    private final OriVertex v0;
    private final OriVertex v1;
    private OriHalfedge v0V1 = null;
    private OriHalfedge v1V0 = null;
    private int type;

    public OriEdge(final OriVertex v0, final OriVertex v1, final int type) {
        this.type = type;
        this.v0 = v0;
        this.v1 = v1;
    }

    /**
     * @return sv
     */
    public OriVertex getV0() {
        return v0;
    }

    /**
     * @return ev
     */
    public OriVertex getV1() {
        return v1;
    }

    /**
     * @return left
     */
    public Optional<OriHalfedge> getHalfedgeV0V1() {
        return Optional.ofNullable(v0V1);
    }

    /**
     * @param left
     *            Sets left
     */
    void setHalfedgeV0V1(final OriHalfedge he) {
        this.v0V1 = he;
    }

    /**
     * @return right
     */
    public Optional<OriHalfedge> getHalfedgeV1V0() {
        return Optional.ofNullable(v1V0);
    }

    /**
     * @param right
     *            Sets right
     */
    void setHalfedgeV1V0(final OriHalfedge he) {
        this.v1V0 = he;
    }

    /**
     * @return type
     */
    @Override
    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public OriVertex oppositeVertex(final OriVertex v) {
        return v == v0 ? v1 : v0;
    }

    /**
     * Computes the angle of the direction of this edge if given {@code sv} is
     * the start point of the direction.
     *
     * @param sv
     *            start vertex of the direction
     * @return arc tangent of direction vector between 0 and 2 * pi.
     */
    public double getAngle(final OriVertex sv) {
        var ev = oppositeVertex(sv);
        Vector2d dir = ev.getPositionBeforeFolding().subtract(sv.getPositionBeforeFolding());

        return dir.ownAngle();
    }

//	@Override
//	public int hashCode() {
//		return Objects.hash(Stream.of(getStartVertex(), getEndVertex()).sorted().toArray(), getType());
//	}
//
//	@Override
//	public boolean equals(final Object obj) {
//		if (obj instanceof OriEdge) {
//			var that = (OriEdge) obj;
//
//			var thisVertices = Stream.of(getStartVertex(), getEndVertex()).toArray();
//			var thatVertices = Stream.of(that.getStartVertex(), that.getEndVertex()).toArray();
//
//			return thisVertices[0].equals(thatVertices[0]) && thisVertices[1].equals(thatVertices[1])
//					&& getType() == that.getType();
//		}
//
//		return false;
//	}

    public Segment toSegment() {
        return new Segment(getV0().getPosition(), getV1().getPosition());
    }

}
