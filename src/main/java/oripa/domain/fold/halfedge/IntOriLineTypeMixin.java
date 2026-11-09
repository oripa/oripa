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
package oripa.domain.fold.halfedge;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public interface IntOriLineTypeMixin {
    int getType();

    default boolean isBoundary() {
        return getType() == OriLine.Type.CUT.toInt();
    }

    default boolean isMountain() {
        return getType() == OriLine.Type.MOUNTAIN.toInt();
    }

    default boolean isValley() {
        return getType() == OriLine.Type.VALLEY.toInt();
    }

    default boolean isUnassigned() {
        return getType() == OriLine.Type.UNASSIGNED.toInt();
    }

    default boolean isAssigned() {
        var type = getType();
        return type == OriLine.Type.MOUNTAIN.toInt() || type == OriLine.Type.VALLEY.toInt();
    }

    default boolean isFoldLine() {
        var type = getType();
        return type == OriLine.Type.MOUNTAIN.toInt() || type == OriLine.Type.VALLEY.toInt()
                || type == OriLine.Type.UNASSIGNED.toInt();
    }

}
