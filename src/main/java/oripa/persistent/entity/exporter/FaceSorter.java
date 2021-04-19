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

package oripa.persistent.entity.exporter;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelationValues;

import java.util.ArrayList;
import java.util.List;

public class FaceSorter {

    private final List<OriFace> faces;
    private final int[][] overlapRelation;

    public FaceSorter(List<OriFace> faces, int[][] overlapRelation) {
        this.faces = faces;
        this.overlapRelation = overlapRelation;
    }

    public List<OriFace> sortFaces() {
        ArrayList<OriFace> sortedFaces = new ArrayList<>();

        boolean[] isSorted = new boolean[faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            for (int j = 0; j < overlapRelation.length; j++) {
                if (!isSorted[j]) {
                    if (canAddFace(isSorted, overlapRelation[j])) {
                        isSorted[j] = true;
                        sortedFaces.add(faces.get(j));
                        break;
                    }
                }
            }
        }

        return sortedFaces;
    }

    private boolean canAddFace(final boolean[] isSorted, final int[] overlapRelationOfJ) {
        for (int k = 0; k < isSorted.length; k++) {
            if ((!isSorted[k])
                    && overlapRelationOfJ[k] == OverlapRelationValues.LOWER) {
                return false;
            }
        }
        return true;
    }
}