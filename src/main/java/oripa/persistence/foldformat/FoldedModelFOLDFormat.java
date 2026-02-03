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
package oripa.persistence.foldformat;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelFOLDFormat extends FOLDFormat {

    @SerializedName("faces_oripa:precreases")
    private List<List<Integer>> facesPrecreases;

    public FoldedModelFOLDFormat() {
        setFileClasses(List.of(FileClass.SINGLE_MODEL));

        setFrameClasses(List.of(FrameClass.FOLDED_FORM));

        setFrameAttributes(List.of(FrameAttribute.TWO_DIMENSION));
    }

    /**
     * Each element of the returned list is a pair of face index and precrease's
     * edge index.
     *
     * @return
     */
    public List<List<Integer>> getFacesPrecreases() {
        return facesPrecreases;
    }

    public void setFacesPrecreases(final List<List<Integer>> facesPrecreases) {
        this.facesPrecreases = facesPrecreases;
    }
}
