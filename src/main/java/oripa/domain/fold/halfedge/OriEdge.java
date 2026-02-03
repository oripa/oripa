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
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Undirected edge of crease pattern (or graph, more abstractly) with reference
 * to half-edges on it.
 *
 * @author OUCHI Koji
 *
 */
public class OriEdge {

    private final OriVertex startVertex;
    private final OriVertex endVertex;
    private OriHalfedge left = null;
    private OriHalfedge right = null;
    private int type;

    public OriEdge(final OriVertex startVertex, final OriVertex endVertex, final int type) {
        this.type = type;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
    }

    /**
     * @return sv
     */
    public OriVertex getStartVertex() {
        return startVertex;
    }

    /**
     * @return ev
     */
    public OriVertex getEndVertex() {
        return endVertex;
    }

    /**
     * @return left
     */
    public Optional<OriHalfedge> getLeft() {
        return Optional.ofNullable(left);
    }

    /**
     * @param left
     *            Sets left
     */
    void setLeft(final OriHalfedge left) {
        this.left = left;
    }

    /**
     * @return right
     */
    public Optional<OriHalfedge> getRight() {
        return Optional.ofNullable(right);
    }

    /**
     * @param right
     *            Sets right
     */
    void setRight(final OriHalfedge right) {
        this.right = right;
    }

    /**
     * @return type
     */
    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public OriVertex oppositeVertex(final OriVertex v) {
        return v == startVertex ? endVertex : startVertex;
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

    public boolean isBoundary() {
        return type == OriLine.Type.CUT.toInt();
    }

    public boolean isMountain() {
        return type == OriLine.Type.MOUNTAIN.toInt();
    }

    public boolean isValley() {
        return type == OriLine.Type.VALLEY.toInt();
    }

    public boolean isUnassigned() {
        return type == OriLine.Type.UNASSIGNED.toInt();
    }

    public boolean isAssigned() {
        return type == OriLine.Type.MOUNTAIN.toInt() || type == OriLine.Type.VALLEY.toInt();
    }

    public boolean isFoldLine() {
        return type == OriLine.Type.MOUNTAIN.toInt() || type == OriLine.Type.VALLEY.toInt()
                || type == OriLine.Type.UNASSIGNED.toInt();
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
        return new Segment(getStartVertex().getPosition(), getEndVertex().getPosition());
    }

}
